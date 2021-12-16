package me.scylla.vaults;

import me.scylla.vaults.configs.PlayerData;
import me.scylla.vaults.configs.VaultLogs;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    Utils utils = Utils.getInstance();
    public static Chat chat = null;

    @Override
    public void onEnable() {

        PlayerData.loadConfig();
        VaultLogs.loadConfig();
        saveDefaultConfig();
        setupChat();
        this.getCommand("sv").setExecutor(new Commands(this));
        Bukkit.getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        PlayerData.reloadConfig();
        reloadConfig();
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
}
