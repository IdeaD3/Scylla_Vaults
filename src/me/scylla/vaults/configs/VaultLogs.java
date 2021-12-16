package me.scylla.vaults.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class VaultLogs {

    private static File confFile;
    private static FileConfiguration fileConfig;


    public static void loadConfig() {
        confFile = new File(Bukkit.getServer().getPluginManager().getPlugin("scyllavault").getDataFolder(), "vaultLogs.yml");
        if (!confFile.exists()) {
            try {
                confFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileConfig = YamlConfiguration.loadConfiguration(confFile);
    }

    public static FileConfiguration get() {
        return fileConfig;
    }

    public static void saveConfig() {
        try {
            fileConfig.save(confFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadConfig() {
        fileConfig = YamlConfiguration.loadConfiguration(confFile);
    }
}
