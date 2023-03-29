package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FileUtil {
    public static File initializeFile(String path) {
        String[] splited = path.split("/");
        File folder = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), String.join("" , Arrays.copyOf(splited , splited.length - 1)));
        File file = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), path);
        if(!folder.exists()) folder.mkdirs();
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File initializeFolder(String path) {
        File folder = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), path);
        if(!folder.exists()) folder.mkdirs();
        return folder;
    }
}
