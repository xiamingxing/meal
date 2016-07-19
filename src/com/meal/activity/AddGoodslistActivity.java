package com.meal.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meal.action.DealManageAction;
import com.meal.action.GrouponManageAction;
import com.meal.action.MenuConfigAction;
import com.meal.action.SellerManageAction;
import com.meal.activity.ipml.AsynThreadImpl;
import com.meal.activity.ipml.UIThreadImpl;
import com.meal.bean.Groupon;
import com.meal.bean.Global;
import com.meal.bean.Seller;
import com.meal.bean.Menu;
import com.meal.bean.Order;
import com.meal.dialog.MyProgressDialog;
import com.meal.util.SysUtil;
public class AddGoodslistActivity extends BaseActivity {
	
	List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	HashMap<Button, EditText> temp = new HashMap<Button, EditText>();
	HashMap<Button, EditText> temp1 = new HashMap<Button, EditText>();
	HashMap<Button, TextView> temp2 = new HashMap<Button, TextView>();
	HashMap<Button, TextView> temp3 = new HashMap<Button, TextView>();
	ListView goodsList;
	Boolean grouponFlag = false;
	
	Boolean loadNextFlag = true;
	Boolean loadPreFlag = true;
	
	ImageView goodsImage;

	TextView goodsName;

	Button moneyButton;
	Button storeBuyBegin;
	Button retButton;
	
	private PopMenu popMenu;
	private Context context;
	TextView defaultorderText;
	
	TextView totalNum;

	ImageView gouwucheImage;
	
	private AlertDialog dialog;

	private List<String> spinlist = new ArrayList<String>();
	private HashMap<String, Integer>numlist = new HashMap<String, Integer>();
	private long sid = -1;
	private long gid = -1;
	private long uid = -1;
	
	private MyProgressDialog myProgressDialog;
	
	private MenuConfigAction menuAction = MenuConfigAction.getInstance();
	private DealManageAction manageAction = DealManageAction.getInstance();
	private SellerManageAction sellerAction = SellerManageAction.getInstance();
	int pageNum = 1;
	int curpageNum = 1;
	ArrayList<Object> list = new ArrayList<Object>();//记录所有商品数据
	
	Button loadNext;
	Button loadPre;
	ArrayList<Long> menuList = new ArrayList<Long>();
	Order order = new Order("", "" , 0.0 , null);
	GrouponManageAction grouponManageAction = GrouponManageAction.getInstance();
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_activity);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		sid = bundle.getLong("sid");
		gid = bundle.getLong("gid");
		uid = bundle.getLong("uid");
//		sid = 1;
//		gid = 1;
		if(sid == -1 || gid==-1 || uid == -1)
		{
			Toast.makeText(this, getResources().getString(R.string.storegone), Toast.LENGTH_SHORT).show();
			return;
		}
        initWidget();
//        initial();
//        startAsynThread("send");
    }
	

	private void initWidget()
	{
		myProgressDialog = MyProgressDialog.createDialog(this);
		goodsList = (ListView) findViewById(R.id.goodsList);
		totalNum = (TextView) findViewById(R.id.totalNum);

		retButton = (Button) findViewById(R.id.retButton_GoodsActivity);
		retButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(AddGoodslistActivity.this, UserMainPageActivity.class);
				startActivity(intent);
				AddGoodslistActivity.this.finish();
				
			}
		});
		
		
		storeBuyBegin = (Button)findViewById(R.id.storeBuyBegin);
		storeBuyBegin.setText("加入跟单");
		storeBuyBegin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				if(Global.user != null && uid != -1 && Global.user.getUid() == uid)
