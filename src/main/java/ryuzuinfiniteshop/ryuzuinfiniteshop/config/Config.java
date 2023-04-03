package ryuzuinfiniteshop.ryuzuinfiniteshop.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Config {
    public static int autoSaveInterval = 20;
    private static BukkitTask autoSaveTask;

    public static void load() {
        File file = FileUtil.initializeFile("config.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        autoSaveInterval = yaml.getInt("AutoSaveInterval", 20);
    }

    public static void save() {
        File file = FileUtil.initializeFile("config.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("AutoSaveInterval", autoSaveInterval);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runAutoSave() {
        if(autoSaveInterval <= 0) return;
        autoSaveTask = Bukkit.getScheduler().runTaskTimer(RyuZUInfiniteShop.getPlugin(), FileUtil::reloadAllWithMessage, 20L * 60 * autoSaveInterval, 20L * 60 * autoSaveInterval);
    }
}
