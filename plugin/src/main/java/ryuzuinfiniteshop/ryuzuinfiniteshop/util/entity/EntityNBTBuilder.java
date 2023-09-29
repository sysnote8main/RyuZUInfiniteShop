package ryuzuinfiniteshop.ryuzuinfiniteshop.util.entity;

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

    public EntityNBTBuilder(Entity entity) {
        if (RyuZUInfiniteShop.VERSION < 14) {
            this.entity = entity;
        } else {
            this.entity = entity;
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
}
