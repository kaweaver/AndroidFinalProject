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
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
 *         Code for dealing with audio was adopted from
 *         http://developer.android.
 *         com/reference/android/media/MediaRecorder.html
 * 
 */

public class ToDoEditView extends Activity {

	EditText title = null;
	EditText description = null;
	ImageView picture = null;
	Button save_button = null;
	Button picture_button = null;
	
	//variables needed for the picture and audio addition to todo items
	Button record_audio = null;
	Button play_audio = null;
	String pictureUri = null;
	String audioUri = null;
	Boolean recordingAudio = false;
	
	int todoId = - 1;
	int todoParentId = - 1;
	ToDoItemHelper helper = null;
	
	//needed to record and play back audio files for todo items
	MediaRecorder recorder = null;
	MediaPlayer player = null;
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

		recorder = new MediaRecorder();

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

		record_audio = ( Button ) findViewById( R.id.record_audio );
		record_audio.setOnClickListener( new OnClickListener() {
			public void onClick( View view ) {
				recordAudio();
			}
		} );

		play_audio = ( Button ) findViewById( R.id.play_audio );
		play_audio.setOnClickListener( new OnClickListener() {
			public void onClick( View view ) {
				playAudio();
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
		audioUri = helper.getAudioUri( c );
		
		setPictureButton();
		setAudioPlayButton();
		c.close();
	}
	
	private void setPictureButton(){
		if ( pictureUri != null ) {
			Bitmap myBitmap = BitmapFactory.decodeFile( ( new File( pictureUri ) ).getAbsolutePath() );
			picture.setImageBitmap( Bitmap.createScaledBitmap( myBitmap, 120, 120, false ) );
			picture.setEnabled( true );
			picture.setVisibility( ImageView.VISIBLE );
		} else {
			picture.setEnabled( false );
			picture.setVisibility( ImageView.INVISIBLE );
		}
	}
	
	private void setAudioPlayButton(){
		if ( audioUri != null ) {
			play_audio.setEnabled( true );
			play_audio.setVisibility( ImageView.VISIBLE );
		} else {
			play_audio.setEnabled( false );
			play_audio.setVisibility( ImageView.INVISIBLE );
		}
	}

	private void save() {
		if ( title.getText().toString().length() > 0 ) {
			if ( todoId == - 1 ) {
				helper.insert( title.getText().toString(), description.getText().toString(), todoParentId, pictureUri, audioUri );
			} else {
				helper.update( todoId, title.getText().toString(), description.getText().toString(), todoParentId, pictureUri, audioUri );
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
				Log.d( "ToDoApp", "null file name" );
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

	private void recordAudio() {
		if ( recordingAudio ) {
			recorder.stop();
			recorder.release();
			record_audio.setText( "Record audio" );
			setAudioPlayButton();
			recordingAudio = false;
		} else {
			recorder.setAudioSource( MediaRecorder.AudioSource.MIC );
			recorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );
			recorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB );
			if ( audioUri == null ) {
				audioUri = getOutputAudioFile().toString();
			}
			recorder.setOutputFile( audioUri );
			try {
				recorder.prepare();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			recorder.start();
			recordingAudio = true;
			record_audio.setText( "Stop Recording" );
		}
	}

	private void playAudio() {
		player = MediaPlayer.create( this, Uri.parse( audioUri ) );
		player.start();
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE ) {
			if ( resultCode == RESULT_OK ) {
				// Image captured and saved to fileUri specified in the Intent
				if ( pictureUri != null ) {
					Bitmap myBitmap = BitmapFactory.decodeFile( ( new File( pictureUri ) ).getAbsolutePath() );
					picture.setImageBitmap( Bitmap.createScaledBitmap( myBitmap, 120, 120, false ) );
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

	private File getOutputAudioFile() {
		File mediaStorageDir = null;
		if ( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) ) {
			mediaStorageDir = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MUSIC ), "ToDoApp" );
			if ( ! mediaStorageDir.exists() ) {
				if ( ! mediaStorageDir.mkdirs() ) {
					Log.d( "ToDoApp", "failed to create external directory" );
					return null;
				}
			}
		} else {
			mediaStorageDir = this.getDir( "ToDoApp", Context.MODE_PRIVATE );
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
		File mediaFile;
		mediaFile = new File( mediaStorageDir.getPath() + File.separator + "AUD_" + timeStamp + ".3gp" );

		return mediaFile;
	}

	private File getOutputMediaFile( int type ) {
		File mediaStorageDir = null;
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		if ( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) ) {
			mediaStorageDir = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ), "ToDoApp" );
			// This location works best if you want the created images to be shared
			// between applications and persist after your app has been uninstalled.

			// Create the storage directory if it does not exist
			if ( ! mediaStorageDir.exists() ) {
				if ( ! mediaStorageDir.mkdirs() ) {
					Log.d( "ToDoApp", "failed to create external directory" );
					return null;
				}
			}
		} else {
			mediaStorageDir = this.getDir( "ToDoApp", Context.MODE_PRIVATE );
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
