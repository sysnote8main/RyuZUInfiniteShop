package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.gui.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.LocationUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.PersistentUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Shop {
    public enum ShopType {TwotoOne, FourtoFour}

    private final Entity npc;
    private final Location location;

    private final ShopType type;
    private final List<ShopTrade> trades;
    public List<ShopGui> pages = new ArrayList<>();

    public Shop(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        spawnNPC(config);
        this.location = LocationUtil.toLocationFromString(file.getName());
        this.npc = spawnNPC(config);
        this.type = ShopType.valueOf(config.getString("ShopType"));
        List<ConfigurationSection> trades = (List<ConfigurationSection>) config.getList("Trades");
        this.trades = trades.stream().map(ShopTrade::new).collect(Collectors.toList());
    }

    public List<ShopTrade> getTrades() {
        return trades;
    }

    public ShopGui getPage(int page) {
        if (page <= 0) return null;
        if (!pages.get(0).existPage(page)) return null;
        return pages.get(page);
    }

    public ShopType getShopType() {
        return type;
    }

    public void saveYaml(File file) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("EntityType", npc.getType().toString());
        config.set("ShopType", type.toString());
        config.set("Trades", getTradesConfig());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeNPC() {
        npc.remove();
    }

    public Entity spawnNPC(YamlConfiguration config) {
        EntityType entityType = EntityType.valueOf(config.getString("EntityType"));
        Entity entity = location.getWorld().spawnEntity(location, entityType);
        PersistentUtil.setNMSTag(entity , "Shop" , LocationUtil.toStringFromLocation(location));
        return location.getWorld().spawnEntity(location, entityType);
    }

    public List<ConfigurationSection> getTradesConfig() {
        return getTrades().stream().map(ShopTrade::getConfig).collect(Collectors.toList());
    }
}
