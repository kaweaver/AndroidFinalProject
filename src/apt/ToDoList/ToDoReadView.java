package apt.ToDoList;

import apt.ToDoList.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class ToDoReadView extends ListActivity {

	Cursor todo = null;
	ToDoAdapter adapter = null;
	ToDoItemHelper helper = null;

	public final static String ID_EXTRA = "apt.ToDoList._id";
	public final static String PARENT_ID_EXTRA = "apt.ToDoList.parent_id";

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		helper = new ToDoItemHelper( this );
		initList();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		helper.close();
	}

	@Override
	public void onListItemClick( ListView list, View view, int position, long id ) {
		Intent i = new Intent( ToDoReadView.this, ToDoEditView.class );
		i.putExtra( ID_EXTRA, id );
		i.putExtra( PARENT_ID_EXTRA, helper.getParentid( todo ) );
		startActivity( i );
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		new MenuInflater( this ).inflate( R.menu.option, menu );
		return super.onCreateOptionsMenu( menu );
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		if ( item.getItemId() == R.id.add ){
			startActivity( new Intent( ToDoReadView.this, ToDoEditView.class ) );
			return true;
		}
		return super.onOptionsItemSelected( item );
	}

	private void initList() {
		if ( todo != null ) {
			stopManagingCursor( todo );
			todo.close();
		}
		// TODO: figure out what parent to use
		todo = helper.getAllbyParent( 0 );
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
