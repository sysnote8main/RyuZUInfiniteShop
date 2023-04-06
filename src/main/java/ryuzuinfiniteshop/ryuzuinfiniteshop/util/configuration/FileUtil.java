package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import com.github.ryuzu.ryuzucommandsgenerator.CommandData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.Config;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.DisplayPanelConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.UnderstandSystemConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class FileUtil {
    @Getter
    private static boolean saveBlock = false;

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

    public static boolean reloadAllWithMessage() {
        if(saveBlock) return false;

        ShopUtil.removeAllNPC();
        saveBlock = true;
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "全てのファイルをリロード中です。しばらくお待ちください。"));
        HashMap<Player, ShopHolder> viewer = ShopUtil.getAllShopInventoryViewer();
        Bukkit.getScheduler().runTaskAsynchronously(RyuZUInfiniteShop.getPlugin(), () -> {
            Config.load();
            TradeUtil.saveTradeLimits();
            ShopUtil.saveAllShops();
            UnderstandSystemConfig.save();
            Config.save();
            DisplayPanelConfig.save();
            UnderstandSystemConfig.load();
            DisplayPanelConfig.load();
            boolean converted = ShopUtil.loadAllShops();
            TradeUtil.loadTradeLimits();
            Bukkit.getScheduler().runTask(RyuZUInfiniteShop.getPlugin(), () -> {
                saveBlock = false;
                if(converted) saveAll(() -> {});
                Config.runAutoSave();
                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "全てのファイルのリロードが完了しました"));
                ShopUtil.getShops().values().forEach(Shop::respawnNPC);
                ShopUtil.openAllShopInventory(viewer);
            });
        });
        return true;
    }

    public static boolean loadAll(Runnable endTask) {
        if(saveBlock) return false;

        ShopUtil.removeAllNPC();
        saveBlock = true;
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "全てのファイルをロード中です。しばらくお待ちください。"));
        Bukkit.getScheduler().runTaskAsynchronously(RyuZUInfiniteShop.getPlugin(), () -> {
            Config.load();
            DisplayPanelConfig.load();
            UnderstandSystemConfig.load();
            boolean converted = ShopUtil.loadAllShops();
            TradeUtil.loadTradeLimits();
            Bukkit.getScheduler().runTask(RyuZUInfiniteShop.getPlugin(), () -> {
                saveBlock = false;
                if(converted) saveAll(() -> {});
                Config.runAutoSave();
                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "全てのファイルのロードが完了しました"));
                ShopUtil.getShops().values().forEach(Shop::respawnNPC);
                endTask.run();
            });
        });
        return true;
    }

    public static boolean unloadAll(Runnable endTask) {
        if(saveBlock) return false;

        ShopUtil.removeAllNPC();
        ShopUtil.getAllShopInventoryViewer();
        saveBlock = true;
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "全てのファイルを保存して、アンロード中です。しばらくお待ちください。"));
        Bukkit.getScheduler().runTaskAsynchronously(RyuZUInfiniteShop.getPlugin(), () -> {
            TradeUtil.saveTradeLimits();
            ShopUtil.saveAllShops();
            UnderstandSystemConfig.save();
            Config.save();
            DisplayPanelConfig.save();
            Bukkit.getScheduler().runTask(RyuZUInfiniteShop.getPlugin(), () -> {
                saveBlock = false;
                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "全てのファイルを保存して、アンロードが完了しました"));
                endTask.run();
            });
        });
        return true;
    }

    public static boolean saveAll(Runnable endTask) {
        if(saveBlock) return false;

        saveBlock = true;
//        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "全てのファイルを保存して中です。しばらくお待ちください。"));
        Bukkit.getScheduler().runTaskAsynchronously(RyuZUInfiniteShop.getPlugin(), () -> {
            TradeUtil.saveTradeLimits();
            ShopUtil.saveAllShops();
            UnderstandSystemConfig.save();
            Config.save();
            DisplayPanelConfig.save();
            Bukkit.getScheduler().runTask(RyuZUInfiniteShop.getPlugin(), () -> {
                saveBlock = false;
//                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "全てのファイルの保存が完了しました"));
                endTask.run();
            });
        });
        return true;
    }

    public static boolean isSaveBlock(Player p) {
        if(saveBlock) {
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "現在リロード処理中のため、すべての処理をブロックしています。");
            SoundUtil.playFailSound(p);
        }
        return saveBlock;
    }

    public static boolean isSaveBlock(CommandData data) {
        if(saveBlock) data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "現在リロード処理中のため、すべての処理をブロックしています。");
        return saveBlock;
    }
}
