package ryuzuinfiniteshop.ryuzuinfiniteshop.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.io.File;
import java.io.IOException;

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
        runAutoSave();
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
        autoSaveTask = Bukkit.getScheduler().runTaskTimer(RyuZUInfiniteShop.getPlugin(), () -> {
            ShopUtil.reloadAllShopTradeInventory(() -> {
                TradeUtil.saveTradeLimits();
                ShopUtil.saveAllShops();
                ShopUtil.removeAllNPC();
                DisplayPanelConfig.load();
                ShopUtil.loadAllShops();
                TradeUtil.loadTradeLimits();
            });
        },0, 20L * 60 * autoSaveInterval);
    }
}
