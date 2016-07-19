package com.meal.action;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.meal.bean.Constant;
import com.meal.bean.Global;
import com.meal.bean.Groupon;
import com.meal.bean.MyNewGroupon;

/**
 * @author xiamingxing
 * 
 */
public class GrouponManageAction extends BaseAction {

	private static GrouponManageAction instance = null;

	/**
	 * @return
	 */
	public static GrouponManageAction getInstance() {

		if (null == instance) {

			instance = new GrouponManageAction();

		}

		return instance;

	}

	private String GROUPON_MODULE;

	private String GROUPON_MODULE_CANCEL_GROUPON_BY_GID;
	private String GROUPON_MODULE_COMPLETE_GROUPON_BY_GID;
	private String GROUPON_MODULE_CREATE_GROUPON;

	private String GROUPON_MODULE_GET_GROUPON_DETAIL_BY_GID;
	private String GROUPON_MODULE_GET_GROUPON_LIST_BY_SID;
	private String GROUPON_MODULE_GET_GROUPON_LIST_BY_UID;
	private String GROUPON_MODULE_GET_GROUPON_LIST_FOR_JOIN;
	private String GROUPON_MODULE_GET_OTHER_GROUPON_LIST_BY_UID;
	private String GROUPON_MODULE_RETREAT_ORDER_BY_UID;

	private String GROUPON_MODULE_JOIN_GROUPON_BY_GID;
	private String GROUPON_MODULE_SET_GROUPON_STATUS_BY_GID;
	private String GROUPON_MODULE_GET_MY_NEW_GROUPON_DETAIL_BY_GID;
	private String GROUPON_MODULE_GET_GROUPON_DETAIL_BY_UID;
	private String GROUPON_MODULE_GET_MY_GROUPON_LIST_BY_STATUS;

	private final String STATUS_CANCEL_GROUPON = "cancel";
	private final String STATUS_COMPLETE_GROUPON = "complete";
	private final String STATUS_NEW_GROUPON = "new";

	private GrouponManageAction() {

		this.GROUPON_MODULE = Constant.GROUPON_MODULE;

		this.GROUPON_MODULE_CANCEL_GROUPON_BY_GID = Constant.GROUPON_MODULE_CANCEL_GROUPON_BY_GID;
		this.GROUPON_MODULE_SET_GROUPON_STATUS_BY_GID = Constant.GROUPON_MODULE_SET_GROUPON_STATUS_BY_GID;
		this.GROUPON_MODULE_COMPLETE_GROUPON_BY_GID = Constant.GROUPON_MODULE_COMPLETE_GROUPON_BY_GID;
		this.GROUPON_MODULE_CREATE_GROUPON = Constant.GROUPON_MODULE_CREATE_GROUPON;
		this.GROUPON_MODULE_JOIN_GROUPON_BY_GID = Constant.GROUPON_MODULE_JOIN_GROUPON_BY_GID;
		this.GROUPON_MODULE_RETREAT_ORDER_BY_UID = Constant.GROUPON_MODULE_RETREAT_ORDER_BY_UID;
		this.GROUPON_MODULE_GET_OTHER_GROUPON_LIST_BY_UID = Constant.GROUPON_MODULE_GET_OTHER_GROUPON_LIST_BY_UID;

		this.GROUPON_MODULE_GET_GROUPON_DETAIL_BY_GID = Constant.GROUPON_MODULE_GET_GROUPON_DETAIL_BY_GID;
		this.GROUPON_MODULE_GET_GROUPON_LIST_BY_SID = Constant.GROUPON_MODULE_GET_GROUPON_LIST_BY_SID;
		this.GROUPON_MODULE_GET_GROUPON_LIST_BY_UID = Constant.GROUPON_MODULE_GET_GROUPON_LIST_BY_UID;
		this.GROUPON_MODULE_GET_GROUPON_LIST_FOR_JOIN = Constant.GROUPON_MODULE_GET_GROUPON_LIST_FOR_JOIN;
		this.GROUPON_MODULE_GET_MY_NEW_GROUPON_DETAIL_BY_GID = Constant.GROUPON_MODULE_GET_MY_NEW_GROUPON_DETAIL_BY_GID;

		this.GROUPON_MODULE_GET_GROUPON_DETAIL_BY_UID = Constant.GROUPON_MODULE_GET_GROUPON_DETAIL_BY_UID;
		this.GROUPON_MODULE_GET_MY_GROUPON_LIST_BY_STATUS = Constant.GROUPON_MODULE_GET_MY_GROUPON_LIST_BY_STATUS;

	}

