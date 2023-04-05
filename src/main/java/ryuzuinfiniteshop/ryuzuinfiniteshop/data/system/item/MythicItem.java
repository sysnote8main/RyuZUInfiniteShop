package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item;

import lombok.Value;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
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
            return ItemUtil.clone(NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.REDSTONE_BLOCK, "§4§l[ERROR] MythicMobsがロードされていません。" + ChatColor.YELLOW + "ID: " + id) , "Error", id) , amount);
        ItemStack item = MythicInstanceProvider.getInstance().getMythicMobsInstance().getItemManager().getItemStack(id);
        if (item == null)
            return ItemUtil.clone(NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.REDSTONE_BLOCK, "§4§l[ERROR] 存在しないMMIDです。" + ChatColor.YELLOW + "ID: " + id) , "Error", id) , amount);
        return MythicInstanceProvider.getInstance().getMythicItem(id, amount);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("amount", amount);
        return result;
    }

    public static MythicItem deserialize(Map<String, Object> map) {
        return new MythicItem(map.get("id").toString(), Integer.parseInt(map.get("amount").toString()));
    }
}
