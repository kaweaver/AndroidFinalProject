package apt.ToDoList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

/**
 * 
 * @author Taylor Wacker
 * 
 *         Code for dealing with pictures was adapted from
 *         http://developer.android.com/guide/topics/media/camera.html
 * 
 */

public class ToDoEditView extends Activity {

	EditText title = null;
	EditText description = null;
	ImageView picture = null;
	Button save_button = null;
	Button picture_button = null;
	String pictureUri = null;
	int todoId = - 1;
	int todoParentId = - 1;
	ToDoItemHelper helper = null;

	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	@Override
	public void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		setContentView( R.layout.edit_form );
		helper = new ToDoItemHelper( this );

		todoId = getIntent().getIntExtra( ToDoReadView.ID_EXTRA, - 1 );
		// TODO: throw exception if todoParentId == -1
		todoParentId = getIntent().getIntExtra( ToDoReadView.PARENT_ID_EXTRA, - 1 );

		title = ( EditText ) findViewById( R.id.title );
		description = ( EditText ) findViewById( R.id.description );
		picture = ( ImageView ) findViewById( R.id.picture );

		save_button = ( Button ) findViewById( R.id.save_button );
		save_button.setOnClickListener( new OnClickListener() {
			public void onClick( View view ) {
				save();
			}
		} );

		picture_button = ( Button ) findViewById( R.id.picture_button );
		picture_button.setOnClickListener( new OnClickListener() {
			public void onClick( View view ) {
				takePicture();
			}
		} );

	}

	@Override
	public void onResume() {

		if ( todoId != - 1 ) {
			load();
		}
		super.onResume();

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
		if ( pictureUri != null ) {
			Bitmap myBitmap = BitmapFactory.decodeFile((new File(pictureUri)).getAbsolutePath());
	    picture.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 120, 120, false));
			picture.setEnabled( true );
			picture.setVisibility( ImageView.VISIBLE );
		} else {
			picture.setEnabled( false );
			picture.setVisibility( ImageView.INVISIBLE );
		}
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

	private void takePicture() {
		Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
		Uri fileUri;
		if ( pictureUri == null ) {
			File file = getOutputMediaFile( MEDIA_TYPE_IMAGE );
			if ( file == null ) {
				Log.d( "MyCameraApp", "null file name" );
				return;
			}
			fileUri = Uri.fromFile( file );
		} else {
			fileUri = Uri.parse( pictureUri );
		}
		intent.putExtra( MediaStore.EXTRA_OUTPUT, fileUri );
		pictureUri = fileUri.getPath();
		startActivityForResult( intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE ) {
			if ( resultCode == RESULT_OK ) {
				// Image captured and saved to fileUri specified in the Intent
				if ( pictureUri != null ) {
					Bitmap myBitmap = BitmapFactory.decodeFile((new File(pictureUri)).getAbsolutePath());
			    picture.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 120, 120, false));
					picture.setEnabled( true );
					picture.setVisibility( ImageView.VISIBLE );
				}
			} else {
				// Image capture failed, advise user
				pictureUri = null;
				picture.setEnabled( false );
				picture.setVisibility( ImageView.INVISIBLE );
			}
		}
	}

	private File getOutputMediaFile( int type ) {
		File mediaStorageDir = null;
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		if ( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) ) {
			mediaStorageDir = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ), "MyCameraApp" );
			// This location works best if you want the created images to be shared
			// between applications and persist after your app has been uninstalled.

			// Create the storage directory if it does not exist
			if ( ! mediaStorageDir.exists() ) {
				if ( ! mediaStorageDir.mkdirs() ) {
					Log.d( "MyCameraApp", "failed to create external directory" );
					return null;
				}
			}
		} else {
			mediaStorageDir = this.getDir( "MyCameraApp", Context.MODE_PRIVATE );
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
		File mediaFile;
		if ( type == MEDIA_TYPE_IMAGE ) {
			mediaFile = new File( mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg" );
		} else {
			return null;
		}

		return mediaFile;
	}

}
