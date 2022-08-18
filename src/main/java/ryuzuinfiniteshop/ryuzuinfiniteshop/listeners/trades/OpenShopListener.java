package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopEditorMainPage;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

public class OpenShopListener implements Listener {
    //ショップを開く
    @EventHandler
    public void openShop(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player p = event.getPlayer();
        if (p.isSneaking()) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        String id = PersistentUtil.getNMSStringTag(entity, "Shop");
        if (id == null) return;
        Shop shop = ShopUtil.getShop(id);
        if (!shop.isAvailableShop(p)) return;

        Inventory inv = shop.getPage(1).getInventory(ShopHolder.ShopMode.Trade, p);
        p.openInventory(inv);
        event.setCancelled(true);
    }

    //ショップのページ切り替え
    @EventHandler
    public void changePage(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (event.getClickedInventory() != null) return;
        if (!ShopUtil.isTradeMode(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        Inventory inv = event.getView().getTopInventory();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();
        ShopHolder.ShopMode mode = shopholder.getShopMode();

        //ページ切り替え
        boolean fail = false;
        if (type.isLeftClick()) {
            if (shop.getPage(shopholder.getPage() - 1) == null)
                fail = true;
            else
                p.openInventory(shop.getPage(shopholder.getPage() - 1).getInventory(mode, p));
        }
        if (type.isRightClick()) {
            if (shop.getPage(shopholder.getPage() + 1) == null) {
                fail = true;
                if (gui instanceof ShopEditorMainPage && shop.ableCreateNewPage()) {
                    shop.createTradeNewPage();
                    p.openInventory(shop.getPage(shopholder.getPage() + 1).getInventory(mode, p));
                    fail = false;
                }
            } else
                p.openInventory(shop.getPage(shopholder.getPage() + 1).getInventory(mode, p));
        }
        if (fail) {
            SoundUtil.playFailSound(p);
        } else {
            SoundUtil.playClickShopSound(p);
            ((ShopTradeGui) gui).setTradeStatus(p, inv);
        }

        //イベントキャンセル
        event.setCancelled(true);
    }

    //ショップのステータスの更新
    @EventHandler
    public void updateStatus(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (!(gui instanceof ShopTradeGui)) return;
        if (!ShopUtil.isTradeMode(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();

        //ショップのステータスを更新
        ((ShopTradeGui) gui).setTradeStatus(p, event.getClickedInventory());

        //イベントをキャンセル
        event.setCancelled(true);
    }
}
