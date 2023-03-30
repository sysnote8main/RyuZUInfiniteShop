package ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory;

import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

public class PersistentUtil {
    public static void setNMSTag(Entity ent , String key , String value) {
        NBTEntity entityTag = new NBTEntity(ent);
        entityTag.setString(RyuZUInfiniteShop.prefixPersistent + key , value);
    }

    public static String getNMSStringTag(Entity ent , String key){
        NBTEntity entityTag = new NBTEntity(ent);
        String value = entityTag.getString(RyuZUInfiniteShop.prefixPersistent + key);
        return value == null || value.isEmpty() ? null : value;
    }

    public static void removeNMSStringTag(Entity ent , String key){
        ent.getPersistentDataContainer().remove(new NamespacedKey(RyuZUInfiniteShop.getPlugin() , key));
    }

    public static ItemStack setNMSTag(ItemStack item , String key , String value){
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(RyuZUInfiniteShop.prefixPersistent + key , value);
        return nbtItem.getItem();
    }

    public static String getNMSStringTag(ItemStack item , String key){
        if(item == null) return null;
        NBTItem nbtItem = new NBTItem(item);
        String value = nbtItem.getString(RyuZUInfiniteShop.prefixPersistent + key);

        return value == null || value.isEmpty() ? null : value;
    }
}
