package be.lacratus.playerdataholder;

import be.lacratus.playerdataholder.commands.FlyCommand;
import be.lacratus.playerdataholder.commands.GamemodeCommand;
import be.lacratus.playerdataholder.commands.PlayerDataCommand;
import be.lacratus.playerdataholder.commands.TimeCommand;
import be.lacratus.playerdataholder.data.files.FileCreation;
import be.lacratus.playerdataholder.data.handler.DataHandler;
import be.lacratus.playerdataholder.data.handler.LocalDataHandler;
import be.lacratus.playerdataholder.data.handler.StoredDataHandler;
import be.lacratus.playerdataholder.listener.PlayerDisconnectListener;
import be.lacratus.playerdataholder.listener.PlayerJoinListener;
import be.lacratus.playerdataholder.objects.DDGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class Playerdataholder extends JavaPlugin {
    //Yml
    private FileCreation LocalPlayerData;

    //Database
    private static Connection connection;
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    //Handlers
    private LocalDataHandler localDataHandler;
    private StoredDataHandler storedDataHandler;

    //Schedulers

    //Maps
    private Map<String, DDGPlayer> playerList = new HashMap<>();
    private Map<String, BukkitTask> cachePlayerInfo = new HashMap<>();

    public Playerdataholder() {
    }

    public void onEnable() {
        //Configs creation
        this.saveDefaultConfig();
        LocalPlayerData = new FileCreation("LocalPlayerData");
        //handlers
        localDataHandler = new LocalDataHandler(this);
        storedDataHandler = new StoredDataHandler(this);

        //Commands
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("time").setExecutor(new TimeCommand(this));
        getCommand("gamemode").setExecutor(new GamemodeCommand(this));
        getCommand("playerdata").setExecutor(new PlayerDataCommand(this));

        //Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDisconnectListener(this), this);

        //scheduler
            //Every 5 min save playerdata of online players
        if(getConfig().getBoolean("use-mysql")) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                System.out.println("updated");
                for (DDGPlayer speler : playerList.values()) {
                    speler.updatePlayer();
                    try(Connection connection = openConnection()) {
                        connection.prepareStatement("UPDATE player_info SET gamemode =  '" + speler.getGamemode() + "', fly = " + speler.isFly() + ",days = " + speler.getOnlineTime()[0] +",hours = " + speler.getOnlineTime()[1] + ", minutes = " + speler.getOnlineTime()[2]
                                + ",seconds = " + speler.getOnlineTime()[3] + ", world = " + speler.getLastLocation().getWorld().getName() + ", x = " + (int) speler.getLastLocation().getX() + ", y = " + (int) speler.getLastLocation().getY() + ", z = " +(int) speler.getLastLocation().getZ()
                                + " WHERE uuid = '" + speler.getUUID() + "';").executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }, 20*300, 20*300);
        }



        this.host = this.getConfig().getString("host");
        this.port = this.getConfig().getInt("port");
        this.database = this.getConfig().getString("database");
        this.username = this.getConfig().getString("username");
        this.password = this.getConfig().getString("password");


    }

    public Connection openConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
             connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
             return connection;
        }
        System.out.println("Something went wrong when opening the connection");
        return null;
    }

    public PreparedStatement prepareStatement(String query) {
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

        return ps;
    }


    //Getters and Setters
    public FileCreation getLocalPlayerData() {
        return LocalPlayerData;
    }

    public LocalDataHandler getLocalDataHandler() {
        return localDataHandler;
    }


    public StoredDataHandler getStoredDataHandler() {
        return storedDataHandler;
    }


    public Map<String, DDGPlayer> getPlayerList() {
        return playerList;
    }

    public Map<String, BukkitTask> getCachePlayerInfo() {
        return cachePlayerInfo;
    }

    public long[] OnlineTimeToLong(long timeInMillis){
        long days = TimeUnit.MILLISECONDS.toDays(timeInMillis);
        timeInMillis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
        timeInMillis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis);
        timeInMillis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis);
        return new long[]{days,hours,minutes,seconds} ;
    }
}
