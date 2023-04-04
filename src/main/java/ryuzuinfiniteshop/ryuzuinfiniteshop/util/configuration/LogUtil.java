package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;


import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityInteractEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LogUtil {
    public enum LogType {
        CREATESHOP,
        REMOVESHOP,
        MERGESHOP,
        ADDTRADE,
        REMOVETRADE,
        REPLACETRADE
    }

    private static List<String> getLog() {
        File file = FileUtil.initializeFile("log.csv");
        List<String> log;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            log = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return log;
    }

    public static void log(LogType type, String player, String id, ShopTrade trade, int limit) {
        List<String> logBuilder = baseLog(type, player, id);
        logBuilder.add(Arrays.stream(trade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(Arrays.stream(trade.getGiveItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(String.valueOf(limit));
        String log = String.join(",", logBuilder);
        log(log);
    }

    public static void log(LogType type, String player, String id, ShopTrade fromTrade, ShopTrade toTrade, int fromLimit, int toLimit) {
        List<String> logBuilder = baseLog(type, player, id);
        logBuilder.add(Arrays.stream(fromTrade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(Arrays.stream(toTrade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(String.valueOf(fromLimit));
        logBuilder.add(String.valueOf(toLimit));
        String log = String.join(",", logBuilder);
        log(log);
    }

    public static void log(LogType type, String player, String id) {
        log(String.join(",", baseLog(type, player, id)));
    }

    public static List<String> baseLog(LogType type, String player, String id) {
        List<String> logBuilder = new ArrayList<>();
        logBuilder.add(type.name());
        logBuilder.add(player);
        logBuilder.add(id.replace(",", "/"));
        logBuilder.add(ChatColor.stripColor(ShopUtil.getShop(id).getDisplayNameOrElseNone()));
        return logBuilder;
    }

    private static void log(String log) {
        File file = FileUtil.initializeFile("log.csv");
        List<String> logs = getLog();
        logs.add(log);

        try (FileWriter fileWriter = new FileWriter(file)) {
            for(String l : logs) {
                fileWriter.write(l + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
