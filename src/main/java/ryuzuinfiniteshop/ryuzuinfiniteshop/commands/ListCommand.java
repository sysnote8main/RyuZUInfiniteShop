package ryuzuinfiniteshop.ryuzuinfiniteshop.commands;

import com.github.ryuzu.ryuzucommandsgenerator.CommandsGenerator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

public class ListCommand {
    public static void registerCommand() {
        CommandsGenerator.registerCommand("ris.list",
                data -> {
                    Player p = (Player) data.getSender();
                    p.openInventory(new ShopListGui(null , 1).getInventory(ShopHolder.ShopMode.Edit));
                    SoundUtil.playClickShopSound(p);
                },
                "ris.op",
                data -> true,
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return data.getArgs().length == 1;
                }
        );
    }
}
