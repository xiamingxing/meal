package com.meal.activity;


import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meal.action.UserManageAction;
import com.meal.activity.ipml.AsynThreadImpl;
import com.meal.activity.ipml.UIThreadImpl;
import com.meal.bean.User;
import com.meal.dialog.MyProgressDialog;
import com.meal.util.SysUtil;

public class LoginActivity extends 	BaseActivity{
	private Button customerRegisterButton;
	private Button retButton;
	private MyProgressDialog loginProgressDialog;
	private HashMap<String, String> loginInfo = new HashMap<String, String>(); //loginInfo

	private UserManageAction userManage = UserManageAction.getInstance();
	private boolean isPhoneNum = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_login);
		customerRegisterButton = (Button)findViewById(R.id.customerRegButton);
		
		customerRegisterButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.setClass(LoginActivity.this,
							RegisterActivity.class);
				startActivity(intent);
			}
		});
		
		retButton = (Button)findViewById(R.id.retButton1);
		retButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LoginActivity.this.finish();
			}
		});
		loginProgressDialog = MyProgressDialog.createDialog(this);
//		initial();
		addEventListener();
	}
	
	private void addEventListener(){
		
		final EditText loginPhone=(EditText)findViewById(R.id.customerLoginName);
	    final EditText loginPasswd=(EditText)findViewById(R.id.customerLoginPasswd); 
	    
	    addClickEventListener(R.id.customerLoginButton, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				int x = SysUtil.getAPNType(getApplicationContext());
//				if(x < 0)
//				{
//					Log.e("net",x+"");
//					Toast.makeText(LoginActivity.this, getResources().getString(R.string.badnet), Toast.LENGTH_SHORT).show();
//					return;
//				}
				initial();
				
				String userName=loginPhone.getText().toString(); 
			    String passWord=loginPasswd.getText().toString(); 
			    Log.d("userName", userName);
			    Log.d("userPasswd", passWord);
			    isPhoneNum = isMobileNO(userName);
			    if(!isPhoneNum)
			    {
			    	Toast.makeText(LoginActivity.this, getResources().getString(R.string.usernamewrong), Toast.LENGTH_SHORT).show();
			    	return;
			    }
			    if(userName == null || userName.equals("") || passWord==null || passWord.equals(""))
			    {
			    	if(userName == null || userName.equals(""))
			    	{
			    		Toast.makeText(LoginActivity.this, getResources().getString(R.string.usernamewrong), Toast.LENGTH_SHORT).show();
			    	}
			    	else if(passWord==null || passWord.equals(""))
			    	{
			    		Toast.makeText(LoginActivity.this, getResources().getString(R.string.passwdwrong), Toast.LENGTH_SHORT).show();
			    	}
			    }
			    else
			    {
			    	loginProgressDialog.show();
				    if(isPhoneNum)
				    {

		    			loginInfo.clear();
		    			loginInfo.put("userName", userName);
		    			loginInfo.put("passWord", passWord);
				    }
				    startAsynThread("login");
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
				loginProgressDialog.dismiss();
				
				if ( 1 == msg.arg2 ){
					loginStart();
				}
				else if(0 == msg.arg2){
					Toast.makeText(LoginActivity.this, getResources().getString(R.string.faillogin), Toast.LENGTH_SHORT).show();
//					DialogUtil.alert(getResources().getString(R.string.faillogin), LoginActivity.this);
				}
				else if( 2== msg.arg2)
				{
					Toast toast=Toast.makeText(LoginActivity.this, getResources().getString(R.string.nonephonenum),Toast.LENGTH_SHORT);
			    	toast.setGravity(Gravity.TOP , 0,180);
			    	toast.show();
				}
			}
		});
	
	setAsynThreadConfig("login", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				if(isPhoneNum)
				{
					msg.obj = userManage.login(loginInfo.get("userName"),
							loginInfo.get("passWord"), LoginActivity.this);
					if ( null != msg.obj ){
						msg.arg2 = 1;
					}
					else{
						msg.arg2 = 0;
					}
				}
				else
				{
					msg.arg2 = 2;
				}
	
				finishAsynThread("login");
				return msg;
			}
	
		});
	}
	
	public void loginStart()
	{
    	Toast.makeText(LoginActivity.this, getResources().getString(R.string.successlogin), Toast.LENGTH_LONG).show();
    	LoginActivity.this.finish();
//		Intent intent=new Intent();
//		intent.setClass(LoginActivity.this,IndividualCenterActivity.class);
//		startActivity(intent);
		
	}
	
	public boolean isMobileNO(String mobiles)
	{  
	   Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
	   Matcher m = p.matcher(mobiles);   
	   System.out.println(m.matches()+"---");    
	   return m.matches();
	}
}
