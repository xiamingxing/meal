package com.meal.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.meal.action.UserManageAction;
import com.meal.activity.ipml.AsynThreadImpl;
import com.meal.activity.ipml.UIThreadImpl;
import com.meal.bean.Global;
import com.meal.bean.User;
import com.meal.dialog.MyProgressDialog;
import com.meal.util.BeanUtil;
import com.meal.util.PhotoUtil;
import com.meal.util.SysUtil;

public class IndividualCenterActivity extends BaseActivity
{
	Button retButtonIndividual;
	EditText nickname;
	Button editNickButton;
	ImageView portrait;
	
	Button activelist;
	Button historylist;
	Button historyshop;
	
	private MyProgressDialog myprogressDialog;
	
	private boolean flagP = false;
	private UserManageAction userManage = UserManageAction.getInstance();
	private User user = null;
	private Uri uri = null;
	private boolean isClick = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.individualcenter);
		try {
			user = (User) BeanUtil.cloneObject(Global.user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initWidget();
	}
	
	private void initWidget()
	{
		retButtonIndividual = (Button)findViewById(R.id.retButtonIndividual);
		nickname = (EditText)findViewById(R.id.nickname);
		if(user != null)
		{
			nickname.setText(user.getName());
		}
		editNickButton = (Button)findViewById(R.id.editNickButton);
		editNickButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nickname.setEnabled(true);
			}
		});
		portrait = (ImageView)findViewById(R.id.portrait);
		if(user != null)
		{
			initial();
			startAsynThread("init");
		}
		portrait.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();  
                intent.setType("image/*");  
                intent.setAction(Intent.ACTION_GET_CONTENT);    
                startActivityForResult(intent, 1);  
			}
		});
		
		activelist = (Button)findViewById(R.id.activelist);
		historylist = (Button)findViewById(R.id.historylist);
		historyshop = (Button)findViewById(R.id.historyshop);
		
		activelist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(SysUtil.getAPNType(getApplicationContext()) == -1){
					Toast.makeText(IndividualCenterActivity.this, "请检查您的网络连接",
							Toast.LENGTH_SHORT).show();
				}
				else{
				Intent intent = new Intent();
				intent.setClass(IndividualCenterActivity.this, AllOrderActivityNew.class);
				startActivity(intent);
				}
			}
		});
		
		historylist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(IndividualCenterActivity.this, "亲爱的，这个功能下个版本再做哦~", Toast.LENGTH_SHORT).show();
			}
		});
		
		historyshop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(IndividualCenterActivity.this, "亲爱的，这个功能下个版本再做哦~", Toast.LENGTH_SHORT).show();
			}
		});
		myprogressDialog = MyProgressDialog.createDialog(this);
		initial();
		addEventListener();
	}
	
	
	
	private void addEventListener(){
	    addClickEventListener(R.id.retButtonIndividual, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isClick == false)
				{
					isClick = true; 
					myprogressDialog.show();
					startAsynThread("upload");
					
				}
			}
		});
		
	}
	private void initial()
	{
		setUIRefreshConfig(new UIThreadImpl() {
			
			@Override
			public void refresh(Message msg) {
				// TODO Auto-generated method stub
				
				myprogressDialog.dismiss();
				if ( 1 == msg.arg2 ){
					Intent intent = new Intent();
					intent.setClass(IndividualCenterActivity.this, UserMainPageActivity.class);
					startActivity(intent);
					IndividualCenterActivity.this.finish();
					
					
//					Toast.makeText(IndividualCenterActivity.this , getResources().getString(R.string.successupdateuser), Toast.LENGTH_SHORT).show();
				}
				else if( 0 == msg.arg2)
				{
					Intent intent = new Intent();
					intent.setClass(IndividualCenterActivity.this, UserMainPageActivity.class);
					startActivity(intent);
					Toast.makeText(IndividualCenterActivity.this , getResources().getString(R.string.failupdateuser), Toast.LENGTH_SHORT).show();
					IndividualCenterActivity.this.finish();
					
				}
				else if( 2 == msg.arg2)
				{
					if(msg.obj != null)
					{
						portrait.setImageBitmap((Bitmap)msg.obj);
					}
				}
				
			}
		});
	
	setAsynThreadConfig("upload", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				// TODO Auto-generated method stub
				
				Message msg = Message.obtain();
				
				String userName=nickname.getText().toString(); 
				if(userName != null && !userName.equals(""))
				{
//					user.setName(userName);
				}
				boolean isUpdate = true;
				String tmp = null;
				
				if(flagP)//选择了照片
				{
					String path = "";
					String fileName = "";
					if(uri != null)
					{
						String realPath = getRealPath(uri);
						File file = new File(realPath);
						if(file.exists())
						{
							String str[] = realPath.split("/");
							fileName = str[str.length-1];
							path = str[0];
							for(int i=1 ; i<str.length-1 ; i++)
							{
								path += ("/"+str[i]);
							}
							path += "/";
							Log.e("path", path);
							Log.e("fileName", fileName);
							tmp = userManage.uploadUserPortrait(path, fileName);//传照片，获取照片url
							if(tmp == null)
							{
								isUpdate = false;
							}
						}	
					}
					flagP = false;
				}
				
				if(isUpdate)
				{
					if(user != null)
					{
						if(tmp != null)
						{
							user.setPortrait(tmp);
							isUpdate = userManage.updateUserInfo(user);//更新用户信息
						}
						 
					}
					if ( isUpdate ){
						msg.arg2 = 1;
					}
					else{
						msg.arg2 = 0;
					}
				}
				isUpdate = true;
				finishAsynThread("upload");
				return msg;
			}
	
		});
	
	setAsynThreadConfig("init", true, new AsynThreadImpl() {

		@Override
		public Message excute() {
			// TODO Auto-generated method stub
			
			Message msg = Message.obtain();
			
			String URL = user.getPortrait();
			Bitmap bm = userManage.getUserPortrait(URL);
			
			msg.obj = bm;
			msg.arg2 = 2;

			finishAsynThread("init");
			return msg;
		}

	});
	
	}
	
	
	
	@SuppressLint("NewApi")
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            uri = data.getData();  
            Log.e("uri", uri.toString());  
            ContentResolver cr = this.getContentResolver();  
            try {  
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri)); 
                String str = getRealPath(uri);
                if(bitmap == null )
                {
                	Toast.makeText(IndividualCenterActivity.this , getResources().getString(R.string.nonephoto), Toast.LENGTH_SHORT).show();
                	return;
                }
                if(str == null)
                {
//                	Toast.makeText(IndividualCenterActivity.this , getResources().getString(R.string.nonephoto), Toast.LENGTH_SHORT).show();
                	Toast.makeText(IndividualCenterActivity.this , "诡异的错误", Toast.LENGTH_SHORT).show();
                	return;
                }
                if(str.lastIndexOf(".jpg")!=str.length()-4 && str.lastIndexOf(".jpeg")!=str.length()-4 && str.lastIndexOf(".png")!=str.length()-4)
                {
                	Toast.makeText(IndividualCenterActivity.this , getResources().getString(R.string.uploadjpg), Toast.LENGTH_SHORT).show();
                	return;
                }
                int size = (bitmap.getRowBytes() * bitmap.getHeight());
                Log.e("store",size +"");
                if(size <= 800000)
                {
//                	Bitmap tmp = PhotoUtil.toRoundCorner(bitmap, 180.0f);
                	portrait.setImageBitmap(bitmap);  
                	flagP = true;
                }
                else
                {
                	Toast.makeText(IndividualCenterActivity.this , getResources().getString(R.string.failupdatebigfile), Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {  
                Log.e("Exception", e.getMessage(),e);  
            }  
        }  
        super.onActivityResult(requestCode, resultCode, data);  
    } 
	
	private String getRealPath(Uri fileUrl) { 
        String fileName = null; 
        Uri filePathUri = fileUrl; 
        if (fileUrl != null) { 
            if (fileUrl.getScheme().toString().compareTo("content") == 0) { 
                // content://开头的uri 
                Cursor cursor = getContentResolver().query(fileUrl, null, null, 
                        null, null); 
//            	Cursor cursor = this.managedQuery(fileUrl, MediaStore.Images.Media.DATA, null, null, null);
                if (cursor != null && cursor.moveToFirst()) { 
                	int column_index = -1;
                	try
                	{
                		Log.e("MediaStore.Images.Media.DATA ",MediaStore.Images.Media.DATA);
                		column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                		fileName = cursor.getString(column_index); // 取出文件路径 
                        if (!fileName.startsWith("/mnt")) { 
//                            fileName = "/mnt" + fileName; 
                        } 
                        cursor.close(); 
                	}catch(Exception e)
                	{
                		e.printStackTrace();
                	}
                    
                } 
            } else if (fileUrl.getScheme().compareTo("file") == 0) { 
                // file:///开头的uri 
                fileName = filePathUri.toString(); 
                fileName = filePathUri.toString().replace("file://", ""); 
                // 替换file:// 
                if (!fileName.startsWith("/mnt")) { 
                    // 加上"/mnt"头 
                    fileName += "/mnt"; 
                } 
            } 
        } 
        return fileName; 
    }
	
}