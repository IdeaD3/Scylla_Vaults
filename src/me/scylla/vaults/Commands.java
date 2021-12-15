package me.scylla.vaults;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class Commands implements CommandExecutor {

    private Main main;

    public Commands(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("sv")) {
            if (main.utils.disabledWorlds().contains(player.getWorld().getName())) {
                player.sendMessage(main.utils.format("&f[&4!&f] &cVaults are disabled in this world!"));
                return true;
            }
            if (args.length == 0) {
                try {
                    player.openInventory(main.utils.loadPlayerVault(player.getUniqueId()));
                    main.utils.inView.put(player, player.getUniqueId());
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!main.utils.isEditor(player.getUniqueId())) {
                player.sendMessage(main.utils.format("&f[&4!&f] &cYou do not have permission to do this!"));
                return true;
            }
            OfflinePlayer target;
            if (args.length == 1) {
                try {
                    if (main.utils.getUUIDfromName(args[0]) == null) {
                        player.sendMessage(main.utils.format("&f[&4!&f] &cUnknown player &b" + args[0]));
                        return true;
                    }
                    target = Bukkit.getOfflinePlayer(main.utils.getUUIDfromName(args[0]));
                    player.openInventory(main.utils.loadPlayerVault(target.getUniqueId()));
                    main.utils.inView.put(player, target.getUniqueId());
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (args[0].equalsIgnoreCase("add")) {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    player.sendMessage(main.utils.format("&f[&4!&f] &cYou must be holding an item!"));
                    return true;
                }
                if (main.utils.getUUIDfromName(args[1]) == null) {
                    player.sendMessage(main.utils.format("&f[&4!&f] &cUnknown player &b" + args[1]));
                    return true;
                }
                ItemStack toGive = player.getInventory().getItemInMainHand();
                target = Bukkit.getOfflinePlayer(main.utils.getUUIDfromName(args[1]));
                try {
                    main.utils.addToPlayerVault(target.getUniqueId(), toGive);
                    player.sendMessage(main.utils.format("&f[&a+&f] &aSuccessfully &cadded item into &b&n" + target.getName() + "&b's &fVault"));
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                player.sendMessage(main.utils.format("&f[&4!&f] &cInvalid arguments. &b/sv user &cor &b/sv add user &cwhilst holding an item."));
                return true;
            }
        }
        return false;
    }
}
