package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ModeHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopListHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;
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

        //イベントキャンセル
        event.setCancelled(true);

        //必要なデータを取得
        ShopListHolder shopListHolder = (ShopListHolder) holder;
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        ShopMode mode = holder.getMode();
        int page = shopListHolder.getGui().getPage();

        //ページ切り替え
        boolean fail = false;
        if (type.isLeftClick()) {
            if (page - 1 == 0)
                fail = true;
            else
                p.openInventory(new ShopListGui(page - 1, shopListHolder.getName()).getInventory(mode));
        }
        if (type.isRightClick()) {
            if (page == shopListHolder.getMaxPage()) {
                fail = true;
            } else
                p.openInventory(new ShopListGui(page + 1, shopListHolder.getName()).getInventory(mode));
        }
        if (fail) {
            SoundUtil.playFailSound(p);
        } else {
            SoundUtil.playClickShopSound(p);
        }
    }

    //ショップのページ切り替え
    @EventHandler
    public void openShopList(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ModeHolder holder = ShopUtil.getModeHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopListGui)) return;
        if (event.getClickedInventory() == null) return;

        //イベントキャンセル
        event.setCancelled(true);

        //必要なデータを取得
        ItemStack item = event.getCurrentItem();
        if (ItemUtil.isAir(item)) return;
        Player p = (Player) event.getWhoClicked();
        ShopMode mode = holder.getMode();
        Shop shop = ShopUtil.getShop(NBTUtil.getNMSStringTag(item, "Shop"));

        if (holder.getMode().equals(ShopMode.Edit)) {
            if (event.isShiftClick()) {
                //編集画面を開く
                ShopUtil.closeAllShopTradeInventory(shop);
                p.openInventory(shop.getEditor(1).getInventory(mode, holder));
                SoundUtil.playClickShopSound(p);
                shop.setEditting(true);
            } else {
                //取引画面を開く
                p.openInventory(shop.getPage(1).getInventory(mode, p, holder));
                SoundUtil.playClickShopSound(p);
                shop.setEditting(true);
            }
        } else {
            p.openInventory(shop.getPage(1).getInventory(mode, p, holder));
            SoundUtil.playClickShopSound(p);
        }
    }
}
