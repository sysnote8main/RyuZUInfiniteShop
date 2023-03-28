package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.effect.SoundUtil;

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
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (event.getClickedInventory() != null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        Inventory inv = event.getView().getTopInventory();
        ShopHolder.ShopMode mode = holder.getMode();
        Shop shop = holder.getShop();
        int page = holder.getGui().getPage();

        //ページ切り替え
        boolean fail = false;
        if (type.isLeftClick()) {
            if (shop.getPage(page - 1) == null)
                fail = true;
            else
                p.openInventory(shop.getPage(page - 1).getInventory(mode, p, holder.getBefore()));
        }
        if (type.isRightClick()) {
            if (shop.getPage(page + 1) == null) {
                fail = true;
                if (ShopUtil.isEditMode(event) && shop.ableCreateNewPage()) {
                    //取引を上書きし、取引として成立しないものは削除する
                    shop.checkTrades(inv);
                    if (shop.ableCreateNewPage()) {
                        shop.createNewPage();
                        p.openInventory(shop.getPage(page + 1).getInventory(mode, p, holder.getBefore()));
                        fail = false;
                    }
                }
            } else
                p.openInventory(shop.getPage(page + 1).getInventory(mode, p, holder.getBefore()));
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

    @EventHandler
    public void openBeforePage(InventoryCloseEvent event) {
        //インベントリがショップなのかチェック
        Inventory inv = event.getInventory();
        ShopHolder holder = ShopUtil.getShopHolder(inv);
        if (holder == null) return;

        //必要なデータを取得
        Player p = (Player) event.getPlayer();

        Bukkit.getScheduler().runTaskLater(RyuZUInfiniteShop.getPlugin(), () -> {
            if(p.getOpenInventory().getType().equals(InventoryType.CREATIVE) || p.getOpenInventory().getType().equals(InventoryType.CRAFTING)) SoundUtil.playCloseShopSound(p);
            if (ShopUtil.getShopHolder(p.getOpenInventory().getTopInventory()) != null) return;
            if (holder.getBefore() == null) return;
            p.openInventory(holder.getBefore().getGui().getInventory(holder.getMode(), holder.getBefore().getBefore()));
        }, 1L);
    }

    //ショップのステータスの更新
    @EventHandler
    public void updateStatus(InventoryDragEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event.getInventory());
        if (holder == null) return;
        if (!ShopUtil.isTradeMode(event.getInventory())) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Inventory inv = event.getView().getTopInventory();

        ((ShopTradeGui) holder.getGui()).setTradeStatus(p, inv);
    }
}
