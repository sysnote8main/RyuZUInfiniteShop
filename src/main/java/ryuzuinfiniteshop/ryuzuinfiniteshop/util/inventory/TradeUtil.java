package ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TradeUtil {
    public static boolean isAvailableTrade(Inventory inv, int slot, Shop.ShopType type) {
        switch (type) {
            case TwotoOne:
                return ItemUtil.getItemSet(inv, slot, 2).length != 0 && inv.getItem(slot + 3) != null;
            case FourtoFour:
                return ItemUtil.getItemSet(inv, slot, 4).length != 0 && ItemUtil.getItemSet(inv, slot + 5, 4).length != 0;
            case SixtoTwo:
                return ItemUtil.getItemSet(inv, slot, 6).length != 0 && ItemUtil.getItemSet(inv, slot + 7, 2).length != 0;
        }
        return false;
    }

    public static ShopTrade getTrade(Inventory inv, int slot, Shop.ShopType type) {
        if (!isAvailableTrade(inv, slot, type)) return null;
        switch (type) {
            case TwotoOne:
                return new ShopTrade(new ItemStack[]{inv.getItem(slot + 3)}, ItemUtil.getItemSet(inv, slot, 2));
            case FourtoFour:
                return new ShopTrade(ItemUtil.getItemSet(inv, slot + 5, 4), ItemUtil.getItemSet(inv, slot, 4));
            case SixtoTwo:
                return new ShopTrade(ItemUtil.getItemSet(inv, slot + 7, 2), ItemUtil.getItemSet(inv, slot, 6));
        }
        return null;
    }

    public static void saveTradeLimits() {
        File file = FileUtil.initializeFile("limits.yml");
        YamlConfiguration config = new YamlConfiguration();
        ShopTrade.tradeUUID.values().forEach(tradeID -> {
            if (ShopTrade.tradeLimits.getOrDefault(tradeID, 0) == 0) return;
            config.set(tradeID.toString() + ".limit", ShopTrade.tradeLimits.get(tradeID));
            ShopTrade.tradeCounts.rowKeySet().forEach(playerID -> {
                config.set(tradeID + ".counts." + playerID.toString(), ShopTrade.tradeCounts.get(playerID, tradeID));
            });
        });

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadTradeLimits() {
        File file = FileUtil.initializeFile("limits.yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String tradeID : config.getKeys(false)) {
            ShopTrade.tradeLimits.put(UUID.fromString(tradeID), config.getInt(tradeID + ".limit"));
            if (config.contains(tradeID + ".counts"))
                for (String playerID : config.getConfigurationSection(tradeID + ".counts").getKeys(false)) {
                    ShopTrade.tradeCounts.put(UUID.fromString(playerID), UUID.fromString(tradeID), config.getInt(tradeID + ".counts." + playerID));
                }
        }
    }

    public static ShopTrade getFirstTrade(ItemStack item) {
        String tag = NBTUtil.getNMSStringTag(item, "TradesSize");
        if (tag == null) return null;
        Shop.ShopType shoptype = Shop.ShopType.valueOf(NBTUtil.getNMSStringTag(item, "ShopType"));
        return new ShopTrade(ItemUtil.toItemStackArrayFromString(NBTUtil.getNMSStringTag(item, "Give" + 0)), ItemUtil.toItemStackArrayFromString(NBTUtil.getNMSStringTag(item, "Take" + 0)));
    }

    public static LinkedHashMap<ShopTrade, Shop> getTradesFromGive(ItemStack item) {
        LinkedHashMap<ShopTrade, Shop> trades = new LinkedHashMap<>();
        ShopUtil.getShops().values().forEach(shop -> {
            if (shop.isSearchable())
                shop.getTrades().forEach(trade -> Arrays.stream(trade.getGiveItems()).forEach(take -> {
                                             if (take.isSimilar(item))
                                                 trades.put(trade, shop);
                                         })
                );
        });
        return trades;
    }

    public static LinkedHashMap<ShopTrade, Shop> getTradesFromTake(ItemStack item) {
        LinkedHashMap<ShopTrade, Shop> trades = new LinkedHashMap<>();
        ShopUtil.getShops().values().forEach(shop -> {
            if (shop.isSearchable())
                shop.getTrades().forEach(trade -> Arrays.stream(trade.getTakeItems()).forEach(take -> {
                                             if (take.isSimilar(item))
                                                 trades.put(trade, shop);
                                         })
                );
        });
        return trades;
    }
}
