package pro.fossa.apps.timer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TimerDBAdapter {

	// поля базы данных
	public static final String KEY_ROWID = "_id";
	public static final String KEY_DELTA = "rdelta";
	public static final String KEY_OFFSET = "roffset";
	public static final String KEY_TITLE = "rtitle";
	public static final String KEY_TIME = "rtime";
	public static final String KEY_CHECKPOINT = "rcheckpoint";
	private static final String DATABASE_TABLE = "messages";
	private Context context;
	private SQLiteDatabase database;
	private TimerDBHelper dbHelper;

	public TimerDBAdapter(Context context) {
		this.context = context;
	}

	public TimerDBAdapter open() throws SQLException {
		dbHelper = new TimerDBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * создать новый элемент списка дел. если создан успешно - возвращается номер строки rowId
	 * иначе -1
	 */
	public long createMessage(long delta, long offset, String title, boolean checkpoint, long time) {
		ContentValues initialValues = createContentValues(delta, time, offset,
				title, checkpoint);

		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * обновить список
	 */
	public boolean updateMessage(long rowId, long delta, long offset,
			String title, boolean checkpoint, long time) {
		ContentValues updateValues = createContentValues(delta, time, offset,
				title, checkpoint);

		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	/**
	 * удаляет элемент списка
	 */
	public boolean deleteMessage(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * удаляет все элементы
	 */
	public boolean deleteAllMessages() {
		return database.delete(DATABASE_TABLE, "1", null) > 0;
	}

	/**
	 * возвращает курсор со всеми элементами списка дел
	 *
	 * @return курсор с результатами всех записей
	 */
	public Cursor fetchAllMessages() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_DELTA, KEY_TIME, KEY_OFFSET, KEY_TITLE, KEY_CHECKPOINT }, null, null, null,
				null, null, null);
	}

	/**
	 * возвращает курсор, спозиционированный на указанной записи
	 */
	public Cursor fetchMessage(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_DELTA, KEY_TIME, KEY_OFFSET, KEY_TITLE, KEY_CHECKPOINT },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(long delta, long time, long offset,
			String title, boolean checkpoint) {
		ContentValues values = new ContentValues();
		values.put(KEY_DELTA, delta);
		values.put(KEY_TIME, time);
		values.put(KEY_OFFSET, offset);
		values.put(KEY_TITLE, title);
		values.put(KEY_CHECKPOINT, checkpoint);
		return values;
	}
}