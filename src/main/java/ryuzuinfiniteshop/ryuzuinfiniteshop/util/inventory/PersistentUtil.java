package ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtinjector.NBTInjector;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

public class PersistentUtil {
    public static void setNMSTag(Entity ent, String key, String value) {
        if (RyuZUInfiniteShop.VERSION < 16) {
            ent = NBTInjector.patchEntity(ent);
            NBTCompound comp = NBTInjector.getNbtData(ent);
            comp.setString(RyuZUInfiniteShop.prefixPersistent + key, value);
        } else {
            PersistentDataContainer entityTag = ent.getPersistentDataContainer();
            entityTag.set(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING, value);
        }
    }

    public static String getNMSStringTag(Entity ent, String key) {
        if (RyuZUInfiniteShop.VERSION < 16) {
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
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(RyuZUInfiniteShop.prefixPersistent + key, value);
        return nbtItem.getItem();
    }

    public static String getNMSStringTag(ItemStack item, String key) {
        if (ItemUtil.isAir(item)) return null;
        NBTItem nbtItem = new NBTItem(item);
        String value = nbtItem.getString(RyuZUInfiniteShop.prefixPersistent + key);

        return value == null || value.isEmpty() ? null : value;
    }
}
