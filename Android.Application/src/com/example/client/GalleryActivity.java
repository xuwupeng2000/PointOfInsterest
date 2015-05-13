package com.example.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.example.database.DatabaseAdapter;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class GalleryActivity extends Activity {
    /** Called when the activity is first created. */
  
	protected Button deletePhoto;
	protected Button cancel;
	protected Button addPhoto;
	protected Button skip;
	protected String _path; // represents the path and filename of the image we will create
	protected String delete_path; //get the path for the picture which need to be deleted
	protected boolean _taken; //set to true once a photo has been take
	protected int image_number; // number of image. this will be the image id as well 
	protected String imageName;
	protected int arraySize;
	private DatabaseAdapter dba;
	protected String poiid;
	public String picid;
	final String smallpic = "NOsmallPic";
	private String username = "whoever";
	final String updatetime = "Now";
	protected String picidfordelete;
	boolean viewing;
	
	//used as a key for determining whether or not a photo was taken
	protected static final String PHOTO_TAKEN	= "photo_taken"; 
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
       
        
        Bundle bundle = getIntent().getExtras();
		poiid = bundle.getString("poiid");
		username=bundle.getString("username");
        if(bundle.getBoolean("viewing")){
        	viewing = bundle.getBoolean("viewing");
        } else {
        	viewing = false;
        }

		File folder = new File(Environment.getExternalStorageDirectory() + "/gallery_images");
    	if(folder.exists()){
    		 String[] children = folder.list();
    	        for (int i = 0; i < children.length; i++) {
    	            new File(folder, children[i]).delete();
    	        }
    	} else {
    	    folder.mkdir();
    	}
        dba = new DatabaseAdapter(this);
        dba.open();
		Cursor cur = dba.fetchPicFromLocalDatabase(poiid);
		
		
		for (cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()) {
			String getpicid = cur.getString(cur.getColumnIndex("picid"));
			String getpicString = cur.getString(cur.getColumnIndex("bigpic"));
			
			if (!getpicString.equals("null")){
		
			byte[] gzipBuff;
			try {
				gzipBuff = Base64.decode(getpicString.getBytes());
				ByteArrayOutputStream baos = new ByteArrayOutputStream(gzipBuff.length);
				baos.write(gzipBuff);
				File d = new File(Environment.getExternalStorageDirectory()+ "/gallery_images/"+getpicid+".jpg");
				FileOutputStream wo = new FileOutputStream(d);
				wo.write(baos.toByteArray());
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
		}
		dba.close();
		Log.i("pic", "i am here3!");

		 Gallery g = (Gallery) findViewById(R.id.gallery);
	        g.setAdapter(new ImageAdapter(this, ReadSDCard()));

	        g.setOnItemClickListener(new OnItemClickListener() {
	            @SuppressWarnings("unchecked")
				public void onItemClick(AdapterView parent, View v, int position, long id) {
	            	//String imageName = FileList.get(position).toString();
	                Toast.makeText(GalleryActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	                
	            }
	        });
		
    	
        //final Random rand = new Random();
		//image_number = rand.nextInt();
       
        deletePhoto = ( Button ) findViewById( R.id.deletephoto );
        addPhoto = ( Button ) findViewById( R.id.addphoto );
        cancel = ( Button ) findViewById( R.id.cancel ); 
        if(viewing){
        	deletePhoto.setVisibility(8);
        	addPhoto.setVisibility(8);
        }
        
        addPhoto.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (arraySize <= 9) {
					startCameraActivity();
				} else { 
					//Toast.makeText(CapturePhoto.this, "You can only upload 5 images", Toast.LENGTH_LONG).show();
					// prepare the alert box
		            AlertDialog.Builder alertbox = new AlertDialog.Builder(GalleryActivity.this);
		            alertbox.setMessage("You can only upload 10 images");
		            alertbox.setNeutralButton("Ok",  null).show();
		            
    	    	}
			}
		});  
        
        
        deletePhoto.setOnClickListener(new OnClickListener() { 
        	
        	public void onClick(View v) { 
                dba.open();
                dba.deletePOIpicture(picidfordelete);
    			Log.i("pic", "send pic succed");
    			dba.close();
        		//Intent delete = new Intent(this.startActivity);
        		//File d = new File(delete_path);
        		//boolean deleted = d.delete();
        		Log.e(" picid for delete", picidfordelete+"");
        		Log.e("pathneeded", delete_path+"");
        		startActivity(getIntent()); finish();
        		
        	}
        });
        
        	cancel.setOnClickListener(new OnClickListener() { 
        	
        	public void onClick(View v) { 
        		setResult(RESULT_CANCELED);
				finish();
        		
        	}
        });
    }
    
    
    
    
    protected void startCameraActivity(){
		 

    	Log.e("Camera Activity", "startCameraActivity()" );
    	File folder = new File(Environment.getExternalStorageDirectory() + "/gallery_images");
    	boolean success = false;
    	if(!folder.exists())
    	{
    	    success = folder.mkdir();
    	    Log.e("folder", "folder made");
    	    if (success) 
	    	{ 
    	    	long timestamp =  Calendar.getInstance().getTimeInMillis();
    	    	String picid = "image"+Long.toString(timestamp);
    	    	_path = Environment.getExternalStorageDirectory() + "/gallery_images/"+picid+".jpg";
    	    	File file = new File( _path );
		    	Uri outputFileUri = Uri.fromFile( file );
		    	Log.e("file", _path);
		    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
		    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
		    	intent.putExtra("picid", picid.toString());
		    	startActivityForResult( intent, 0 );
	    	}
    	} else {
    		long timestamp =  Calendar.getInstance().getTimeInMillis();
        	picid = "image"+Long.toString(timestamp);
    		_path = Environment.getExternalStorageDirectory() + "/gallery_images/"+picid+".jpg";
    		Log.e("folder", "Already exist");
    		File file = new File( _path );
	    	Uri outputFileUri = Uri.fromFile( file );
	    	Log.e("file", _path);
	    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
	    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
	    	startActivityForResult( intent, 0 );

    	}
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	Log.i( "Result", "resultCode: " + resultCode );
    	Log.i( "Result", "requestCode: " + requestCode );
    	Log.i( "Result", "Intent: " + data );
    	switch( resultCode )
    	{
    		case 0:
    			Log.i( "MakeMachine", "User cancelled" );
    			
    		case -1:
    			onPhotoTaken();
    			Gallery gd = (Gallery) findViewById(R.id.gallery);
    	        gd.setAdapter(new ImageAdapter(this, ReadSDCard()));
    			break;
    		
    		case 1:
    			Gallery gdd = (Gallery) findViewById(R.id.gallery);
    	        gdd.setAdapter(new ImageAdapter(this, ReadSDCard()));
    			break;		
    	}
    }
    
    private List<String> ReadSDCard()  {  
    	List<String> tFileList = new ArrayList<String>();  
    	//It have to be matched with the directory in SDCard  
    	File f = new File(Environment.getExternalStorageDirectory() + "/gallery_images/");  
    	File[] files = f.listFiles(); 
    	for(int i=0; i<files.length; i++)  {  
    		File file = files[i];  
    		tFileList.add(file.getPath());  
    		Log.e("path", file.getPath()+""); 
    	}  
    	arraySize = tFileList.size();
    	Log.e("array size", tFileList.size()+"");
    	Log.e("image number", arraySize+"");
     return tFileList;  
    }  
    
    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;
        private List<String> FileList;  
        

        public ImageAdapter(Context c, List<String> fList) {
            mContext = c;
            FileList = fList; 
            TypedArray a = c.obtainStyledAttributes(R.styleable.Gallery1);
            mGalleryItemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();
        }

        public int getCount() {
            return FileList.size();
        }

        public Object getItem(int position) {
        	
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
            delete_path = FileList.get(position).toString();
            picidfordelete = delete_path.substring(27, 45);
            Log.e("Deleted path", picidfordelete+"");
            Bitmap bm = BitmapFactory.decodeFile( FileList.get(position).toString());
            Log.e("Deleted path", FileList.get(position).toString()+"");
            i.setImageBitmap(bm);
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            i.setBackgroundResource(mGalleryItemBackground);
            return i;
        }
    }
    
    protected void onPhotoTaken() {
    	
    	Log.i( "MakeMachine", "onPhotoTaken" );
    	
    	_taken = true;
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 7;
    	Bitmap bitmap = BitmapFactory.decodeFile( _path, options );

    	try { 
	    	
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
			byte [] ba1 = bytes.toByteArray();
			String imageAsString = Base64.encodeBytes(ba1);
			File f = new File(Environment.getExternalStorageDirectory()+ "/gallery_images/"+picid+".jpg");
			f.createNewFile();
			//write the bytes in file
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			
			
			Log.e("POI ID", poiid+"");
			Log.e("PIC ID", picid+"");
			Log.e("username", username+"");
			Log.e("Small PIC", smallpic+"");
			Log.e("image string", imageAsString+"");
			Log.e("updatetime", updatetime+"");
			
			
			
			//sending to the database
			dba = new DatabaseAdapter(this);
			dba.open();
			dba.savePicToLocalDatabase(poiid, picid, imageAsString, smallpic, updatetime, username);
			Log.i("pic", "send pic succed");
			dba.close();
    	} catch (Exception e) {
			// TODO Auto-generated catch blockle.printStackTrace();
			Log.e("something wrong", "Cannot write to database");
		}
    	 

    }
    
    
    @Override 
    protected void onRestoreInstanceState( Bundle savedInstanceState){
    	Log.i( "MakeMachine", "onRestoreInstanceState()");
    	if( savedInstanceState.getBoolean( GalleryActivity.PHOTO_TAKEN ) ) {
    		onPhotoTaken();
    	}
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) {
    	outState.putBoolean( GalleryActivity.PHOTO_TAKEN, _taken );
    }
    
   

  
}