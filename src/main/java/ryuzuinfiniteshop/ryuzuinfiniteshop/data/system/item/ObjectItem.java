package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.Config;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.MythicInstanceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class ObjectItem {
    private Object object;

    public ObjectItem(ItemStack item) {
        this.object = convert(item);
    }

    public void setObject(ItemStack item) {
        this.object = convert(item);
    }

    public ItemStack toItemStack() {
        if (object instanceof MythicItem)
            return ((MythicItem) object).convertItemStack();
        else if (object == null || ItemUtil.isAir((ItemStack) object))
            return new ItemStack(Material.AIR);
        else
            return ((ItemStack) object).clone();
    }

    public boolean isSimilar(ItemStack item) {
        if (object instanceof MythicItem)
            return ((MythicItem) object).getId().equals(MythicInstanceProvider.getInstance().getID(item));
        else if (object == null || ItemUtil.isAir((ItemStack) object))
            return ItemUtil.isAir(item);
        else
            return ((ItemStack) object).isSimilar(item);
    }

    private static Object convert(Object object) {
        if (object instanceof ItemStack && MythicInstanceProvider.isLoaded() && Config.saveByMMID && MythicInstanceProvider.getInstance().getID((ItemStack) object) != null)
            return new MythicItem(MythicInstanceProvider.getInstance().getID((ItemStack) object), ((ItemStack) object).getAmount());
        else
            return object;
    }
}
