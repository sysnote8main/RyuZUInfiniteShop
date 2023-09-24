package ryuzuinfiniteshop.ryuzuinfiniteshop.util.entity;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtinjector.NBTInjector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

import java.util.Arrays;
import java.util.HashMap;

public class EntityNBTBuilder {
    private final Entity entity;
    private final NBTCompound compound;
    private final static HashMap<String, Integer> variantMap = new HashMap<String, Integer>() {{
        Arrays.stream(Horse.Style.values()).forEach(style -> put(style.name(), style.ordinal() * 256));
        Arrays.stream(Horse.Color.values()).forEach(color -> put(color.name(), color.ordinal()));
    }};

    public EntityNBTBuilder(Entity entity) {
        if (RyuZUInfiniteShop.VERSION < 14) {
            this.entity = entity;
            this.compound = new NBTEntity(entity);
        } else {
            this.entity = entity;
            this.compound = null;
        }
    }

    public void setInvisible(boolean invisible) {
//        compound.setByte("Invisible", invisible ? (byte) 1 : (byte) 0);
        if (RyuZUInfiniteShop.VERSION <= 15) {
            if (invisible)
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, false, false));
            else
                ((LivingEntity) entity).removePotionEffect(PotionEffectType.INVISIBILITY);
        } else
            ((LivingEntity) entity).setInvisible(invisible);
    }

    public void setInvulnerable(boolean invulnerable) {
        compound.setByte("Invulnerable", invulnerable ? (byte) 1 : (byte) 0);
    }

    public void setNoGravity(boolean noGravity) {
        compound.setByte("NoGravity", noGravity ? (byte) 1 : (byte) 0);
    }

    public void setSilent(boolean silent) {
        compound.setByte("Silent", silent ? (byte) 1 : (byte) 0);
    }

    public void setIsBaby(boolean isBaby) {
        compound.setByte("IsBaby", isBaby ? (byte) 1 : (byte) 0);
        compound.setByte("Age", isBaby ? (byte) -32768 : (byte) 0);
    }

    public void setColor(int color) {
        compound.setInteger("Color", color);
        compound.setInteger("CollarColor", color);
    }

    public void setOptionalInfo(boolean optionalInfo) {
        compound.setByte("Sheared", optionalInfo ? (byte) 1 : (byte) 0);
        compound.setByte("Sitting", optionalInfo ? (byte) 1 : (byte) 0);
    }

    public void setShowBottom(boolean showBottom) {
        compound.setByte("ShowBottom", showBottom ? (byte) 1 : (byte) 0);
    }

    public void setPowered(boolean powered) {
        compound.setByte("powered", powered ? (byte) 1 : (byte) 0);
    }

    public void setVariant(String color, String style) {
        compound.setInteger("Variant", variantMap.get(style) + variantMap.get(color));
    }

    public void setVariant(int variant) {
        compound.setInteger("Variant", variant);
    }

    public void setVillagerData(Villager.Profession profession, Villager.Type type, int level) {
        NBTCompound villagerData = compound.addCompound("VillagerData");
        villagerData.setString("type", type.name().toLowerCase());
        villagerData.setString("profession", profession.name().toLowerCase());
        villagerData.setInteger("level", level);
    }

    public void setNoAI(boolean noAI) {
        compound.setByte("NoAI", noAI ? (byte) 1 : (byte) 0);
    }

    public void setPersistenceRequired(boolean persistenceRequired) {
        compound.setByte("PersistenceRequired", persistenceRequired ? (byte) 1 : (byte) 0);
    }
}
