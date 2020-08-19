package be.lacratus.playerdataholder.commands;

import be.lacratus.playerdataholder.Playerdataholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    private Playerdataholder main;
    private ConfigurationSection localplayerdata;
    private String uuid;

    public FlyCommand(Playerdataholder main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        uuid = player.getUniqueId().toString();
        if(args.length == 0){
            localplayerdata = main.getLocalPlayerData().getConfig().getConfigurationSection(uuid);
            //Check if player has fly
            if(player.getAllowFlight()){
                player.setAllowFlight(false);
                player.sendMessage("Fly Disabled");
                localplayerdata.set("fly",false);
                return true;
                //Player has no fly
            } else {
                player.setAllowFlight(true);
                player.sendMessage("Fly Enabled");
                localplayerdata.set("fly",true);
            }
        }
        return false;
    }
}
