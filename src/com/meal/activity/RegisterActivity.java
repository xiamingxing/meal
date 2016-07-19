package com.meal.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.meal.action.UserManageAction;
import com.meal.activity.ipml.AsynThreadImpl;
import com.meal.activity.ipml.UIThreadImpl;
import com.meal.bean.User;
import com.meal.dialog.MyProgressDialog;
import com.meal.util.SysUtil;

@SuppressLint("ShowToast")
public class RegisterActivity extends BaseActivity {
	private Button customerRegButton;
	private Button retbutton;
	
	MyProgressDialog regProgressDialog;
	private UserManageAction userManage = UserManageAction.getInstance();
	private User user = null;
	private boolean isPasswdSame = false;
	private Spinner addressSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_register);
		customerRegButton = (Button)findViewById(R.id.customerRegButton);
		retbutton = (Button)findViewById(R.id.retButtonReg);
		retbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RegisterActivity.this.finish();
			}
		});
		
		regProgressDialog = MyProgressDialog.createDialog(this);
		addressSpinner = (Spinner)findViewById(R.id.addressSpinner);
		List<String> spinlist = new ArrayList<String>();
		spinlist.add("奇虎360A座");
		spinlist.add("奇虎360B座");
		ArrayAdapter<String> spinadapter;
		spinadapter = new ArrayAdapter<String>(RegisterActivity.this,
				android.R.layout.simple_spinner_item, spinlist);
		spinadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		addressSpinner.setAdapter(spinadapter);
//		initial();
		addEventListener();
	}
	
	@SuppressLint("ShowToast")
	private void addEventListener(){
		
		final EditText regPhone=(EditText)findViewById(R.id.customerRegPhoneNum);
	    final EditText regPasswd=(EditText)findViewById(R.id.customerRegPasswd);
	    final EditText regPasswdConfirm = (EditText)findViewById(R.id.customerConfirmPasswd);
	    
	    addClickEventListener(R.id.customerRegButton, new View.OnClickListener() {
			
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initial();
				regProgressDialog.show();
				String userName=regPhone.getText().toString(); 
			    String passWord=regPasswd.getText().toString(); 
			    String passWordConfirm = regPasswdConfirm.getText().toString();
			    int position = addressSpinner.getSelectedItemPosition();
			    String address = "";
			    if(position == 0)
			    {
			    	address = "奇虎360A座";
			    }
			    else if(position == 1)
			    {
			    	address = "奇虎360B座";
			    }
			    isPasswdSame = passWord.equals(passWordConfirm);
			    if(!isMobileNO(userName))
			    {
			    	regProgressDialog.dismiss();
			    	Toast.makeText(RegisterActivity.this, getResources().getString(R.string.invalidphonenum), Toast.LENGTH_SHORT).show();
			    	return;
			    }
			    if(!isPasswdSame)
			    {
			    	regProgressDialog.dismiss();
			    	Toast.makeText(RegisterActivity.this, getResources().getString(R.string.passwdnotsame), Toast.LENGTH_SHORT).show();
			    	return;
			    }
			    if(isPasswdSame)
			    {
			    	if(!isRightPasswd(passWord))
			    	{
			    		regProgressDialog.dismiss();
			    		Toast.makeText(RegisterActivity.this, getResources().getString(R.string.passwdwrong), Toast.LENGTH_SHORT).show();
				    	return;
			    	}
			    }
			    if(SysUtil.getAPNType(getApplicationContext()) == -1){
					Toast.makeText(RegisterActivity.this, "请检查您的网络连接",
							Toast.LENGTH_SHORT).show();
					return;
				}
			    user = new User(userName, passWord, null, userName,
			    		address);
			    startAsynThread("register");
			  
			}
		});
		
	}
	private void initial()
	{
		setUIRefreshConfig(new UIThreadImpl() {
			
			@Override
			public void refresh(Message msg) {
				// TODO Auto-generated method stub
				regProgressDialog.dismiss();
				
				if ( 1 == msg.arg2 ){
					regStart();
				}
				else if( 0 == msg.arg2){
					Toast.makeText(RegisterActivity.this, getResources().getString(R.string.failreg), Toast.LENGTH_LONG).show();
//					DialogUtil.alert(getResources().getString(R.string.faillogin), LoginActivity.this);
				}
				else if(2 == msg.arg2)
				{
					Toast toast=Toast.makeText(RegisterActivity.this, getResources().getString(R.string.nonephonenum),Toast.LENGTH_LONG);
			    	toast.setGravity(Gravity.TOP , 0,180);
			    	toast.show();
				}
				else if(3 == msg.arg2)
				{
					Toast.makeText(RegisterActivity.this, getResources().getString(R.string.passwdnoconfirm), Toast.LENGTH_SHORT).show();
				}
			}
		});
	
	setAsynThreadConfig("register", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				// TODO Auto-generated method stub
				
				Message msg = Message.obtain();
				boolean isSuccess =false;
				if(user !=null)
				{
					isSuccess = userManage.register(user);
					if ( isSuccess ){
						msg.arg2 = 1;
					}
					else{
						msg.arg2 = 0;
					}
				}
				else if(user == null && isPasswdSame)
				{
					msg.arg2 = 2;
				}
				else if(user == null && !isSuccess)
				{
					msg.arg2 = 3;
				}
				
	
				finishAsynThread("register");
				return msg;
			}
	
		});
	}
	
	public void regStart()
	{
    	Toast.makeText(RegisterActivity.this, getResources().getString(R.string.successreg), Toast.LENGTH_LONG).show();
    	
		Intent intent=new Intent();
		intent.setClass(RegisterActivity.this,IndividualCenterActivity.class);
		startActivity(intent);
		
	}
	
	public boolean isMobileNO(String mobiles)
	{  
	   Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
	   Matcher m = p.matcher(mobiles);   
	   System.out.println(m.matches()+"---");    
	    return m.matches();
	}
	
	private boolean isRightPasswd(String passwd)//只允许6到20位数字+字符
	{
		boolean isRight = false;

		if(passwd == null || passwd.equals("") || passwd.length()<6 || passwd.length()>20)
		{
			return isRight;
		}
		
		for(int i=0 ; i<passwd.length() ; i++)
		{
			char c = passwd.charAt(i);
			if((c>='0' && c<='9') || (c>='a' && c<='z') || c>='A' && c<='Z')
			{
				
			}
			else
			{
				return isRight;
			}
		}
		
		isRight = true;
		return isRight;
	}

}
