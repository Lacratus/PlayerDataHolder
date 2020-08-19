package be.lacratus.playerdataholder.data.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileCreation {

    private String fileName;

    private File file;
    private FileConfiguration configFile;

    public FileCreation(String fileName) {
        this.fileName = fileName;
        create();
        getConfig().options().copyDefaults(true);
        saveFile();
    }

    public void create() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("PlayerdataHolder").getDataFolder(), fileName);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        configFile = YamlConfiguration.loadConfiguration(file);
    }

    public  FileConfiguration getConfig() {
        return configFile;
    }

    public  File getFile() {
        return file;
    }

    public  void saveFile() {
        try {
            configFile.save(file);
        } catch (IOException e) {
            System.out.println("Opslaan is niet gelukt");
        }

    }
}
