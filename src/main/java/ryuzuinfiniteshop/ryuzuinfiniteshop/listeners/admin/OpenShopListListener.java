package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.admin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.effect.SoundUtil;

public class OpenShopListListener implements Listener {
    //ショップのページ切り替え
    @EventHandler
    public void changePage(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopListGui)) return;
        if (event.getClickedInventory() != null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        Inventory inv = event.getView().getTopInventory();
        ShopHolder.ShopMode mode = holder.getShopMode();
        Shop shop = holder.getShop();
        int page = holder.getGui().getPage();
        int maxpage = ShopUtil.getShops().size() / 54 + 1;

        //ページ切り替え
        boolean fail = false;
        if (type.isLeftClick()) {
            if (page - 1 == 0)
                fail = true;
            else
                p.openInventory(new ShopListGui(shop , page - 1).getInventory(mode));
        }
        if (type.isRightClick()) {
            if (page == maxpage) {
                fail = true;
            } else
                p.openInventory(new ShopListGui(shop , page + 1).getInventory(mode));
        }
        if (fail) {
            SoundUtil.playFailSound(p);
        } else {
            SoundUtil.playClickShopSound(p);
        }

        if (ShopUtil.isTradeMode(event)) ((ShopTradeGui) holder.getGui()).setTradeStatus(p, inv);

        //イベントキャンセル
        event.setCancelled(true);
    }

    //ショップのページ切り替え
    @EventHandler
    public void openEditor(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopListGui)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        ItemStack item = event.getCurrentItem();
        if(item == null) return;
        Player p = (Player) event.getWhoClicked();
        ShopHolder.ShopMode mode = holder.getShopMode();
        Shop shop = ShopUtil.getShop(PersistentUtil.getNMSStringTag(item, "Shop"));

        if(event.isShiftClick()) {
            //ショップにTPする
            p.closeInventory();
            p.teleport(shop.getLocation());
            SoundUtil.playSuccessSound(p);
            p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + shop.getDisplayName() + "にテレポートしました");
        } else {
            //エディターを開く
            p.openInventory(shop.getEditor(1).getInventory(mode));
            SoundUtil.playClickShopSound(p);
        }
    }
}