	/**
	 * @param gid
	 * @return
	 */
	public boolean cancelGroupon(final Long gid) {

		boolean result = false;

		if (null != gid) {

			result = setGrouponStatus(gid, STATUS_CANCEL_GROUPON);

		}

		return result;

	}

	/**
	 * @param gid
	 * @return
	 */
	public boolean completeGroupon(final Long gid) {

		boolean result = false;

		if (null != gid) {

			result = setGrouponStatus(gid, STATUS_COMPLETE_GROUPON);

		}

		return result;

	}

	/**
	 * @param groupon
	 * @return
	 */
	public Long createGroupon(Groupon groupon) {

		Long gid = null;

		if (null != groupon && null != Global.user) {

			groupon.setUid(Global.user.getUid());
			groupon.setStatus(STATUS_NEW_GROUPON);

			JSONObject jsonObject = (JSONObject) send(GROUPON_MODULE,
					GROUPON_MODULE_CREATE_GROUPON, groupon, null, null);

			if (null != jsonObject) {

				try {
					gid = jsonObject.getLong("gid");

				} catch (JSONException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();

				}

			}

		}

		return gid;

	}

	/**
	 * @param gid
	 * @return
	 */
	public Groupon getGrouponDetail(final Long gid) {

		Groupon groupon = null;

		if (null != gid) {

			groupon = (Groupon) load(GROUPON_MODULE,
					GROUPON_MODULE_GET_GROUPON_DETAIL_BY_GID, Groupon.class,
					new HashMap<String, String>() {
						{
							put("gid", String.valueOf(gid));
						}
					});

		}

		return groupon;

	}

	/**
	 * @param gid
	 * @return
	 */
	public MyNewGroupon getMyNewGrouponDetail(final Long gid) {

		MyNewGroupon myNewGroupon = null;

		if (null != gid) {

			myNewGroupon = (MyNewGroupon) load(GROUPON_MODULE,
					GROUPON_MODULE_GET_MY_NEW_GROUPON_DETAIL_BY_GID,
					MyNewGroupon.class, new HashMap<String, String>() {
						{
							put("gid", String.valueOf(gid));
						}
					});

		}

		return myNewGroupon;

	}

	/**
	 * @return
	 */
	public ArrayList<Object> getGrouponListForJoin() {

		ArrayList<Object> grouponList = null;

		JSONObject jsonObject = (JSONObject) load(GROUPON_MODULE,
				GROUPON_MODULE_GET_GROUPON_LIST_FOR_JOIN, null,
				new HashMap<String, String>() {
					{
						put("status", "new");
					}
				});

		grouponList = _parserJavaBeanList(jsonObject, Groupon.class,
				"grouponList");

		return grouponList;

	}

	/**
	 * @param sid
	 * @return
	 */
	public ArrayList<Object> getSellerGrouponList(final String sid) {

		ArrayList<Object> sellerGroupList = null;

		if (null != sid) {
			JSONObject jsonObject = (JSONObject) load(GROUPON_MODULE,
					GROUPON_MODULE_GET_GROUPON_LIST_BY_SID, null,
					new HashMap<String, String>() {
						{
							put("sid", sid);// TODO

						}
					});

			sellerGroupList = _parserJavaBeanList(jsonObject, Groupon.class,
					"grouponList");
		}

		return sellerGroupList;

	}

