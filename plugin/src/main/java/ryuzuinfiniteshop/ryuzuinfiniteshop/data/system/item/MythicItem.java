package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item;

import lombok.Value;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.MythicInstanceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;

import java.util.LinkedHashMap;
import java.util.Map;

@Value
public class MythicItem implements ConfigurationSerializable {
    String id;
    int amount;

    public ItemStack convertItemStack() {
        if(!MythicInstanceProvider.isLoaded())
            return ItemUtil.clone(NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.REDSTONE_BLOCK, ChatColor.RED + LanguageKey.ERROR_INVALID_LOADED_MYTHICMOBS.getMessage(id)) , "Error", id) , amount);
        ItemStack item = MythicInstanceProvider.getInstance().getMythicItem(id, amount);
        if (item == null)
            return ItemUtil.clone(NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.REDSTONE_BLOCK, ChatColor.RED + LanguageKey.ERROR_MYTHICMOBS_INVALID_ID.getMessage(id)) , "Error", id) , amount);
        return item;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("amount", amount);
        return result;
    }

    public static MythicItem deserialize(Map<String, Object> map) {
        return new MythicItem(map.get("id").toString(), Integer.parseInt(map.get("amount").toString()));
    }
}
