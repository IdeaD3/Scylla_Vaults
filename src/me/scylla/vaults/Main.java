package me.scylla.vaults;

import me.scylla.vaults.configs.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    Utils utils = Utils.getInstance();

    @Override
    public void onEnable() {

        PlayerData.loadConfig();
        saveDefaultConfig();
        this.getCommand("sv").setExecutor(new Commands(this));
        Bukkit.getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        PlayerData.reloadConfig();
        reloadConfig();
    }
}
