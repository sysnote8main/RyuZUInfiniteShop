package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopEditorMainPage;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.AgeableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.PoweredableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.VillagerableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

//NPCのエンティティタイプごとの固有のNBTを変更
public class ChangeIndividualSettingsListener implements Listener {

    //年齢の変更
    @EventHandler
    public void changeSettings(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorMainPage)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();
        int slot = event.getSlot();
        ShopEditorMainPage editor = (ShopEditorMainPage) holder.getGui();

        if (!editor.getSettingsMap().containsKey(slot)) return;
        changeAgeLook(holder, event.getSlot());
        changePowered(holder, event.getSlot());
        changeProfession(holder, event.getSlot());
        changeBiome(holder, event.getSlot());

        //音を出す
        SoundUtil.playClickShopSound(p);

        //GUIのアイテムを更新
        editor.setSettings(event.getView().getTopInventory());
    }

    //年齢の変更
    public void changeAgeLook(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorMainPage editor = (ShopEditorMainPage) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorMainPage.ShopSettings.Age)) return;
        if (!(holder.getShop() instanceof AgeableShop)) return;

        //年齢の変更
        ((AgeableShop) holder.getShop()).setAgeLook(!((AgeableShop) holder.getShop()).isAdult());
    }

    //クリーパーを帯電させるか変更
    public void changePowered(ShopHolder holder, int slot) {
        //必要なデータを取得
        if (!((ShopEditorMainPage) holder.getGui()).getSettingsMap().get(slot).equals(ShopEditorMainPage.ShopSettings.Power))
            return;
        if (!(holder.getShop() instanceof PoweredableShop)) return;

        //帯電させるか変更
        ((PoweredableShop) holder.getShop()).setPowered(!((PoweredableShop) holder.getShop()).isPowered());
    }

    //村人の職業を変更
    public void changeProfession(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorMainPage editor = (ShopEditorMainPage) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorMainPage.ShopSettings.Profession)) return;
        if (!(holder.getShop() instanceof VillagerableShop)) return;

        //職業を変更
        ((VillagerableShop) holder.getShop()).setProfession(((VillagerableShop) holder.getShop()).getNextProfession());
    }

    //村人のバイオームを変更
    public void changeBiome(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorMainPage editor = (ShopEditorMainPage) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorMainPage.ShopSettings.Biome)) return;
        if (!(holder.getShop() instanceof VillagerableShop)) return;

        //バイオームを変更
        ((VillagerableShop) holder.getShop()).setBiome(((VillagerableShop) holder.getShop()).getNextBiome());
    }
}
