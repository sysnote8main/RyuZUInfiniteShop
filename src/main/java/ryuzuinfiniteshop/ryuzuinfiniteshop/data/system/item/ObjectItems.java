package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item;

import lombok.EqualsAndHashCode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.Config;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.MythicInstanceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class ObjectItems {
    private final List<Object> objects;

    public ObjectItems(Object object) {
        this.objects = (List<Object>) object;
    }

    public ObjectItems(List<Object> objects) {
        this.objects = objects;
    }

    public ObjectItems(ItemStack[] items) {
        this.objects = Arrays.stream(items).map(ObjectItems::convert).collect(Collectors.toList());
    }

    public List<Object> getObjects() {
        return new ArrayList<>(reconvert().objects);
    }

    public ItemStack[] toItemStacks() {
        return objects.stream().map(obj -> {
            if (obj instanceof MythicItem)
                return ((MythicItem) obj).convertItemStack();
            else if (obj == null || ItemUtil.isAir((ItemStack) obj))
                return new ItemStack(Material.AIR);
            else
                return (ItemStack) obj;
        }).filter(Objects::nonNull).map(ItemStack::clone).toArray(ItemStack[]::new);
    }

    public void setObject(Object object, int index) {
        objects.set(index , convert(object));
    }

    //return MythicItems or ItemStack
    //from MythicItems or ItemStack
    private static Object convert(Object object) {
        if(object instanceof ItemStack && NBTUtil.getNMSStringTag((ItemStack) object, "Error") != null)
            return new MythicItem(NBTUtil.getNMSStringTag((ItemStack) object, "Error"), ((ItemStack) object).getAmount());
        if (object instanceof ItemStack && MythicInstanceProvider.isLoaded() && Config.saveByMMID && MythicInstanceProvider.getInstance().getID((ItemStack) object) != null)
            return new MythicItem(MythicInstanceProvider.getInstance().getID((ItemStack) object), ((ItemStack) object).getAmount());
        else
            return object;
    }

    public ObjectItems reconvert() {
        return new ObjectItems(toItemStacks());
    }
}
