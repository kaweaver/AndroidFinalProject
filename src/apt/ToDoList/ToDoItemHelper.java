package apt.ToDoList;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;

class ToDoItemHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "todolist.db";
	private static final int SCHEMA_VERSION = 3;

	private static final String TABLE_NAME = "todos";
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + DATABASE_NAME + ";";
	private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, description TEXT, parent INTEGER, pictureuri TEXT, audiouri TEXT);";
	private static final String INITIAL_ITEM = "INSERT INTO " + TABLE_NAME + " (_id,title) VALUES (1,\"This Is My List\");";
	private String alterPicture = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN pictureuri TEXT";
	private String alterAudio = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN audiouri TEXT";

	public ToDoItemHelper( Context context ) {
		super( context, DATABASE_NAME, null, SCHEMA_VERSION );
	}

	@Override
	public void onCreate( SQLiteDatabase db ) {
		db.execSQL( DROP_TABLE );
		db.execSQL( CREATE_TABLE );
		db.execSQL( INITIAL_ITEM );
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		switch ( oldVersion ) {
		case 0 :
		case 1 :
			db.execSQL( alterPicture );
		case 2 :
			db.execSQL( alterAudio );
		default :
			// do nothing
			break;
		}
	}

	public void insert( String title, String description, int parentId, String pictureUri, String audioUri ) {
		ContentValues cv = new ContentValues();

		cv.put( "title", title );
		cv.put( "description", description );
		cv.put( "parent", parentId );
		cv.put( "pictureuri", pictureUri );
		cv.put( "audiouri", audioUri );

		getWritableDatabase().insert( TABLE_NAME, "table", cv );
	}

	public Cursor getAllbyParent( int parentId ) {
		String sqlStatement = "SELECT * FROM " + TABLE_NAME + " WHERE parent = " + Integer.toString( parentId ) + ";";
		
		return getReadableDatabase().rawQuery( sqlStatement, null );
	}

	public Cursor getAll() {
		String sqlStatement = "SELECT * from " + TABLE_NAME + ";";
		
		return getReadableDatabase().rawQuery( sqlStatement, null );
	}

	public Cursor getById( int id ) {
		String[] args = { Integer.toString( id ) };
		String sqlStatement = "SELECT * FROM " + TABLE_NAME + " WHERE _id=?";
		
		return getReadableDatabase().rawQuery( sqlStatement, args );
	}

	public int getId( Cursor c ) {
		return c.getInt( 0 );
	}

	public String getTitle( Cursor c ) {
		return c.getString( 1 );
	}

	public String getDescription( Cursor c ) {
		return c.getString( 2 );
	}

	public int getParentid( Cursor c ) {
		return c.getInt( 3 );
	}
	
	public String getPictureUri( Cursor c) {
		return c.getString( 4 );
	}
	
	public String getAudioUri( Cursor c ) {
		return c.getString( 5 );
	}

	public void update( int id, String title, String description, int parentId, String pictureUri, String audioUri ) {
		ContentValues cv = new ContentValues();
		String[] args = { Integer.toString( id ) };

		cv.put( "title", title );
		cv.put( "description", description );
		cv.put( "parent", parentId );
		cv.put( "pictureuri", pictureUri);
		cv.put( "audiouri", audioUri );

		getWritableDatabase().update( TABLE_NAME, cv, "_id=?", args );
	}
	
	public void updateParent( int id, int parentId){
		ContentValues cv = new ContentValues();
		String[] args = { Integer.toString( id ) };
		cv.put( "parent", parentId );
		
		getWritableDatabase().update( TABLE_NAME, cv, "_id=?", args );
	}

	public void delete( int id ) {
		String[] args = { Integer.toString( id ) };
		
		getWritableDatabase().delete( TABLE_NAME, "_id=?", args );
	}
}
