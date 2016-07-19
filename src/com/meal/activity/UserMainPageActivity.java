package com.meal.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.meal.action.GrouponManageAction;
import com.meal.action.SellerManageAction;
import com.meal.action.UserManageAction;
import com.meal.activity.ipml.AsynThreadImpl;
import com.meal.activity.ipml.UIThreadImpl;
import com.meal.bean.Global;
import com.meal.bean.Groupon;
import com.meal.bean.Seller;
import com.meal.bean.User;
import com.meal.dialog.MyProgressDialog;
import com.meal.update.UpdateManager;
import com.meal.util.CallBackForDialogBtn;
import com.meal.util.DialogUtil;
import com.meal.util.SysUtil;

@SuppressLint("ResourceAsColor")
public class UserMainPageActivity extends BaseActivity {

	private SellerManageAction sellerManage = SellerManageAction.getInstance();
	private GrouponManageAction grouponManage = GrouponManageAction
			.getInstance();

	private TabHost user_mainTab_host;

	private ImageView user_MainPageArrowdown;

	private String[] texts;

	private ListView waitList;
	
	private ListView allStoreList;


	private List<Map<String, Object>> waitData;
	private List<Map<String, Object>> storeData;
	private ArrayList<Object> waitDataArray;
	private ArrayList<Object> storeDataArray;
	Map<String, Object> waitdatamap = new HashMap<String, Object>();
	Map<String, Object> storedatamap = null;
	ArrayList<Long> waitsidarray = new ArrayList<Long>();
	ArrayList<Long> storesidarray = new ArrayList<Long>();
	ArrayList<Boolean> storestatusarray = new ArrayList<Boolean>();
	ArrayList<Long> waitgidarray = new ArrayList<Long>();
	ArrayList<Long> waituidarray = new ArrayList<Long>();
	
	
	private int mainpageButtonFlag = 0;
	private int tabFlag = 0;
	private boolean storeFlag = true;
	private boolean waitFlag = true;
	
	private Button user_mainButton;
	private LinearLayout user_mainpageLayout2;
	private LinearLayout user_mainpageLayoutRight;
	
	private ImageView user_mainpagePhoto;
	private Button user_mainpageLogin;
	
	private Button attendOrderButton;
	private Button todayNotCompleteButton;
	private Button todayAcceptButton;
	private Button hisOrderButton;
	private Button evaluedButton;
	private Button logoutButton;
	private Button user_mainpage_versionupdate;
	private Button user_mainpage_gotobuy;
	
	private Button refreshButton;
	
	private UserManageAction userManage = UserManageAction.getInstance();
	private MyProgressDialog progressDialog;
	
	private int quittimes = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_mainpage_activity);
		texts = new String[] { getResources().getString(R.string.waiting),
				getResources().getString(R.string.totalshop) };
		initial();
		initWidget();
	}

	private void initWidget() {
		progressDialog = MyProgressDialog.createDialog(this);
		
		
		user_mainpageLayout2 = (LinearLayout)findViewById(R.id.user_mainpageLayout2);
		user_mainpageLayout2.setVisibility(View.INVISIBLE);
		user_mainpageLayout2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		user_mainpageLogin = (Button)findViewById(R.id.user_mainpageLogin);
		user_mainpageLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Global.user == null)
				{
					Intent intent = new Intent();
					intent.setClass(UserMainPageActivity.this,
								LoginActivity.class);
					startActivity(intent);
				}
				else
				{
					Intent intent = new Intent();
					intent.setClass(UserMainPageActivity.this,
								IndividualCenterActivity.class);
					startActivity(intent);
				}
			}
		});
		
		user_mainpagePhoto = (ImageView)findViewById(R.id.user_mainpagePhoto);
		
		attendOrderButton = (Button)findViewById(R.id.user_mainpage1);
		user_mainpage_gotobuy = (Button)findViewById(R.id.user_mainpage_gotobuy);
		user_mainpage_versionupdate = (Button)findViewById(R.id.user_mainpage_versionupdate);
		todayNotCompleteButton = (Button)findViewById(R.id.user_prepare_order);
		todayAcceptButton = (Button)findViewById(R.id.user_queren_order);
		hisOrderButton = (Button)findViewById(R.id.user_mainpage2);
		evaluedButton = (Button)findViewById(R.id.user_mainpage3);
		logoutButton = (Button)findViewById(R.id.user_mainpage4);
		
		attendOrderButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(SysUtil.getAPNType(getApplicationContext()) == -1){
					Toast.makeText(UserMainPageActivity.this, "请检查您的网络连接",
							Toast.LENGTH_SHORT).show();
				}else{
//					AllOrderActivityNew.this.finish();
					if(Global.user == null)
					{
						Toast.makeText(UserMainPageActivity.this, "请先登录",
								Toast.LENGTH_SHORT).show();
					}
					else
					{
						Intent intent = new Intent();
						intent.setClass(UserMainPageActivity.this, AllOrderActivityNew.class);
						startActivity(intent);
					}
				}
			}
		});
		user_mainpage_versionupdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UpdateManager updateManager = new UpdateManager(UserMainPageActivity.this);
				updateManager.checkUpdate();
			}
		});
		todayNotCompleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(UserMainPageActivity.this, WaitForSellerAccept.class);
				startActivity(intent);
