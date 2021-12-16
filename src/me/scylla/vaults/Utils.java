package me.scylla.vaults;

import me.scylla.vaults.configs.PlayerData;
import me.scylla.vaults.configs.VaultLogs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    public HashMap<Player, UUID> inView = new HashMap<>();

    static Utils instance = new Utils();
    private Main main;

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
        VaultLogs.saveConfig();
        VaultLogs.reloadConfig();
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

    public String updateTime(String time) {
        Random random = new Random();
        int rand = random.nextInt(100);
        return time + "_" + rand;
    }

    public void logEvent(UUID userUUID, ItemStack item, String eventType, UUID targetUUID) {
        Date current = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        Player player = Bukkit.getPlayer(userUUID);
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);

        String name = "None";
        String user = player.getName();
        String userRole = main.chat.getPrimaryGroup(player);
        String time = format.format(current);
        time = updateTime(time);
        List<String> lore;

        FileConfiguration cfg = VaultLogs.get();
        String path = "logs." + target.getUniqueId() + "." + time + ".";

        cfg.set(path + "item_type", item.getType().name());
        if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
            name = item.getItemMeta().getDisplayName();
            if (item.getItemMeta().hasLore()) {
                lore = item.getItemMeta().getLore();
                cfg.set(path + "item_lore", lore);
            }
        }

        cfg.set(path + "item_name", name);
        cfg.set(path + "type", eventType);
        cfg.set(path + "user", user);
        cfg.set(path + "user_UUID", userUUID.toString());
        cfg.set(path + "user_role", userRole);
        reloadConfig();

    }
}


