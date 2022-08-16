package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
    public static Location toLocationFromString(String stloc) {
        String[] datas = stloc.split(",");
        return new Location(Bukkit.getWorld(datas[0]), Double.parseDouble(datas[1]), Double.parseDouble(datas[2]), Double.parseDouble(datas[3]));
    }

    public static String toStringFromLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public static Location toBlockLocationFromLocation(Location loc) {
        return loc.set(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
    }
}
