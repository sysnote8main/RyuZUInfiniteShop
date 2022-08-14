package ryuzuinfiniteshop.ryuzuinfiniteshop.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtil {
    public static boolean canGive(Inventory inventory, ItemStack item) {
        if (inventory.firstEmpty() > 0) return true;
        int stackSize = item.getType().getMaxStackSize();
        List<ItemStack> list = Arrays.stream(inventory.getContents()).filter(e -> e != null && e.isSimilar(item)).collect(Collectors.toList());
        int slotCount = list.size();
        int sum = list.stream().mapToInt(ItemStack::getAmount).sum();
        return stackSize * slotCount > sum;
    }

    public static ItemStack getNamedItem(Material material , String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
