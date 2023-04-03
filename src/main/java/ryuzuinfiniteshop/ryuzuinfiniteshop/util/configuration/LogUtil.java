package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;


import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
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

    public static void log(LogType type, String player, String id, ShopTrade trade, int limit) {
        File file = FileUtil.initializeFile("log.csv");
        List<String> logBuilder = new ArrayList<>();
        logBuilder.add(type.name());
        logBuilder.add(player);
        logBuilder.add(id);
        logBuilder.add(ShopUtil.getShop(id).getDisplayNameOrElseNone());
        logBuilder.add(String.join("+", Arrays.stream(trade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining(","))));
        logBuilder.add(String.join("+", Arrays.stream(trade.getGiveItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining(","))));
        logBuilder.add(String.valueOf(limit));
        String log = String.join("+", logBuilder);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(log);
            fileWriter.write("\r\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void log(LogType type, String player, String id, ShopTrade fromTrade, ShopTrade toTrade, int fromLimit, int toLimit) {
        File file = FileUtil.initializeFile("log.csv");
        List<String> logBuilder = new ArrayList<>();
        logBuilder.add(type.name());
        logBuilder.add(player);
        logBuilder.add(id);
        logBuilder.add(ShopUtil.getShop(id).getDisplayNameOrElseNone());
        logBuilder.add(Arrays.stream(fromTrade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining(",")));
        logBuilder.add(Arrays.stream(toTrade.getTakeItems()).filter(Objects::nonNull).map(ItemUtil::getString).collect(Collectors.joining(",")));
        logBuilder.add(String.valueOf(fromLimit));
        logBuilder.add(String.valueOf(toLimit));
        String log = String.join("+", logBuilder);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(log);
            fileWriter.write("\r\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void log(LogType type, String player, String id) {
        File file = FileUtil.initializeFile("log.csv");
        List<String> logBuilder = new ArrayList<>();
        logBuilder.add(type.name());
        logBuilder.add(player);
        logBuilder.add(id);
        logBuilder.add(ShopUtil.getShop(id).getDisplayNameOrElseNone());
        String log = String.join("+", logBuilder);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(log);
            fileWriter.write("\r\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //        try (FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                String[] arrayStr = line.split(",");
//
//                for (String str : arrayStr) {
//                    System.out.println(str);
//                }
//            }
//        } catch (IOException e) {
//            System.out.println(e);
//        }
}
