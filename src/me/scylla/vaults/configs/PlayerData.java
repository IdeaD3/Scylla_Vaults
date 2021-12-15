package me.scylla.vaults.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerData {

    private static File confFile;
    private static FileConfiguration fileConfig;


    public static void loadConfig() {
        PlayerData.confFile = new File(Bukkit.getServer().getPluginManager().getPlugin("scyllavault").getDataFolder(), "playerData.yml");
        if (!PlayerData.confFile.exists()) {
            try {
                PlayerData.confFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PlayerData.fileConfig = YamlConfiguration.loadConfiguration(PlayerData.confFile);
    }

    public static FileConfiguration get() {
        return PlayerData.fileConfig;
    }

    public static void saveConfig() {
        try {
            PlayerData.fileConfig.save(PlayerData.confFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadConfig() {
        PlayerData.fileConfig = YamlConfiguration.loadConfiguration(PlayerData.confFile);
    }
}

