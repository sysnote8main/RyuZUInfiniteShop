package ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory;

import com.mojang.authlib.GameProfile;
import com.saicone.rtag.RtagEntity;
import com.saicone.rtag.RtagItem;
import lombok.NonNull;
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

public class NBTUtil {
    private static Method metaSetProfileMethod;
    private static Field metaProfileField;

    public static Entity setNMSTag(Entity ent, String key, String value) {
        if (RyuZUInfiniteShop.VERSION < 14) {
            return RtagEntity.edit(ent, tag -> {
                tag.set(value, "__extraData", RyuZUInfiniteShop.prefixPersistent + key);
                return tag.load();
            });
        } else {
            PersistentDataContainer container = ent.getPersistentDataContainer();
            container.set(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING, value);
            return ent;
        }
    }

    public static String getNMSStringTag(Entity ent, String key) {
        if (ent instanceof Player) return null;
        if (RyuZUInfiniteShop.VERSION < 14) {
            return RtagEntity.edit(ent, tag -> {
                return tag.getOptional("__extraData", RyuZUInfiniteShop.prefixPersistent + key).asString(null);
            });
        } else {
            String value = ent.getPersistentDataContainer().get(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING);
            return value == null || value.equalsIgnoreCase("") ? null : value;
        }
    }

    public static ItemStack setNMSTag(@NonNull ItemStack item, String key, String value) {
        if (RyuZUInfiniteShop.VERSION < 14) {
            return RtagItem.edit(item, tag -> {
                tag.set(value, "__extraData", RyuZUInfiniteShop.prefixPersistent + key);
                return tag.load();
            });
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
            return RtagItem.edit(item, tag -> {
                for (String key : map.keySet())
                    tag.set(map.get(key), "__extraData", RyuZUInfiniteShop.prefixPersistent + key);
                return tag.load();
            });
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
            return RtagItem.edit(item, tag -> {
                return tag.getOptional("__extraData", RyuZUInfiniteShop.prefixPersistent + key).asString(null);
            });
        } else {
            String value = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(RyuZUInfiniteShop.getPlugin(), key), PersistentDataType.STRING);
            return value == null || value.equalsIgnoreCase("") ? null : value;
        }
    }


    public static ItemStack itemWithBase64(ItemStack item, GameProfile profile) {
        if (ItemUtil.isAir(item)) return null;
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
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | NullPointerException |
                 InvocationTargetException ex) {
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
