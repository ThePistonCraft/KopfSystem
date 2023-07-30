package de.maxcode.kopf.commands;

import de.maxcode.kopf.KopfSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class KopfCommand
        implements CommandExecutor {

    private final File file = new File("plugins//KopfSystem/heads.yml");
    private final YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        final Player player = (Player) sender;

        if (!player.hasPermission("kopfsystem.kopf")) {
            player.sendMessage(KopfSystem.getInstance().getPrefix() + "§cDu hast dazu keine Rechte.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(KopfSystem.getInstance().getPrefix() + "Bitte benutze: §e/kopf <name>");
            return true;
        }

        if (invFull(player)) {
            player.sendMessage(KopfSystem.getInstance().getPrefix() + "§cDu hast nicht genug Platz in deinem Inventar.");
            return true;
        }

        if (!player.hasPermission("kopfsystem.bypass")) {
            if (this.yamlConfiguration.getLong(player.getUniqueId().toString()) < System.currentTimeMillis()) {
                this.yamlConfiguration.set(player.getUniqueId().toString(), null);

                try {
                    this.yamlConfiguration.save(file);
                } catch (IOException ignored) { }
            }
            if (this.yamlConfiguration.get(player.getUniqueId().toString()) == null) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                getPlayerHead(player, target.getName(), "§8» §7Kopf von §e" + target.getName());

                int time = 0;
                time = Integer.parseInt(String.valueOf(KopfSystem.getInstance().getConfig().getInt("HeadWaitTime")));
                setHeadTime(player, time * 60);
                player.sendMessage(KopfSystem.getInstance().getPrefix() + "Du hast den Kopf von §e" + target.getName() + " §7erhalten!");
                return true;
            }

            Date date = new Date(this.yamlConfiguration.getLong(player.getUniqueId().toString()));
            String mm_dd_yyyy = new SimpleDateFormat("dd.MM.yyyy").format(date);
            String hour_min = new SimpleDateFormat("HH:mm").format(date);

            player.sendMessage(KopfSystem.getInstance().getPrefix() +
                    "Du kannst diesen Befehl erst wieder am §e" + mm_dd_yyyy + " §7um §e " + hour_min + " §7Uhr benutze.");

            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        getPlayerHead(player, target.getName(), "§8» §7Kopf von §e" + target.getName());
        player.sendMessage(KopfSystem.getInstance().getPrefix() + "Du hast den Kopf von §e" + target.getName() + " §7erhalten!");
        return false;
    }

    public void setHeadTime(Player player, int time) {
        this.yamlConfiguration.set(player.getUniqueId().toString(), System.currentTimeMillis() + time * 1000L);
        try {
            this.yamlConfiguration.save(file);
        } catch (IOException ignored) {  };
    }

    private static void getPlayerHead(Player player, String headName, String displayName) {
        ItemStack headItem = createPlayerHead(headName, displayName);

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != Material.PLAYER_HEAD) continue;

            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            if (skullMeta == null) continue;

            String itemHeadName = skullMeta.getOwner();
            if (itemHeadName != null && itemHeadName.equals(headName)) {
                int remainingSpace = item.getMaxStackSize() - item.getAmount();
                if (remainingSpace > 0) {
                    int amountToAdd = Math.min(remainingSpace, headItem.getAmount());
                    item.setAmount(item.getAmount() + amountToAdd);
                    headItem.setAmount(headItem.getAmount() - amountToAdd);
                    if (headItem.getAmount() == 0) return;
                }
            }
        }
        player.getInventory().addItem(headItem);
    }

    private static ItemStack createPlayerHead(String playerName, String displayName) {
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
            if (displayName != null && !displayName.isEmpty()) {
                skullMeta.setDisplayName(displayName);
            }
            headItem.setItemMeta(skullMeta);
        }
        return headItem;
    }

    private boolean invFull(Player player) {
        return !Arrays.asList(player.getInventory().getStorageContents()).contains(null);
    }
}
