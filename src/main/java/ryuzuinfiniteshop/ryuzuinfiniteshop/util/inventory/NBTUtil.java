package ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtinjector.NBTInjector;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

import java.util.HashMap;

public class NBTUtil {
    public static void setNMSTag(Entity ent, String key, String value) {
        if (RyuZUInfiniteShop.VERSION < 14) {
            ent = NBTInjector.patchEntity(ent);
            NBTCompound comp = NBTInjector.getNbtData(ent);
            comp.setString(RyuZUInfiniteShop.prefixPersistent + key, value);
        } else {
            PersistentDataContainer container = ent.getPersistentDataContainer();
            container.set(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING, value);
        }
    }

    public static String getNMSStringTag(Entity ent, String key) {
        if(ent instanceof Player) return null;
        if (RyuZUInfiniteShop.VERSION < 14) {
            ent = NBTInjector.patchEntity(ent);
            NBTCompound comp = NBTInjector.getNbtData(ent);
            String value = comp.getString(RyuZUInfiniteShop.prefixPersistent + key);
            return value == null || value.isEmpty() ? null : value;
        } else {
            String value = ent.getPersistentDataContainer().get(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING);
            return value == null || value.equalsIgnoreCase("") ? null : value;
        }
    }

    public static ItemStack setNMSTag(ItemStack item, String key, String value) {
        if(ItemUtil.isAir(item) || !item.hasItemMeta()) return null;
        if (RyuZUInfiniteShop.VERSION < 14) {
            NBTItem nbtItem = new NBTItem(item);
            nbtItem.setString(RyuZUInfiniteShop.prefixPersistent + key, value);
            return nbtItem.getItem();
        } else {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING, value);
            item.setItemMeta(meta);
            return item;
        }
    }

    public static ItemStack setNMSTag(ItemStack item, HashMap<String, String> map) {
        if(ItemUtil.isAir(item) || !item.hasItemMeta()) return null;
        if (RyuZUInfiniteShop.VERSION < 14) {
            NBTItem nbtItem = new NBTItem(item);
            for(String key : map.keySet()) {
                nbtItem.setString(RyuZUInfiniteShop.prefixPersistent + key, map.get(key));
            }
            return nbtItem.getItem();
        } else {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            for(String key : map.keySet()) {
                container.set(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING, map.get(key));
            }
            item.setItemMeta(meta);
            return item;
        }
    }

    public static String getNMSStringTag(ItemStack item, String key) {
        if(ItemUtil.isAir(item) || !item.hasItemMeta()) return null;
        if (RyuZUInfiniteShop.VERSION < 14) {
            NBTItem nbtItem = new NBTItem(item);
            String value = nbtItem.getString(RyuZUInfiniteShop.prefixPersistent + key);
            return value == null || value.isEmpty() ? null : value;
        } else {
            String value = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING);
            return value == null || value.equalsIgnoreCase("") ? null : value;
        }
    }
}
