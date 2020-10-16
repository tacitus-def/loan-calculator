package pro.fossa.apps.timer;

public class ItemData {
	public long id = 0;
	public long delta;
	public long offset;
	public long time;
	public String title = null;
	public boolean checkpoint = false;
	
	public ItemData (long delta, long offset, boolean checkpoint, long time) {
		this.delta = delta;
		this.offset = offset;
		this.checkpoint = checkpoint;
		this.time = time;
	}

	public ItemData (long delta, long offset, String title, boolean checkpoint, long time) {
		this.delta = delta;
		this.title = title;
		this.offset = offset;
		this.checkpoint = checkpoint;
		this.time = time;
	}

	public ItemData (long id, long delta, long offset, String title, boolean checkpoint, long time) {
		this.id = id;
		this.delta = delta;
		this.title = title;
		this.offset = offset;
		this.checkpoint = checkpoint;
		this.time = time;
	}
}
