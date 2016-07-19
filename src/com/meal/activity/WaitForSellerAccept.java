package com.meal.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meal.action.GrouponManageAction;
import com.meal.action.SellerManageAction;
import com.meal.activity.ipml.AsynThreadImpl;
import com.meal.activity.ipml.UIThreadImpl;
import com.meal.bean.Global;
import com.meal.bean.Groupon;
import com.meal.bean.MyNewGouponMenuItem;
import com.meal.bean.MyNewGroupon;
import com.meal.bean.Seller;
import com.meal.dialog.MyProgressDialog;
import com.meal.util.SysUtil;

public class WaitForSellerAccept extends BaseActivity
{
	ListView waitForSellerAcceptList;
	Button retButton;
	private GrouponManageAction grouponManage = GrouponManageAction
			.getInstance();
	private SellerManageAction sellerManage = SellerManageAction.getInstance();

	private ArrayList<Object> addDataArray=null;
	private List<Map<String, Object>> addData=null;
	private List<Map<String, Object>> addMenuData=null;

	Map<String, Object> adddatamap = new HashMap<String, Object>();
	Map<String, Object> addmenumap = null;
	private MyProgressDialog progressDialog;
	AlertDialog dialog;
	
	ListView menulist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waitforselleraccept);
		if(Global.user == null)
		{
			Intent intent = new Intent();
			intent.setClass(WaitForSellerAccept.this, LoginActivity.class);
			startActivity(intent);
			WaitForSellerAccept.this.finish();
		}
		if(SysUtil.getAPNType(getApplicationContext()) == -1){
			Toast.makeText(WaitForSellerAccept.this, "请检查您的网络连接",
					Toast.LENGTH_SHORT).show();
			WaitForSellerAccept.this.finish();
		}
		progressDialog = MyProgressDialog.createDialog(WaitForSellerAccept.this);
		progressDialog.show();
		waitForSellerAcceptList = (ListView)findViewById(R.id.waitForSellerAcceptList);
		waitForSellerAcceptList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				dialog = new AlertDialog.Builder(WaitForSellerAccept.this)
				.create();
				dialog.show();
				dialog.setContentView(R.layout.waitforselleracceptdialog);
				menulist = (ListView) dialog
						.findViewById(R.id.waitForSellerAcceptDialog);
				if(addMenuData == null)
				{
					addMenuData = new ArrayList<Map<String,Object>>();
				}
				else 
				{
					addMenuData.clear();
				}
