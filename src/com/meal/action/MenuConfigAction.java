package com.meal.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.meal.bean.Constant;
import com.meal.bean.Global;
import com.meal.bean.Menu;
import com.meal.util.HttpUtil;

/**
 * @author xiamingxing
 * 
 */
public class MenuConfigAction extends BaseAction {

	private static MenuConfigAction instance = null;

	public static MenuConfigAction getInstance() {

		if (null == instance) {

			instance = new MenuConfigAction();

		}

		return instance;

	}

	private String MENU_MODULE;
	private String MENU_MODULE_ADD_MENU;
	private String MENU_MODULE_CLEAR_MENU_LIST_BY_SID;
	private String MENU_MODULE_DELETE_MENU_BY_MID;

	private String MENU_MODULE_GET_MENU_BY_MID;
	private String MENU_MODULE_GET_MENU_LIST_BY_SID;
	private String MENU_MODULE_GET_SID_BY_MID;

	private String MENU_MODULE_UPDATE_MENU_BY_MID;

	private MenuConfigAction() {

		this.MENU_MODULE = Constant.MENU_MODULE;

		this.MENU_MODULE_ADD_MENU = Constant.MENU_MODULE_ADD_MENU;
		this.MENU_MODULE_UPDATE_MENU_BY_MID = Constant.MENU_MODULE_UPDATE_MENU_BY_MID;
		this.MENU_MODULE_DELETE_MENU_BY_MID = Constant.MENU_MODULE_DELETE_MENU_BY_MID;
		this.MENU_MODULE_CLEAR_MENU_LIST_BY_SID = Constant.MENU_MODULE_CLEAR_MENU_LIST_BY_SID;

		this.MENU_MODULE_GET_MENU_BY_MID = Constant.MENU_MODULE_GET_MENU_BY_MID;
		this.MENU_MODULE_GET_MENU_LIST_BY_SID = Constant.MENU_MODULE_GET_MENU_LIST_BY_SID;
		this.MENU_MODULE_GET_SID_BY_MID = Constant.MENU_MODULE_GET_SID_BY_MID;

	}

	/**
	 * @param menu
	 * @return
	 */
	public Long addMenu(Menu menu) {

		Long mid = null;

		if (null != menu && null != Global.seller) {

			menu.setSid(Global.seller.getSid());

			JSONObject jsonObject = (JSONObject) send(MENU_MODULE,
					MENU_MODULE_ADD_MENU, menu, null, null);

			if (null != jsonObject) {

				try {

					mid = jsonObject.getLong("mid");

				} catch (Exception e) {

					e.printStackTrace();

				}

			}

		}

		return mid;

	}

	/**
	 * @param sid
	 * @return
	 */
	public boolean clearMenuListBySid(final Long sid) {

		boolean result = false;

		if (null != sid && null != Global.seller) {

			result = load(MENU_MODULE, MENU_MODULE_CLEAR_MENU_LIST_BY_SID,
					new HashMap<String, String>() {
						{
							put("sid", String.valueOf(sid));
						}
					});

		}

		return result;
	}

	/**
	 * @param mid
	 * @return
	 */
	public boolean deleteMenu(final Long mid) {

		boolean result = false;

		if (null != mid && null != Global.seller) {

			result = load(MENU_MODULE, MENU_MODULE_DELETE_MENU_BY_MID,
					new HashMap<String, String>() {
						{
							put("mid", String.valueOf(mid));
						}
					});

		}

		return result;

	}

	/**
	 * @param mid
	 * @return
	 */
	public Menu getMenuDetail(final Long mid) {

		Menu menu = null;

		if (null != mid) {

			menu = (Menu) load(MENU_MODULE, MENU_MODULE_GET_MENU_BY_MID,
					Menu.class, new HashMap<String, String>() {
						{
							put("mid", String.valueOf(mid));
						}
					});

		}

		return menu;

	}

	/**
	 * @param sid
	 * @return
	 */
	public ArrayList<Object> getMenuList(final Long sid, final int pageNum) {

		ArrayList<Object> menuList = null;

		if (null != sid && 0 <= sid) {

			JSONObject jsonObject = (JSONObject) load(MENU_MODULE,
					MENU_MODULE_GET_MENU_LIST_BY_SID, null,
					new HashMap<String, String>() {
						{
							put("sid", String.valueOf(sid));
							put("pageNum", String.valueOf(pageNum));
						}
					});

			menuList = _parserJavaBeanList(jsonObject, Menu.class, "MenuList");

		}
		return menuList;

	}

	/**
	 * @param URL
	 * @return
	 */
	public Bitmap getMenuPhoto(String URL) {

		Bitmap bmp = null;

		if (null != URL) {

			bmp = loadImage(URL);

		}

		return bmp;

	}

	/**
	 * @param mid
	 * @return
	 */
	public Long getSidByMid(final Long mid) {

		Long sid = null;

		if (null != mid) {

			JSONObject jsonObject = (JSONObject) load(MENU_MODULE,
					MENU_MODULE_GET_SID_BY_MID, Menu.class,
					new HashMap<String, String>() {
						{
							put("mid", String.valueOf(mid));
						}
					});
			if (null != jsonObject) {

				try {

					sid = jsonObject.getLong("sid");

				} catch (JSONException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();

				}

			}

		}

		return sid;

	}

	/**
	 * @param mid
	 * @param menu
	 * @return
	 */
	public boolean updateMenu(final Long mid, Menu menu) {

		boolean result = false;

		if (null != mid && null != menu && null != Global.seller) {

			menu.setSid(Global.seller.getSid());

			// /menu.setMid(mid);

			result = send(MENU_MODULE, MENU_MODULE_UPDATE_MENU_BY_MID, menu,
					new HashMap<String, String>() {
						{
							put("mid", String.valueOf(mid));
						}
					});

		}

		return result;

	}

	/**
	 * @param path
	 * @param fileName
	 * @param uid
	 * @return
	 */
	public String uploadMenuPhoto(String path, String fileName) {

		String fileURL = null;

		fileURL = upload(MENU_MODULE, path, fileName, null);

		return fileURL;

	}
}
