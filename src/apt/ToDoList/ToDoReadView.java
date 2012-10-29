package apt.ToDoList;

import apt.ToDoList.R;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class ToDoReadView extends ListActivity {

	Cursor todo = null;
	ToDoAdapter adapter = null;
	ToDoItemHelper helper = null;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		helper = new ToDoItemHelper( this );
		initList();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		helper.close();
	}
	
	private void initList() {
		if ( todo != null ) {
			stopManagingCursor( todo );
			todo.close();
		}
		// TODO: figure out what parent to use
		todo = helper.getAllbyParent( - 1 );
		startManagingCursor( todo );
		adapter = new ToDoAdapter( todo );
		setListAdapter( adapter );
	}

	class ToDoAdapter extends CursorAdapter {

		ToDoAdapter( Cursor c ) {
			super( ToDoReadView.this, c );
		}

		@Override
		public void bindView( View row, Context ctxt, Cursor c ) {
			ToDoHolder holder = ( ToDoHolder ) row.getTag();
			holder.populateForm( c, helper );
		}

		@Override
		public View newView( Context ctxt, Cursor c, ViewGroup parent ) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate( R.layout.row, parent, false );
			ToDoHolder holder = new ToDoHolder( row );
			row.setTag( holder );
			return row;
		}

	}

	static class ToDoHolder {

		private TextView title = null;
		private TextView description = null;

		ToDoHolder( View row ) {
			title = ( TextView ) row.findViewById( R.id.title );
			description = ( TextView ) row.findViewById( R.id.description );
		}

		void populateForm( Cursor c, ToDoItemHelper helper ) {
			title.setText( helper.getTitle( c ) );
			description.setText( helper.getDescription( c ) );
		}

	}

}