//				if(addMenuData == null)
//				{
//					Toast.makeText(dialog.getContext(), "获取订单列表失败", Toast.LENGTH_SHORT).show();
//					dialog.dismiss();
//					return;
//				}
				initdialoglist(position);
				startAsynThread("getdialoglist");
			}
		});
		
		retButton = (Button)findViewById(R.id.waitforselleraccept_retbutton);
		retButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WaitForSellerAccept.this.finish();
			}
		});
		if(addData == null)
		{
			addData = new ArrayList<Map<String, Object>>();
		}
		else
		{
			addData.clear();
		}

		init();
		startAsynThread("getwaitforselleracceptlist");
	}
	private void init()
	{
		setAsynThreadConfig("getwaitforselleracceptlist", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				// TODO Auto-generated method stub

				Message msg = Message.obtain();
				// msg.obj = sellerManage.getSellerListByType("all");

				if (Global.user != null) {
					try {
						// Long tempuid = 1L;
						Long tempuid = Global.user.getUid();

						addDataArray = grouponManage.getMyGrouponListByStatus(String.valueOf(tempuid), "complete");
						if (addDataArray == null) {
							finishAsynThread("getwaitforselleracceptlist");
							msg.arg1 = 1;//获取列表失败
							
							if (progressDialog != null
									&& progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
							return msg;
						}
						Log.e("addDataArray", addDataArray.size() + "");
						for (int i = 0; i < addDataArray.size(); i++) {
							adddatamap = new HashMap<String, Object>();
							Groupon grouponseller = (Groupon) addDataArray
									.get(i);
							Long tempsid = grouponseller.getSid();
							Long tempgid = grouponseller.getGid();
							Seller tempgrouponseller = sellerManage
									.getSellerInfo(tempsid);
							
							if (tempgrouponseller == null) {
								finishAsynThread("getwaitforselleracceptlist");
								msg.arg1 = 1;//获取列表失败
								return msg;
							}
							String tmp = tempgrouponseller.getLogo();
							Bitmap bm = null;
							if (tmp == null || tmp.equals("")) {
								bm = BitmapFactory.decodeResource(getResources(),
										R.drawable.defaultstore);
							} else {
								bm = sellerManage.getLogo(tmp);
							}
							
							adddatamap.put("poster", bm);
							adddatamap.put("name", tempgrouponseller.getDescription());
							adddatamap.put("gid", tempgid);
							addData.add(adddatamap);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					msg.arg1 = 4;//成功

				} else {

					msg.arg1 = 5;//用户未登录，失败
				}
				
				finishAsynThread("getwaitforselleracceptlist");
				return msg;
			}

		});
		setUIRefreshConfig(new UIThreadImpl() {
			
			@Override
			public void refresh(Message msg) {
				// TODO Auto-generated method stub
				if(msg.arg1 == 4)
				{
					
					waitForSellerAcceptList.setAdapter(new MyAdapter(WaitForSellerAccept.this,
							R.layout.waitforselleracceptitem, addData,
							new String[] { "poster", "name" }, new int[] {
									R.id.shop_poster_waitlistitem, R.id.shop_name_waitlistitem}));
					
				}
				else if(msg.arg1 == 1)
				{
					Toast.makeText(WaitForSellerAccept.this, "获取列表失败", Toast.LENGTH_SHORT).show();
				}
				else if(msg.arg1 == 2)
				{
					Toast.makeText(WaitForSellerAccept.this, "获取订单商品列表失败", Toast.LENGTH_SHORT).show();
				}
				else if(msg.arg1 == 3)
				{
					menulist.setAdapter(new ItemAdapter(WaitForSellerAccept.this,
							R.layout.mybuylistitem, addMenuData,
							new String[] { "name", "price" ,"count"}, new int[] {
							R.id.mybuylistNameText,
							R.id.mybuylistMoneyText,
							R.id.mybuylistCountText}));
				}
				if(progressDialog.isShowing())
				{
					progressDialog.dismiss();
				}
			}
		});
	}
	
	
	private void initdialoglist(final int position)
	{
		setAsynThreadConfig("getdialoglist", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				// TODO Auto-generated method stub

				Message msg = Message.obtain();
				// msg.obj = sellerManage.getSellerListByType("all");

				if (Global.user != null) {
						Long tempuid = Global.user.getUid();
						Map<String,Object> data = addData.get(position);
						Long gid = (Long)data.get("gid");
						MyNewGroupon addmenulist = grouponManage.getGrouponDetailByUid(
									gid, tempuid);
						ArrayList<MyNewGouponMenuItem> addmenu = addmenulist
								.getMenudata();
						
						if(addmenu != null)
						{
							for(int i=0 ; i<addmenu.size() ; i++)
							{
								addmenumap = new HashMap<String, Object>();
								MyNewGouponMenuItem item = addmenu.get(i);
								addmenumap.put("name", item.getName());
								addmenumap.put("price", item.getPrice());
								addmenumap.put("count", item.getCount());
								addMenuData.add(addmenumap);
							}
							msg.arg1 = 3;//获取订单列表成功
						}
						else
						{
							msg.arg1 = 2;//获取订单列表失败
						}
				}
				finishAsynThread("getdialoglist");
				return msg;
			}
		});
		
	}
	
	public class ItemAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		private int resId;

		private List<Map<String, Object>> listData;

		private String[] from;

		private int[] to;

		private OnClickListener clickListener;

		public ItemAdapter(Context context, int resId,
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
		public View getView(int position, View convertView, ViewGroup parent) {

			Map<String, Object> viewItem = listData.get(position);

			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(resId, null);

				holder = new ViewHolder();
				holder.itemname = (TextView) convertView.findViewById(to[0]);
				holder.itemprice = (TextView) convertView.findViewById(to[1]);
				holder.itemcount = (TextView) convertView.findViewById(to[2]);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			holder.itemname.setText(String.valueOf(viewItem.get(from[0])));
			holder.itemprice.setText(String.valueOf(viewItem.get(from[1])));
			holder.itemcount.setText(String.valueOf(viewItem.get(from[2])));
			return convertView;
		}

		private class ViewHolder {

			TextView itemname;

			TextView itemprice;

			TextView itemcount;

		}

	}
	
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		private int resId;

		private List<Map<String, Object>> listData;

		private String[] from;

		private int[] to;

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
				
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.shopPoster.setImageBitmap((Bitmap) viewItem.get(from[0]));
			holder.shopName.setText((String) viewItem.get(from[1]));

			return convertView;
		}

		private class ViewHolder {

			ImageView shopPoster;

			TextView shopName;

		}

	}
	
	
	
}