package be.lacratus.playerdataholder.data.handler;

import be.lacratus.playerdataholder.objects.DDGPlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface DataHandler {

    void saveData(DDGPlayer data) throws SQLException;

    CompletableFuture<DDGPlayer> loadData(String uuid);

}
