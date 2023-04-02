package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.edit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;

public class EditTradePageListener implements Listener {
    //ショップのラインナップを変更
    @EventHandler
    public void onEdit(InventoryCloseEvent event) {
        //インベントリがショップなのかチェック
        Inventory inv = event.getInventory();
        ShopHolder holder = ShopUtil.getShopHolder(inv);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!ShopUtil.isEditMode(inv)) return;

        //必要なデータを取得
        Player p = (Player) event.getPlayer();
        Shop shop = holder.getShop();

        //取引を上書きし、取引として成立しないものは削除する
        boolean warn = shop.checkTrades(inv);
        if(warn) p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "重複している取引がありました");
    }

    //ディスプレイをクリックしたときイベントをキャンセルする
    @EventHandler
    public void cancelClickDisplay(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;
        int slot = event.getSlot();
        if (!((ShopTradeGui) holder.getGui()).isDisplaySlot(slot)) return;

        Player p = (Player) event.getWhoClicked();
        ItemStack item = holder.getShop().convertTrade(event.getClickedInventory(), slot);
        //トレードをアイテム化する
        if (!event.isShiftClick()) return;
        if (!((ShopTradeGui) holder.getGui()).isConvertSlot(slot)) return;
        if (ItemUtil.isAir(item)) {
            SoundUtil.playFailSound(p);
            return;
        }
        if (ItemUtil.ableGive(p.getInventory(), item)) {
            p.getInventory().addItem(item);
            SoundUtil.playSuccessSound(p);
        } else
            SoundUtil.playFailSound(p);

        //イベントキャンセル
        event.setCancelled(true);
    }

    //ショップの取引編集ページを開く
    @EventHandler
    public void openTradePage(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ShopEditorGui editormainpage = (ShopEditorGui) holder.getGui();
        Shop shop = holder.getShop();
        int slot = event.getSlot();

        int lastslot = editormainpage.getTradeLastSlotNumber();
        int newslot = editormainpage.getTradeNewSlotNumber();
        int page = editormainpage.getTradePageRawNumber(slot);

        //存在するページなのかチェック
        if (slot > lastslot && slot != newslot) return;

        //取引編集ページを開く
        if (slot == newslot) {
            shop.createTradeNewPage();
            p.openInventory(shop.getPage(page).getInventory(holder.getMode() , holder));
        } else
            p.openInventory(shop.getPage(editormainpage.getTradePageNumber(slot)).getInventory(holder.getMode(), holder));

        //音を出す
        SoundUtil.playClickShopSound(p);
    }

    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!ShopUtil.isTradeMode(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        int slot = event.getSlot();
        ShopTrade trade = ((ShopTradeGui) holder.getGui()).getTradeFromSlot(slot);

        if (trade == null) return;

        //取引
        int times = 1;
        switch (type) {
            case SHIFT_RIGHT:
            case SHIFT_LEFT:
                times = 10;
                break;
            case MIDDLE:
                times = 64;
                break;
        }
        trade.trade(p, times);

        //イベントキャンセル
        event.setCancelled(true);
    }

    //ディスプレイをクリックしたときイベントをキャンセルする
    @EventHandler
    public void changeTradeLimit(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;
        int slot = event.getSlot();
        if (!((ShopTradeGui) holder.getGui()).isDisplaySlot(slot)) return;
        //イベントキャンセル
        event.setCancelled(true);

        Player p = (Player) event.getWhoClicked();
        ShopTrade trade = holder.getShop().getTrade(event.getClickedInventory(), slot);
        //トレードをアイテム化する
        if (event.isShiftClick()) return;
        if (!((ShopTradeGui) holder.getGui()).isConvertSlot(slot)) return;
        if (!(trade.getLimit() == 0 && event.isLeftClick())) {
            event.setCurrentItem(trade.changeLimit(event.isLeftClick() ? -1 : 1));
            SoundUtil.playClickShopSound(p);
        } else
            SoundUtil.playFailSound(p);
    }
}
