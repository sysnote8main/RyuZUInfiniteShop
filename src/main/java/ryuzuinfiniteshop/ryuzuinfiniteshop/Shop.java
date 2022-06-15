package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.gui.ShopGui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Shop {
    public Entity npc;
    public Location location;
    public List<ShopGui> pages;

    public void saveYaml(File file) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("location", location);
        config.set("entityType", npc.getType().toString());
        List<ConfigurationSection> trades = new ArrayList<>();
        for (ShopGui page: pages) {
            for (ShopTrade trade: page.trades) {
                trades.add(trade.getConfig());
            }
        }
        config.set("trades", trades);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Shop(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        location = config.getLocation("location");
        EntityType entityType = EntityType.valueOf(config.getString("entityType"));
        npc = location.getWorld().spawnEntity(location, entityType);
        List<ConfigurationSection> trades = (List<ConfigurationSection>) config.getList("trades");
        
    }
}
