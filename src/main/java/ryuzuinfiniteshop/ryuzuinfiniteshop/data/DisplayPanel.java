package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import lombok.Value;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ItemUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Value
public class DisplayPanel {
    private static final HashMap<ShopTrade.TradeResult, String> panels = new HashMap<ShopTrade.TradeResult, String>() {{
        put(ShopTrade.TradeResult.Success, ChatColor.GREEN + "購入可能");
        put(ShopTrade.TradeResult.NotAfford, ChatColor.RED + "アイテムが足りません");
        put(ShopTrade.TradeResult.Full, ChatColor.YELLOW + "インベントリに十分な空きがありません");
        put(ShopTrade.TradeResult.Limited, ChatColor.RED + "取引上限です");
    }};
    Material material;
    int data;
    public ConfigurationSection serialize() {
        ConfigurationSection result = new YamlConfiguration();
        result.set("Material", material.name());
        result.set("CustomModelData", data);
        return result;
    }

    public ItemStack getItemStack(ShopTrade.TradeResult result, int limit, int count) {
        ItemStack item = ItemUtil.withCustomModelData(ItemUtil.getNamedItem(material, panels.get(result)), data);
        if(result.equals(ShopTrade.TradeResult.Success) && limit != 0) item = ItemUtil.withLore(item, ChatColor.YELLOW + "残り取引回数: " + (limit - count) + "回");
        return item;
    }

    public ItemStack getItemStack(ShopTrade.TradeResult result) {
        return getItemStack(result, 0, 0);
    }
}
