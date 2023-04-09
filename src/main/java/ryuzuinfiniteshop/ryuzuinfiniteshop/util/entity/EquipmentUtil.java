package ryuzuinfiniteshop.ryuzuinfiniteshop.util.entity;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

import java.util.HashMap;

public class EquipmentUtil {
    private static final HashMap<Integer, EquipmentSlot> equipmentslots = new HashMap<Integer, EquipmentSlot>() {{
            put(2 * 9 + 1, EquipmentSlot.HEAD);
            put(3 * 9 + 1, EquipmentSlot.CHEST);
            put(4 * 9 + 1, EquipmentSlot.LEGS);
            put(5 * 9 + 1, EquipmentSlot.FEET);
            put(3 * 9 + 0, EquipmentSlot.HAND);
            put(3 * 9 + 2, EquipmentSlot.OFF_HAND);
    }};
    private static final HashMap<EquipmentSlot, String> equipmentDisplayName = new HashMap<EquipmentSlot, String>() {{
        put(EquipmentSlot.HAND, ChatColor.GREEN + "メインハンド");
        put(EquipmentSlot.HEAD, ChatColor.GREEN + "ヘルメット");
        put(EquipmentSlot.CHEST, ChatColor.GREEN + "チェストプレート");
        put(EquipmentSlot.LEGS, ChatColor.GREEN + "レギンス");
        put(EquipmentSlot.FEET, ChatColor.GREEN + "ブーツ");
        put(EquipmentSlot.OFF_HAND, ChatColor.GREEN + "オフハンド");
    }};

    public static HashMap<Integer, EquipmentSlot> getEquipmentsSlot() {
        return equipmentslots;
    }

    public static EquipmentSlot getEquipmentSlot(int i) {
        return equipmentslots.get(i);
    }

    public static String getEquipmentDisplayName(EquipmentSlot slot) {
        return equipmentDisplayName.get(slot);
    }

    public static ItemStack getEquipmentDisplayItem(EquipmentSlot slot) {
        return ItemUtil.getNamedItem(ItemUtil.getColoredItem("BLACK_STAINED_GLASS_PANE"), getEquipmentDisplayName(slot));
    }
}
