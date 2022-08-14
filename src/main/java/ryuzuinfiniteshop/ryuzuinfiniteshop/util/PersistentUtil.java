package ryuzuinfiniteshop.ryuzuinfiniteshop.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

public class PersistentUtil {
    public static void setNMSTag(Entity ent , String key , String value) {
        PersistentDataContainer entityTag = ent.getPersistentDataContainer();
        entityTag.set(new NamespacedKey(RyuZUInfiniteShop.getPlugin() , key), PersistentDataType.STRING , value);
    }

    public static String getNMSStringTag(Entity ent , String key){
        String value = ent.getPersistentDataContainer().get(new NamespacedKey(RyuZUInfiniteShop.getPlugin() , key), PersistentDataType.STRING);
        return value == null || value.equalsIgnoreCase("") ? null : value;
    }

    public static void removeNMSStringTag(Entity ent , String key){
        ent.getPersistentDataContainer().remove(new NamespacedKey(RyuZUInfiniteShop.getPlugin() , key));
    }

}
