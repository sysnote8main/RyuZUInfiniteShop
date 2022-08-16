package ryuzuinfiniteshop.ryuzuinfiniteshop.utils.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;

import java.util.function.Consumer;

public abstract class RyuZUBaseTask implements Runnable {
	// スケジュールの実行に必要なプラグインインスタンス
	protected JavaPlugin plugin;

	// スケジュールに割り振られたタスクID
	protected BukkitTask bukkitTask;

	// 回数
	protected long times = 0;

	// 遅延
	protected long delay = 0L;

	// 残り時間
	protected int count = 0;

	// キャンセルフラグ
	protected boolean cancel = false;

	// 停止フラグ
	protected boolean stop = false;

	// 処理
	protected Consumer<RyuZUBaseTask> loopTask;
	protected Runnable loopEndTask;
	protected Runnable loopStartTask;
	protected RyuZUBaseTask afterTask;
	protected RyuZUBaseTask root;

	public RyuZUBaseTask(JavaPlugin plugin){
		this.plugin = plugin;
	}

	public RyuZUBaseTask(){
		this.plugin = RyuZUInfiniteShop.getPlugin();
	}

	@Override
	public abstract void run();

	public abstract void start();

	public void cancel(){
		this.cancel = true;
	}

	public void stop(){
		this.stop = true;
	}

	public RyuZUBaseTask loop(long times, long delay, Consumer<RyuZUBaseTask> loopTask){
		this.times = times;
		this.delay = delay;
		this.loopTask = loopTask;
		return this;
	}

	public RyuZUBaseTask whileTask(long delay, Consumer<RyuZUBaseTask> loopTask){
		this.times = Long.MAX_VALUE;
		this.delay = delay;
		this.loopTask = loopTask;
		return this;
	}

	public RyuZUBaseTask waitTick(long delay){
		this.times = 1L;
		this.delay = delay;
		this.loopTask = (task) -> {};
		return this;
	}

	public RyuZUBaseTask loopEnd(Runnable loopEndTask){
		this.loopEndTask = loopEndTask;
		return this;
	}

	public RyuZUBaseTask loopStart(Runnable loopStartTask){
		this.loopStartTask = loopStartTask;
		return this;
	}

	public RyuZUBaseTask single(long delay, Consumer<RyuZUBaseTask> loopTask){
		this.times = 1L;
		this.delay = delay;
		this.loopTask = loopTask;
		return this;
	}

	public RyuZUBaseTask after(RyuZUBaseTask afterTask){
		this.afterTask = afterTask;
		if(this.root == null) this.root = this;
		this.afterTask.root = this.root;
		return this.afterTask;
	}

	public RyuZUBaseTask root(){
		if(root == null) return this;
		return root;
	}

	public void stopLoop(){
		if(bukkitTask != null) bukkitTask.cancel();
	}

	public int getCount(){ return count; }

	public void setCount(int count){ this.count = count; }

	public boolean isCancel() { return cancel; }
}
