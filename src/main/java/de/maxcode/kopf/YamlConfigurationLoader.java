package de.maxcode.kopf;

import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class YamlConfigurationLoader {
    public static synchronized YamlConfiguration loadConfiguration(@NonNull JavaPlugin plugin, String fileName) {
        if(plugin == null)
            throw new NullPointerException("plugin is marked non-null but is null");
        if(!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();
        File config = new File(plugin.getDataFolder(), fileName);
        if(!config.exists())
            try {
                plugin.saveResource(fileName, false);
            }catch (IllegalArgumentException exception) {
                return null;
            }
        final YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.load(new InputStreamReader(new FileInputStream(config), StandardCharsets.UTF_8));
        } catch (InvalidConfigurationException |java.io.IOException var5) {
            var5.printStackTrace();
        }
        if(yamlConfiguration.getDefaults() != null)
            yamlConfiguration.getValues(true).forEach((key, value) -> yamlConfiguration.getDefaults().set(key, null));
        return yamlConfiguration;
    }
}
