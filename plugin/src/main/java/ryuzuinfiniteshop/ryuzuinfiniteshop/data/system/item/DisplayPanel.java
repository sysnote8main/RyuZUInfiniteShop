package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item;

import lombok.Value;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

import java.util.HashMap;

@Value
public class DisplayPanel {
    private static final HashMap<ShopTrade.TradeResult, String> panels = new HashMap<ShopTrade.TradeResult, String>() {{
        put(ShopTrade.TradeResult.Success, ChatColor.GREEN + LanguageKey.ITEM_TRADE_SUCCESS.getMessage());
        put(ShopTrade.TradeResult.NotEnoughItems, ChatColor.RED + LanguageKey.ITEM_NOT_ENOUGH_ITEMS.getMessage());
        put(ShopTrade.TradeResult.Full, ChatColor.YELLOW + LanguageKey.ITEM_NOT_ENOUGH_SPACE.getMessage());
        put(ShopTrade.TradeResult.Limited, ChatColor.RED + LanguageKey.ITEM_TRADE_LIMITED.getMessage());
        put(ShopTrade.TradeResult.Normal, ChatColor.GREEN + LanguageKey.ITEM_TRADE_NORMAL.getMessage());
        put(ShopTrade.TradeResult.Error, ChatColor.RED + LanguageKey.ITEM_TRADE_INVALID.getMessage());
    }};

    ShopTrade.TradeResult result;
    ItemStack item;
    int data;

    public ConfigurationSection serialize() {
        ConfigurationSection result = new YamlConfiguration();
        result.set("Material", item.getType().name());
        result.set("CustomModelData", data);
        return result;
    }

    public ItemStack getItemStack() {
        ItemStack item = ItemUtil.withCustomModelData(ItemUtil.getNamedItem(this.item, panels.get(result)), data);
        if (result.equals(ShopTrade.TradeResult.Success)) {
            ItemUtil.withLore(item, ChatColor.GREEN + LanguageKey.ITEM_TRADE_ONCE.getMessage());
            ItemUtil.withLore(item, ChatColor.GREEN + LanguageKey.ITEM_TRADE_EIGHT.getMessage());
            ItemUtil.withLore(item, ChatColor.GREEN + LanguageKey.ITEM_TRADE_STACK.getMessage());
            ItemUtil.withLore(item, ChatColor.GREEN + LanguageKey.ITEM_TRADE_LIMIT.getMessage());
        }
//        else if(!result.equals(ShopTrade.TradeResult.Normal)) {
//            ItemUtil.withLore(item, ChatColor.GREEN + LanguageKey.ITEM_SEARCH_BY_VALUEORPRODUCT.getMessage());
//        }
        return item;
    }
}