//				Toast.makeText(UserMainPageActivity.this, "亲爱的，这个功能下个版本再做哦~", Toast.LENGTH_SHORT).show();
			}
		});
		todayAcceptButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(UserMainPageActivity.this, "亲爱的，这个功能下个版本再做哦~", Toast.LENGTH_SHORT).show();
			}
		});
		hisOrderButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(UserMainPageActivity.this, "亲爱的，这个功能下个版本再做哦~", Toast.LENGTH_SHORT).show();
			}
		});
		evaluedButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(UserMainPageActivity.this, "亲爱的，这个功能下个版本再做哦~", Toast.LENGTH_SHORT).show();
			}
		});
		logoutButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Global.user != null)
				{
					DialogUtil.prompt("确认注销吗？", UserMainPageActivity.this, new CallBackForDialogBtn(){

						@Override
						public void cancel() {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void confirm() {
							// TODO Auto-generated method stub
							userManage.logout(UserMainPageActivity.this);
							user_mainpageLogin.setText(getResources().getString(R.string.logintext));
							user_mainpagePhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.defaultman));
						}
						
					});
					
				}
			}
		});
		
		
		user_mainButton = (Button)findViewById(R.id.user_mainButton);
		user_mainButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mainpageButtonFlag == 0)
				{
					mainpageButtonFlag++;
					user_mainpageLayout2.setVisibility(View.VISIBLE);
				}
				else if(mainpageButtonFlag == 1)
				{
					mainpageButtonFlag--;
					user_mainpageLayout2.setVisibility(View.INVISIBLE);
				}
				if(Global.user != null)
				{
					initial();
					startAsynThread("init");	
				}
				else
				{
					user_mainpageLogin.setText(getResources().getString(R.string.logintext));
					user_mainpagePhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.defaultman));
				}
			}
		});

		user_mainpageLayoutRight = (LinearLayout)findViewById(R.id.user_mainpageLayoutRight);
		user_mainpageLayoutRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mainpageButtonFlag--;
				user_mainpageLayout2.setVisibility(View.INVISIBLE);
			}
		});
		user_mainpage_gotobuy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mainpageButtonFlag--;
				user_mainpageLayout2.setVisibility(View.INVISIBLE);
			}
		});
		user_mainTab_host = (TabHost) findViewById(R.id.user_mainTab_host);
		user_mainTab_host.setup();
		initTabHost();

		waitList = (ListView) findViewById(R.id.userTab_spec_first);
		initList();
		
		allStoreList = (ListView)findViewById(R.id.userTab_spec_second);
		initStoreList();
		refreshButton = (Button)findViewById(R.id.clickRefresh);
		refreshButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				waitList = (ListView) findViewById(R.id.userTab_spec_first);
				allStoreList = (ListView)findViewById(R.id.userTab_spec_second);
				if (waitData == null) {
					waitData = new ArrayList<Map<String, Object>>();
				}
				else
				{
					waitData.clear();
				}
				if (storeData == null) {
					storeData = new ArrayList<Map<String, Object>>();
				}
				else
				{
					storeData.clear();
				}
				refreshButton.setEnabled(false);
				if(waitFlag && storeFlag)
				{

						initList();

						initStoreList();

				}

			}
		});
	}

	private void initTabHost() {
		user_mainTab_host.addTab(user_mainTab_host.newTabSpec("tab_spec_first")
				.setIndicator(getItemView(R.layout.tab_widget, 0, true))
				.setContent(R.id.userTab_spec_first));
		user_mainTab_host.addTab(user_mainTab_host
				.newTabSpec("tab_spec_second")
				.setIndicator(getItemView(R.layout.tab_widget, 1, false))
				.setContent(R.id.userTab_spec_second));

		user_mainTab_host.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				if ("tab_spec_first".equals(tabId)) {
					ImageView imgView1 = (ImageView) user_mainTab_host
							.getTabWidget().getChildAt(0)
							.findViewById(R.id.image);
					imgView1.setBackgroundColor(getResources().getColor(
							R.color.head_yellow));

					ImageView imgView2 = (ImageView) user_mainTab_host
							.getTabWidget().getChildAt(1)
							.findViewById(R.id.image);
					imgView2.setBackgroundColor(getResources().getColor(
							R.color.white));
					tabFlag = 0;
					
				} else if ("tab_spec_second".equals(tabId)) {
					ImageView imgView1 = (ImageView) user_mainTab_host
							.getTabWidget().getChildAt(0)
							.findViewById(R.id.image);
					imgView1.setBackgroundColor(getResources().getColor(
							R.color.white));

					ImageView imgView2 = (ImageView) user_mainTab_host
							.getTabWidget().getChildAt(1)
							.findViewById(R.id.image);
					imgView2.setBackgroundColor(getResources().getColor(
							R.color.head_yellow));
					tabFlag = 1;
				}
			}
		});
	}

	private View getItemView(int resId, int index, boolean focus) {
		LinearLayout ll = (LinearLayout) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.tab_widget, null);
		TextView textView = (TextView) ll.findViewById(R.id.text);
		textView.setText(texts[index]);
		user_MainPageArrowdown = (ImageView) ll
				.findViewById(R.id.user_MainPageArrowdown);
		user_MainPageArrowdown.setVisibility(View.INVISIBLE);
		if (index == 1) {
//			user_MainPageArrowdown.setVisibility(View.VISIBLE);
		}
		ImageView imgView = (ImageView) ll.findViewById(R.id.image);
		if (focus) {
			imgView.setBackgroundColor(getResources().getColor(
					R.color.head_yellow));
		} else {
			imgView.setBackgroundColor(getResources().getColor(R.color.white));
		}
		return ll;
	}

	private void initList() {
		if(!progressDialog.isShowing())
		{
			progressDialog.show();
		}
		processWaitingList(false);
	}

	private void processWaitingList(boolean realImage) {

		if (waitData == null) {
			waitData = new ArrayList<Map<String, Object>>();
		}
		else
		{
			waitData.clear();
		}
		initial();
		waitFlag = false;
		
		startAsynThread("getGrouponListForJoin");
	}

	private void initStoreList()
	{
		if(!progressDialog.isShowing())
		{
			progressDialog.show();
		}
		processStoreList(false);
	}
	
	
	private void processStoreList(boolean realImage) {

		if (storeData == null) {
			storeData = new ArrayList<Map<String, Object>>();
		}
		else
		{
			storeData.clear();
		}
		initial();
		storeFlag = false;
		startAsynThread("getSellerListByType");

	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		private int resId;

		private List<Map<String, Object>> listData;

		private String[] from;

		private int[] to;

		private OnClickListener clickListener;

		public MyAdapter(Context context, int resId,
				List<Map<String, Object>> listData, String[] from, int[] to) {

			mInflater = LayoutInflater.from(context);
			this.resId = resId;
			this.listData = listData;
			this.from = from;
			this.to = to;

		}

		// @Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listData.size();
		}

		// @Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listData.get(position);
		}

		// @Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public void setClickListener(OnClickListener clickListener) {
			this.clickListener = clickListener;
			
		}

		// @Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Map<String, Object> viewItem = null;
			try
			{
				if(position>listData.size()-1)
				{
					return convertView;
				}
				viewItem = listData.get(position);
			}catch(Exception e)
			{
				e.printStackTrace();
				return convertView;
			}

			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(resId, null);

				holder = new ViewHolder();
				holder.shopPoster = (ImageView) convertView.findViewById(to[0]);
				holder.shopName = (TextView) convertView.findViewById(to[1]);
				holder.shopQuality = (ImageView) convertView
						.findViewById(to[2]);
				holder.shopTotalMoney = (TextView) convertView
						.findViewById(to[3]);
				holder.shopLeftTime = (TextView) convertView
						.findViewById(to[4]);
				holder.shopLeftMoney = (TextView) convertView
						.findViewById(to[5]);
				holder.shopGoFollow = (Button) convertView.findViewById(to[6]);
				
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.shopPoster.setImageBitmap((Bitmap) viewItem.get(from[0]));
			holder.shopName.setText((String) viewItem.get(from[1]));
			holder.shopQuality.setBackgroundResource((Integer) viewItem
					.get(from[2]));
			holder.shopTotalMoney.setText((String) viewItem.get(from[3]));
			holder.shopLeftTime.setText((String) viewItem.get(from[4]));
			holder.shopLeftMoney.setText((String) viewItem.get(from[5]));
			holder.shopGoFollow.setText((String) viewItem.get(from[6]));

			holder.shopGoFollow.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(tabFlag == 0)
					{
						Long tempuid = waituidarray.get(position);
						if(Global.user == null)
						{
							Toast.makeText(UserMainPageActivity.this, "请您先登录", Toast.LENGTH_SHORT).show();
							return;
						}
//						if(tempuid != null && Global.user!=null && Global.user.getUid()!= null && tempuid.equals(Global.user.getUid()))
//						{
//							Toast.makeText(UserMainPageActivity.this, "亲，别跟自己的单", Toast.LENGTH_SHORT).show();
//						}
//						else
//						{
							Bundle bundle = new Bundle();
							bundle.putLong("sid",waitsidarray.get(position));
							bundle.putLong("gid",waitgidarray.get(position));
							bundle.putLong("uid",waituidarray.get(position));
							Intent intent = new Intent();
							intent.putExtras(bundle);
							intent.setClass(UserMainPageActivity.this,
									AddGoodslistActivity.class);
							startActivity(intent);
//						}
					}
					else if(tabFlag == 1)
					{
						if(storestatusarray.get(position)!=null && storestatusarray.get(position))
						{
							Bundle bundle = new Bundle();
							bundle.putLong("sid",storesidarray.get(position));
							Intent intent = new Intent();
							intent.putExtras(bundle);
							intent.setClass(UserMainPageActivity.this,
									GoodslistActivity.class);
							startActivity(intent);
						}
						else
						{
							Toast.makeText(UserMainPageActivity.this, "商家去休息了，过一会再来吧",Toast.LENGTH_SHORT ).show();
						}
						
					}
				}
			});

			return convertView;
		}

		private class ViewHolder {

			ImageView shopPoster;

			TextView shopName;

			ImageView shopQuality;

			TextView shopTotalMoney;

			TextView shopLeftTime;

			TextView shopLeftMoney;

			Button shopGoFollow;

		}

	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(Global.user != null)
		{
//			addEventListener();
			initial();
			startAsynThread("refresh");
			
			
		}
		else
		{
			user_mainpageLogin.setText(getResources().getString(R.string.logintext));
			user_mainpagePhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.defaultman));
		}
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(quittimes == 0)
		{
			quittimes++;
		}
		else if(quittimes == 1)
		{
			UserMainPageActivity.this.finish();
		}
		return true;
	}

	private void initial()
	{
		setUIRefreshConfig(new UIThreadImpl() {
			
			@Override
			public void refresh(Message msg) {
				// TODO Auto-generated method stub
				if(msg.arg1 == 3)
				{
					user_mainpageLogin.setText(Global.user.getName());
					user_mainpagePhoto.setImageBitmap((Bitmap)msg.obj);
				}
				else if(msg.arg1 == 1)
				{
					Toast.makeText(UserMainPageActivity.this, "获取商家列表失败",Toast.LENGTH_SHORT ).show();
					refreshButton.setEnabled(true);
				}
				else if(msg.arg1 == 2)
				{
					Toast.makeText(UserMainPageActivity.this, "获取凑单列表失败",Toast.LENGTH_SHORT ).show();
					refreshButton.setEnabled(true);
				}
				else if(msg.arg1 == 4)
				{
					allStoreList.setAdapter(new MyAdapter(UserMainPageActivity.this,
							R.layout.user_waiting_list_item, storeData, new String[] {
									"poster", "name", "quality", "totalmoney", "lefttime",
									"leftmoney", "follow" }, new int[] { R.id.shop_poster,
									R.id.shop_name, R.id.shop_quality,
									R.id.shop_total_money, R.id.shop_left_time,
									R.id.shop_left_money, R.id.shop_go_follow }));
					refreshButton.setEnabled(true);
				}
				else if(msg.arg1 == 5)
				{
					waitList.setAdapter(new MyAdapter(UserMainPageActivity.this,
							R.layout.user_waiting_list_item, waitData, new String[] {
									"poster", "name", "quality", "totalmoney", "lefttime",
									"leftmoney", "follow" }, new int[] { R.id.shop_poster,
									R.id.shop_name, R.id.shop_quality,
									R.id.shop_total_money, R.id.shop_left_time,
									R.id.shop_left_money, R.id.shop_go_follow }));
					refreshButton.setEnabled(true);
				}
				else if(msg.arg1 == 6)
				{
					Toast.makeText(UserMainPageActivity.this, "没有人发起凑单，赶紧去发起吧~", Toast.LENGTH_LONG).show();
				}
			}
		});
	
	    setAsynThreadConfig("refresh", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				// TODO Auto-generated method stub
				
				Message msg = Message.obtain();
				
				
				Bitmap bm = userManage.getUserPortrait(Global.user.getPortrait());
//				Bitmap bm = userManage.getUserPortrait("http://tb1.bdstatic.com/tb/bxb.png");
				
				if(bm == null)
				{
					bm = BitmapFactory.decodeResource(getResources(), R.drawable.defaultman);
				}
				msg.obj = bm;
				
				msg.arg1 = 3;
				finishAsynThread("refresh");
				return msg;
			}
	
		});
	    setAsynThreadConfig("init", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				// TODO Auto-generated method stub
				
				Message msg = Message.obtain();
				Bitmap bm = userManage.getUserPortrait(Global.user.getPortrait());
				if(bm == null)
				{
					bm = BitmapFactory.decodeResource(getResources(), R.drawable.defaultman);
				}
				
				msg.obj = bm;
				msg.arg1 = 3;
				
				finishAsynThread("init");
				return msg;
			}
	
		});
	    setAsynThreadConfig("getSellerListByType", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				// TODO Auto-generated method stub

				Message msg = Message.obtain();
				// msg.obj = sellerManage.getSellerListByType("all");

				storeDataArray = sellerManage.getSellerList("all");
				if(storeDataArray != null)
				{
					for (int i = 0; i < storeDataArray.size(); i++) {
						Seller tempseller = (Seller) storeDataArray.get(i);
						if (null != tempseller) {
							String logo = tempseller.getLogo();
							storedatamap = new HashMap<String, Object>();
							Bitmap tmp = sellerManage.getLogo(logo);
							if(tmp == null || tmp.getHeight()<=0 || tmp.getWidth()<=0)
							{
								tmp = BitmapFactory.decodeResource(getResources(), R.drawable.defaultstore);
							}
							storedatamap.put("poster", tmp);
							storedatamap.put("name", tempseller.getDescription()+"  "+tempseller.getPhone());
							storedatamap.put("quality", R.drawable.fivestar);
							storedatamap.put("totalmoney", tempseller.getMinCost()
									+ "元起送");
							storedatamap.put("lefttime", "");
							storedatamap.put("leftmoney", "");
							storedatamap.put("follow", "进入店铺");
							storesidarray.add(i, tempseller.getSid());
							storestatusarray.add(i,tempseller.getOrderFunctionSwitch());
	
							storeData.add(storedatamap);
	
						}
	
					}
					msg.arg1 = 4;
				}
				else
				{
					msg.arg1 = 1;
				}
				
				finishAsynThread("getSellerListByType");
				
				storeFlag = true;
				if(progressDialog != null && progressDialog.isShowing() && waitFlag == true)
				{
					progressDialog.dismiss();
				}
				return msg;
			}

		});
	    setAsynThreadConfig("getGrouponListForJoin", true,
				new AsynThreadImpl() {

					@Override
					public Message excute() {
						// TODO Auto-generated method stub

						Message msg = Message.obtain();
						// msg.obj = sellerManage.getSellerListByType("all");

						waitDataArray = grouponManage
								.getGrouponListForJoin();

						if(waitDataArray != null)
						{
							if(waitDataArray.size()==0)
							{
								msg.arg1 = 6;
								finishAsynThread("getGrouponListForJoin");
								waitFlag = true;
								if(progressDialog != null && progressDialog.isShowing() && storeFlag == true)
								{
									progressDialog.dismiss();
								}
								return msg;
								
							}
							for (int i = 0; i < waitDataArray.size(); i++) {
								Groupon grouponseller = (Groupon) waitDataArray
										.get(i);
								Long tempsid = grouponseller.getSid();
								Long tempgid = grouponseller.getGid();
								Long tempuid = grouponseller.getUid();
								Seller tempgrouponseller = sellerManage.getSellerInfo(tempsid);
								if(tempgrouponseller == null)
								{
									msg.arg1 = 2;
								}
								else
								{
									waitdatamap = new HashMap<String, Object>();
									Bitmap templogo = null;
									try
									{
										templogo = sellerManage
												.getLogo(tempgrouponseller.getLogo());
									}catch(Exception e)
									{
										e.printStackTrace();
									}
									
									if(templogo == null || templogo.getHeight()<=0 || templogo.getWidth()<=0)
									{
										templogo = BitmapFactory.decodeResource(getResources(), R.drawable.defaultstore);
									}
									Log.d("logo height ",templogo.getHeight()+"");
									Log.d("logo width ",templogo.getWidth()+"");
									waitdatamap.put("poster", templogo);
									waitdatamap.put("name", tempgrouponseller.getDescription());
									waitdatamap.put("quality", R.drawable.fivestar);
									String phone = "";
									Long _uid = grouponseller.getUid();
									if(_uid!=null && _uid>0)
									{
										User user = userManage.getUserInfoByUid(_uid);
										if(user!=null)
										{
											phone = user.getPhone();
										}
									}
									if(phone.equals(""))
									{
										waitdatamap.put("totalmoney",
												tempgrouponseller.getMinCost() + "元起送");
									}
									else
									{
										waitdatamap.put("totalmoney",
												phone+"发起的凑单"+"\n"+tempgrouponseller.getMinCost() + "元起送");
									}
									
									Long time = grouponseller.getResidueTime();
									if(time < 0)
									{
										time = 0l;
									}
									waitdatamap.put("lefttime",
											"剩余时间" + time
													+ "分钟");
									double cost = grouponseller.getResidueCost();
									
									if(cost < 0)
									{
										cost = 0;
									}
									else
									{
										cost = (double)(Math.round(cost*100))/100;
									}
									
									waitdatamap.put("leftmoney",
												"还差" + cost
														+ "元即可提交");
									waitdatamap.put("follow", "去跟单");
									waitsidarray.add(i, tempsid);
									waitgidarray.add(i, tempgid);
									waituidarray.add(i, tempuid);
									waitData.add(waitdatamap);
								}
								msg.arg1 = 5;
							}
						}
						else
						{
							msg.arg1 = 2;
						}

						finishAsynThread("getGrouponListForJoin");
						
						waitFlag = true;
						if(progressDialog != null && progressDialog.isShowing() && storeFlag == true)
						{
							progressDialog.dismiss();
						}
						return msg;
					}

				});
	}

}
