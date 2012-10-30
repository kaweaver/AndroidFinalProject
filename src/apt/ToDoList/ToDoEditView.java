package apt.ToDoList;

import apt.ToDoList.R;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;

public class ToDoEditView extends Activity {

	EditText title = null;
	EditText description = null;
	int id = - 1;
	int parentId = - 1;
	ToDoItemHelper helper = null;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.edit_form );

		helper = new ToDoItemHelper( this );
		title = ( EditText ) findViewById( R.id.title );
		description = ( EditText ) findViewById( R.id.description );
		parentId = getIntent().getIntExtra( ToDoReadView.PARENT_ID_EXTRA, - 1 );
		id = getIntent().getIntExtra( ToDoReadView.ID_EXTRA, - 1 );

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		helper.close();
	}

	@Override
	public void onPause() {
		super.onPause();
		// TODO: change to button
		save();
	}

	private void load() {
		Cursor c = helper.getById( id );

		c.moveToFirst();
		title.setText( helper.getTitle( c ) );
		description.setText( helper.getDescription( c ) );
		c.close();
		
	}

	private void save() {
		if ( title.getText().toString().length() > 0 ) {
			if ( id == - 1 ) {
				helper.insert( title.getText().toString(), description.getText().toString(), parentId );
			} else {
				helper.update( id, title.getText().toString(), description.getText().toString(), parentId );
			}
		}
	}

}
