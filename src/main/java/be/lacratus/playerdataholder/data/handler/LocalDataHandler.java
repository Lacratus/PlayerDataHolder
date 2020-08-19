package be.lacratus.playerdataholder.data.handler;

import be.lacratus.playerdataholder.objects.DDGPlayer;
import be.lacratus.playerdataholder.Playerdataholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LocalDataHandler implements DataHandler {

    private Playerdataholder main;
    private ConfigurationSection localPlayerData;
    private DDGPlayer target;
    private String uuid;

    public LocalDataHandler(Playerdataholder main) {
        this.main = main;
        localPlayerData = main.getLocalPlayerData().getConfig();
    }

    //Save all playerdata of certain player
    @Override
    public void saveData(DDGPlayer data) {
        //Update data lokaal.
        localPlayerData = main.getLocalPlayerData().getConfig().getConfigurationSection(data.getUUID());
        localPlayerData.getConfigurationSection("location").set("world",data.getLastLocation().getWorld().getName());
        localPlayerData.getConfigurationSection("location").set("x",data.getLastLocation().getX());
        localPlayerData.getConfigurationSection("location").set("y",data.getLastLocation().getY());
        localPlayerData.getConfigurationSection("location").set("z",data.getLastLocation().getZ());
        long[] onlineTime = data.getOnlineTime();
        System.out.println(onlineTime[0] + onlineTime[1] + onlineTime[2] + onlineTime[3]);
        localPlayerData.getConfigurationSection("onlinetime").set("days",onlineTime[0]);
        localPlayerData.getConfigurationSection("onlinetime").set("hours",onlineTime[1]);
        localPlayerData.getConfigurationSection("onlinetime").set("minutes",onlineTime[2]);
        localPlayerData.getConfigurationSection("onlinetime").set("seconds",onlineTime[3]);
        localPlayerData.set("gamemode",data.getGamemode());
        localPlayerData.set("fly",data.isFly());
        main.getLocalPlayerData().saveFile();
    }

    //Load all playerdata of certain player
    @Override
    public CompletableFuture<DDGPlayer> loadData(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            //Bij eerste keer inloggen wordt de speler lokaal bijgehouden.
            if (!main.getLocalPlayerData().getConfig().contains(uuid)) {
                localPlayerData.createSection(uuid);
                localPlayerData.getConfigurationSection(uuid).set("gamemode", 0);
                localPlayerData.getConfigurationSection(uuid).set("onlinetime", 0);
                localPlayerData.getConfigurationSection(uuid).set("fly", false);
                localPlayerData.getConfigurationSection(uuid).createSection("onlinetime");
                localPlayerData.getConfigurationSection(uuid).getConfigurationSection("onlinetime").set("days", 0);
                localPlayerData.getConfigurationSection(uuid).getConfigurationSection("onlinetime").set("hours", 0);
                localPlayerData.getConfigurationSection(uuid).getConfigurationSection("onlinetime").set("minutes", 0);
                localPlayerData.getConfigurationSection(uuid).getConfigurationSection("onlinetime").set("seconds", 0);;
                localPlayerData.getConfigurationSection(uuid).createSection("location");
                localPlayerData.getConfigurationSection(uuid).getConfigurationSection("location").set("world",Bukkit.getWorld(UUID.fromString(uuid)));
                localPlayerData.getConfigurationSection(uuid).getConfigurationSection("location").set("x", 0);
                localPlayerData.getConfigurationSection(uuid).getConfigurationSection("location").set("y", 0);
                localPlayerData.getConfigurationSection(uuid).getConfigurationSection("location").set("z", 0);

                main.getServer().broadcastMessage("Er is een nieuwe speler voor het eerst ingelogd! Heet van harte welkom " + Bukkit.getPlayer(UUID.fromString(uuid)));
                main.getLocalPlayerData().saveFile();
                target = new DDGPlayer(uuid);
                //Bij inloggen wordt je data ingeladen
            } else {
                localPlayerData = main.getLocalPlayerData().getConfig().getConfigurationSection(uuid);
                ConfigurationSection locationSection = localPlayerData.getConfigurationSection("location");
                double x = locationSection.getInt("x");
                double y = locationSection.getInt("y");
                double z = locationSection.getInt("z");
                Location lastLocation = new Location(Bukkit.getWorld(uuid), x, y, z);
                ConfigurationSection onlineTimeSection = localPlayerData.getConfigurationSection("onlinetime");
                long days = onlineTimeSection.getInt("days");
                long hours = onlineTimeSection.getInt("hours");
                long minutes = onlineTimeSection.getInt("minutes");
                long seconds = onlineTimeSection.getInt("seconds");
                long[] onlinetime = new long[]{days,hours,minutes,seconds};
                target = new DDGPlayer(uuid,lastLocation,localPlayerData.getString("gamemode"),onlinetime,localPlayerData.getBoolean("fly"));
            }
            return target;
        });
    }
}
