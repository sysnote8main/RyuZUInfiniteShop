package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ModeHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.PageableHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;

public class ShopListListener implements Listener {
    //ショップのページ切り替え
    @EventHandler
    public void changePage(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ModeHolder holder = ShopUtil.getModeHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopListGui)) return;
        if (event.getClickedInventory() != null) return;

        //必要なデータを取得
        PageableHolder pageableHolder = (PageableHolder) holder;
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        Inventory inv = event.getView().getTopInventory();
        ShopMode mode = holder.getMode();
        int page = pageableHolder.getGui().getPage();

        //ページ切り替え
        boolean fail = false;
        if (type.isLeftClick()) {
            if (page - 1 == 0)
                fail = true;
            else
                p.openInventory(new ShopListGui(page - 1).getInventory(mode));
        }
        if (type.isRightClick()) {
            if (page == pageableHolder.getMaxPage()) {
                fail = true;
            } else
                p.openInventory(new ShopListGui(page + 1).getInventory(mode));
        }
        if (fail) {
            SoundUtil.playFailSound(p);
        } else {
            SoundUtil.playClickShopSound(p);
        }

        //イベントキャンセル
        event.setCancelled(true);
    }

    //ショップのページ切り替え
    @EventHandler
    public void openEditor(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ModeHolder holder = ShopUtil.getModeHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopListGui)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        ItemStack item = event.getCurrentItem();
        if(item == null) return;
        Player p = (Player) event.getWhoClicked();
        ShopMode mode = holder.getMode();
        Shop shop = ShopUtil.getShop(PersistentUtil.getNMSStringTag(item, "Shop"));

        if(event.isShiftClick()) {
            //ショップにTPする
            p.closeInventory();
            p.teleport(shop.getLocation());
            SoundUtil.playSuccessSound(p);
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + shop.getDisplayName() + "にテレポートしました");
        } else {
            //エディターを開く
            ShopUtil.closeAllShopTradeInventory(shop);
            p.openInventory(shop.getEditor(1).getInventory(mode, holder));
            SoundUtil.playClickShopSound(p);
            shop.setEditting(true);
        }
    }
}
