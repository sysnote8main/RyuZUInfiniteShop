package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ItemUtil {
    //アイテムを与えることが可能か調べる
    public static boolean ableGive(Inventory inventory, ItemStack item) {
        if (inventory.firstEmpty() != -1) return true;
        if (item == null) return true;
        int stackSize = item.getType().getMaxStackSize();
        int sum = Arrays.stream(getContents(inventory)).filter(Objects::nonNull).filter(i -> i.isSimilar(item)).mapToInt(i -> stackSize - i.getAmount()).sum();
        return item.getAmount() <= sum;
    }

    //アイテムを与えることが可能か調べる
    public static boolean ableGive(Inventory inventory, ItemStack... items) {
        if (items == null) return true;
        HashMap<ItemStack, Integer> give = new HashMap<>();
        Arrays.stream(items).forEach(item -> give.put(item, containsCount(items , item)));
        give.replaceAll((i, v) -> give.get(i) - capacityCount(getContents(inventory), i));
        int needslot = give.keySet().stream()
                .filter(item -> give.get(item) > 0)
                .mapToInt(item -> {
            int size = give.get(item) / item.getType().getMaxStackSize();
            if (give.get(item) % item.getType().getMaxStackSize() != 0) size++;
            return size;
        }).sum();
        int emptyslot = (int) Arrays.stream(getContents(inventory)).filter(Objects::isNull).count();
        return needslot <= emptyslot;
    }

    //アイテムを含んでいるか調べる
    public static boolean contains(Inventory inventory, ItemStack... items) {
        if (items == null) return true;
        HashMap<ItemStack, Integer> need = new HashMap<>();
        Arrays.stream(items).filter(Objects::nonNull).forEach(item -> need.put(item, containsCount(items , item)));
        HashMap<ItemStack, Integer> has = new HashMap<>();
        if (need.keySet().stream().anyMatch(item -> Arrays.stream(getContents(inventory)).noneMatch(item::isSimilar))) return false;
        need.keySet().forEach(item -> has.put(item, containsCount(getContents(inventory) , item)));
        return need.keySet().stream().noneMatch(item -> has.get(item) < need.get(item));
    }

    public static ItemStack[] getContents(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        return inventory instanceof PlayerInventory ? Arrays.copyOf(contents , contents.length - 5) : contents;
    }

    public static ItemStack getOneItemStack(ItemStack item) {
        ItemStack copy = new ItemStack(item);
        copy.setAmount(1);
        return copy;
    }

    public static boolean isAir(@Nullable ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    //アイテムを含んでいるか調べる
    public static boolean contains(Inventory inventory, ItemStack item) {
        if (item == null) return true;
        int sum = containsCount(getContents(inventory), item);
        return item.getAmount() <= sum;
    }

    public static int containsCount(ItemStack[] contents, ItemStack item) {
        return Arrays.stream(contents).filter(Objects::nonNull).filter(i -> i.isSimilar(item)).mapToInt(ItemStack::getAmount).sum();
    }

    public static int capacityCount(ItemStack[] contents, ItemStack item) {
        return Arrays.stream(contents).filter(Objects::nonNull).filter(i -> i.isSimilar(item)).mapToInt(i -> i.getType().getMaxStackSize() - i.getAmount()).sum();
    }

    //名前付きアイテムを返す
    public static ItemStack getNamedItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    //名前付きアイテムを返す
    public static ItemStack getNamedItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack[] getItemSet(Inventory inv, int slot, int lengh) {
        List<ItemStack> set = new ArrayList<>();
        for (int i = 0; i < lengh; i++) {
            if (inv.getItem(slot + i) == null) continue;
            set.add(inv.getItem(slot + i));
        }
        return set.toArray(new ItemStack[0]);
    }
}
