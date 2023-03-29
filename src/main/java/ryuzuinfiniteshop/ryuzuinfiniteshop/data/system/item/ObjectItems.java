package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item;

import lombok.EqualsAndHashCode;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.admin.MythicListener;

import java.util.Arrays;
import java.util.List;
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
        return objects;
    }

    public ItemStack[] toItemStacks() {
        return objects.stream().map(obj -> {
            if (obj instanceof MythicItem)
                return ((MythicItem) obj).convertItemStack();
            else
                return obj;
        }).map(obj -> ((ItemStack) obj).clone()).toArray(ItemStack[]::new);
    }

    public void setObject(Object object, int index) {
        objects.set(index , convert(object));
    }

    private static Object convert(Object object) {
        if (object instanceof ItemStack && MythicListener.getID((ItemStack) object) != null)
            return new MythicItem(MythicListener.getID((ItemStack) object), ((ItemStack) object).getAmount());
        else
            return object;
    }
}
