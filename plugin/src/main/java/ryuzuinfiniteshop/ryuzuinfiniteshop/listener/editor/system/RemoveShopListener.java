package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ConfirmRemoveGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

//ショップのNPCの装備を変更する
public class RemoveShopListener implements Listener {
    @EventHandler
    public void removeShop(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        if (slot != 5 * 9 + 4) return;

        //音を出す
        SoundUtil.playCautionSound(p);

        //確認画面を開く
        p.openInventory(new ConfirmRemoveGui(holder.getShop(), holder.getGui().getPage()).getInventory(holder.getMode()));
    }

    @EventHandler
    public void confirmRemoveShop(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ConfirmRemoveGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;
        if (event.getClickedInventory() == null) return;

        event.setCancelled(true);
        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 0 && slot != 8) return;

        if (slot == 0) {
            //音を出す
            SoundUtil.playClickShopSound(p);

            //エディター画面に戻る
            p.openInventory(shop.getEditor(holder.getGui().getPage()).getInventory(holder.getMode()));
        } else {
            //ショップを開いている人がいたら閉じる
            for (Player opener : Bukkit.getServer().getOnlinePlayers()) {
                ShopHolder listholder = ShopUtil.getShopHolder(opener.getOpenInventory().getTopInventory());
                if (listholder == null) continue;
                if (!(listholder.getShop().equals(shop))) continue;
                opener.closeInventory();
            }
            //ショップを削除
            shop.removeShop(p);

            //音を出し、メッセージを送信する
            SoundUtil.playSuccessSound(p);
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_SUCCESS_SHOP_DELETE.getMessage(shop.getDisplayNameOrElseShop()));

            //インベントリを閉じる
            Bukkit.getScheduler().runTaskLater(RyuZUInfiniteShop.getPlugin(), p::closeInventory, 1L);
        }
    }
}
