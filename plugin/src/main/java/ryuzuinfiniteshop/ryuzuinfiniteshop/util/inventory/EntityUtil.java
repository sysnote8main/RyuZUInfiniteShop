package ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory;

import org.bukkit.DyeColor;

import java.util.Arrays;

public class EntityUtil {
    public static DyeColor getNextColor(DyeColor color) {
        int nextindex = Arrays.asList(DyeColor.values()).indexOf(color) + 1;
        return nextindex == DyeColor.values().length ?
                DyeColor.values()[0] :
                DyeColor.values()[nextindex];
    }
}
