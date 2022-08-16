package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ItemUtil {
    //アイテムを与えることが可能か調べる
    public static boolean ableGive(Inventory inventory, ItemStack item) {
        if (inventory.firstEmpty() != -1) return true;
        if (item == null) return true;
        int stackSize = item.getType().getMaxStackSize();
        int sum = Arrays.stream(inventory.getContents()).filter(Objects::nonNull).filter(i -> i.isSimilar(item)).mapToInt(i -> stackSize - i.getAmount()).sum();
        return item.getAmount() <= sum;
    }

    //アイテムを与えることが可能か調べる
    public static boolean ableGive(Inventory inventory, ItemStack... items) {
        if (items == null) return true;
        HashMap<ItemStack, Integer> give = new HashMap<>();
        Arrays.stream(items).forEach(item -> give.put(item, give.getOrDefault(item, 0)));
        HashMap<ItemStack, Integer> capacity = new HashMap<>();
        Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .filter(give::containsKey)
                .forEach(item -> capacity.put(item, (item.getType().getMaxStackSize() - item.getAmount()) + capacity.getOrDefault(item, 0)));
        capacity.keySet().forEach(item -> capacity.put(item, give.get(item) - capacity.get(item)));
        int needslot = capacity.keySet().stream().mapToInt(item -> {
            int size = capacity.get(item) / item.getType().getMaxStackSize();
            if (capacity.get(item) % item.getType().getMaxStackSize() != 0) size++;
            return size;
        }).sum();
        int emptyslot = (int) Arrays.stream(inventory.getContents()).filter(Objects::isNull).count();
        return needslot <= emptyslot;
    }

    //アイテムを含んでいるか調べる
    public static boolean contains(Inventory inventory, ItemStack... items) {
        if (items == null) return true;
        HashMap<ItemStack, Integer> need = new HashMap<>();
        Arrays.stream(items).forEach(item -> need.put(item, need.getOrDefault(item, 0)));
        HashMap<ItemStack, Integer> has = new HashMap<>();
        Arrays.stream(inventory.getContents()).filter(need::containsKey).forEach(item -> has.put(item, item.getAmount() + has.getOrDefault(item, 0)));
        return Arrays.stream(inventory.getContents()).anyMatch(item -> has.get(item) < need.get(item));
    }

    //アイテムを含んでいるか調べる
    public static boolean contains(Inventory inventory, ItemStack item) {
        if (item == null) return true;
        int sum = Arrays.stream(inventory.getContents()).filter(Objects::nonNull).filter(i -> i.isSimilar(item)).mapToInt(ItemStack::getAmount).sum();
        return item.getAmount() <= sum;
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