//				{
//					Toast.makeText(AddGoodslistActivity.this, "亲，别跟自己的单", Toast.LENGTH_SHORT).show();
//					return;
//				}
				if(menuList.isEmpty())
				{
					Toast.makeText(AddGoodslistActivity.this, "您还没有选购", Toast.LENGTH_SHORT).show();
					return;
				}
				if(Global.user == null)
				{
					Intent intent = new Intent();
					intent.setClass(AddGoodslistActivity.this, LoginActivity.class);
					startActivity(intent);
					return;
				}
				
				if(SysUtil.getAPNType(getApplicationContext()) == -1){
					Toast.makeText(AddGoodslistActivity.this, "请检查您的网络连接",
							Toast.LENGTH_SHORT).show();
				}         
				else{
					if (!myProgressDialog.isShowing()) {
						myProgressDialog.show();
					}
					grouponFlag = false;
					startAsynThread("addsellerstatus");
				}
				
			}
		});
		
		gouwucheImage = (ImageView)findViewById(R.id.gouwucheImage);
		gouwucheImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		defaultorderText = (TextView)findViewById(R.id.defaultorderText);
		
		defaultorderText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				popMenu.showAsDropDown(v);
				// TODO Auto-generated method stub
				
			}
		});
		
		context = AddGoodslistActivity.this;
		popMenu = new PopMenu(context);
		popMenu.addItems(new String[] { getResources().getString(R.string.defaultorder), getResources().getString(R.string.mostbuy) ,
				getResources().getString(R.string.descend), getResources().getString(R.string.ascend)});

		popMenu.setOnItemClickListener(popmenuItemClickListener);
		
		initial();
		startAsynThread("goodslist");
		myProgressDialog.show();
		loadNext = (Button)findViewById(R.id.loadNext);
		loadNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(loadNextFlag == true && loadPreFlag == true){
					if(curpageNum == pageNum)
					{
						loadNextFlag = false;
						pageNum++;
						curpageNum = pageNum;
						initial();
						myProgressDialog.show();
						startAsynThread("goodslist");
					}
					else
					{
						loadNextFlag = false;
						curpageNum++;
						int totalSize = list.size();
						int size = totalSize%5;
						if(size == 0)
						{
							size = 5;
						}
						int start = (curpageNum-1) * 5;
						int end = (curpageNum==pageNum)?(start+size-1):(start+4);
						Log.e("loadNext start", start+"");
						Log.e("loadNext end", end+"");
						Log.e("loadNext curpageNum", curpageNum+"");
						Log.e("loadNext pageNum", pageNum+"");
						Map<String, Object> map = null;
						dataList.clear();
						for(int i=start ; i<=end ; i++)
						{
							map = new HashMap<String, Object>();
							String photo = ((Menu)list.get(i)).getPhoto();
							Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.defaultgoods);
							if(photo != null && !photo.equals(""))
							{
								bm = menuAction.getMenuPhoto(photo);
							}
							map.put("goodsimage", bm);//明天需改部分
							map.put("name", ((Menu)list.get(i)).getName());
							map.put("price", ((Menu)list.get(i)).getPrice());
							map.put("buynum", "");
							if(numlist.get(i+"") != null && !(numlist.get(i+"")+"").equals("") && !(numlist.get(i+"")+"").equals("null"))
							{
								map.put("numtext", numlist.get(i+"")+"");
							}
							else
							{
								map.put("numtext", "0");
							}
//							map.put("numtext", numlist.get(i));
//							map.put("numtext", "0"); 
							dataList.add(map);
						}
						goodsList.setAdapter(new MyAdapter
								(AddGoodslistActivity.this,R.layout.goodslistitem, dataList,
										new String[] {"goodsImage", "name", "price", "buynum" }, 
										new int[] { R.id.goodsImage,R.id.goodsName, R.id.moneyText,R.id.hasboughtPeopleNum}));
						loadNextFlag = true;
					}
					Log.e("loadNext curpageNum", curpageNum+"");
					Log.e("loadNext pageNum", pageNum+"");
				}
				
			}
		});
		loadPre = (Button)findViewById(R.id.loadPre);
		loadPre.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(loadNextFlag == true && loadPreFlag == true){
					if(curpageNum > 1)
					{
						loadPreFlag = false;
						curpageNum--;
						int totalSize = list.size();
						int size = totalSize%5;
						int start = (curpageNum-1) * 5;
//						int end = (curpageNum<pageNum)?(start+4):(start+size-1);
						int end = start+4;
						Log.e("loadPre start", start+"");
						Log.e("loadPre end", end+"");
						Log.e("loadPre curpageNum", curpageNum+"");
						Log.e("loadPre pageNum", pageNum+"");
						Map<String, Object> map = null;
						dataList.clear();
						for(int i=start ; i<=end ; i++)
						{
							map = new HashMap<String, Object>();
							String photo = ((Menu)list.get(i)).getPhoto();
							Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.defaultgoods);
							if(photo != null && !photo.equals(""))
							{
								bm = menuAction.getMenuPhoto(photo);
							}
							map.put("goodsimage", bm);//明天需改部分
							map.put("name", ((Menu)list.get(i)).getName());
							map.put("price", ((Menu)list.get(i)).getPrice());
							map.put("buynum", "");
							if(numlist.get(i+"") != null && !(numlist.get(i+"")+"").equals("") && !(numlist.get(i+"")+"").equals("null"))
							{
								map.put("numtext", numlist.get(i+"")+"");
							}
							else
							{
								map.put("numtext", "0");
							}
//							map.put("numtext", numlist.get(i));
//							map.put("numtext", "0");
							dataList.add(map);
						}
						goodsList.setAdapter(new MyAdapter
								(AddGoodslistActivity.this,R.layout.goodslistitem, dataList,
										new String[] {"goodsImage", "name", "price", "buynum" }, 
										new int[] { R.id.goodsImage,R.id.goodsName, R.id.moneyText,R.id.hasboughtPeopleNum}));
						loadPreFlag = true;
					}
					Log.e("loadPre curpageNum", curpageNum+"");
					Log.e("loadPre pageNum", pageNum+"");
				}
				
			}
		});
	}
	OnItemClickListener popmenuItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			popMenu.dismiss();
		}
	};
	
	private void initial()
	{
		setUIRefreshConfig(new UIThreadImpl() {
			
			@Override
			public void refresh(Message msg) {
				// TODO Auto-generated method stub
				myProgressDialog.dismiss();
				
				if(5 == msg.arg1)
				{
					pageNum--;
					curpageNum = pageNum;
					Log.e("failget curpageNum", curpageNum+"");
					Log.e("failget pageNum", pageNum+"");
					Toast.makeText(AddGoodslistActivity.this, getResources().getString(R.string.failgetgoodslist), Toast.LENGTH_SHORT).show();
					loadNextFlag = true;
					loadPreFlag = true;
				}
				if ( 6 == msg.arg1 )//获取商品列表成功，显示在listview上面
				{
					goodsList.setAdapter(new MyAdapter
							(AddGoodslistActivity.this,R.layout.goodslistitem, dataList,
									new String[] {"goodsImage", "name", "price", "buynum" }, 
									new int[] { R.id.goodsImage,R.id.goodsName, R.id.moneyText,R.id.hasboughtPeopleNum}));
					loadNextFlag = true;
					loadPreFlag = true;
				}
				else if(7 == msg.arg1)
				{
					Boolean isOK = (Boolean)msg.obj;
					if(isOK == null || isOK == false)
					{
						Toast.makeText(AddGoodslistActivity.this, "加入团购失败", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(AddGoodslistActivity.this,
								UserMainPageActivity.class);
						startActivity(intent);
						AddGoodslistActivity.this.finish();
					}
					else
					{
						Intent intent = new Intent();
						intent.setClass(AddGoodslistActivity.this,
								AllOrderActivityNew.class);
						startActivity(intent);
						AddGoodslistActivity.this.finish();
					}
				}
				else if(8 == msg.arg1)
				{
					
					Toast.makeText(AddGoodslistActivity.this, "商家已关闭接单了", Toast.LENGTH_SHORT).show();
					if(myProgressDialog.isShowing())
					{
						myProgressDialog.dismiss();
					}
					AddGoodslistActivity.this.finish();
				}
				else if(9 == msg.arg1)
				{
					startAsynThread("groupon");
				}
				
			}

		});
	
		setAsynThreadConfig("goodslist", true, new AsynThreadImpl() {//点击加载下一页拉取数据，每次拉取10条

			@Override
			public Message excute() {
				// TODO Auto-generated method stub
				
				Message msg = Message.obtain();
				ArrayList<Object> tmp = null;
				tmp = menuAction.getMenuList(sid, pageNum);//获取某一页的商品列表
//				Log.e("sid", sid+"");
//				Log.e("pageNum", pageNum+"");
				if(tmp == null)
				{
					msg.arg1 = 5;//获取列表失败
				}
				else
				{
					msg.arg1 = 6;//获取列表成功
					
					list.addAll( tmp );
					Map<String, Object> map = null;
					dataList.clear();
//					Log.e("size", tmp.size()+"");
					for(int i=0 ; i<tmp.size() ; i++)
					{
						map = new HashMap<String, Object>();
						String photo = ((Menu)tmp.get(i)).getPhoto();
						Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.defaultgoods);
						if(photo != null && !photo.equals(""))
						{
							bm = menuAction.getMenuPhoto(photo);
						}
						map.put("goodsimage", bm);
						map.put("name", ((Menu)tmp.get(i)).getName());
						map.put("price", ((Menu)tmp.get(i)).getPrice());
						map.put("buynum", "");
						map.put("numtext", "0");
						dataList.add(map);//记录所有显示的数据
					}
					
				}
				
				finishAsynThread("goodslist");
				return msg;
			}
	
		});
		setAsynThreadConfig("groupon", true, new AsynThreadImpl() {//点击加载下一页拉取数据，每次拉取10条

			@Override
			public Message excute() {
				// TODO Auto-generated method stub
				
				Message msg = Message.obtain();
				order.setPhone(Global.user.getPhone());
				order.setAddress(Global.user.getAddress());
				order.setPay(Float.parseFloat(String.valueOf(totalNum.getText())));
				order.setMenuList(menuList);
				Long oid = manageAction.addOrder(order);
				Boolean isOK = grouponManageAction.joinGroupon(gid, oid);
				msg.obj = isOK;
				msg.arg1 = 7 ;
				finishAsynThread("groupon");
				grouponFlag = true;
				myProgressDialog.dismiss();
				return msg;
			}
	
		});
		setAsynThreadConfig("addsellerstatus", true, new AsynThreadImpl() {//点击加载下一页拉取数据，每次拉取10条

			@Override
			public Message excute() {
				// TODO Auto-generated method stub
				
				Message msg = Message.obtain();
				Seller seller = sellerAction.getSellerInfo(sid);
				
				if(seller==null || seller.getOrderFunctionSwitch()==null || seller.getOrderFunctionSwitch()==false)
				{
					msg.arg1 = 8;
				}
				else
				{
					msg.arg1 = 9;
				}
				
				finishAsynThread("addsellerstatus");
				return msg;
			}
	
		});
	}
	public final class ViewHolder 
	{
		ImageView goodsImage;
		TextView goodsName;
		TextView moneyText;
		TextView hasboughtPeopleNum;
		Button plusButton;
		EditText numText;
		Button minusButton;
	}	
    
	
	public class MyAdapter extends BaseAdapter
	{

		private LayoutInflater mInflater;
		private int resId;

		private List<Map<String, Object>> listData;

		private String[] from;

		private int[] to;
		
		public MyAdapter(Context context) 
		{
			this.mInflater = LayoutInflater.from(context);
		}

		public MyAdapter(Context context, int resId,
				List<Map<String, Object>> listData, String[] from, int[] to) {

			mInflater = LayoutInflater.from(context);
			this.resId = resId;
			this.listData = listData;
			this.from = from;
			this.to = to;

		}
		//@Override
		public int getCount() 
		{
			// TODO Auto-generated method stub
			return listData.size();
		}

		//@Override
		public Object getItem(int arg0) 
		{
			// TODO Auto-generated method stub
			return null;
		}

		//@Override
		public long getItemId(int arg0) 
		{
			// TODO Auto-generated method stub
			return 0;
		}

		// @Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			HashMap<String, Object> viewItem = (HashMap<String, Object>) listData
					.get(position);
			

			ViewHolder holder = null;
			

			if (convertView == null) 
			{
				convertView = mInflater.inflate(R.layout.goodslistitem, null);
				holder = new ViewHolder();
				holder.goodsImage = (ImageView) convertView.findViewById(R.id.goodsImage);
				holder.goodsName = (TextView) convertView.findViewById(R.id.goodsName);
				holder.moneyText = (TextView) convertView.findViewById(R.id.moneyText);
				holder.hasboughtPeopleNum = (TextView) convertView.findViewById(R.id.hasboughtPeopleNum);
				holder.minusButton = (Button) convertView.findViewById(R.id.minusButton);
				holder.numText = (EditText) convertView.findViewById(R.id.numText);
				holder.plusButton = (Button) convertView.findViewById(R.id.plusButton);
				convertView.setTag(holder);

			} else 
			{

				holder = (ViewHolder) convertView.getTag();
			}
			
			Bitmap tempBm = (Bitmap)viewItem.get("goodsimage");

			holder.goodsImage.setImageBitmap(tempBm);
			holder.goodsName.setText(String.valueOf(viewItem.get("name")));
			holder.moneyText.setText("￥"+String.valueOf(viewItem.get("price")));
			holder.hasboughtPeopleNum.setText(String.valueOf( viewItem.get("buynum")));
			holder.numText.setText(String.valueOf(viewItem.get("numtext")));
			temp.put(holder.minusButton, holder.numText);
			temp2.put(holder.minusButton, holder.moneyText);
			holder.minusButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					EditText editNum = temp.get(v);
					TextView calmoney = temp2.get(v);
					Integer num = Integer.parseInt(editNum.getText().toString());
					if(num > 0)
					{
						editNum.setText(--num+"");
						float inttotalnum = Float.parseFloat(totalNum.getText().toString() == "" ? "0" : totalNum.getText().toString());
						String addmoney = calmoney.getText().toString();
						addmoney = addmoney.substring(1, addmoney.length());
						inttotalnum = inttotalnum - Float.parseFloat(addmoney);
						inttotalnum = (float)(Math.round(inttotalnum*100))/100;
						totalNum.setText(String.valueOf(inttotalnum));
						int pos;
						if(curpageNum<=1)
						{
							pos = position;
						}
						else
						{
							pos = 5*(curpageNum-1) + position;
						}
						Log.d("remove mid position ", pos+"");
						menuList.remove(((Menu)(list.get(pos))).getMid());
						numlist.put(pos+"", num);
					}
					if(num == 0)
					{
//						v.setEnabled(false);
					}
					
					//设置totalNum总钱数值
				}
			});
			
			temp1.put(holder.plusButton, holder.numText);
			temp3.put(holder.plusButton, holder.moneyText);
			holder.plusButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					EditText editNum = temp1.get(v);
					TextView calmoney = temp3.get(v);
					Integer num = Integer.parseInt(editNum.getText().toString());
					editNum.setText(++num+"");
					float inttotalnum = Float.parseFloat(totalNum.getText().toString() == "" ? "0" : totalNum.getText().toString());
					String addmoney = calmoney.getText().toString();
					addmoney = addmoney.substring(1, addmoney.length());
					inttotalnum = inttotalnum + Float.parseFloat(addmoney);
					inttotalnum = (float)(Math.round(inttotalnum*100))/100;
					totalNum.setText(String.valueOf(inttotalnum));
					int pos;
					if(curpageNum<=1)
					{
						pos = position;
					}
					else
					{
						pos = 5*(curpageNum-1) + position;
					}
					Log.d("add mid position ", pos+"");
					menuList.add(((Menu)(list.get(pos))).getMid());
					numlist.put(pos+"", num);
					if(num > 0)
					{
//						v.setEnabled(true);
					}
					//设置totalNum总钱数值
				}
			});
			

			return convertView;
		}

	}
}
