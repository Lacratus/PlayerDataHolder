package be.lacratus.playerdataholder.commands;

import be.lacratus.playerdataholder.Playerdataholder;
import be.lacratus.playerdataholder.data.handler.StoredDataHandler;
import be.lacratus.playerdataholder.objects.DDGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PlayerDataCommand implements CommandExecutor {

    private Playerdataholder main;
    private Location lastLocation;
    private String gamemode;
    private long[] onlineTime;
    private boolean fly;

    private StoredDataHandler storedDataHandler;

    public PlayerDataCommand(Playerdataholder main) {
        this.main = main;
        this.storedDataHandler = main.getStoredDataHandler();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            //Ingevoerde speler is online
            long millis;
            if (main.getServer().getPlayerExact(args[0]) != null) {
                Player target = main.getServer().getPlayerExact(args[0]);
                lastLocation = target.getLocation();
                gamemode = target.getGameMode().toString();
                millis = (long) (target.getStatistic(Statistic.PLAY_ONE_TICK) * 0.05D * 1000.0D);
                onlineTime = main.OnlineTimeToLong(millis);
                fly = target.isFlying();

                sendPlayerInfo(sender, lastLocation, gamemode, onlineTime, fly);
                return true;
            }
            //Er wordt geen gebruik gemaakt van mysql
            else if (!main.getConfig().getBoolean("use-mysql")) {
                //Ingevoerde speler is offline
                if (main.getLocalPlayerData().getConfig().contains(args[0])) {
                    ConfigurationSection localplayerdata = main.getLocalPlayerData().getConfig().getConfigurationSection(args[0]);
                    ConfigurationSection locationPlayer = localplayerdata.getConfigurationSection("location");

                    gamemode = localplayerdata.getString("gamemode");

                    fly = localplayerdata.getBoolean("fly");
                    lastLocation = new Location(main.getServer().getWorld(locationPlayer.getString("world")), locationPlayer.getDouble("x"), locationPlayer.getDouble("y"), locationPlayer.getDouble("z"));
                    int days = localplayerdata.getConfigurationSection("onlinetime").getInt("days");
                    int hours = localplayerdata.getConfigurationSection("onlinetime").getInt("hours");
                    int minutes = localplayerdata.getConfigurationSection("onlinetime").getInt("minutes");
                    int seconds = localplayerdata.getConfigurationSection("onlinetime").getInt("seconds");
                    onlineTime = new long[]{days,hours,minutes,seconds};
                    sendPlayerInfo(sender, lastLocation, gamemode, onlineTime, fly);
                    return true;
                    //Onjuiste UUID is ingegeven
                } else {
                    sender.sendMessage("This player doesn't exist or is offline! Use UUID to see offlineplayer details(Use '-'')");
                    return false;
                }
                //Bij gebruik van mysql
            } else if (main.getConfig().getBoolean("use-mysql")) {
                System.out.println("SQL is being used");
                //Kijken in cache of informatie van speler er nog instaat.
                if (main.getPlayerList().containsKey(args[0])) {
                    DDGPlayer target = main.getPlayerList().get(args[0]);
                    lastLocation = target.getLastLocation();
                    gamemode = target.getGamemode();
                    onlineTime = target.getOnlineTime();
                    fly = target.isFly();

                    sendPlayerInfo(sender, lastLocation, gamemode, onlineTime, fly);

                    return true;
                    //Speler is offline en info is niet opgevraagd
                } else {
                    storedDataHandler.loadData(args[0]).thenAccept(DDGspeler -> {
                        main.getPlayerList().put(args[0], DDGspeler);
                        DDGPlayer target = main.getPlayerList().get(args[0]);
                        lastLocation = target.getLastLocation();
                        gamemode = target.getGamemode();
                        onlineTime = target.getOnlineTime();
                        fly = target.isFly();

                        sendPlayerInfo(sender, lastLocation, gamemode, onlineTime, fly);
                        if (main.getCachePlayerInfo().containsKey(target.getUUID())) {
                            main.getCachePlayerInfo().get(target.getUUID()).cancel();
                            main.getServer().broadcastMessage("Debug: Target Added");
                        }

                        main.getCachePlayerInfo().put(target.getUUID(), Bukkit.getScheduler().runTaskLater(main, () -> {
                                    main.getCachePlayerInfo().remove(target.getUUID());
                                    main.getServer().broadcastMessage("Debug: Target removed");
                                }
                                , 20L * 60));
                    }).exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });
                    return true;
                }
                //Verkeerde notatie in config bij 'my-sql'
            } else {
                sender.sendMessage("Error: Wrong notation in configFile at 'my-sql' ");
                return false;
            }
        }
        sender.sendMessage("Syntax: /Playerdata 'Naam'");
        return false;
    }

    private void sendPlayerInfo(CommandSender sender, Location lastLocation, String gamemode, long[] onlineTime, Boolean fly) {
        sender.sendMessage("Time: " + onlineTime[0] + " dagen, " + onlineTime[1] + " uren, " + onlineTime[2] + " minuten, " + onlineTime[3] + " seconden." + "\n"
                + "Gamemode: " + gamemode + "\n"
                + "Fly: " + fly + "\n"
                + "location: " + (int) lastLocation.getX() + ", " + (int) lastLocation.getY() + ", " + (int) lastLocation.getZ());
    }
}
