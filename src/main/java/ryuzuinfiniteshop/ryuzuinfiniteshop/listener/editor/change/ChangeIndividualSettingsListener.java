package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.change;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;

//NPCのエンティティタイプごとの固有のNBTを変更
public class ChangeIndividualSettingsListener implements Listener {

    //年齢の変更
    @EventHandler
    public void changeSettings(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;
        if(ItemUtil.getNamedItem(Material.WHITE_STAINED_GLASS_PANE, "").equals(event.getCurrentItem())) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().containsKey(slot)) return;
        changeAgeLook(holder, event.getSlot());
        changePowered(holder, event.getSlot());
        changeProfession(holder, event.getSlot());
        changeBiome(holder, event.getSlot());
        changeLevel(holder, event.getSlot());
        changeVisible(holder, event.getSlot());
        changeParrotColor(holder, event.getSlot());
        changeDyeColor(holder, event.getSlot());
        changeOptionalInfo(holder, event.getSlot());
        changeHorseColor(holder, event.getSlot());
        changeHorseStyle(holder, event.getSlot());

        //音を出す
        SoundUtil.playClickShopSound(p);

        //GUIのアイテムを更新
        editor.setSettings(event.getView().getTopInventory());
    }

    //年齢の変更
    public void changeAgeLook(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.Age)) return;
        if (!(holder.getShop() instanceof AgeableShop)) return;

        //年齢の変更
        ((AgeableShop) holder.getShop()).setAgeLook(!((AgeableShop) holder.getShop()).isAdult());
    }

    //クリーパーを帯電させるか変更
    public void changePowered(ShopHolder holder, int slot) {
        //必要なデータを取得
        if (!((ShopEditorGui) holder.getGui()).getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.Power))
            return;
        if (!(holder.getShop() instanceof PoweredableShop)) return;

        //帯電させるか変更
        ((PoweredableShop) holder.getShop()).setPowered(!((PoweredableShop) holder.getShop()).isPowered());
    }

    //村人の職業を変更
    public void changeProfession(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.Profession)) return;
        if (!(holder.getShop() instanceof VillagerableShop)) return;

        //職業を変更
        ((VillagerableShop) holder.getShop()).setProfession(((VillagerableShop) holder.getShop()).getNextProfession());
    }

    //村人のバイオームを変更
    public void changeBiome(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.Biome)) return;
        if (!(holder.getShop() instanceof VillagerableShop)) return;

        //バイオームを変更
        ((VillagerableShop) holder.getShop()).setBiome(((VillagerableShop) holder.getShop()).getNextBiome());
    }

    //村人のレベル
    public void changeLevel(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.Level)) return;
        if (!(holder.getShop() instanceof VillagerableShop)) return;

        //レベルを変更
        int level = ((VillagerableShop) holder.getShop()).getLevel();
        ((VillagerableShop) holder.getShop()).setLevel(level >= 5 ? 1 : level + 1);
    }

    //透明か変更
    public void changeVisible(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.Visible)) return;
        if (!(holder.getShop().getNpc() instanceof LivingEntity)) return;

        //透明か変更
        holder.getShop().changeInvisible();
    }

    //オウムの色を変更
    public void changeParrotColor(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.ParrotColor)) return;
        if (!(holder.getShop() instanceof ParrotShop)) return;

        //色を変更
        ((ParrotShop) holder.getShop()).setColor(((ParrotShop) holder.getShop()).getNextColor());
    }

    //色を変更
    public void changeDyeColor(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.DyeColor)) return;
        if (!(holder.getShop() instanceof DyeableShop)) return;

        //色を変更
        ((DyeableShop) holder.getShop()).setColor(((DyeableShop) holder.getShop()).getNextColor());
    }

    //追加情報の変更
    public void changeOptionalInfo(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.OptionalInfo)) return;
        if (!(holder.getShop() instanceof DyeableShop)) return;

        //色を変更
        ((DyeableShop) holder.getShop()).setOptionalInfo(!((DyeableShop) holder.getShop()).getOptionalInfo());
    }

    //馬の色を変更
    public void changeHorseColor(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.HorseColor)) return;
        if (!(holder.getShop() instanceof HorseShop)) return;

        //色を変更
        ((HorseShop) holder.getShop()).setColor(((HorseShop) holder.getShop()).getNextColor());
    }

    //馬の模様を変更
    public void changeHorseStyle(ShopHolder holder, int slot) {
        //必要なデータを取得
        ShopEditorGui editor = (ShopEditorGui) holder.getGui();

        if (!editor.getSettingsMap().get(slot).equals(ShopEditorGui.ShopSettings.HorseStyle)) return;
        if (!(holder.getShop() instanceof HorseShop)) return;

        //色を変更
        ((HorseShop) holder.getShop()).setStyle(((HorseShop) holder.getShop()).getNextStyle());
    }
}
