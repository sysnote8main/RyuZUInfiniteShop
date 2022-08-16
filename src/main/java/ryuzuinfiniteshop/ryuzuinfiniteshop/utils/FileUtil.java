package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

import java.io.File;

public class FileUtil {
    public static File initializeFile(String path) {
        File file = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), path);
        if(!file.exists()) file.mkdirs();
        return file;
    }
}