	/**
	 * @param uid
	 * @return
	 */
	public ArrayList<Object> getUserGrouponList(final String uid) {

		ArrayList<Object> userGrouponList = null;

		if (null != uid) {
			JSONObject jsonObject = (JSONObject) load(GROUPON_MODULE,
					GROUPON_MODULE_GET_GROUPON_LIST_BY_UID, null,
					new HashMap<String, String>() {
						{
							put("uid", uid);// TODO

						}
					});

			userGrouponList = _parserJavaBeanList(jsonObject, Groupon.class,
					"grouponList");
		}

		return userGrouponList;

	}

	public ArrayList<Object> getOtherGrouponList(final String uid) {
		ArrayList<Object> otherGrouponList = null;

		if (null != uid) {
			JSONObject jsonObject = (JSONObject) load(GROUPON_MODULE,
					GROUPON_MODULE_GET_OTHER_GROUPON_LIST_BY_UID, null,
					new HashMap<String, String>() {
						{
							put("uid", uid);// TODO

						}
					});

			otherGrouponList = _parserJavaBeanList(jsonObject, Groupon.class,
					"grouponList");
		}
		return otherGrouponList;
	}
	
	public ArrayList<Object> getMyGrouponListByStatus(final String uid, final String status){
		ArrayList<Object> myGrouponList = null;
		
		if ( null!=uid && null!= status){
			
			JSONObject jsonObject = (JSONObject) load(GROUPON_MODULE, 
					GROUPON_MODULE_GET_MY_GROUPON_LIST_BY_STATUS, null, 
					new HashMap<String, String>(){
						{
							put("uid", uid);
							put("status", status);
						}
				
					});
			
			myGrouponList = _parserJavaBeanList(jsonObject, Groupon.class,
					"grouponList");
		}
		
		return myGrouponList;
		
	}

	/**
	 * @param gid
	 * @param oid
	 * @return
	 */
	public boolean joinGroupon(final Long gid, final Long oid) {

		boolean result = false;

		if (null != gid && null != oid) {

			result = load(GROUPON_MODULE, GROUPON_MODULE_JOIN_GROUPON_BY_GID,
					new HashMap<String, String>() {
						{
							put("gid", String.valueOf(gid));
							put("oid", String.valueOf(oid));
						}
					});

		}

		return result;

	}
	
	public boolean retreatOrderByUid(final Long uid, final Long gid) {

		boolean result = false;

		if (null != uid && null != gid) {

			result = load(GROUPON_MODULE, GROUPON_MODULE_RETREAT_ORDER_BY_UID,
					new HashMap<String, String>() {
						{
							put("uid", String.valueOf(uid));
							put("gid", String.valueOf(gid));
						}
					});

		}

		return result;

	}


	/**
	 * @param gid
	 * @param status
	 * @return
	 */
	public boolean setGrouponStatus(final Long gid, final String status) {

		boolean result = false;

		if (null != gid && null != status) {

			result = load(GROUPON_MODULE,
					GROUPON_MODULE_SET_GROUPON_STATUS_BY_GID,
					new HashMap<String, String>() {
						{
							put("gid", String.valueOf(gid));
							put("status", status);
						}
					});

		}

		return result;

	}
	
	/**
	 * @param gid
	 * @param uid
	 * @return
	 */
	public MyNewGroupon getGrouponDetailByUid(final Long gid, final Long uid){
		
		MyNewGroupon myNewGroupon = null;

		if (null != gid && null != uid) {

			myNewGroupon = (MyNewGroupon) load(GROUPON_MODULE,
					GROUPON_MODULE_GET_GROUPON_DETAIL_BY_UID,
					MyNewGroupon.class, new HashMap<String, String>() {
						{
							put("gid", String.valueOf(gid));
							put("uid", String.valueOf(uid));
						}
					});

		}

		return myNewGroupon;
	}
}
