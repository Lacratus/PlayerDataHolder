package be.lacratus.playerdataholder.listener;

import be.lacratus.playerdataholder.Playerdataholder;
import be.lacratus.playerdataholder.data.handler.DataHandler;
import be.lacratus.playerdataholder.data.handler.LocalDataHandler;
import be.lacratus.playerdataholder.data.handler.StoredDataHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private Playerdataholder main;
    private FileConfiguration localPlayerData;
    private LocalDataHandler localDataHandler;
    private StoredDataHandler storedDataHandler;
    private String uuid;

    public PlayerJoinListener(Playerdataholder main) {
        this.main = main;
        this.localPlayerData = main.getLocalPlayerData().getConfig();
        this.localDataHandler = main.getLocalDataHandler();
        this.storedDataHandler = main.getStoredDataHandler();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        uuid = player.getUniqueId().toString();
        if(!main.getConfig().getBoolean("use-mysql")) {
            localDataHandler.loadData(uuid).thenAccept(DDGspeler -> {
                main.getPlayerList().put(uuid,DDGspeler);
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        } else if(main.getConfig().getBoolean("use-mysql")){
            storedDataHandler.loadData(uuid).thenAccept(DDGspeler -> {
                main.getPlayerList().put(uuid,DDGspeler);
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        } else {
            player.sendMessage("ERROR: wrong notation in config at 'use-mysqsl'");
        }
    }
}
