package de.maxcode.kopf;

import de.maxcode.kopf.commands.KopfCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public class KopfSystem
        extends JavaPlugin {

    @Getter
    public static KopfSystem instance;
    public String prefix = "§8[§aKopfSystem§8] §7";

    @Override
    public void onEnable() {
        instance = this;

        YamlConfigurationLoader.loadConfiguration(this, "config.yml");
        getCommand("kopf").setExecutor(new KopfCommand());

        Bukkit.getConsoleSender().sendMessage("§aKopfSystem by §eeinCode_ §awas sucessfully loaded...");
        Bukkit.getConsoleSender().sendMessage("§aDiscord: §bhttps://discord.max-code.de/");
    }
}
