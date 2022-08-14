package ryuzuinfiniteshop.ryuzuinfiniteshop.util;

import org.bukkit.Bukkit;

public class LocationUtil {
    public static org.bukkit.Location toLocationFromString(String stloc) {
        String[] datas = stloc.split(",");
        return new org.bukkit.Location(Bukkit.getWorld(datas[0]), Double.parseDouble(datas[1]) , Double.parseDouble(datas[2]), Double.parseDouble(datas[3]));
    }

    public static String toStringFromLocation(org.bukkit.Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}
