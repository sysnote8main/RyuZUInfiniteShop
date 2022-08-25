package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

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
        shop.checkTrades(inv);

        //1tick送らせてエディターのメインページに戻る
        Bukkit.getScheduler().runTaskLater(RyuZUInfiniteShop.getPlugin(), () -> {
            if (ShopUtil.getShopHolder(p.getOpenInventory().getTopInventory()) == null) {
                int page = holder.getGui().getPage() / 18;
                if (holder.getGui().getPage() % 18 != 0) page++;
                p.openInventory(shop.getEditor(page).getInventory(holder.getShopMode()));

                //音を出す
                SoundUtil.playCloseShopSound(p);
            }
        }, 1L);
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
        //イベントキャンセル
        event.setCancelled(true);

        Player p = (Player) event.getWhoClicked();
        ItemStack item = holder.getShop().convertTrade(event.getClickedInventory(), slot);
        //トレードをアイテム化する
        if (!event.isShiftClick()) return;
        if(!((ShopTradeGui) holder.getGui()).isConvertSlot(slot)) return;
        if (item == null) {
            SoundUtil.playFailSound(p);
            return;
        }
        if (ItemUtil.ableGive(p.getInventory(), item)) {
            p.getInventory().addItem(item);
            SoundUtil.playSuccessSound(p);
        } else
            SoundUtil.playFailSound(p);

        //音を出す
        SoundUtil.playSuccessSound(p);
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
            p.openInventory(shop.getPage(page).getInventory(holder.getShopMode()));
        } else
            p.openInventory(shop.getPage(editormainpage.getTradePageNumber(slot)).getInventory(holder.getShopMode()));

        //音を出す
        SoundUtil.playClickShopSound(p);
    }
}
