package pro.fossa.apps.timer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.support.v7.widget.AppCompatImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OnClickListener {
	protected AppCompatImageButton btn_control, btn_reset, btn_new;
	protected ViewSwitcher switcher;
	protected ListView list_items;
	protected EditText detail_title;
	
	protected TextView display, millis, global;
	protected boolean showDetail = false;
	protected ItemData detail_item = null;
	
	private Handler handler;
	private Runnable updater;

	private AppData prefs;
	private SharedPreferences settings;
	
	private boolean prefs_use_hw_btn = false;
	private boolean prefs_vibrate = false;
	private boolean prefs_night_mode = false;

    private int SETTINGS_ACTION = 1;

	private final int REQUEST_PERMISSION_WRITE_STORAGE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        set_prefs();
        setActivityStyle();
        super.onCreate(savedInstanceState);

        prefs = new AppData (this);

        setContentView(R.layout.activity_main);
        
        switcher    = (ViewSwitcher) findViewById(R.id.switcher);
        list_items  = (ListView) findViewById(R.id.list_items);
        btn_new     = (AppCompatImageButton) findViewById(R.id.btn_new);
        btn_reset   = (AppCompatImageButton) findViewById(R.id.btn_reset);
        btn_control = (AppCompatImageButton) findViewById(R.id.btn_control);
        global		= (TextView) findViewById(R.id.global);
        display		= (TextView) findViewById(R.id.display);
        millis		= (TextView) findViewById(R.id.millis);
        
        final TextView detail_offset  = (TextView) findViewById(R.id.detail_offset);
        final TextView detail_delta   = (TextView) findViewById(R.id.detail_delta);
        final TextView detail_created = (TextView) findViewById(R.id.detail_created);
        detail_title  = (EditText) findViewById(R.id.detail_title);
        
        btn_new.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_control.setOnClickListener(this);
        
        list_items = (ListView) findViewById(R.id.list_items);
        list_items.setAdapter(prefs.getAdapter());
        
    	final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    	final SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    	
        list_items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
            	detail_item = (ItemData) parent.getItemAtPosition(i);
            	Date dt = new Date(detail_item.time);
            	showDetail = true;
                detail_offset.setText(Helpers.getTimeText(detail_item.offset));
                detail_delta.setText("+" + Helpers.getTimeText(detail_item.delta));
                String time_text = getResources().getString(R.string.detail_created, df.format(dt), tf.format(dt));
                detail_created.setText(time_text);
                detail_title.setText(detail_item.title);
                switcher.showNext();
                
                return false;
            }
        });
        
        handler = new Handler();
		updater = new Runnable () {

			@Override
			public void run() {
				
				if (prefs.isActive()) {
					displayTime(prefs.getCurrentTime());
					handler.postDelayed(this, 100);
				}
				
			}
		};

		keepScreenOn();
    }

    @Override
    protected void onPause() {
    	super.onPause();

    	prefs.save();
    }

    protected void displayTime (long time) {
    	String gt = Helpers.getTimeText(time);
    	String lt = Helpers.getTimeText(time - prefs.getLastOffset());
    	String[] t = lt.split("\\.");
		
    	global.setText(gt);
		display.setText(t[0]);
		millis.setText("." + t[1]);
    }
    
    @Override
    protected void onResume() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        set_prefs();
        setActivityStyle();
    	super.onResume();


    	prefs.fill();
    	
		displayTime(prefs.getElapsedTime ());
		timer_interface ();

		keepScreenOn();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void timer_interface () {
		if (prefs.isActive()) {
			handler.post(updater);
            if (prefs_night_mode)
                btn_control.setImageResource(R.drawable.ic_pause_night);
            else
			    btn_control.setImageResource(R.drawable.ic_pause);
		}
		else {
            if (prefs_night_mode)
                btn_control.setImageResource(R.drawable.ic_play_night);
            else
                btn_control.setImageResource(R.drawable.ic_play);
		}
    }
    
    protected void action_control () {
    	prefs.control();
		
		displayTime(prefs.getElapsedTime());
		timer_interface ();
    }

    protected void setActivityStyle() {
        if (prefs_night_mode) {
            setTheme(R.style.AppThemeNight);
        }
        else {
            setTheme(R.style.AppTheme);
        }
    }

    protected void action_reset () {
    	Alert.confirm(this, R.string.dlg_reset_title, R.string.dlg_reset_message, 
            R.string.dlg_btn_yes, R.string.dlg_btn_no, 
            new Runnable() {
    		
    			public void run() {
	            	prefs.reset();
	        		displayTime(0);
	            }
            }, 
            null);
    }
    
    protected void action_new () {
    	prefs.touch();
    	
		if (! prefs.isActive()) {
			Toast toast = Toast.makeText(getApplicationContext(), 
					getResources().getString(R.string.toast_paused),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_control:
			action_control ();
			vibrate();
			break;
		case R.id.btn_reset:
			action_reset ();
			vibrate();
			break;
		case R.id.btn_new:
			action_new ();
			vibrate();
			break;
		}
	}
	
	protected void saveDetail() {
		prefs.update (detail_item.id, detail_title.getText().toString());
		
		Toast toast = Toast.makeText(getApplicationContext(), 
				getResources().getString(R.string.toast_item_saved),
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (prefs_use_hw_btn) {
				action_reset ();
				vibrate();
				
				return true;
			}
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (prefs_use_hw_btn) {
				action_new ();
				vibrate();
				return true;
			}
		case KeyEvent.KEYCODE_BACK:
			if (showDetail) {
				saveDetail();
				switcher.showPrevious();
				showDetail = false;
			
				vibrate();
				return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_export:
			actionExport();
	      	return true;
		case R.id.action_share_to:
			actionShareTo();
			return true;
		case R.id.action_copy:
			actionCopy();
   			return true;
		case R.id.action_settings:
			actionSettings();
            return true;
		}
		
		return super.onOptionsItemSelected(item);
    }
	
	private void actionSettings() {
		Intent intent = new Intent();
        intent.setClass(this, Preferences.class);
        startActivityForResult(intent, SETTINGS_ACTION);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				exportCsv();
			}
			else {
				Toast toast = Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.toast_permission_denied),
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}

		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void exportCsv() {
		File baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		String csvDir = "StopWatch";
		String fullCsvDir = baseDir.toString() + File.separator + csvDir;
		// create a File object for the parent directory
		File csvDirObj = new File(baseDir, csvDir);
		// have the object build the directory structure, if needed.
		if (!csvDirObj.exists()) {
			if (!csvDirObj.mkdirs()) {
				Toast toast = Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.toast_dir_create_error, csvDirObj.toString()),
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		}
		final CSV csv = new CSV(MainActivity.this, prefs.getItems());
		try {
			Date date = new Date();
			final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
			String fileName = fullCsvDir + File.separator + settings.getString("PREFS_FILENAME_PREFIX", "stopwatch_") + df.format(date) + ".csv";
			csv.export(fileName);

			Toast toast = Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.toast_exported, fileName),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} catch (IOException e) {
		}
	}

	private void actionExport() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
					REQUEST_PERMISSION_WRITE_STORAGE);
		}
		else {
			exportCsv();
		}
	}
	
	private void actionShareTo () {
		PlainText text = new PlainText (prefs.getItems());
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text.export());
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.dlg_share_to)));
	}

	private void actionCopy() {
		ClipboardManager myClipboard;
		myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		
		PlainText text = new PlainText(prefs.getItems());
		myClipboard.setText((CharSequence) text.export());
        
		Toast toast = Toast.makeText(getApplicationContext(), 
				getResources().getString(R.string.toast_copied),
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
	}
	
	private void keepScreenOn() {
		if (settings.getBoolean("PREFS_KEEP_SCREEN_ON", false))
	    {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    } else
	    {
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }		
	}
	
	private void set_prefs () {
		prefs_vibrate = settings.getBoolean("PREFS_VIBRATE", false);
		prefs_use_hw_btn = settings.getBoolean("PREFS_USE_HW_BTN", false);
		prefs_night_mode = settings.getBoolean("PREFS_NIGHT_MODE", false);
	}
	
	private void vibrate() {
		if (prefs_vibrate) {
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			// Vibrate for 50 milliseconds
			v.vibrate(50);		
		}
	}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTION) {
                finish();
                startActivity(getIntent());
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
