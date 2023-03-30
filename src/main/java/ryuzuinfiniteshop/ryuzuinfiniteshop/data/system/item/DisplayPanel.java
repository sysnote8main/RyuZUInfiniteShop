package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item;

import lombok.Value;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

import java.util.HashMap;

@Value
public class DisplayPanel {
    private static final HashMap<ShopTrade.TradeResult, String> panels = new HashMap<ShopTrade.TradeResult, String>() {{
        put(ShopTrade.TradeResult.Success, ChatColor.GREEN + "購入可能");
        put(ShopTrade.TradeResult.NotAfford, ChatColor.RED + "アイテムが足りません");
        put(ShopTrade.TradeResult.Full, ChatColor.YELLOW + "インベントリに十分な空きがありません");
        put(ShopTrade.TradeResult.Limited, ChatColor.RED + "取引上限です");
    }};

    ShopTrade.TradeResult result;
    Material material;
    int data;
    public ConfigurationSection serialize() {
        ConfigurationSection result = new YamlConfiguration();
        result.set("Material", material.name());
        result.set("CustomModelData", data);
        return result;
    }

    public ItemStack getItemStack(int limit, int count) {
        ItemStack item = ItemUtil.withCustomModelData(ItemUtil.getNamedItem(material, panels.get(result)), data);
        if(result.equals(ShopTrade.TradeResult.Success) && limit != 0) {
            ItemUtil.withLore(item, ChatColor.YELLOW + "残り取引回数: " + (limit - count) + "回");
        }
        return item;
    }

    public ItemStack getItemStack() {
        return getItemStack(0, 0);
    }
}
