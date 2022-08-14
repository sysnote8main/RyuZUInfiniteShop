package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.Location;

public class ShopLocation {
    public Location location;

    public ShopLocation(Location loc) {

    }

    private static String toPath(Location loc) {
        return loc.getWorld().toString() + "," +
                loc.getX() + "," +
                loc.getY() + "," +
                loc.getZ() + ",";
    }
}
