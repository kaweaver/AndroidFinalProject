package apt.ToDoList;

import android.net.Uri;
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
	Cursor current = null;
	TextView title = null;
	TextView description = null;
	ImageView picture = null;
	ToDoAdapter adapter = null;
	ToDoItemHelper helper = null;
	String pictureUri = null;
	int todoId = - 1;
	int todoParentId = - 1;
	static int cutId = - 1;

	public final static String ID_EXTRA = "apt.ToDoList._id";
	public final static String PARENT_ID_EXTRA = "apt.ToDoList.parent_id";

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		helper = new ToDoItemHelper( this );
		title = ( TextView ) findViewById( R.id.currenttitle );
		description = ( TextView ) findViewById( R.id.currentdescription );
		picture = ( ImageView ) findViewById( R.id.currentpicture );
		todoParentId = getIntent().getIntExtra( PARENT_ID_EXTRA, - 1 );
		todoId = getIntent().getIntExtra( ID_EXTRA, 1 );
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		helper.close();
	}

	@Override
	public void onListItemClick( ListView list, View view, int position, long id ) {
		Intent i = new Intent( ToDoReadView.this, ToDoReadView.class );
		i.putExtra( ID_EXTRA, ( int ) id );
		i.putExtra( PARENT_ID_EXTRA, this.todoId );
		startActivity( i );
	}

	@Override
	public void onResume() {
		super.onResume();
		initList();
	}

	@Override
	public boolean onPrepareOptionsMenu( Menu menu ) {
		/*
		 * 0 is add 1 is edit 2 is delete branch 3 is delete item 4 is cut / cancel
		 * cut 5 is paste
		 */
		menu.clear();
		new MenuInflater( this ).inflate( R.menu.option, menu );
		if ( todoId == 1 ) {
			menu.getItem( 2 ).setEnabled( false );
			menu.getItem( 2 ).setVisible( false );

			menu.getItem( 3 ).setEnabled( false );
			menu.getItem( 3 ).setVisible( false );

			if ( cutId == - 1 ) {
				menu.getItem( 4 ).setEnabled( false );
				menu.getItem( 4 ).setVisible( false );
			}
		}

		if ( cutId != - 1 ) {
			menu.getItem( 4 ).setTitle( "Cancel Cut" );

			if ( ! isDescendant( cutId, this.todoId ) ) {
				menu.getItem( 5 ).setEnabled( true );
				menu.getItem( 5 ).setVisible( true );
			} else {
				menu.getItem( 5 ).setEnabled( false );
				menu.getItem( 5 ).setVisible( false );
			}

		} else {
			menu.getItem( 5 ).setEnabled( false );
			menu.getItem( 5 ).setVisible( false );
		}
		return super.onPrepareOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		if ( item.getItemId() == R.id.add ) {
			Intent i = new Intent( ToDoReadView.this, ToDoEditView.class );
			i.putExtra( PARENT_ID_EXTRA, todoId );
			startActivity( i );
			return true;
		} else if ( item.getItemId() == R.id.edit ) {
			Intent i = new Intent( ToDoReadView.this, ToDoEditView.class );
			i.putExtra( ID_EXTRA, this.todoId );
			i.putExtra( PARENT_ID_EXTRA, this.todoParentId );
			startActivity( i );
			return true;
		} else if ( item.getItemId() == R.id.delete_branch ) {
			deleteBranch();
		} else if ( item.getItemId() == R.id.delete_single ) {
			deleteItem();
		} else if ( item.getItemId() == R.id.cut_item ) {
			if ( cutId == - 1 ) {
				cutId = todoId;
			} else {
				cutId = - 1;
			}
			invalidateOptionsMenu();
		} else if ( item.getItemId() == R.id.paste_item ) {
			pasteItem();
			invalidateOptionsMenu();
		}
		return super.onOptionsItemSelected( item );
	}

	private void pasteItem() {
		if ( current != null ) {
			stopManagingCursor( current );
			current.close();
		}
		current = helper.getById( cutId );
		startManagingCursor( current );
		current.moveToFirst();
		helper.updateParent( cutId, todoId );

		cutId = - 1;

		initList();
	}

	/**
	 * This will tell if question_id is an ancestor of this.todoId
	 * 
	 * @param question_id
	 *          the id in question ancestor the ancestor id
	 * @return true if question_id is an ancestor of this.todoId false otherwise
	 */
	private boolean isDescendant( int ancestor, int question_id ) {
		if ( question_id <= 1 ) {
			return false;
		} else if ( ancestor == question_id ) {
			return true;
		}

		if ( current != null ) {
			stopManagingCursor( current );
			current.close();
		}
		current = helper.getById( question_id );
		startManagingCursor( current );
		current.moveToFirst();

		return isDescendant( ancestor, helper.getParentid( current ) );
	}

	private void deleteBranch() {
		deleteAllChildren( todoId );
		finish();
	}

	private void deleteAllChildren( int id ) {
		Cursor c = helper.getAllbyParent( id );
		c.moveToFirst();
		for ( int i = 0; i < c.getCount(); i ++ ) {
			deleteAllChildren( helper.getId( c ) );
			c.moveToNext();
		}
		helper.delete( id );
	}

	private void deleteItem() {
		reassignParents( todoParentId, todoId );
		helper.delete( todoId );
		finish();
	}

	private void reassignParents( int parentId, int id ) {
		Cursor c = helper.getAllbyParent( id );
		c.moveToFirst();
		for ( int i = 0; i < c.getCount(); i ++ ) {
			helper.updateParent( helper.getId( c ), parentId );
			c.moveToNext();
		}
	}

	private void initList() {
		if ( current != null ) {
			stopManagingCursor( current );
			current.close();
		}
		current = helper.getById( todoId );
		startManagingCursor( current );
		current.moveToFirst();
		title.setText( helper.getTitle( current ) );
		description.setText( helper.getDescription( current ) );
		pictureUri = helper.getPictureUri( current );
		if(pictureUri != null){
			picture.setImageURI( Uri.parse(pictureUri) );
			picture.setEnabled( true );
			picture.setVisibility( ImageView.VISIBLE );
		} else {
			picture.setEnabled( false );
			picture.setVisibility( ImageView.INVISIBLE );
		}

		if ( todo != null ) {
			stopManagingCursor( todo );
			todo.close();
		}
		todo = helper.getAllbyParent( todoId );
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
