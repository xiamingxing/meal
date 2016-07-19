package com.meal.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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
public class AllOrderActivityNew extends BaseActivity{

	Button retButton;
	ListView myList;
	ListView myMenuList;
	private String text;
	
	boolean listLodingFlag = false;
	boolean menuLoadingFlag = false;
	boolean resumeFlag = false;
	boolean refreshFlag = false;
	private AlertDialog dialog;
	
	private List<Map<String, Object>> allListData;
	private List<Map<String, Object>> newListData;
	private List<Map<String, Object>> completeListData;
	private List<Map<String, Object>> successListData;
	private List<Map<String, Object>> refuseListData;
	private List<Map<String, Object>> closeListData;
	private List<Map<String, Object>> dieListData;
	
	private List<Map<String, Object>> newMenuData;
	private List<Map<String, Object>> completeMenuData;
	private List<Map<String, Object>> successMenuData;
	private List<Map<String, Object>> refuseMenuData;
	private List<Map<String, Object>> closeMenuData;
	private List<Map<String, Object>> dieMenuData;
	
	private ArrayList<Object> AllDataArray;
	private ArrayList<Object> newDataArray;
	private ArrayList<Object> completeDataArray;
	private ArrayList<Object> successDataArray;
	private ArrayList<Object> refuseDataArray;
	private ArrayList<Object> closeDataArray;
	private ArrayList<Object> dieDataArray;
	
	Map<String, Object> ListDataMap = new HashMap<String, Object>();
	private List<Map<String, Object>> menuData;
	Map<String, Object> menuMap = new HashMap<String, Object>();
//	Map<String, Object> completeListDataMap = new HashMap<String, Object>();
//	Map<String, Object> successListDataMap = new HashMap<String, Object>();
//	Map<String, Object> refuseListDataMap = new HashMap<String, Object>();
//	Map<String, Object> closeListDataMap = new HashMap<String, Object>();
//	Map<String, Object> dieListDataMap = new HashMap<String, Object>();
	
	Map<String, Object> newMenuDataMap = new HashMap<String, Object>();
	Map<String, Object> completeMenuDataMap = new HashMap<String, Object>();
	Map<String, Object> successMenuDataMap = new HashMap<String, Object>();
	Map<String, Object> refuseMenuDataMap = new HashMap<String, Object>();
	Map<String, Object> closeMenuDataMap = new HashMap<String, Object>();
	Map<String, Object> dieMenuDataMap = new HashMap<String, Object>();
	
	ArrayList<Long> allSidArray = new ArrayList<Long>();
	ArrayList<Long> newSidArray = new ArrayList<Long>();
	ArrayList<Long> completeSidArray = new ArrayList<Long>();
	ArrayList<Long> successSidArray = new ArrayList<Long>();
	ArrayList<Long> refuseSidArray = new ArrayList<Long>();
	ArrayList<Long> closeSidArray = new ArrayList<Long>();
	ArrayList<Long> dieSidArray = new ArrayList<Long>();
	
	ArrayList<Long> allGidArray = new ArrayList<Long>();
	ArrayList<Long> newGidArray = new ArrayList<Long>();
	ArrayList<Long> completeGidArray = new ArrayList<Long>();
	ArrayList<Long> successGidArray = new ArrayList<Long>();
	ArrayList<Long> refuseGidArray = new ArrayList<Long>();
	ArrayList<Long> closeGidArray = new ArrayList<Long>();
	ArrayList<Long> dieGidArray = new ArrayList<Long>();
	
