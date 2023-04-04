package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.SelectSearchItemGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.TradeSearchGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.util.LinkedHashMap;

public class SearchTradeListener implements Listener {
    @EventHandler
    public void selectSearchItem(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ModeHolder holder = ShopUtil.getModeHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof SelectSearchItemGui)) return;
        if (event.getClickedInventory() == null) return;

        event.setCancelled(true);
        //必要なデータを取得

        Player p = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        ClickType type = event.getClick();
        ItemStack searchItem = event.getView().getTopInventory().getItem(4);
        ItemStack panel = ItemUtil.getNamedItem(Material.WHITE_STAINED_GLASS_PANE, ChatColor.BLUE + "検索するアイテムを持ってクリック", ChatColor.GREEN + "シフトクリック: NPCの名前で検索");
        if (slot != 0 && slot != 4 && slot != 8) return;

        ShopMode mode = p.hasPermission("sis.op") ? ShopMode.Edit : ShopMode.Search;

        if ((type.isRightClick() || type.isLeftClick())) {
            if (slot == 4) {
                if (event.isShiftClick()) {
                    SchedulerListener.setSchedulers(p, "search", (message) -> {
                        //成功時の処理
                        p.openInventory(new ShopListGui(1, message).getInventory(mode));
                        SoundUtil.playClickShopSound(p);
                    });
                    p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "検索するNPCの名前をチャットに入力してください");
                    p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "20秒待つか'Cancel'と入力することでキャンセルことができます");
                } else if (ItemUtil.isAir(event.getCursor()))
                    event.setCurrentItem(panel);
                else
                    event.setCurrentItem(ItemUtil.getOneItemStack(event.getCursor()));
                SoundUtil.playClickShopSound(p);
            } else {
                if (panel.equals(searchItem)) {
                    if(event.isShiftClick()) {
                        SchedulerListener.setSchedulers(p, "search", (message) -> {
                            //成功時の処理
                            LinkedHashMap<ShopTrade, Shop> searchedTrades = slot == 0 ? TradeUtil.getTradesFromTakeByDisplayName(message, mode) : TradeUtil.getTradesFromGiveByDisplayName(message, mode);
                            if (searchedTrades.size() == 0) {
                                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "検索結果がありませんでした");
                                SoundUtil.playFailSound(p);
                                return;
                            }
                            p.openInventory(new ShopListGui(1, message).getInventory(mode));
                            SoundUtil.playClickShopSound(p);
                        });
                        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "検索するアイテムの名前をチャットに入力してください");
                        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "20秒待つか'Cancel'と入力することでキャンセルことができます");
                    } else
                        SoundUtil.playFailSound(p);
                } else {
                    LinkedHashMap<ShopTrade, Shop> searchedTrades = slot == 0 ? TradeUtil.getTradesFromTake(searchItem, mode) : TradeUtil.getTradesFromGive(searchItem, mode);
                    if (searchedTrades.size() == 0) {
                        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "検索結果がありませんでした");
                        SoundUtil.playFailSound(p);
                        return;
                    }
                    p.openInventory(new TradeSearchGui(1, p, searchedTrades).getInventory(mode));
                    SoundUtil.playClickShopSound(p);
                }
            }
        }
    }

    @EventHandler
    public void changePage(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ModeHolder holder = ShopUtil.getModeHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof TradeSearchGui)) return;
        if (event.getClickedInventory() != null) return;

        //必要なデータを取得
        SeachTradeHolder seachTradeHolder = (SeachTradeHolder) holder;
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        ShopMode mode = holder.getMode();
        int page = seachTradeHolder.getGui().getPage();

        //ページ切り替え
        boolean fail = false;
        if (type.isLeftClick()) {
            if (page - 1 == 0)
                fail = true;
            else
                p.openInventory(new TradeSearchGui(page - 1, p, seachTradeHolder.getTrades()).getInventory(mode));
        }
        if (type.isRightClick()) {
            if (page == seachTradeHolder.getMaxPage()) {
                fail = true;
            } else
                p.openInventory(new TradeSearchGui(page + 1, p, seachTradeHolder.getTrades()).getInventory(mode));
        }
        if (fail) {
            SoundUtil.playFailSound(p);
        } else {
            SoundUtil.playClickShopSound(p);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void openShop(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ModeHolder holder = ShopUtil.getModeHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof TradeSearchGui)) return;
        if (event.getClickedInventory() == null) return;

        event.setCancelled(true);

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Inventory inv = event.getView().getTopInventory();
        int base = (event.getSlot() / 9) * 9;
        ItemStack item = NBTUtil.getNMSStringTag(inv.getItem(4 + base), "Shop") == null ? inv.getItem(6 + base) : inv.getItem(4 + base);
        Shop shop = ShopUtil.getShop(NBTUtil.getNMSStringTag(item, "Shop"));
        if (shop == null) {
            SoundUtil.playFailSound(p);
            return;
        }

        if (event.isShiftClick()) {
            //編集画面を開く
            ShopUtil.closeAllShopTradeInventory(shop);
            p.openInventory(shop.getEditor(1).getInventory(ShopMode.Edit, holder));
            SoundUtil.playClickShopSound(p);
            shop.setEditting(true);
        } else {
            if (!shop.isAvailableShop(p)) return;
            ShopTradeGui gui = shop.getPage(Integer.parseInt(NBTUtil.getNMSStringTag(item, "Page")));
            if (gui == null) {
                SoundUtil.playFailSound(p);
                return;
            }
            p.openInventory(gui.getInventory(ShopMode.Trade, p, holder));
            SoundUtil.playClickShopSound(p);
        }
    }
}
