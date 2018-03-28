package com.example.toto;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends Activity implements OnClickListener
{
	ImageView ImgGo;
	File photo;
	Button Supprimer;
	TextView HoroDate;
	SimpleDateFormat sdf;
	Date currentTime;
	String nouvelleDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ImgGo = (ImageView)findViewById(R.id.imageView1);
		HoroDate =(TextView)findViewById(R.id.textView2);
		
		
		sdf = new SimpleDateFormat("dd_MM_yyyy HH_mm_ss");

		Button Commencer =(Button)findViewById(R.id.button1);
		Commencer.setOnClickListener(this);
		Supprimer = (Button)findViewById(R.id.button2);
		Supprimer.setOnClickListener(this);
	}
	
	//redimension de l'image
	private void ResizePic(File photoFile)
	{
        int mOutputY = 400;
        int mOutputX = 400;
        
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap croppedImage;
        Bitmap mBitmap = null;
        opts.inJustDecodeBounds = true;
        
        try 
        {
        	mBitmap = BitmapFactory.decodeFile(photoFile.toString(), opts);
		}
        catch (Exception e)
        {
                    // TODO: handle exception
        }
        int i = opts.outHeight * opts.outWidth;
        opts.inJustDecodeBounds = false;
        if (i > 5E6)
        {
              opts.inSampleSize = 2;
        }
        else 
        {
              opts.inSampleSize = 1;
        }
        try 
        {
             mBitmap = BitmapFactory.decodeFile(photoFile.toString(), opts);
        }
        catch (Exception e) 
        {
                    // TODO: handle exception
        }
        
        int mInputX = mBitmap.getWidth();
        int mInputY = mBitmap.getHeight();
        //Log.e("cc", mInputX +"/" +mInputY);

        if (mInputX>mInputY){
              i = mOutputY;
              mOutputY = mOutputX;
              mOutputX = i;
        }
        
        croppedImage = Bitmap.createBitmap(mOutputX, mOutputY, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(croppedImage);
        Rect srcRect = new Rect(0, 0, mInputX, mInputY);
        Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);
        canvas.drawBitmap(mBitmap, srcRect, dstRect, null);
        mBitmap.recycle();
        OutputStream outputStream = null;
        
        try
        {
              outputStream = getContentResolver().openOutputStream(Uri.fromFile(photoFile));
              if (outputStream != null)
              {
                     croppedImage.compress(Bitmap.CompressFormat.JPEG, 65, outputStream);
              }
              outputStream.flush();
              outputStream.close();
              croppedImage.recycle();
        }
        catch (FileNotFoundException e)
        {
              e.printStackTrace();
        }
        catch (IOException e)
        {
              e.printStackTrace();
        }
	}

    public static Bitmap decodeFile(String pathName)
    {
       Bitmap bitmap = null;
       BitmapFactory.Options options = new BitmapFactory.Options();
       
       for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++)
       {
    	   try
    	   {
    		   bitmap = BitmapFactory.decodeFile(pathName, options);
    		   Log.e("photo", "Decoded successfully for sampleSize " + options.inSampleSize);
    		   break;
    	   }
    	   catch (OutOfMemoryError outOfMemoryError)
    	   {
    		   // If an OutOfMemoryError occurred, we continue with for loop and next inSampleSize value
    		   Log.e("photo", "outOfMemoryError while reading file for sampleSize " + options.inSampleSize + " retrying with higher value");
    	   }
       }
       return bitmap;
    }
    
    
    //lorsque l'on affiche l'appareil photo et qu'on prend une photo
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.button1:
				Log.e("Mon Tag", "Commencer");
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				
				currentTime = Calendar.getInstance().getTime();
				nouvelleDate = sdf.format(currentTime);
			    photo = new File(Environment.getExternalStorageDirectory(), nouvelleDate + ".jpg");
			    //photo = new File(Environment.getExternalStorageDirectory(), sdf.format(currentTime)) + ".jpg");
			    
			    intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
//			    imageUri = Uri.fromFile(photo);
	
			    startActivityForResult(intent, 1);
				break;
			case R.id.button2:
				Log.e("Supp","Supprimer");
				SupprimerPhoto(photo,ImgGo);
			default:
				break;
		}
	}
	
	private void SupprimerPhoto(File MonFichier, ImageView MonImageView)
	{
		// TODO Auto-generated method stub
		if(MonFichier.exists())
		{
			if(MonFichier.delete())
			{
				MonImageView.setImageBitmap(null);
				Toast.makeText(getApplicationContext(),"Supression OK", Toast.LENGTH_LONG).show();
				Supprimer.setVisibility(View.GONE);
				HoroDate.setVisibility(View.GONE);
			}
		}
	}

	//cas de retour de la photo ou non
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	switch (requestCode)
	{
	case 1:
		//Toast.makeText(getApplicationContext(),"Je suis de retour -->"+requestCode+"-Avec le code--"+resultCode, Toast.LENGTH_LONG).show();
		switch (resultCode)
		{
		case RESULT_OK:
			Log.e("TOTO", "data n'est pas NULL");
			//Toast.makeText(getApplicationContext(),"data n'est pas NULL -->"+photo.getAbsolutePath(), Toast.LENGTH_LONG).show();
			Log.e("TOTO", "Mon Fichier est OK");
			
			try
			{
				ResizePic(photo);
				Bitmap MaPhoto=android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(photo));
				ImgGo.setImageBitmap(MaPhoto);
				Supprimer.setVisibility(View.VISIBLE);
				currentTime = Calendar.getInstance().getTime();
				HoroDate.setText(sdf.format(currentTime));
				Toast.makeText(getApplicationContext(),"Mon Fichier est " + photo, Toast.LENGTH_LONG).show();

			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(getApplicationContext(),"IO FileNotFoundException -->"+e.toString(), Toast.LENGTH_LONG).show();
			}
			catch (IOException e)
			{

				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(getApplicationContext(),"IO Exception -->"+e.toString(), Toast.LENGTH_LONG).show()
			}
			break;
		case RESULT_CANCELED:
				//Toast.makeText(getApplicationContext(),"Je suis de retour -->RESULT_CANCELED", Toast.LENGTH_LONG).show();
			SupprimerPhoto(photo,ImgGo);
				break;
		}
		break;
		default:
			break;
	}
	}
}