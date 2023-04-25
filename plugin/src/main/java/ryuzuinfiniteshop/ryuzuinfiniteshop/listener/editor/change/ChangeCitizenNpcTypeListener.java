package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.change;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ScheduleEntityData;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ScheduleStringData;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.CitizensHandler;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

//ショップのNPCの名前を変更する
public class ChangeCitizenNpcTypeListener implements Listener {
    private static final HashMap<UUID, ScheduleEntityData> schedulers = new HashMap<>();

    public static void setSchedulers(Player p, String id, Inventory inv, Consumer<Entity> successProcess) {
        schedulers.put(p.getUniqueId(), new ScheduleEntityData(System.currentTimeMillis(), id, inv, successProcess));
        Bukkit.getScheduler().runTaskLater(RyuZUInfiniteShop.getPlugin(), p::closeInventory, 1L);
    }

    @EventHandler
    public void changeMythicMobType(InventoryClickEvent event) {
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
        if (slot != 2 * 9 + 7) return;
        //チャット入力待機
        setSchedulers(p, shop.getID(), event.getClickedInventory(), (entity) -> {
            //成功時の処理
            //NPCを再構築する
            if (!CitizensHandler.isNPC(entity)) {
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "そのEntityはCitizenのNPCではありません");
                SoundUtil.playFailSound(p);
                return;
            }
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "登録が完了しました");
            SoundUtil.playSuccessSound(p);
            shop.setCitizen(entity);
        });
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "登録したいCitizenのNPCを右クリックしてください");
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_ENTER_CANCEL.getMessage());

        SoundUtil.playClickShopSound(p);
        holder.getShop().setEditting(false);
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player p = event.getPlayer();
        if (!schedulers.containsKey(p.getUniqueId())) return;
        if(FileUtil.isSaveBlock(p)) return;
        ScheduleEntityData data = schedulers.get(p.getUniqueId());
        if((System.currentTimeMillis() - data.getTime()) / 1000d > 20) return;
        Shop shop = ShopUtil.getShop(data.getId());
        if(shop == null) return;
        event.setCancelled(true);
        data.getSuccessProcess().accept(entity);
        schedulers.remove(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void change(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (!schedulers.containsKey(p.getUniqueId())) return;
        if (FileUtil.isSaveBlock(p)) return;
        ScheduleEntityData data = schedulers.get(p.getUniqueId());
        if ((System.currentTimeMillis() - schedulers.get(p.getUniqueId()).getTime()) / 1000d > 20 || event.getMessage().equalsIgnoreCase("Cancel"))  {
            event.setCancelled(true);
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_ENTER_CANCELLED.getMessage());
            SoundUtil.playClickShopSound(p);
            p.openInventory(data.getInventory());
        }
    }
}
