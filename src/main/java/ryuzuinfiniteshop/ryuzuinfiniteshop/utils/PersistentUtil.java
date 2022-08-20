package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

import java.util.Objects;

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

    public static org.bukkit.inventory.ItemStack setNMSTag(ItemStack item , String key , String value){
        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbttag = nmsItem.getTag();

        if(nbttag == null) return null;
        nbttag.setString(key, value);

        nmsItem.setTag(nbttag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static String getNMSStringTag(ItemStack item , String key){
        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbttag = nmsItem.getTag();

        if(nbttag == null) return null;
        if(nbttag.getString(key) == null) return null;
        if(nbttag.getString(key).equals("")) return null;

        return nbttag.getString(key);
    }
}
