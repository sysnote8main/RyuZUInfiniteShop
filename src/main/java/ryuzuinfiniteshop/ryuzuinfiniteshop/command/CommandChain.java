package ryuzuinfiniteshop.ryuzuinfiniteshop.command;

import com.github.ryuzu.ryuzucommandsgenerator.CommandData;
import com.github.ryuzu.ryuzucommandsgenerator.CommandsGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.Config;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.DisplayPanelConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.SelectSearchItemGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.MythicInstanceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.LocationUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.util.function.Predicate;

public class CommandChain {
    public static void registerCommand() {
        CommandsGenerator.registerCommand(
                "ris",
                data -> {
                    data.sendMessage("§a§l-------§e§l=====§b§lRyuZU Infinite Shop§e§l=====§a§l-------");
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " spawn " + "§6§oショップの作成または更新をします");
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " spawn [x,y,z/EntityType/MythicMobID] " + "§6§oショップの作成または更新をします");
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " list " + "§6§oショップの一覧を表示します");
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " limit [increase/decrease/set] [player] [limit] " + "§6§o取引回数を変更します");
                    data.sendMessage("§a§l-------§e§l==============================§a§l-------");
                },
                "ris.op",
                data -> data.getArgs().length == 0
        );

        CommandsGenerator.registerCommand(
                "ris.reload",
                data -> {
                    ShopUtil.reloadAllShopTradeInventory(() -> {
                        TradeUtil.saveTradeLimits();
                        ShopUtil.saveAllShops();
                        ShopUtil.removeAllNPC();
                        Config.load();
                        DisplayPanelConfig.load();
                        ShopUtil.loadAllShops();
                        TradeUtil.loadTradeLimits();
                    });
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "全てのショップを更新しました");
                },
                "ris.op",
                data -> true,
                data -> data.getArgs().length == 1
        );

        CommandsGenerator.registerCommand(
                "ris.spawn",
                data -> {
                    Player p = (Player) data.getSender();
                    Location loc = p.getLocation();
                    if (ShopUtil.getShops().containsKey(LocationUtil.toStringFromLocation(loc))) {
                        ShopUtil.reloadShop(ShopUtil.getShop(LocationUtil.toStringFromLocation(loc)));
                        p.sendMessage(RyuZUInfiniteShop.prefixCommand + RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "ショップを更新しました");
                        return;
                    }
                    ShopUtil.createNewShop(loc, EntityType.VILLAGER);
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
                    Location loc;
                    if(LocationUtil.isLocationString(data.getArgs()[1])) {
                        loc = LocationUtil.toLocationFromString(data.getArgs()[1]);
                        if (ShopUtil.getShops().containsKey(data.getArgs()[1])) {
                            ShopUtil.reloadShop(ShopUtil.getShop(data.getArgs()[1]));
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "ショップを更新しました");
                            return;
                        }
                    } else {
                        Player p = (Player) data.getSender();
                        loc = p.getLocation();
                    }
                    if(MythicInstanceProvider.getInstance().getMythicMob(data.getArgs()[1]) == null)
                        ShopUtil.createNewShop(loc, EntityType.valueOf(data.getArgs()[1].toUpperCase()));
                    else
                        ShopUtil.createNewShop(loc, data.getArgs()[1]);
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "ショップを設置しました");
                },
                "ris.op",
                data -> {
                    if(LocationUtil.isLocationString(data.getArgs()[1]))
                        return true;
                    else {
                        Player p = (Player) data.getSender();
                        Location loc = p.getLocation();
                        if (ShopUtil.getShops().containsKey(LocationUtil.toStringFromLocation(loc))) {
                            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "既にその場所にはショップが存在します");
                            return false;
                        }
                    }
                    try {
                        EntityType.valueOf(data.getArgs()[1].toUpperCase());
                        return true;
                    } catch (IllegalArgumentException e) {
                        if(MythicInstanceProvider.getInstance().getMythicMob(data.getArgs()[1]) == null) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "有効なエンティティタイプまたはMythicMobIDを入力して下さい");
                            return false;
                        }
                        return true;
                    }
                },
                data -> data.getArgs().length != 1
        );

        CommandsGenerator.registerCommand(
                "ris.list",
                data -> {
                    Player p = (Player) data.getSender();
                    p.openInventory(new ShopListGui( 1).getInventory(ShopMode.Edit));
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
                    return data.getArgs().length <= 3;
                }
        );

        CommandsGenerator.registerCommand(
                "ris.search",
                data -> {
                    Player p = (Player) data.getSender();
                    p.openInventory(new SelectSearchItemGui().getInventory(ShopMode.Search));
                    SoundUtil.playClickShopSound(p);
                },
                "ris.op",
                data -> true,
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return true;
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
            return true;
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
