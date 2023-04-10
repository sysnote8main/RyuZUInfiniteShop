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
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class CommandChain {
    public static void registerCommand() {
        Set<String> args1 = Sets.newHashSet("list", "search", "spawn", "open", "list", "reload", "load", "save", "limit");

        CommandsGenerator.registerCommand(
                "sis",
                data -> {
                    data.sendMessage("§a§l-------§e§l=====§b§lSearchable Infinite Shop§e§l=====§a§l-------");
                    if (data.getSender().hasPermission("sis.list") || data.getSender().hasPermission("sis.op")) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " list " + ChatColor.GOLD + LanguageKey.COMMAND_LIST_SHOPS.getMessage());
                    }
                    if (data.getSender().hasPermission("sis.search") || data.getSender().hasPermission("sis.op")) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " search " + ChatColor.GOLD + LanguageKey.COMMAND_SEARCH_TRADES.getMessage());
                    }
                    if (data.getSender().hasPermission("sis.op")) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " spawn <world,x,y,z/Block/EntityType/MythicMobID> " + ChatColor.GOLD + LanguageKey.COMMAND_SPAWN_SHOP.getMessage());
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " open [world,x,y,z] <player>" + ChatColor.GOLD + LanguageKey.COMMAND_OPEN_TRADE_GUI.getMessage());
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " list " + ChatColor.GOLD + LanguageKey.COMMAND_LIST_SHOPS.getMessage());
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " reload " + ChatColor.GOLD + LanguageKey.COMMAND_RELOAD_ALL_DATA.getMessage());
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " load " + ChatColor.GOLD + LanguageKey.COMMAND_LOAD_ALL_DATA.getMessage());
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " unload " + ChatColor.GOLD + LanguageKey.COMMAND_UNLOAD_ALL_DATA.getMessage());
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "/" + data.getLabel() + " limit [increase/decrease/set] [player] [value] " + ChatColor.GOLD + LanguageKey.COMMAND_CHANGE_TRADE_LIMIT.getMessage());
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.BLUE + "[arg] " + ChatColor.GOLD + LanguageKey.COMMAND_ARGUMENT_REQUIRED.getMessage() + ", " + ChatColor.BLUE + "<arg> " + ChatColor.GOLD + LanguageKey.COMMAND_ARGUMENT_OPTIONAL.getMessage());
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
                "sis.save",
                data -> FileUtil.saveAll(() -> {}),
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
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.COMMAND_PLAYER_ONLY.getMessage());
                        return false;
                    }
                    return !UnderstandSystemConfig.signedPlayers.contains(((Player) data.getSender()).getUniqueId().toString());
                }
        );

        CommandsGenerator.registerCommand(
                "sis.spawn",
                data -> {
                    Player player = (Player) data.getSender();
                    Location location = player.getLocation();
                    if (ShopUtil.getShops().containsKey(LocationUtil.toStringFromLocation(location))) {
                        ShopUtil.reloadShop(ShopUtil.getShop(LocationUtil.toStringFromLocation(location)));
                        player.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN +  LanguageKey.MESSAGE_SHOP_UPDATED.getMessage());
                        return;
                    }
                    ShopUtil.createNewShop(location, EntityType.VILLAGER);
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_SHOP_CREATED.getMessage());
                    LogUtil.log(LogUtil.LogType.CREATESHOP, data.getSender().getName(), LocationUtil.toStringFromLocation(location));
                },
                "sis.op",
                data -> true,
                data -> {
                    if (data.getArgs().length != 1) return false;
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.COMMAND_PLAYER_ONLY.getMessage());
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
                            data.sendMessage(ChatColor.GREEN + LanguageKey.MESSAGE_SHOP_UPDATED.getMessage());
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
                    ShopUtil.getShop(LocationUtil.toStringFromLocation(loc)).respawnNPC();
                    data.sendMessage(ChatColor.GREEN + LanguageKey.MESSAGE_SHOP_CREATED.getMessage());
                    LogUtil.log(LogUtil.LogType.CREATESHOP, data.getSender().getName(), LocationUtil.toStringFromLocation(loc));
                },
                "sis.op",
                data -> {
                    if (LocationUtil.isLocationString(data.getArgs()[1]))
                        return true;
                    try {
                        EntityType.valueOf(data.getArgs()[1].toUpperCase());
                        return true;
                    } catch (IllegalArgumentException e) {
                        if (MythicInstanceProvider.isLoaded() && MythicInstanceProvider.getInstance().getMythicMob(data.getArgs()[1]) == null) {
                            data.sendMessage(ChatColor.RED + LanguageKey.MESSAGE_ERROR_ENTITY_INVALID.getMessage());
                            return false;
                        }
                        return true;
                    }
                },
                data -> {
                    if (data.getArgs().length < 2) return false;
                    if (FileUtil.isSaveBlock(data)) return false;
                    return !FileUtil.isSaveBlock(data);
                }
        );

        CommandsGenerator.registerCommand(
                "sis.open",
                data -> {
                    Player p = data.getArgs().length == 2 ? (Player) data.getSender() : Bukkit.getServer().getPlayer(data.getArgs()[2]);
                    p.openInventory(ShopUtil.getShop(data.getArgs()[1]).getPage(1).getInventory(ShopMode.TRADE, p));
                },
                "sis.op",
                data -> {
                    if (data.getArgs().length == 2) {
                        if (!(data.getSender() instanceof Player)) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.COMMAND_PLAYER_ONLY.getMessage());
                            return false;
                        }
                        if (!ShopUtil.getShops().containsKey(data.getArgs()[1])) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_SHOP_NOT_EXIST.getMessage());
                            return false;
                        }
                        if (ShopUtil.getShops().get(data.getArgs()[1]).getPageCount() == 0) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_SHOP_NO_TRADES.getMessage());
                            return false;
                        }
                        return true;
                    } else {
                        if (!ShopUtil.getShops().containsKey(data.getArgs()[1])) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_SHOP_NOT_EXIST.getMessage());
                            return false;
                        }
                        if (ShopUtil.getShops().get(data.getArgs()[1]).getPageCount() == 0) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_SHOP_NO_TRADES.getMessage());
                            return false;
                        }
                        if (Bukkit.getServer().getPlayer(data.getArgs()[2]) == null) {
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_NOT_EXIST_PLAYER.getMessage());
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
                    ShopMode mode = p.hasPermission("sis.op") ? ShopMode.EDIT : ShopMode.TRADE;
                    p.openInventory(new ShopListGui(1, ShopUtil.getSortedShops(mode, null)).getInventory(mode));
                    SoundUtil.playClickShopSound(p);
                },
                Arrays.asList("sis.list" , "sis.op"),
                data -> true,
                data -> {
                    if (data.getArgs().length != 1) return false;
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.COMMAND_PLAYER_ONLY.getMessage());
                        return false;
                    }
                    return !FileUtil.isSaveBlock(data);
                }
        );

        CommandsGenerator.registerCommand(
                "sis.search",
                data -> {
                    Player p = (Player) data.getSender();
                    p.openInventory(new SelectSearchItemGui().getInventory(ShopMode.SEARCH));
                    SoundUtil.playClickShopSound(p);
                },
                Arrays.asList("sis.search" , "sis.op"),
                data -> true,
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.COMMAND_PLAYER_ONLY.getMessage());
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
                    data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_TRADE_REQUIRED_GEM.getMessage());
                },
                "sis.op",
                data -> true,
                data -> {
                    if (data.getArgs().length >= 4) return false;
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.COMMAND_PLAYER_ONLY.getMessage());
                        return false;
                    }
                    return !FileUtil.isSaveBlock(data);
                }
        );

        Predicate<CommandData> limitCondition = data -> {
            if (Bukkit.getServer().getPlayer(data.getArgs()[2]) == null) {
                data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_NOT_EXIST_PLAYER.getMessage());
                return false;
            }
            Player p = (Player) data.getSender();
            ShopTrade trade = TradeUtil.getFirstTrade(p.getInventory().getItemInMainHand());
            if (trade == null) {
                data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_TRADE_REQUIRED_GEM.getMessage());
                return false;
            }
            try {
                Integer.parseInt(data.getArgs()[3]);
                return true;
            } catch (IllegalArgumentException e) {
                data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.COMMAND_INVALID_INTEGER.getMessage());
                return false;
            }
        };

        Predicate<CommandData> limitTabCondition = data -> {
            if (!(data.getSender() instanceof Player)) {
                data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.COMMAND_PLAYER_ONLY.getMessage());
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
                            data.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_SUCCESS_LIMIT_CHANGE.getMessage(target.getName()));
                            SoundUtil.playClickShopSound(p);
                        },
                        "sis.op", limitCondition, limitTabCondition
                )
        );
    }
}
