package ryuzuinfiniteshop.ryuzuinfiniteshop.util.entity;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.JavaUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class EntityUtil {
    public static DyeColor getNextColor(DyeColor color) {
        int nextindex = Arrays.asList(DyeColor.values()).indexOf(color) + 1;
        return nextindex == DyeColor.values().length ?
                DyeColor.values()[0] :
                DyeColor.values()[nextindex];
    }

    public static ArmorStand spawnHologram(Location location, String text) {
        if(JavaUtil.isEmptyString(text)) return null;
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        NBTUtil.setNMSTag(hologram, "Shop", "Hologram");
        new EntityNBTBuilder(hologram).setInvisible(true);
        hologram.setCustomName(text);
        hologram.setCustomNameVisible(true);
        hologram.setInvulnerable(true);
        hologram.setGravity(false);
        hologram.setAI(false);
        hologram.setMarker(true);
        hologram.setVisible(false);
        hologram.setRemoveWhenFarAway(true);
        hologram.setSmall(true);
        return hologram;
    }

    public static org.bukkit.entity.Entity spawnEntity(Location location, EntityType entityType, CreatureSpawnEvent.SpawnReason spawnReason) {
        try {
            // エンティティのクラスを取得する
            Class<?> entityClass = Class.forName("net.minecraft.server." + getVersion() + ".Entity" + entityType.getName());

            // エンティティのコンストラクタを取得する
            Constructor<?> entityConstructor = entityClass.getConstructor(Class.forName("net.minecraft.server." + getVersion() + ".EntityTypes"), Class.forName("net.minecraft.server." + getVersion() + ".World"));

            // ワールドのインスタンスを取得する
            Object world = location.getWorld().getClass().getMethod("getHandle").invoke(location.getWorld());

            // エンティティのインスタンスを生成する
            Object entity = entityConstructor.newInstance(getEntityTypes(entityType), world);

            // エンティティの位置を設定する
            entity.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            // エンティティをスポーンさせる
            world.getClass().getMethod("addEntity", entityClass, Class.forName("net.minecraft.server." + getVersion() + ".EnumMobSpawnType")).invoke(world, entity, getSpawnType(spawnReason));

            // スポーンしたエンティティのBukkitのEntityオブジェクトを返す
            return Bukkit.getEntity(((Entity) entity).getUniqueId());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    private static Object getEntityTypes(EntityType entityType) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Class<?> entityTypesClass = Class.forName("net.minecraft.server." + getVersion() + ".EntityTypes");
        return entityTypesClass.getMethod("a", Class.forName("net.minecraft.server." + getVersion() + ".EntityTypes")).invoke(null, entityTypesClass.getField(entityType.name()).get(null));
    }

    private static Object getSpawnType(CreatureSpawnEvent.SpawnReason spawnReason) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> enumSpawnType = Class.forName("net.minecraft.server." + getVersion() + ".EnumMobSpawnType");
        return enumSpawnType.getField(spawnReason.name()).get(null);
    }
}
