package apt.ToDoList;

import apt.ToDoList.R;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class ToDoEditView extends Activity {

	EditText title = null;
	EditText description = null;
	Button save_button = null;
	String pictureUri = null;
	int todoId = - 1;
	int todoParentId = -1;
	ToDoItemHelper helper = null;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.edit_form );
		helper = new ToDoItemHelper( this );

		todoId = getIntent().getIntExtra( ToDoReadView.ID_EXTRA, - 1 );
		//TODO: throw exception if todoParentId == -1 
		todoParentId = getIntent().getIntExtra( ToDoReadView.PARENT_ID_EXTRA, - 1 );

		title = ( EditText ) findViewById( R.id.title );
		description = ( EditText ) findViewById( R.id.description );

		if ( todoId != - 1 ) {
			load();
		}

		save_button = ( Button ) findViewById( R.id.save_button );
		save_button.setOnClickListener( new OnClickListener() {
			public void onClick( View view ) {
				save();
			}
		} );

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		helper.close();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void load() {		
		Cursor c = helper.getById( todoId );
		c.moveToFirst();
		title.setText( helper.getTitle( c ) );
		description.setText( helper.getDescription( c ) );
		pictureUri = helper.getPictureUri( c );
		//do something with the picture uri
		
		
		c.close();
	}

	private void save() {
		if ( title.getText().toString().length() > 0 ) {
			if ( todoId == - 1 ) {
				helper.insert( title.getText().toString(), description.getText().toString(), todoParentId, pictureUri );
			} else {
				helper.update( todoId, title.getText().toString(), description.getText().toString(), todoParentId, pictureUri );
			}
		}
		finish();
	}

}
