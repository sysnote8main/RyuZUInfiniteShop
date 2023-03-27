package ryuzuinfiniteshop.ryuzuinfiniteshop.commands;

import com.github.ryuzu.ryuzucommandsgenerator.CommandData;
import com.github.ryuzu.ryuzucommandsgenerator.CommandsGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.configuration.LocationUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.TradeUtil;

import java.util.function.Predicate;

public class CommandChain {
    public static void registerCommand() {
        CommandsGenerator.registerCommand(
                "ris",
                data -> {
                    if (data.getSender().hasPermission("ris.op")) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "/" + data.getLabel() + " [spawn/list/limit]");
                    } else {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "/" + data.getLabel() + " []");
                    }
                },
                "ris.player",
                data -> data.getArgs().length == 0
        );

        CommandsGenerator.registerCommand(
                "ris.spawn",
                data -> {
                    Player p = (Player) data.getSender();
                    Location loc = p.getLocation();
                    if (ShopUtil.getShops().containsKey(LocationUtil.toStringFromLocation(loc))) {
                        p.sendMessage(RyuZUInfiniteShop.prefixCommand + RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "既にその場所にはショップが存在します");
                        return;
                    }
                    ShopUtil.createShop(loc, EntityType.VILLAGER);
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "ショップを設置しました");
                },
                "ris.op",
                data -> true,
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return data.getArgs().length == 1;
                }
        );

        CommandsGenerator.registerCommand(
                "ris.spawn",
                data -> {
                    Player p = (Player) data.getSender();
                    Location loc = p.getLocation();
                    ShopUtil.createShop(loc, EntityType.valueOf(data.getArgs()[1].toUpperCase()));
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "ショップを設置しました");
                },
                "ris.op",
                data -> {
                    Player p = (Player) data.getSender();
                    Location loc = p.getLocation();
                    if (ShopUtil.getShops().containsKey(LocationUtil.toStringFromLocation(loc))) {
                        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "既にその場所にはショップが存在します");
                        return false;
                    }
                    try {
                        EntityType.valueOf(data.getArgs()[1].toUpperCase());
                        return true;
                    } catch (IllegalArgumentException e) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "有効なエンティティタイプを入力して下さい");
                        return false;
                    }
                },
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return data.getArgs().length != 1;
                }
        );

        CommandsGenerator.registerCommand(
                "ris.list",
                data -> {
                    Player p = (Player) data.getSender();
                    p.openInventory(new ShopListGui(null, 1).getInventory(ShopHolder.ShopMode.Edit));
                    SoundUtil.playClickShopSound(p);
                },
                "ris.op",
                data -> true,
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return data.getArgs().length == 1;
                }
        );

        CommandsGenerator.registerCommand(
                "ris.limit",
                data -> {
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "/" + data.getLabel() + "limit [increase/decrease/set] [player] [limit]");
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "トレード圧縮宝石を持った状態で実行してください");
                },
                "ris.op",
                data -> true,
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return data.getArgs().length <= 2;
                }
        );

        Predicate<CommandData> limitCondition = data -> {
            if (Bukkit.getServer().getPlayer(data.getArgs()[2]) == null) {
                data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "そのプレイヤーは存在しません");
                return false;
            }
            Player p = (Player) data.getSender();
            ShopTrade trade = TradeUtil.getFirstTrade(p.getInventory().getItemInMainHand());
            if (trade == null) {
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "トレード圧縮宝石を持って実行してください");
                return false;
            }
            try {
                Integer.parseInt(data.getArgs()[3]);
                return true;
            } catch (IllegalArgumentException e) {
                data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "0以上の整数を入力してください");
                return false;
            }
        };
        Predicate<CommandData> limitTabCondition = data -> {
            if (!(data.getSender() instanceof Player)) {
                data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                return false;
            }
            return data.getArgs().length >= 3;
        };

        CommandsGenerator.registerCommand(
                "ris.limit.increase",
                data -> {
                    Player p = (Player) data.getSender();
                    Player target = Bukkit.getServer().getPlayer(data.getArgs()[2]);
                    ShopTrade trade = TradeUtil.getFirstTrade(p.getInventory().getItemInMainHand());
                    trade.setTradeCount(target, trade.getCounts(target) + Integer.parseInt(data.getArgs()[3]));
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + target.getName() + "の取引上限に変更を加えました");
                    SoundUtil.playClickShopSound(p);
                },
                "ris.op", limitCondition, limitTabCondition
        );

        CommandsGenerator.registerCommand(
                "ris.limit.decrease",
                data -> {
                    Player p = (Player) data.getSender();
                    Player target = Bukkit.getServer().getPlayer(data.getArgs()[2]);
                    ShopTrade trade = TradeUtil.getFirstTrade(p.getInventory().getItemInMainHand());
                    trade.setTradeCount(target, trade.getCounts(target) - Integer.parseInt(data.getArgs()[3]));
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + target.getName() + "の取引上限に変更を加えました");
                    SoundUtil.playClickShopSound(p);
                },
                "ris.op", limitCondition, limitTabCondition
        );

        CommandsGenerator.registerCommand(
                "ris.limit.set",
                data -> {
                    Player p = (Player) data.getSender();
                    Player target = Bukkit.getServer().getPlayer(data.getArgs()[2]);
                    ShopTrade trade = TradeUtil.getFirstTrade(p.getInventory().getItemInMainHand());
                    trade.setTradeCount(target, Integer.parseInt(data.getArgs()[3]));
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + target.getName() + "の取引上限に変更を加えました");
                    SoundUtil.playClickShopSound(p);
                },
                "ris.op", limitCondition, limitTabCondition
        );
    }
}
