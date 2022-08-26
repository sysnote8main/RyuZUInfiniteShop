package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ConfirmRemoveGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

//ショップのNPCの装備を変更する
public class RemoveShopListener implements Listener {
    @EventHandler
    public void removeShop(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        if (slot != 3 * 9 + 6) return;

        //音を出す
        SoundUtil.playCautionSound(p);

        //確認画面を開く
        p.openInventory(new ConfirmRemoveGui(holder.getShop(), holder.getGui().getPage()).getInventory(holder.getShopMode()));
    }

    @EventHandler
    public void confirmRemoveShop(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ConfirmRemoveGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 0 && slot != 8) return;

        if(slot == 0) {
            //音を出す
            SoundUtil.playClickShopSound(p);

            //エディター画面に戻る
            p.openInventory(shop.getEditor(holder.getGui().getPage()).getInventory(holder.getShopMode()));
        } else {
            //ショップを削除
            shop.removeShop();
            for(Player opener : Bukkit.getServer().getOnlinePlayers()) {
                ShopHolder listholder = ShopUtil.getShopHolder(opener.getOpenInventory().getTopInventory());
                if (listholder == null) continue;
                if (!(listholder.getGui() instanceof ShopListGui)) continue;
                opener.openInventory(new ShopListGui(null , listholder.getGui().getPage()).getInventory(ShopHolder.ShopMode.Edit));
            }

            //音を出し、メッセージを送信する
            SoundUtil.playSuccessSound(p);
            p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + shop.getDisplayName() + "を削除しました");

            //インベントリを閉じる
            p.closeInventory();
        }
    }
}
