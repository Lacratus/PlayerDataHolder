package be.lacratus.playerdataholder.data.handler;

import be.lacratus.playerdataholder.objects.DDGPlayer;
import be.lacratus.playerdataholder.Playerdataholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StoredDataHandler implements DataHandler {

    private Playerdataholder main;
    private DDGPlayer target;

    public StoredDataHandler(Playerdataholder main) {
        this.main = main;
    }

    @Override
    public void saveData(DDGPlayer data) throws SQLException {
        //Data wordt geupdate naar de database
        try(Connection connection = main.openConnection()){
            long[] onlineTime = data.getOnlineTime();
            long days = onlineTime[0];
            long hours = onlineTime[1];
            long minutes = onlineTime[2];
            long seconds = onlineTime[3];

            Location lastLocation = data.getLastLocation();
            connection.prepareStatement("UPDATE player_info SET gamemode =  '" + data.getGamemode() + "', fly = " + data.isFly() + ",days = " + days +",hours = " + hours + ", minutes = " + minutes
                    + ",seconds = " + seconds + ", world = " +lastLocation.getWorld().getName() + ", x = " + (int) lastLocation.getX() + ", y = " + (int) lastLocation.getY() + ", z = " +(int) lastLocation.getZ()
                    + " WHERE uuid = '" + data.getUUID() + "';").executeUpdate();
        }


    }

    @Override
    public CompletableFuture<DDGPlayer> loadData(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            //Eerste keer inloggen maakt een rij aan in database
            try(Connection connection = main.openConnection()){
                ResultSet rs = connection.prepareStatement("SELECT COUNT(uuid) FROM player_info WHERE uuid = '" + uuid + "';").executeQuery();
                rs.next();
                if(rs.getInt(1) == 0){
                    connection.prepareStatement("INSERT INTO player_info(uuid,gamemode,fly,days,hours,minutes,seconds,world,x,y,z) VALUES ('"
                            + uuid + "',DEFAULT,DEFAULT,DEFAULT,DEFAULT,DEFAULT,DEFAULT,'world',DEFAULT,DEFAULT,DEFAULT)").executeUpdate();
                    target = new DDGPlayer(uuid);
                    //Informatie vanuit database wordt ingeladen
                } else {
                    ResultSet rs2 = connection.prepareStatement("SELECT * FROM player_info WHERE uuid = '"+ uuid +"';").executeQuery();
                    rs2.next();
                    String gamemode = rs2.getString("gamemode");
                    boolean fly = rs2.getBoolean("fly");
                    int days = rs2.getInt("days");
                    int hours = rs2.getInt("hours");
                    int minutes = rs2.getInt("minutes");
                    int seconds = rs2.getInt("seconds");
                    long[] onlineTime = new long[]{days,hours,minutes,seconds};
                    String world = rs2.getString("world");
                    int x = rs2.getInt("x");
                    int y = rs2.getInt("y");
                    int z = rs2.getInt("z");
                    Location lastLocation = new Location(Bukkit.getWorld(world),x,y,z);

                    target = new DDGPlayer(uuid,lastLocation,gamemode,onlineTime,fly);
                }
                return target;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
