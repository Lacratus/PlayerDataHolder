package be.lacratus.playerdataholder.commands;

import be.lacratus.playerdataholder.Playerdataholder;
import be.lacratus.playerdataholder.data.files.FileCreation;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    private Playerdataholder main;
    private ConfigurationSection localPlayerData;
    private String uuid;

    public GamemodeCommand(Playerdataholder main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if(args.length == 1) {
            uuid = player.getUniqueId().toString();
            localPlayerData = main.getLocalPlayerData().getConfig().getConfigurationSection(uuid);
            //Gamemode set to Survival
            if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("Survival")) {
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage("U zit nu in gamemode SURVIVAL");
                localPlayerData.set("gamemode","survival");
                //Gamemode set to Creative
            } else if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("Creative")) {
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage("U zit nu in gamemode CREATIVE");
                localPlayerData.set("gamemode","creative");
                //Gamemode set to Adventure
            } else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("Adventure")) {
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage("U zit nu in gamemode ADVENTURE");
                localPlayerData.set("gamemode","adventure");
                //Gamemode set to Spectator
            } else if (args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("Spectator")) {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage("U zit nu in gamemode SPECTATOR");
                localPlayerData.set("gamemode","spectator");
            } else {
                player.sendMessage("This gamemode doesn't exist");
            }
            return true;
        }
        player.sendMessage("Syntax: /Gamemode [Survival,Creative,Adventure,Spectator]");
        return false;
    }
}
