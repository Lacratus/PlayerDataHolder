package be.lacratus.playerdataholder.commands;

import be.lacratus.playerdataholder.Playerdataholder;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCommand implements CommandExecutor {

    private Playerdataholder main;

    public TimeCommand(Playerdataholder main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        //Bekijken van je eigen time
        if (args.length == 0) {
            long millis = (long) (player.getStatistic(Statistic.PLAY_ONE_TICK) * 0.05D * 1000.0D);
            long[] onlineTime = main.OnlineTimeToLong(millis);
            player.sendMessage("Totaal: " + onlineTime[0] + " dagen, " + onlineTime[1] + " uren, " + onlineTime[2] + " minuten, " + onlineTime[3] + " seconden.");
            return true;
        }
        player.sendMessage("No Arguments allowed");
        return false;
    }
}



