package pro.fossa.apps.timer;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TimerDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "timer";

	private static final int DATABASE_VERSION = 4;

	// запрос на создание базы данных
	private static final String DATABASE_CREATE = "create table messages ("
			+ "_id integer primary key autoincrement, "
			+ "rdelta integer not null, "
			+ "rtime integer not null, "
			+ "roffset integer not null, "
			+ "rtitle text not null, "
			+ "rcheckpoint integer not null);";

	public TimerDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// метод вызывается при создании базы данных
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	// метод вызывается при обновлении базы данных, например, когда вы увеличиваете номер версии базы данных
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TimerDBHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS messages");
		onCreate(database);
	}
}
