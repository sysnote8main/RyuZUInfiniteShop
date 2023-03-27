package ryuzuinfiniteshop.ryuzuinfiniteshop.utils.configuration;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ItemUtil;

import java.util.HashMap;

public class EquipmentUtil {
    static final HashMap<Integer, EquipmentSlot> equipmentslots = new HashMap<>();

    static {
        equipmentslots.put(2 * 9 + 2, EquipmentSlot.HEAD);
        equipmentslots.put(3 * 9 + 2, EquipmentSlot.CHEST);
        equipmentslots.put(4 * 9 + 2, EquipmentSlot.LEGS);
        equipmentslots.put(5 * 9 + 2, EquipmentSlot.FEET);
        equipmentslots.put(3 * 9 + 1, EquipmentSlot.HAND);
        equipmentslots.put(3 * 9 + 3, EquipmentSlot.OFF_HAND);
    }

    public static HashMap<Integer, EquipmentSlot> getEquipmentsSlot() {
        return equipmentslots;
    }

    public static EquipmentSlot getEquipmentSlot(int i) {
        return equipmentslots.get(i);
    }

    public static int getEquipmentSlotNumber(EquipmentSlot slot) {
        int number = -1;
        switch (slot) {
            case HAND:
                number = 0;
                break;
            case HEAD:
                number = 1;
                break;
            case CHEST:
                number = 2;
                break;
            case LEGS:
                number = 3;
                break;
            case FEET:
                number = 4;
                break;
            case OFF_HAND:
                number = 5;
                break;
        }
        return number;
    }

    public static String getEquipmentDisplayName(EquipmentSlot slot) {
        String name = "";
        switch (slot) {
            case HAND:
                name = "メインハンド";
                break;
            case HEAD:
                name = "ヘルメット";
                break;
            case CHEST:
                name = "チェストプレート";
                break;
            case LEGS:
                name = "レギンス";
                break;
            case FEET:
                name = "ブーツ";
                break;
            case OFF_HAND:
                name = "オフハンド";
                break;
        }
        return name;
    }

    public static ItemStack getEquipmentDisplayItem(EquipmentSlot slot) {
        return ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, getEquipmentDisplayName(slot));
    }
}
