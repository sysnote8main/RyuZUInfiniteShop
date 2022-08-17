package ryuzuinfiniteshop.ryuzuinfiniteshop.utils.schedulers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RyuZUMagicSyncTask extends RyuZUBaseTask {

    // コンストラクタ
    public RyuZUMagicSyncTask(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void run() {

        // 停止
        if (stop) {
            stopLoop();
            return;
        }

        // 開始
        if (count == 0) {
            count++;
            if (loopTask != null) {
                if (times == 1) bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, 0L);
                else bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, delay);
            }
            return;
        }

        // ループ
        loopTask.accept(this);

        // 終了
        if (count >= times || cancel) {
            if (loopEndTask != null) loopEndTask.run();
            if (afterTask != null) {
                afterTask.start();
            }
            stopLoop();
        }
    }

    @Override
    public void start() {

        if (loopStartTask != null) loopStartTask.run();

        // 遅延がない場合
        if (delay <= 0L) {
            for (int i = 0; i < times; i++) {
                Bukkit.getScheduler().runTask(plugin, () -> loopTask.accept(this));
            }
            if (loopEndTask != null) loopEndTask.run();
            if (afterTask != null) afterTask.start();
        } else {
            this.run();
        }
    }
}
