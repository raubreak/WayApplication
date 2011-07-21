package com.wayapp.contacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import com.wayapp.services.Constants;
import com.wayapp.tools.TypeInfo;

/**
 * @author raubreak
 *
 */
public class Contact {

	
	private int id = 0;
	private String jid ="";
	private String name = "";
	private String phone = "";
	private String photo = "";
	private String nameOfZumb = "";
	private String dateOfZumb = "";
	private String nameOfWayApp = "";
	private String dateOfWayApp = "";
	private String deviceID = "";
	private String post = "";
	private String rpost = "";
	private int ispost = -1;
	private String status = "";
	private int mode=5;
	private boolean lockway=false;
	private boolean lockzumb=false;
	private boolean lockpost=false;
	private boolean lockstatus=false;

	
	/**
	 * @return
	 */
	public String toContactString(){
		String values="";
		
		values = "id: " +
			id + " name: " +
			name + " phone: " +
			phone + " deviceid: "+
			deviceID + " way: "+
			nameOfWayApp +" zumb: "+
			nameOfZumb + " post: "+
			post + " rpost: "+
			rpost + " ispost: "+
			Integer.toString(ispost)+ " status: "+
			status
			
			;
		
		return values;
	}
	
	/**
	 * @param act
	 * @param phone
	 */
	public Contact(Context act, String phone) {

		this.phone = phone;
		this.jid = phone +"@userver";
//		Log.i("ContactConstructor-->", phone);
		initContactInfo(act);

	}
	/**
	 * @param id
	 * @param username
	 */
	public Contact (int id, String username){
		this.id = id;
		this.name = username;
		this.phone = username;
		this.jid= username +"@userver";
	}
	
	/**
	 * @param id
	 * @param phone
	 * @param name
	 * @param photoid
	 * @param nameOfWay
	 * @param nameOfZumb
	 * @param deviceid
	 * @param post
	 * @param rpost
	 * @param ispost
	 * @param status
	 * @param waylock
	 * @param zumblock
	 * @param postlock
	 * @param statuslock
	 * @param mode
	 */
	public Contact(String id ,String phone, String name,String photoid, String nameOfWay,String nameOfZumb,String deviceid,String post, String rpost, String ispost, String status,boolean waylock,boolean zumblock ,boolean postlock,boolean statuslock,String mode){

		this.id = Integer.parseInt(id);
		this.name=name;
		this.phone=phone;
		this.photo =photoid;
		this.nameOfWayApp=nameOfWay;
		this.nameOfZumb=nameOfZumb;
		this.post=post;
		this.rpost=rpost;
		this.deviceID=deviceid;
		if (ispost!=null){
			this.ispost=Integer.parseInt(ispost);
		}
		
		this.status=status;
		this.lockpost=postlock;
		this.lockstatus=statuslock;
		this.lockway=waylock;
		this.lockzumb=zumblock;
		this.jid = phone +"@userver";
		if (mode!=null){
			this.mode=Integer.parseInt(mode);
		}
	}

	/**
	 * @param act
	 */
	private void initContactInfo(Context act) {
		Log.i("initContactInfo----------------------", "");
		Cursor phones = act.getContentResolver().query(
				Phone.CONTENT_URI, // uri
				null, // campos que queremos (todos)
				null, null,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"); // orden

		while (phones.moveToNext()) {
			String number = phones.getString(phones
					.getColumnIndex(Phone.NUMBER)); // recoje telefono en number
			String name = phones.getString(phones
					.getColumnIndex(Phone.DISPLAY_NAME)); // recoje telefono en
															// number
			String photo = phones.getString(phones
					.getColumnIndex(Phone.PHOTO_ID));

			//limpieza del number
			number = number.replace(" ", "");
			number = number.replace("-", "");
			
			if(number.length()>9){
				number=number.substring(number.length()-9, number.length());
			}
			
			// verificamos si el numero se encuentra en fichero de SD
			if (this.phone.compareTo(number) == 0) {
				this.phone = number;
				this.name = name;
				this.photo = photo;
			}

		}
		phones.close();

	}

	/**
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return
	 */
	public String getPhone() {

		return this.phone;
	}

	/**
	 * @return
	 */
	public String getPhotoId() {

		return this.photo;
	}

	/**
	 * @return
	 */
	public String getDeviceID() {

		return this.deviceID;
	}

	/**
	 * @param deviceID
	 */
	public void setDeviceID(String deviceID) {

		this.deviceID = deviceID;
	}

	/**
	 * @param name
	 * @param date
	 */
	public void setLastWayapp(String name, String date) {

		this.nameOfWayApp = name;
		this.dateOfWayApp = name;

	}

	/**
	 * @param name
	 * @param date
	 */
	public void setLastZumb(String name, String date) {

		this.nameOfZumb = name;
		this.dateOfZumb = date;
	}

	/**
	 * @return
	 */
	public String getLastWayApp() {

		if (nameOfWayApp==null){
			nameOfWayApp="";
		}
		return this.nameOfWayApp;// + "," + this.dateOfWayApp;

	}

	/**
	 * @return
	 */
	public String getLastZumb() {
		if (nameOfZumb==null){
			nameOfZumb="";
		}
		return this.nameOfZumb; // + "," + this.dateOfZumb;

		// if (resul==","){
		// resul=null;
		// }

	}


	/**
	 * @param contact
	 */
	public void copyContact(Contact contact) {

		this.phone = contact.getPhone();
		this.name = contact.getName();
		this.photo = contact.getPhotoId();
		this.deviceID = contact.getDeviceID();
		this.dateOfWayApp = contact.getLastWayApp();
		this.dateOfZumb = contact.getLastZumb();
		this.id = contact.getId();
		this.lockpost = contact.isLockpost();
		this.lockstatus = contact.isLockstatus();
		this.lockway = contact.isLockway();
		this.lockzumb = contact.isLockzumb();
		this.ispost = contact.ispost;
		this.mode = contact.getMode();
		this.status = contact.getStatus();		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPost() {
		if(post==null){
			post="";
		}
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getRpost() {
		if(rpost==null){
			rpost="";
		}
		return rpost;
	}

	public void setRpost(String rpost) {
		this.rpost = rpost;
	}

	public int getIspost() {
		return ispost;
	}

	public void setIspost(int ispost) {
		
		this.ispost = ispost;
	}
	
	public void setLock(int TYPE, boolean VALUE){
		if(TYPE==TypeInfo.WAY){
			this.lockway=VALUE;
		}
		else if (TYPE==TypeInfo.ZUMB){
			this.lockzumb=VALUE;
		}
		else if (TYPE==TypeInfo.POST_INFO){
			this.lockpost=VALUE;
		}
		else if (TYPE == TypeInfo.STATUS){
			this.lockstatus=VALUE;
		}
	}

	public boolean isLockway() {
		return lockway;
	}

	public void setLockway(boolean lockway) {
		this.lockway = lockway;
	}

	public boolean isLockzumb() {
		return lockzumb;
	}

	public void setLockzumb(boolean lockzumb) {
		this.lockzumb = lockzumb;
	}

	public boolean isLockpost() {
		return lockpost;
	}

	public void setLockpost(boolean lockpost) {
		this.lockpost = lockpost;
	}

	public boolean isLockstatus() {
		return lockstatus;
	}

	public void setLockstatus(boolean lockstatus) {
		this.lockstatus = lockstatus;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	
	

}
