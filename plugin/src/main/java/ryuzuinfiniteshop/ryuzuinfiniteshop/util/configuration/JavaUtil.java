package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class JavaUtil {
    public static <T> List<T> getSubList(List<T> list, int from, int to) {
        int toIndex = Math.min(to, list.size());
        return new ArrayList<>(list.subList(from, toIndex));
    }

    public static <T> List<T>[] splitList(List<T> list, int n) {
        int size = list.size();
        int m = size / n;
        if (size % n != 0) m++;

        List<T>[] splitted = new ArrayList[m];
        for (int i = 0; i < m; i++) {
            int fromIndex = i * n;
            int toIndex = Math.min(i * n + n, size);

            splitted[i] = new ArrayList<>(list.subList(fromIndex, toIndex));
        }

        return splitted;
    }

    public static <T> T getOrDefault(@Nullable T obj, T defaultobj) {
        return obj == null || (obj instanceof ItemStack && ((ItemStack)obj).getType().equals(Material.AIR)) || (obj instanceof String && ((String)obj).isEmpty()) ? defaultobj : obj;
    }

    public static boolean containsIgnoreCase(@Nullable ItemStack item,@Nullable String str2) {
        if(ItemUtil.isAir(item)) return false;
        if(!item.hasItemMeta()) return false;
        return containsIgnoreCase(item.getItemMeta().getDisplayName(), str2);
    }

    public static boolean containsIgnoreCase(@Nullable String str1,@Nullable String str2) {
        if (str1 == null || str1.isEmpty()) return false;
        if (str2 == null || str2.isEmpty()) return false;
        return ChatColor.stripColor(str1).toLowerCase().contains(ChatColor.stripColor(str2).toLowerCase());
    }
}
