package ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTListCompound;
import de.tr7zw.nbtinjector.NBTInjector;
import dev.dbassett.skullcreator.SkullCreator;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class NBTUtil {
    private static Method metaSetProfileMethod;
    private static Field metaProfileField;

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
        if (ent instanceof Player) return null;
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

    public static ItemStack setNMSTag(@NonNull ItemStack item, String key, String value) {
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
        if (ItemUtil.isAir(item) || !item.hasItemMeta()) return null;
        if (RyuZUInfiniteShop.VERSION < 14) {
            NBTItem nbtItem = new NBTItem(item);
            for (String key : map.keySet()) {
                nbtItem.setString(RyuZUInfiniteShop.prefixPersistent + key, map.get(key));
            }
            return nbtItem.getItem();
        } else {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            for (String key : map.keySet()) {
                container.set(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING, map.get(key));
            }
            item.setItemMeta(meta);
            return item;
        }
    }

    public static String getNMSStringTag(ItemStack item, String key) {
        if (ItemUtil.isAir(item) || !item.hasItemMeta()) return null;
        if (RyuZUInfiniteShop.VERSION < 14) {
            NBTItem nbtItem = new NBTItem(item);
            String value = nbtItem.getString(RyuZUInfiniteShop.prefixPersistent + key);
            return value == null || value.isEmpty() ? null : value;
        } else {
            String value = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING);
            return value == null || value.equalsIgnoreCase("") ? null : value;
        }
    }

    //    public static void getNMSSkullHead() {
//        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
//        NBTItem nbti = new NBTItem(head);
//        NBTCompound skull = nbti.addCompound("SkullOwner");
//        skull.setString("Id", "fce0323d-7f50-4317-9720-5f6b14cf78ea");
//        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
//        texture.setString("Value", "eyJ0aW1lc3RhbXAiOjE0OTMwNDkwMTcxNTIsInByb2ZpbGVJZCI6ImZjZTAzMjNkN2Y1MDQzMTc5NzIwNWY2YjE0Y2Y3OGVhIiwicHJvZmlsZU5hbWUiOiJ0cjd6dyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI3NDZlNWU5OGMwZWRmZTU1YTI3ZGRjNjUxMmJmNjllYzJiYmNlNmM3ZmNhNTQ5YmEzNjZkYThiNTRjZTRkYiJ9fX0=");
//        head = nbti.getItem();
//        Material.matchMaterial()
//    }


    public static ItemStack itemWithBase64(ItemStack item, GameProfile profile) {
        if(ItemUtil.isAir(item)) return null;
        if (!(item.getItemMeta() instanceof SkullMeta))
            return null;
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        setSkullData(meta, profile);
        item.setItemMeta(meta);

        return item;
    }

    public static void setSkullData(SkullMeta meta, GameProfile profile) {
        try {
            if (metaSetProfileMethod == null) {
                metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                metaSetProfileMethod.setAccessible(true);
            }
            metaSetProfileMethod.invoke(meta, profile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            try {
                if (metaProfileField == null) {
                    metaProfileField = meta.getClass().getDeclaredField("profile");
                    metaProfileField.setAccessible(true);
                }
                metaProfileField.set(meta, profile);

            } catch (NoSuchFieldException | IllegalAccessException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public static GameProfile getSkullData(SkullMeta meta) {
        try {
            if (metaSetProfileMethod == null) {
                metaSetProfileMethod = meta.getClass().getDeclaredMethod("getProfile", GameProfile.class);
                metaSetProfileMethod.setAccessible(true);
            }
            return ((GameProfile) metaSetProfileMethod.invoke(meta));
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | NullPointerException | InvocationTargetException ex) {
            try {
                if (metaProfileField == null) {
                    metaProfileField = meta.getClass().getDeclaredField("profile");
                    metaProfileField.setAccessible(true);
                }
                return ((GameProfile) metaProfileField.get(meta));
//                Optional<Property> property = ((GameProfile) metaProfileField.get(meta)).getProperties().get("textures").stream().findFirst();
//                return property.map(Property::getValue).orElse(null);
            } catch (NoSuchFieldException | IllegalAccessException ex2) {
                ex2.printStackTrace();
            } catch (NullPointerException ex2) {
                return null;
            }
        }
        return null;
    }
}
