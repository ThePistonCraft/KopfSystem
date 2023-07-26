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
import java.util.Date;

public class KopfCommand
        implements CommandExecutor {

    private final File file = new File("plugins//" + KopfSystem.getInstance().getDataFolder() + "/heads.yml");
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

        if (!player.hasPermission("kopfsystem.bypass")) {
            if (this.yamlConfiguration.getLong(player.getUniqueId().toString()) < System.currentTimeMillis()) {
                this.yamlConfiguration.set(player.getUniqueId().toString(), null);

                try {
                    this.yamlConfiguration.save(file);
                } catch (IOException ignored) { }
            }
            if (this.yamlConfiguration.get(player.getUniqueId().toString()) == null) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                player.getInventory().addItem(giveHead(target.getName()));

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

        player.getInventory().addItem(giveHead(target.getName()));
        player.sendMessage(KopfSystem.getInstance().getPrefix() + "Du hast den Kopf von §e" + target.getName() + " §7erhalten!");
        return false;
    }

    public void setHeadTime(Player player, int time) {
        this.yamlConfiguration.set(player.getUniqueId().toString(), System.currentTimeMillis() + time * 1000L);
        try {
            this.yamlConfiguration.save(file);
        } catch (IOException ignored) {  };
    }

    public ItemStack giveHead(String targetName) {
        ItemStack itemStack = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setDisplayName("§8» §7Kopf von §e" + targetName);
        skullMeta.setOwner(targetName);
        itemStack.setItemMeta(skullMeta);

        return itemStack;
    }
}
