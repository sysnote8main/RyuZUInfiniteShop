package ryuzuinfiniteshop.ryuzuinfiniteshop.command;

import com.github.ryuzu.ryuzucommandsgenerator.CommandData;
import com.github.ryuzu.ryuzucommandsgenerator.CommandsGenerator;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.UnderstandSystemConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.SelectSearchItemGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.LogUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.MythicInstanceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.LocationUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.util.HashMap;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommandChain {
    public static void registerCommand() {
        Set<String> args1 = Sets.newHashSet("list", "search", "spawn", "open", "list", "reload", "load", "unload", "limit");

        CommandsGenerator.registerCommand(
                "sis",
                data -> {
                    data.sendMessage("§a§l-------§e§l=====§b§lSearchable Infinite Shop§e§l=====§a§l-------");
                    if (data.getSender().hasPermission("sis.list")) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " list " + "§6§oショップの一覧を表示します");
                    }
                    if (data.getSender().hasPermission("sis.search")) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " search " + "§6§oショップや取引を検索します");
                    }
                    if (data.getSender().hasPermission("sis.op")) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " spawn <world,x,y,z/Block/EntityType/MythicMobID> " + "§6§oショップの作成または更新をします");
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " open [world,x,y,z] <player>" + "§6§oショップの取引画面を開きます");
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " list " + "§6§oショップの一覧を表示します");
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " reload " + "§6§o全てのデータをリロードします");
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " load " + "§6§o全てのデータをファイルから読み取り、ショップをロードします");
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " unload " + "§6§o全てのデータをファイルに保存し、ショップをアンロードします");
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " limit [increase/decrease/set] [player] [value] " + "§6§o取引回数を変更します");
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "[arg] " + "§6§o必須, " + ChatColor.BLUE + "<arg> " + "§6§o任意");
                    }
                    data.sendMessage("§a§l-------§e§l==============================§a§l-------");
                },
                "sis.player",
                data -> data.getArgs().length == 0 || (data.getArgs().length == 1 && !args1.contains(data.getArgs()[0]))
        );

        CommandsGenerator.registerCommand(
                "sis.reload",
                data -> FileUtil.reloadAllWithMessage(),
                "sis.op",
                data -> true,
                data -> !FileUtil.isSaveBlock(data)
        );

        CommandsGenerator.registerCommand(
                "sis.unload",
                data -> FileUtil.unloadAll(() -> {}),
                "sis.op",
                data -> true,
                data -> !FileUtil.isSaveBlock(data)
        );

        CommandsGenerator.registerCommand(
                "sis.load",
                data -> FileUtil.loadAll(() -> {}),
                "sis.op",
                data -> true,
                data -> !FileUtil.isSaveBlock(data)
        );

        CommandsGenerator.registerCommand(
                "sis.understand",
                data -> {
                    Player p = (Player) data.getSender();
                    UnderstandSystemConfig.signedPlayers.add(p.getUniqueId().toString());
                    SoundUtil.playClickShopSound(p);
                },
                "sis.player",
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return !UnderstandSystemConfig.signedPlayers.contains(((Player) data.getSender()).getUniqueId().toString());
                }
        );

        CommandsGenerator.registerCommand(
                "sis.spawn",
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
                    LogUtil.log(LogUtil.LogType.CREATESHOP, data.getSender().getName(), LocationUtil.toStringFromLocation(loc));
                },
                "sis.op",
                data -> true,
                data -> {
                    if (data.getArgs().length != 1) return false;
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return !FileUtil.isSaveBlock(data);
                }
        );

        CommandsGenerator.registerCommand(
                "sis.spawn",
                data -> {
                    Location loc;
                    if (LocationUtil.isLocationString(data.getArgs()[1])) {
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
                    if (MythicInstanceProvider.isLoaded() && MythicInstanceProvider.getInstance().getMythicMob(data.getArgs()[1]) != null)
                        ShopUtil.createNewShop(loc, data.getArgs()[1]);
                    else
                        ShopUtil.createNewShop(loc, EntityType.valueOf(data.getArgs()[1].toUpperCase()));
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "ショップを設置しました");
                    LogUtil.log(LogUtil.LogType.CREATESHOP, data.getSender().getName(), LocationUtil.toStringFromLocation(loc));
                },
                "sis.op",
                data -> {
                    if (LocationUtil.isLocationString(data.getArgs()[1]))
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
                        if (MythicInstanceProvider.isLoaded() && MythicInstanceProvider.getInstance().getMythicMob(data.getArgs()[1]) == null) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "有効なエンティティタイプまたはMythicMobIDを入力して下さい");
                            return false;
                        }
                        return true;
                    }
                },
                data -> {
                    if (data.getArgs().length >= 2) return false;
                    if (FileUtil.isSaveBlock(data)) return false;
                    return !FileUtil.isSaveBlock(data);
                }
        );

        CommandsGenerator.registerCommand(
                "sis.open",
                data -> {
                    Player p = data.getArgs().length == 2 ? (Player) data.getSender() : Bukkit.getServer().getPlayer(data.getArgs()[2]);
                    p.openInventory(ShopUtil.getShop(data.getArgs()[1]).getPage(1).getInventory(ShopMode.Trade, p));
                },
                "sis.op",
                data -> {
                    if (data.getArgs().length == 2) {
                        if (!(data.getSender() instanceof Player)) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                            return false;
                        }
                        if (!ShopUtil.getShops().containsKey(data.getArgs()[1])) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "そのショップは存在しません");
                            return false;
                        }
                        if (ShopUtil.getShops().get(data.getArgs()[1]).getPageCount() == 0) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "そのショップには取引がありません");
                            return false;
                        }
                        return true;
                    } else {
                        if (!ShopUtil.getShops().containsKey(data.getArgs()[1])) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "そのショップは存在しません");
                            return false;
                        }
                        if (ShopUtil.getShops().get(data.getArgs()[1]).getPageCount() == 0) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "そのショップには取引がありません");
                            return false;
                        }
                        if (Bukkit.getServer().getPlayer(data.getArgs()[2]) == null) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "そのプレイヤーは存在しません");
                            return false;
                        }
                        return true;
                    }
                }, data -> !FileUtil.isSaveBlock(data)
        );

        CommandsGenerator.registerCommand(
                "sis.list",
                data -> {
                    Player p = (Player) data.getSender();
                    ShopMode mode = p.hasPermission("sis.op") ? ShopMode.Edit : ShopMode.Trade;
                    p.openInventory(new ShopListGui(1, ShopUtil.getSortedShops(mode, null)).getInventory(mode));
                    SoundUtil.playClickShopSound(p);
                },
                "sis.list",
                data -> true,
                data -> {
                    if (data.getArgs().length != 1) return false;
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return !FileUtil.isSaveBlock(data);
                }
        );

        CommandsGenerator.registerCommand(
                "sis.search",
                data -> {
                    Player p = (Player) data.getSender();
                    p.openInventory(new SelectSearchItemGui().getInventory(ShopMode.Search));
                    SoundUtil.playClickShopSound(p);
                },
                "sis.search",
                data -> true,
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return !FileUtil.isSaveBlock(data);
                }
        );
        HashMap<String, BiFunction<Integer, Integer, Integer>> limitargs2 = new HashMap<String, BiFunction<Integer, Integer, Integer>>() {{
            put("increase", (a, b) -> a + b);
            put("decrease", (a, b) -> a - b);
            put("set", (a, b) -> b);
        }};

        CommandsGenerator.registerCommand(
                "sis.limit",
                data -> {
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "/" + data.getLabel() + "limit [increase/decrease/set] [player] [limit]");
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "トレード圧縮宝石を持った状態で実行してください");
                },
                "sis.op",
                data -> true,
                data -> {
                    if (data.getArgs().length >= 4) return false;
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return !FileUtil.isSaveBlock(data);
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
            return !FileUtil.isSaveBlock(data);
        };

        limitargs2.keySet().forEach(
                key -> CommandsGenerator.registerCommand(
                        "sis.limit." + key,
                        data -> {
                            Player p = (Player) data.getSender();
                            Player target = Bukkit.getServer().getPlayer(data.getArgs()[2]);
                            ShopTrade trade = TradeUtil.getFirstTrade(p.getInventory().getItemInMainHand());
                            trade.setTradeCount(target, limitargs2.get(key).apply(trade.getCounts(target), Integer.parseInt(data.getArgs()[3])));
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + target.getName() + "の取引上限に変更を加えました");
                            SoundUtil.playClickShopSound(p);
                        },
                        "sis.op", limitCondition, limitTabCondition
                )
        );
    }
}
