package pro.fossa.apps.timer;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

public class AppData {
	private long skip;
	private boolean active;
	private long paused;
	private int counter;
	private long resumed;
	private long started;
	private long last_offset;
	
	private final String APP_PREFERENCES = "timer";
	private final String APP_PREFERENCES_SKIP = "skip";
	private final String APP_PREFERENCES_PAUSED = "paused";
	private final String APP_PREFERENCES_ACTIVE = "active";
	private final String APP_PREFERENCES_RESUMED = "resumed";
	private final String APP_PREFERENCES_STARTED = "started";
	private final String APP_PREFERENCES_COUNTER = "counter";
	private final String APP_PREFERENCES_LOFFSET = "last_offset";

	private SharedPreferences settings;
	private Context context;
	
	private ItemListAdapter adapter;
	
	private TimerDBAdapter db;
	private final ItemComparator compare = new ItemComparator();
	private final ArrayList<ItemData> items = new ArrayList<ItemData> ();
	
	public AppData (Context context) {
		this.context = context;
		settings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		
        adapter = new ItemListAdapter(context, android.R.layout.simple_list_item_1, items);

        db = new TimerDBAdapter(context);
        db.open();
	}
	
	public ItemListAdapter getAdapter() {
		return adapter;
	}
	
	public boolean isActive () {
		return active;
	}
	
	public long getCurrentTime () {
		return (System.currentTimeMillis() - started - skip);
	}
	
    public long getElapsedTime () {
    	long time = (active ? System.currentTimeMillis() : paused) - started - skip;
    	return time;
    }
    
    public long getLastOffset () {
    	return last_offset;
    }
    
    public ArrayList<ItemData> getItems () {
    	return items;
    }
    
	public void touch () {
		counter ++;
		long time = getElapsedTime ();
		long delta = time - last_offset;
		last_offset = time;
    	add(delta, time, context.getResources().getString(R.string.log_checkpoint) + String.valueOf(counter), true, System.currentTimeMillis());
		save();
	}
	
	public void add (long delta, long elapsed, String str, boolean checkpoint, long time) {
    	ItemData item = new ItemData (delta, elapsed, str, checkpoint, time);
		long id = db.createMessage(item.delta, item.offset, item.title, item.checkpoint, item.time);
		if (id != -1) {
			item.id = id;
		}
    	items.add(item);
    	java.util.Collections.sort(items, compare);
    	adapter.notifyDataSetChanged();
	}
	
	public void reset () {
    	counter = 0;
    	skip = 0;
    	last_offset = 0;
    	started = paused = System.currentTimeMillis();
    	if (active) {
    		resumed = started;
    	}
    	else {
    		resumed = 0;
    	}
    	items.clear();
    	db.deleteAllMessages();
    	save();
    	add(0, 0, context.getResources().getString(R.string.log_reset), false, started);
	}
	
	public void save () {
    	Editor editor = settings.edit();
    	editor.putInt(APP_PREFERENCES_COUNTER, counter);
    	editor.putLong(APP_PREFERENCES_SKIP, skip);
    	editor.putLong(APP_PREFERENCES_PAUSED, paused);
    	editor.putLong(APP_PREFERENCES_RESUMED, resumed);
    	editor.putLong(APP_PREFERENCES_STARTED, started);
    	editor.putLong(APP_PREFERENCES_LOFFSET, last_offset);
    	editor.putBoolean(APP_PREFERENCES_ACTIVE, active);
    	
    	editor.apply();
	}
	
	public void update (long id, String title) {
		for (int i=0; i < items.size(); i++) {
			ItemData item = items.get(i); 
			if (item.id == id) {
				item.title = title;
				adapter.notifyDataSetChanged();
				db.updateMessage(item.id, item.delta, item.offset, item.title, item.checkpoint, item.time);
				break;
			}
		}
	}
	
	public void load () {
		if (settings.contains(APP_PREFERENCES_SKIP))
			skip = settings.getLong(APP_PREFERENCES_SKIP, 0);
		if (settings.contains(APP_PREFERENCES_ACTIVE))
			active = settings.getBoolean(APP_PREFERENCES_ACTIVE, false);
		if (settings.contains(APP_PREFERENCES_PAUSED))
			paused = settings.getLong(APP_PREFERENCES_PAUSED, 0);
		if (settings.contains(APP_PREFERENCES_COUNTER))
			counter = settings.getInt(APP_PREFERENCES_COUNTER, 0);
		if (settings.contains(APP_PREFERENCES_RESUMED))
			resumed = settings.getLong(APP_PREFERENCES_RESUMED, 0);
		if (settings.contains(APP_PREFERENCES_STARTED))
			started = settings.getLong(APP_PREFERENCES_STARTED, 0);
		if (settings.contains(APP_PREFERENCES_LOFFSET))
			last_offset = settings.getLong(APP_PREFERENCES_LOFFSET, 0);
	}
	
    public void close() {
        if (db != null) {
        	db.close();
        }
    }
    
    public void fill () {
		long d = System.currentTimeMillis();
		
		Cursor cursor = db.fetchAllMessages();
		if (cursor.moveToFirst()) {
			items.clear();
			while (cursor.isAfterLast() == false) {
				Long id      = cursor.getLong(cursor.getColumnIndex(TimerDBAdapter.KEY_ROWID));
				Long delta   = cursor.getLong(cursor.getColumnIndex(TimerDBAdapter.KEY_DELTA));
				long time    = cursor.getLong(cursor.getColumnIndex(TimerDBAdapter.KEY_TIME));
				Long offset  = cursor.getLong(cursor.getColumnIndex(TimerDBAdapter.KEY_OFFSET));
				String title = cursor.getString(cursor.getColumnIndex(TimerDBAdapter.KEY_TITLE));
				boolean checkpoint = false;
				if (cursor.getInt(cursor.getColumnIndex(TimerDBAdapter.KEY_CHECKPOINT)) != 0) {
					checkpoint = true;
				}
				
				items.add (new ItemData (id, delta, offset, title, checkpoint, time));
				cursor.moveToNext();
			}
			java.util.Collections.sort(items, compare);
			adapter.notifyDataSetChanged();

			load ();
			
			if (! active) {
				skip += d - paused;
				paused = d;
			}
		}
		else {
			SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();			
		}
    }
    
    public void control () {
    	long elapsed = getElapsedTime ();
    	int res;
    	
		active = ! active;
		
		if (active) {
			if (resumed == 0) {
				started = paused = resumed = System.currentTimeMillis();
				skip = 0;
			}
			else {
				resumed = System.currentTimeMillis();
			}
			
			skip += resumed - paused;
			res = R.string.log_started; 
		}
		else {
			paused = System.currentTimeMillis();
	    	res = R.string.log_paused;
		}
		
		save();
		add(0, elapsed, context.getResources().getString(res), false, active ? resumed : paused);
    }
}
