package apt.ToDoList;

import android.content.*;
import android.database.sqlite.*;

class ToDoItemHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "todolist.db";
	private static final int SCHEMA_VERSION = 1;

	private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + DATABASE_NAME + ";";
	private static final String CREATE_TABLE = "CREATE TABLE todos (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, parent INTEGER);";

	public ToDoItemHelper( Context context ) {
		super( context, DATABASE_NAME, null, SCHEMA_VERSION );
	}

	@Override
	public void onCreate( SQLiteDatabase db ) {
		db.execSQL( DROP_TABLE );
		db.execSQL( CREATE_TABLE );
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		switch ( oldVersion ) {
		case 0 :
		case 1 :
		default :
			// do nothing
			break;
		}
	}

}
