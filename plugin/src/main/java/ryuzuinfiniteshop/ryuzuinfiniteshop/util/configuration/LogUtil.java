package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.Config;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.TradeOption;
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
        REPLACETRADE,
        TRADE
    }

    private static List<String> getLog() {
        File file = FileUtil.initializeFile("log.csv");
        List<String> log = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            log = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            if (!Config.readOnlyIgnoreIOException) throw new RuntimeException(e);
        }
        return log;
    }

    public static void log(LogType type, String player, String id, ShopTrade trade, int limit) {
        if (!Config.editLog) return;
        List<String> logBuilder = baseLog(type, player, id);
        logBuilder.add(Arrays.stream(trade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(Arrays.stream(trade.getGiveItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(String.valueOf(limit));
        String log = String.join(",", logBuilder);
        log(log);
    }

    public static void log(LogType type, String player, String id, ShopTrade fromTrade, ShopTrade toTrade, TradeOption fromOption, TradeOption toOption) {
        if (!Config.editLog) return;
        List<String> logBuilder = baseLog(type, player, id);
        logBuilder.add(Arrays.stream(fromTrade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(Arrays.stream(toTrade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(fromOption.serialize().keySet().stream().map(Objects::toString).collect(Collectors.joining("+")));
        logBuilder.add(toOption.serialize().keySet().stream().map(Objects::toString).collect(Collectors.joining("+")));
        String log = String.join(",", logBuilder);
        log(log);
    }

    public static void log(String player, String id, ShopTrade trade, int times) {
        if (!Config.tradeLog) return;
        List<String> logBuilder = baseLog(LogType.TRADE, player, id);
        logBuilder.add(Arrays.stream(trade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(Arrays.stream(trade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining("+")));
        logBuilder.add(String.valueOf(times));
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

        Bukkit.getScheduler().runTaskAsynchronously(RyuZUInfiniteShop.getPlugin(), () -> {
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                fileWriter.write(log + System.lineSeparator());
            } catch (IOException e) {
                if (!Config.readOnlyIgnoreIOException) throw new RuntimeException(e);
            }
        });
    }
}
