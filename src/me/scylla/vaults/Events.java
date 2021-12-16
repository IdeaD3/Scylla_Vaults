package me.scylla.vaults;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import me.scylla.vaults.configs.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Events implements Listener {
    private Main main;

    public Events(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onBossDeath(MythicMobDeathEvent e) {
        if (e.getKiller() instanceof Player && e.getDrops() != null) {
            final List<ItemStack> drops = e.getDrops();
            drops.forEach(drop -> {
                try {
                    main.utils.addToPlayerVault(e.getKiller().getUniqueId(), drop);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            e.getDrops().clear();
            e.getKiller().sendMessage(main.utils.format("&f[&a+&f] &cLore item has been added to your /sv!"));
        }
    }

    @EventHandler
    public void onVaultClick(InventoryClickEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();
        if (main.utils.inView.containsKey(player)) {
            if (top.getSize() == 27 && e.getRawSlot() >= 0 && e.getRawSlot() <= 26) {
                ItemStack i;
                if (e.getCursor() == null || e.getCursor().getType() == Material.AIR && e.getCurrentItem() != null) {
                    i = e.getCurrentItem();
                    main.utils.logEvent(player.getUniqueId(), i, "Remove", main.utils.inView.get(player));
                } else {
                    if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR && e.getCursor() != null) {
                        if (main.utils.isEditor(player.getUniqueId())) {
                            i = e.getCursor();
                            main.utils.logEvent(player.getUniqueId(), i, "insert", main.utils.inView.get(player));
                        } else {
                            e.setCancelled(true);
                        }
                    }
                    return;
                }

                if (e.getClick() == ClickType.NUMBER_KEY) {
                    e.setCancelled(true);
                }
                if (e.getClickedInventory() == player.getInventory()) {
                    if (e.getCursor() != null && e.getCursor().getType() == Material.AIR) {
                        e.setCancelled(true);
                    }
                    return;
                }
                UUID uuid = main.utils.inView.get(player);
                if (e.getView().getTitle().equalsIgnoreCase(Bukkit.getOfflinePlayer(uuid).getName() + "'s vault")) {
                    if (e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                        e.setCancelled(true);
                    }
                }
            } else {
                if (!main.utils.isEditor(player.getUniqueId())) {
                    if (!(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR && e.getCursor() != null)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInvDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();

        if (main.utils.isEditor(player.getUniqueId())) {
            return;
        }

        if (main.utils.inView.containsKey(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (PlayerData.get().getConfigurationSection("data") != null) {
            if (PlayerData.get().getConfigurationSection("data").getKeys(false).contains(e.getPlayer().getUniqueId().toString())) {
                return;
            }
            return;
        }
        PlayerData.get().set("data." + e.getPlayer().getUniqueId() + ".name", e.getPlayer().getName());
        main.utils.reloadConfig();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (main.utils.inView.containsKey(player)) {
            if (main.utils.inView.get(player) == player.getUniqueId()) {
                Save.savePlayerInventory(e.getPlayer().getUniqueId(), e.getInventory());
                main.utils.inView.remove(player);
            } else {
                main.utils.savePlayerVault(main.utils.inView.get(player), e.getInventory());
            }
        }
    }
}
