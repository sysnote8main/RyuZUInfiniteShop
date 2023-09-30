package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.change;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system.SchedulerListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

//ショップのNPCの名前を変更する
public class ChangeNpcTypeListener implements Listener {
    @EventHandler
    public void changeNpcType(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;
        if (event.getClickedInventory() == null) return;
        if (ItemUtil.getWhitePanel().equals(event.getCurrentItem())) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 4 * 9 + 4) return;
        //チャット入力待機
        SchedulerListener.setSchedulers(p, shop.getID(), event.getClickedInventory(), (message) -> {
            // Successful process when completed
            // Reconstruct NPC
            try {
                shop.setNpcType(EntityType.valueOf(message.toUpperCase()).name());
                ShopUtil.reloadShop(shop);
            } catch (IllegalArgumentException e) {
                if (message.equalsIgnoreCase("Block"))
                    shop.setBlock();
                else {
                    p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_INVALID_ENTITY_TYPE.getMessage());
                    SoundUtil.playFailSound(p);
                    return;
                }
            }
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_ENTITY_TYPE_CHANGE.getMessage());
            SoundUtil.playSuccessSound(p);
        });
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_INPUT_ENTITY_ID.getMessage());
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_BLOCK_SHOP.getMessage());
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_CANCEL_INPUT.getMessage());

        SoundUtil.playClickShopSound(p);
        holder.getShop().setEditting(false);
    }
}
