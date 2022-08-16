package ryuzuinfiniteshop.ryuzuinfiniteshop.commands;

import com.github.ryuzu.ryuzucommandsgenerator.CommandsGenerator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.TradeListener;

public class SpawnCommand {
    public static void registerCommand() {
        CommandsGenerator.registerCommand("ris",
                data -> {
                    if (data.getSender().hasPermission("ris.op")) {
                        data.sendMessage(ChatColor.RED + "/" + data.getLabel() + " [spawn/add/remove]");
                    } else {
                        data.sendMessage(ChatColor.RED + "/" + data.getLabel() + " []");
                    }
                },
                "ris.player",
                data -> data.getArgs().length == 0
        );

        CommandsGenerator.registerCommand("ris.spawn",
                data -> {
                    TradeListener.createNewShop((Player) data.getSender());
                    data.sendMessage(ChatColor.GREEN + "ショップを設置しました");
                },
                "ris.op",
                data -> true,
                data -> {
                    if (!(data.getSender() instanceof Player)) {
                        data.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみ実行できます");
                        return false;
                    }
                    return true;
                }
        );
    }
}
