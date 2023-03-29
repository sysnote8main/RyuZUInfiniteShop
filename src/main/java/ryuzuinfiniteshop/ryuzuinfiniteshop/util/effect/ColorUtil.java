package ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect;

public class ColorUtil {

    public static String color(String str) {
        return str.replaceAll("&", "§");
    }

    public static String uncolor(String str) {
        return str.replaceAll("§", "&");
    }
}
