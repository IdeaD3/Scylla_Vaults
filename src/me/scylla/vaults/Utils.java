package me.scylla.vaults;

import me.scylla.vaults.configs.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Utils {

    public HashMap<Player, UUID> inView = new HashMap<>();

    static Utils instance = new Utils();

    public static Utils getInstance() {
        return instance;
    }

    public Inventory loadPlayerVault(UUID uuid) throws IOException {
        String data = PlayerData.get().getString("data." + uuid + ".inventory");
        if (data == null) {
            Inventory inv = Bukkit.createInventory(null, 27, Bukkit.getOfflinePlayer(uuid).getName() + "'s vault");
            return inv;
        }
        Inventory inv = Bukkit.createInventory(null, 27, Bukkit.getOfflinePlayer(uuid).getName() + "'s vault");
        Inventory items = Save.inventoryFromBase64(data);
        inv.setContents(items.getContents());

        return inv;
    }

    public UUID getUUIDfromName(String name) {
        for (String s : PlayerData.get().getConfigurationSection("data").getKeys(false)) {
            if (Objects.requireNonNull(PlayerData.get().getString("data." + UUID.fromString(s) + ".name")).equalsIgnoreCase(name.toLowerCase())) {
                return UUID.fromString(s);
            }
        }
        return null;
    }

    public void savePlayerVault(UUID uuid, Inventory inventory) {
        Save.savePlayerInventory(uuid, inventory);
    }

    public void addToPlayerVault(UUID uuid, ItemStack item) throws IOException {
        Inventory inv = loadPlayerVault(uuid);
        inv.addItem(item);
        savePlayerVault(uuid, inv);
    }

    public void reloadConfig() {
        PlayerData.saveConfig();
        PlayerData.reloadConfig();
    }

    public boolean isEditor(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player.hasPermission("sv.admin") || player.isOp()) {
            return true;
        }
        return false;
    }

    public List<String> disabledWorlds() {
        return Main.getPlugin(Main.class).getConfig().getStringList("disabled-worlds");

    }

    public String format(String text) {
        return text == null ? null : ChatColor.translateAlternateColorCodes('&', text);
    }
}