	private SellerManageAction sellerManage = SellerManageAction.getInstance();
	private GrouponManageAction grouponManage = GrouponManageAction
			.getInstance();
	private MyProgressDialog progressDialog;
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_allorder_activity_new);
		text = getResources().getString(R.string.mylisttext);
		if (SysUtil.getAPNType(getApplicationContext()) == -1) {
			Toast.makeText(AllOrderActivityNew.this, "请检查您的网络连接",
					Toast.LENGTH_SHORT).show();
			// AllOrderActivityNew.this.finish();
		} else {
			initWidget();
		}
	}
	
	private void initdelete(final int position)
	{
		setAsynThreadConfig("delete", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				Message msg = Message.obtain();
				Long gidmap = allGidArray.get(position);
				if(gidmap == null)
				{
					msg.arg1 = 8;//gid error
					finishAsynThread("delete");
				}
				else
				{
					//调用后台删除函数
					if(Global.user == null)
					{
						Toast.makeText(AllOrderActivityNew.this, "请先登录",
								Toast.LENGTH_SHORT).show();
						finishAsynThread("delete");
						msg.arg1 = 10;//未登录
						return msg;
					}
					if(grouponManage.retreatOrderByUid(Global.user.getUid(), gidmap))
					{
						finishAsynThread("delete");
						msg.arg1 = 9;//删除成功
					}
					else
					{
						finishAsynThread("delete");
						msg.arg1 = 11;//删除失败
					}
				}
				return msg;
			}
		});
	}
	
	private void initWidget() {
		
		progressDialog = MyProgressDialog.createDialog(this);
		retButton = (Button) findViewById(R.id.user_orderRetButton);
		retButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(AllOrderActivityNew.this,
						UserMainPageActivity.class);
				startActivity(intent);
				AllOrderActivityNew.this.finish();

				// 向商家发送信息
			}
		});
		
		//UI刷新toast提示，需修改
		setUIRefreshConfig(new UIThreadImpl() {

			@Override
			public void refresh(Message msg) {
				try {
					
					// TODO Auto-generated method stub
					if (msg.arg1 == 0) {
						Toast.makeText(AllOrderActivityNew.this, "没有任何凑单信息哦",
								Toast.LENGTH_SHORT).show();
					} else if (msg.arg1 == 1) {
						Toast.makeText(AllOrderActivityNew.this, "获取商家信息失败",
								Toast.LENGTH_SHORT).show();
					} else if (msg.arg1 == 2) {
						Toast.makeText(AllOrderActivityNew.this, "团购单状态异常",
								Toast.LENGTH_SHORT).show();
					} else if (msg.arg1 == 3) {
						myList.setAdapter(new MyAdapter(
								AllOrderActivityNew.this, R.layout.user_order_list_item,
								allListData, new String[] { "poster", "name", "quality",
										"leftmoney", "lefttime", "status"}, new int[] {
										R.id.ordershop_poster, R.id.ordershop_name,
										R.id.ordershop_quality,
										R.id.ordershop_leftmoney,
										R.id.ordershop_lefttime,
										R.id.order_status}));
					} else if (msg.arg1 == 4) {
						Toast.makeText(AllOrderActivityNew.this, "请您先登录",
								Toast.LENGTH_SHORT).show();
					} else if (msg.arg1 == 5) {
						Toast.makeText(AllOrderActivityNew.this, "获取订单详情失败",
								Toast.LENGTH_SHORT).show();
					} else if (msg.arg1 == 6) {
						myMenuList.setAdapter(new ItemAdapter(
								AllOrderActivityNew.this, R.layout.mybuylistitem,
								menuData, new String[] { "name", "price",
										"count" }, new int[] {
										R.id.mybuylistNameText,
										R.id.mybuylistMoneyText,
										R.id.mybuylistCountText }));
					} else if (msg.arg1 == 7) {
						Toast.makeText(AllOrderActivityNew.this, "获取物品详情失败",
								Toast.LENGTH_SHORT).show();
						if(progressDialog.isShowing())
						{
							progressDialog.dismiss();
						}
					} 
					else if (msg.arg1 == 8)
					{
						Toast.makeText(AllOrderActivityNew.this, "这个订单已经提交,不能再退单了",
								Toast.LENGTH_SHORT).show();
						if(progressDialog.isShowing())
						{
							progressDialog.dismiss();
						}
						
					}
					else if(msg.arg1 == 9)
					{
						Toast.makeText(AllOrderActivityNew.this, "退单成功",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						if(progressDialog.isShowing())
						{
							progressDialog.dismiss();
						}
						AllOrderActivityNew.this.refresh();
					}
					else if(msg.arg1 == 11)
					{
						Toast.makeText(AllOrderActivityNew.this, "退单失败",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						if(progressDialog.isShowing())
						{
							progressDialog.dismiss();
						}
						AllOrderActivityNew.this.refresh();
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
		});
		
		myList = (ListView) findViewById(R.id.userOrderListView);
		if (SysUtil.getAPNType(getApplicationContext()) == -1) {
			Toast.makeText(AllOrderActivityNew.this, "请检查您的网络连接",
					Toast.LENGTH_SHORT).show();
			// AllOrderActivityNew.this.finish();
		} else {
			if (refreshFlag == false){
				refreshFlag = true;
				initList();
			}
		}
		myList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				
				if (!menuLoadingFlag) {
					menuLoadingFlag = true;
					dialog = new AlertDialog.Builder(AllOrderActivityNew.this)
							.create();
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
					dialog.setContentView(R.layout.mybeginorder);
					myMenuList = (ListView) dialog
							.findViewById(R.id.listD);
					progressDialog = MyProgressDialog.createDialog(AllOrderActivityNew.this);
					if (!progressDialog.isShowing()) {
						progressDialog.show();
					}
					initMenuList(position);
					ImageView imageDStore = (ImageView) dialog
							.findViewById(R.id.imageDStore);
					TextView storeDName = (TextView) dialog
							.findViewById(R.id.storeDName);
					TextView restDTime = (TextView) dialog
							.findViewById(R.id.restDTime);
					TextView restDMoney = (TextView) dialog
							.findViewById(R.id.restDMoney);

					Bitmap bm = (Bitmap) allListData.get(position).get("poster");
					if (bm == null) {
						bm = BitmapFactory.decodeResource(getResources(),
								R.drawable.defaultstore);
					}
					imageDStore.setImageBitmap(bm);
					storeDName.setText(String.valueOf(allListData.get(position)
							.get("name")));
					restDMoney.setText(String.valueOf(allListData.get(position)
							.get("leftmoney")));
					restDTime.setText(String.valueOf(allListData.get(position).get(
							"lefttime")));

					Button delete = (Button) dialog.findViewById(R.id.delete);
					Button addition = (Button) dialog.findViewById(R.id.addition);
					Button closeButton = (Button) dialog
							.findViewById(R.id.closeButton);
					if((Boolean)allListData.get(position).get("deleteFlag") == false)
					{
						delete.setEnabled(false);
						addition.setEnabled(false);
						delete.setBackgroundColor(getResources().getColor(R.color.lightgray));
						addition.setBackgroundColor(getResources().getColor(R.color.lightgray));
					}
					delete.setOnClickListener(new View.OnClickListener() {

						@Override
						//删除订单，未做
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (!progressDialog.isShowing()) {
								progressDialog.show();
							}
							menuLoadingFlag = false;
							// 向商家发送信息
							//initial(position);
							if(SysUtil.getAPNType(getApplicationContext()) == -1){
								Toast.makeText(AllOrderActivityNew.this, "请检查您的网络连接",
										Toast.LENGTH_SHORT).show();
							}else{
								
								AlertDialog.Builder builder = new Builder(AllOrderActivityNew.this);
						        builder.setTitle("退单");
						        builder.setMessage("确认退单?不能恢复");
						        // 更新
						        builder.setPositiveButton("确认", new OnClickListener()
						        {
						            public void onClick(DialogInterface dialog, int which)
						            {
						            	initdelete(position);
						            	startAsynThread("delete");
						            	if (!progressDialog.isShowing()) {
											progressDialog.show();
										}
						            }
						        });
						        // 稍后更新
						        builder.setNegativeButton("取消", new OnClickListener()
						        {
						            public void onClick(DialogInterface dialog, int which)
						            {
						                dialog.dismiss();
						                if (progressDialog.isShowing()) {
											progressDialog.dismiss();
										}
						            }

						        });
						        Dialog noticeDialog = builder.create();
						        noticeDialog.show();
								
							}
							
						}

					});

					addition.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Bundle bundle = new Bundle();
							bundle.putLong("sid", allSidArray.get(position));
							bundle.putLong("gid", allGidArray.get(position));
							Intent intent = new Intent();
							intent.putExtras(bundle);

							intent.setClass(AllOrderActivityNew.this,
									AddGoodslistActivity.class);
							startActivity(intent);
							dialog.dismiss();
							AllOrderActivityNew.this.finish();
							menuLoadingFlag = false;
						}
					});
					
					closeButton.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							menuLoadingFlag = false;
						}
					});
				}
			}
		});
	}
	private void initList() {
		
		Log.e("initiallist", "initiallist");
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		if (allListData == null) {
			allListData = new ArrayList<Map<String, Object>>();
			newListData = new ArrayList<Map<String, Object>>();
			completeListData = new ArrayList<Map<String, Object>>();
			successListData = new ArrayList<Map<String, Object>>();
			refuseListData = new ArrayList<Map<String, Object>>();
			closeListData = new ArrayList<Map<String, Object>>();
			dieListData = new ArrayList<Map<String, Object>>();
		} else {
			allListData.clear();
			newListData.clear();
			completeListData.clear();
			successListData.clear();
			refuseListData.clear();
			closeListData.clear();
			dieListData.clear();
		}
		
		
		
		//拉取所有团购单
		setAsynThreadConfig("getUserGrouponList", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				Message msg = Message.obtain();
				if (Global.user != null) {
					Long tempuid = Global.user.getUid();
					AllDataArray = grouponManage.getUserGrouponList(tempuid
							.toString());
					if (AllDataArray == null) {
						finishAsynThread("getUserGrouponList");
						msg.arg1 = 0;//没有任何凑单
						listLodingFlag = true;
						refreshFlag = false;
						if (progressDialog != null
								&& progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						return msg;
					}
					//将团购单内信息按单提取出来
					//ListDataMap中
					for (int i = 0; i < AllDataArray.size(); i++) {
						ListDataMap = new HashMap<String, Object>();  
						Groupon singleGroupon = (Groupon) AllDataArray.get(i);
						Long tempsid = singleGroupon.getSid();
						Long tempgid = singleGroupon.getGid();
						Seller tempgrouponseller = sellerManage
								.getSellerInfo(tempsid);
						if (tempgrouponseller == null) {
							finishAsynThread("getUserGrouponList");
							msg.arg1 = 1;//商家不存在
							listLodingFlag = true;
							refreshFlag = false;
							if (progressDialog != null
									&& progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
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
						ListDataMap.put("poster", bm);
						ListDataMap.put("name", tempgrouponseller.getDescription());
						ListDataMap.put("quality", R.drawable.fivestar);
						double moneyLast = singleGroupon.getResidueCost();
						if (moneyLast < 0) {
							moneyLast = 0d;
						} else {
							moneyLast = (double) (Math.round(moneyLast * 100)) / 100;
						}
						ListDataMap.put("leftmoney", "还剩" + moneyLast
								+ "元凑单成功");
						Long timeLast = singleGroupon.getResidueTime();
						if (timeLast < 0) {
							timeLast = 0l;
						}
						ListDataMap.put("lefttime", "还剩" + timeLast + "分钟");
						String statusString = singleGroupon.getStatus();
						//将团购单详情按status字段放入不同的list中
						if(statusString.compareTo("new") == 0){
							ListDataMap.put("deleteFlag", true);
							ListDataMap.put("status", "正在凑");
							newSidArray.add(tempsid);
							newGidArray.add(tempgid);
							newListData.add(ListDataMap);
						} else if(statusString.compareTo("complete") == 0) {
							ListDataMap.put("deleteFlag", false);
							ListDataMap.put("status", "已提交");
							completeSidArray.add(tempsid);
							completeGidArray.add(tempgid);
							completeListData.add(ListDataMap);
						} else if(statusString.compareTo("success") == 0) {
							ListDataMap.put("deleteFlag", false);
							ListDataMap.put("status", "已确认");
							successSidArray.add(tempsid);
							successGidArray.add(tempgid);
							successListData.add(ListDataMap);
						} else if(statusString.compareTo("refuse") == 0) {
							ListDataMap.put("deleteFlag", false);
							ListDataMap.put("status", "已拒绝");
							refuseSidArray.add(tempsid);
							refuseGidArray.add(tempgid);
							refuseListData.add(ListDataMap);
						} else if(statusString.compareTo("close") == 0) {
							ListDataMap.put("deleteFlag", false);
							ListDataMap.put("status", "已关闭");
							closeSidArray.add(tempsid);
							closeGidArray.add(tempgid);
							closeListData.add(ListDataMap);
						} else if(statusString.compareTo("die") == 0) {
							ListDataMap.put("deleteFlag", false);
							ListDataMap.put("status", "凑单失败");
							dieSidArray.add(tempsid);
							dieGidArray.add(tempgid);
							dieListData.add(ListDataMap);
						} else {
							finishAsynThread("getUserGrouponList");
							msg.arg1 = 2;//团购单状态异常
							listLodingFlag = true;
							refreshFlag = false;
							if (progressDialog != null
									&& progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
							return msg;
						}
					}
					//将团购单详情按顺序放入allListData内
					for(int j = 0; j < newListData.size(); j++){
						allListData.add(newListData.get(j));
						allGidArray.add(newGidArray.get(j));
						allSidArray.add(newSidArray.get(j));
					}
					for(int j = 0; j < completeListData.size(); j++){
						allListData.add(completeListData.get(j));
						allGidArray.add(completeGidArray.get(j));
						allSidArray.add(completeSidArray.get(j));
					}
					for(int j = 0; j < successListData.size(); j++){
						allListData.add(successListData.get(j));
						allGidArray.add(successGidArray.get(j));
						allSidArray.add(successSidArray.get(j));
					}
					for(int j = 0; j < refuseListData.size(); j++){
						allListData.add(refuseListData.get(j));
						allGidArray.add(refuseGidArray.get(j));
						allSidArray.add(refuseSidArray.get(j));
					}
					for(int j = 0; j < closeListData.size(); j++){
						allListData.add(closeListData.get(j));
						allGidArray.add(closeGidArray.get(j));
						allSidArray.add(closeSidArray.get(j));
					}
					for(int j = 0; j < dieListData.size(); j++){
						allListData.add(dieListData.get(j));
						allGidArray.add(dieGidArray.get(j));
						allSidArray.add(dieSidArray.get(j));
					}
					msg.arg1 = 3;//拉取成功
				} else {
					msg.arg1 = 4;//尚未登录
				}
				finishAsynThread("getUserGrouponList");
				listLodingFlag = true;
				refreshFlag = false;
				if (progressDialog != null
						&& progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				return msg;
			}
		});
		startAsynThread("getUserGrouponList");
	}
	private void initMenuList(final int position){
		if (menuData == null) {
			menuData = new ArrayList<Map<String, Object>>();
		} else {
			menuData.clear();
		}
		
		
		setAsynThreadConfig("getMenuList", true, new AsynThreadImpl() {

			@Override
			public Message excute() {
				// TODO Auto-generated method stub

				Message msg = Message.obtain();

				Long tempgid = allGidArray.get(position);
				Long tempuid = Global.user.getUid();
				MyNewGroupon menulist = grouponManage.getGrouponDetailByUid(
						tempgid, tempuid);
				if (menulist == null) {
					finishAsynThread("getMenuList");
					menuLoadingFlag = true;
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					msg.arg1 = 5;//获取订单详情失败
					return msg;
				}
				ArrayList<MyNewGouponMenuItem> menu = menulist
						.getMenudata();
				if (menu != null) {
					for (int i = 0; i < menu.size(); i++) {
						menuMap = new HashMap<String, Object>();
						MyNewGouponMenuItem item = (MyNewGouponMenuItem) menu
								.get(i);

						menuMap.put("name", item.getName());
						menuMap.put("price", item.getPrice());
						menuMap.put("count", item.getCount());
						menuData.add(menuMap);
					}

					finishAsynThread("getMenuList");
					menuLoadingFlag = true;
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					msg.arg1 = 6;//成功获取订单详情
					return msg;
				} else {
					finishAsynThread("getMenuList");
					menuLoadingFlag = true;
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					msg.arg1 = 7;//获取物品详情失败
					return msg;
				}
			}
		});
		startAsynThread("getMenuList");
		
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
		public View getView(int position, View convertView, ViewGroup parent) {

			Map<String, Object> viewItem = listData.get(position);

			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(resId, null);

				holder = new ViewHolder();
				holder.shopPoster = (ImageView) convertView.findViewById(to[0]);
				holder.shopName = (TextView) convertView.findViewById(to[1]);
				holder.shopQuality = (ImageView) convertView
						.findViewById(to[2]);
				holder.leftMoney = (TextView) convertView
						.findViewById(to[3]);
				holder.leftTime = (TextView) convertView
						.findViewById(to[4]);
				holder.status = (TextView) convertView
						.findViewById(to[5]);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.shopPoster.setImageBitmap((Bitmap) viewItem.get(from[0]));
			holder.shopName.setText((String) viewItem.get(from[1]));
			holder.shopQuality.setBackgroundResource((Integer) viewItem
					.get(from[2]));
			holder.leftMoney.setText((String) viewItem.get(from[3]));
			holder.leftTime.setText((String) viewItem.get(from[4]));
			holder.status.setText((String) viewItem.get(from[5]));

			return convertView;
		}

		private class ViewHolder {

			ImageView shopPoster;

			TextView shopName;

			ImageView shopQuality;

			TextView leftMoney;

			TextView leftTime;
			
			TextView status;

		}

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
			Log.d("itemname2", from[0]);
			Log.d("itemprice2", from[1]);
			Log.d("itemcount2", from[2]);
			Log.d("itemname3", String.valueOf(viewItem.get(from[0])));
			Log.d("itemprice3", String.valueOf(viewItem.get(from[1])));
			Log.d("itemcount3", String.valueOf(viewItem.get(from[2])));
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
	void refresh() {
		if (allListData == null) {
			allListData = new ArrayList<Map<String, Object>>();
		} else {
			allListData.clear();
		}
			initList();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("on resume", "onResume");
		resumeFlag = true;
		//refresh();

		

	}
	
	

	
}
