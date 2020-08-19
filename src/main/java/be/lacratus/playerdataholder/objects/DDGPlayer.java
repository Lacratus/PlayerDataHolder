package be.lacratus.playerdataholder.objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DDGPlayer {

    private Player player;
    private String uuid;
    private Location lastLocation;
    private String gamemode;
    private long[] onlineTime;
    private boolean fly;

    public DDGPlayer(String uuid) {
        this.player = Bukkit.getPlayer(UUID.fromString(uuid));
        this.uuid = uuid;
        this.lastLocation = new Location(player.getWorld(),0,0,0);
        this.gamemode = "Survival";
        this.onlineTime = new long[]{0,0,0,0};
        this.fly = false;
    }

    public DDGPlayer(String uuid,Location lastLocation, String gamemode, long[] onlineTime, boolean fly){
        this.player = Bukkit.getPlayer(UUID.fromString(uuid));
        this.uuid = uuid;
        this.lastLocation = lastLocation;
        this.gamemode = gamemode;
        this.onlineTime = onlineTime;
        this.fly = fly;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String UUID) {
        this.uuid = UUID;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getGamemode() {
        return gamemode;
    }

    public void setGamemode(String gamemode) {
        this.gamemode = gamemode;
    }

    public long[] getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long[] onlineTime) {
        this.onlineTime = onlineTime;
    }

    public boolean isFly() {
        return fly;
    }

    public void setFly(boolean fly) {
        this.fly = fly;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String sendPlayerDataMessage(){
        return "Time: " + onlineTime[0] + " dagen, " + onlineTime[1] + " uren, " + onlineTime[2] + " minuten, " + onlineTime[3] + " seconden." + "\n"
                +"Gamemode: " + gamemode +"\n"
                +"Fly: " + fly + "\n"
                +"location: " + (int) lastLocation.getX() + ", " + (int) lastLocation.getY() + ", " + (int) lastLocation.getZ();
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

    public void updatePlayer(){
        lastLocation = player.getLocation();
        gamemode = player.getGameMode().toString();
        Long millis = (long) (player.getStatistic(Statistic.PLAY_ONE_TICK) * 0.05D * 1000.0D);
        onlineTime = OnlineTimeToLong(millis);
        fly = player.getAllowFlight();
    }
}
