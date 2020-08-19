package be.lacratus.playerdataholder.listener;

import be.lacratus.playerdataholder.Playerdataholder;
import be.lacratus.playerdataholder.data.handler.LocalDataHandler;
import be.lacratus.playerdataholder.data.handler.StoredDataHandler;
import be.lacratus.playerdataholder.objects.DDGPlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class PlayerDisconnectListener implements Listener {

    private Playerdataholder main;
    private FileConfiguration localPlayerData;
    private LocalDataHandler localDataHandler;
    private StoredDataHandler storedDataHandler;
    private Player player;
    private DDGPlayer target;

    public PlayerDisconnectListener(Playerdataholder main) {
        this.main = main;
        this.localDataHandler = main.getLocalDataHandler();
        this.storedDataHandler = main.getStoredDataHandler();
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) throws SQLException {
        player = e.getPlayer();
        target = main.getPlayerList().get(player.getUniqueId().toString());
        target.setFly(player.getAllowFlight());
        target.setGamemode(player.getGameMode().toString());
        target.setLastLocation(player.getLocation());
        target.setOnlineTime(main.OnlineTimeToLong((int) (player.getStatistic(Statistic.PLAY_ONE_TICK) * 0.05D * 1000.0D)));
        target.setUUID(player.getUniqueId().toString());
        if (!main.getConfig().getBoolean("use-mysql")) {
            localDataHandler.saveData(target);
        } else if (main.getConfig().getBoolean("use-mysql")) {
            storedDataHandler.saveData(target);
        } else {
            player.sendMessage("ERROR: wrong notation in config at 'use-mysql'");
        }
        main.getPlayerList().remove(target.getUUID());

    }
}
