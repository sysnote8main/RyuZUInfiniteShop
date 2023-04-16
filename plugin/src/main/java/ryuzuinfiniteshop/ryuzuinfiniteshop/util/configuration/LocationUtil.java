package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;

public class LocationUtil {
    public static Location toLocationFromString(String stloc) {
        String[] datas = stloc.split(",");
        return new Location(Bukkit.getWorld(datas[0]), Double.parseDouble(datas[1]), Double.parseDouble(datas[2]), Double.parseDouble(datas[3]));
    }

    public static String toStringFromLocation(Location loc) {
        if(loc.getWorld() == null) throw new RuntimeException(LanguageKey.ERROR_WORLD_NOT_FOUND.getMessage(loc));
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public static Location toBlockLocationFromLocation(Location loc) {
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY());
        loc.setZ(loc.getBlockZ() + 0.5);
        return loc;
    }

    public static boolean isLocationString(String stloc) {
        String[] datas = stloc.split(",");
        if (datas.length != 4) return false;
        try {
            Double.parseDouble(datas[1]);
            Double.parseDouble(datas[2]);
            Double.parseDouble(datas[3]);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
