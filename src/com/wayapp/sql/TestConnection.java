package com.wayapp.sql;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import com.wayapp.contacts.Contact;
import com.wayapp.contacts.ContactList;
import com.wayapp.sql.DatabaseSQLite;
import com.wayapp.sql.InteractSqLite;
import com.wayapp.tools.InteractFile;

/**
 * @author raubreak
 *
 */
public class TestConnection {

	// IP SERVIDOR MYSQL Y MQTT
	public static String IP = "wayapp.xeill.net";
	private Context act;
	public static boolean test = true;
	public String resultel = "";
	private static String LOCALHOST = "http://" + IP + "/json/";
	private static final String MQTT_HOST = "http://" + IP;

	public TestConnection(Context act) {
		this.act = act;
	}

	/*
	 * +-----+------+-----------+------------------+----------------------+
	 * | idu | codp | ntel      | deviceid         | status               |
	 * +-----+------+-----------+------------------+----------------------+
	 * 
	 * Interaccion BD
	 */
	// COMPROBACION DE NUMERO DE TELEFONO EN BD
	/**
	 * @param ntel
	 * @return
	 */
	public boolean verifiedUser(String ntel) {

		InputStream is = null;

		// comprueba telefono
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("ntel", ntel));

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(LOCALHOST + "veriuser.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
			test = false;

		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			resultel = sb.toString();
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}

		// parse json data
		try {
			JSONArray jArray = new JSONArray(resultel);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				Log.i("log_tag", "id: " + json_data.getInt("ntel")
						+ ", Telefon: ");
			}

		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
			test = false;
		}
		return test;
	}

	// INTRODUCE NUEVO REGISTRO BASE DE DATOS
	/**
	 * @param ntel
	 * @param deviceid
	 * @return Boleano de comprobación si el nuevo usuario ha sido registrado
	 * 		   en la base de datos
	 */
	public boolean newReg(String ntel, String deviceid) {
		test = true;
		InputStream is = null;

		// data send
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("ntel", ntel));
		nameValuePairs.add(new BasicNameValuePair("imei", deviceid));

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(LOCALHOST + "regtel.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
			test = false;

		}
		return test;
	}
	/*
	 * Funciones filtro de BD
	 */

	/**
	 * @return  actualiza el sqlite  de contactos recogiendo los valores 
	 * 			del MySql del servidor
	 */
	public boolean updateContactList(){
		boolean result=false;
		
		ArrayList<Contact> serverList = getFilterPhoneListBD();
		SQLiteDatabase db = new DatabaseSQLite(act).getWritableDatabase();
		InteractSqLite sqlite = new InteractSqLite(db,act);
		ArrayList<Contact> sqlist = sqlite.getContactCollection();
		
		if (serverList.size()==sqlist.size()){
			Log.i("updateContactList", "contienen lo mismo");
			result = false;
		}else{
			int i =-1;
			Log.i("updateContactList", "Distinto!");
			for (Contact contact : serverList){
				for (Contact contact2 : sqlist){
					if (contact.equals(contact2)){
						i=sqlist.indexOf(contact2);
					}
				}
				
				if (i==-1){
					sqlist.add(contact);
					Log.i("updateContactList-> New Contact!", contact.getName() +"-" + contact.getPhone());
				}
				i=-1;
			}
			sqlite.setListContact(sqlist);//ordenamos
			sqlist = sqlite.getContactCollection();
			sqlite.setListContact(sqlist);
			result = true;
		}
		
		
		db.close();
		return result;
	}
	
	// ARRAY DE CONTACTOSSS
	/**
	 * +-----+------+-----------+------------------+----------------------+
	 * | idu | codp | ntel      | deviceid         | status               |
	 * +-----+------+-----------+------------------+----------------------+
	 * 
	 * @return
	 */
	public ArrayList<Contact> getFilterPhoneListBD() {

		Log.i("sqlite.getFilterPhoneListBD()","--recogiendo valores de MYSQL");
		String selection = getSelectSqlPhones();
		String resultel = null;
		InputStream is = null;

		// comprueba telefono
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("selection", selection));

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(LOCALHOST + "getphonelist.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());

		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {

				sb.append(line + "\n");
			}
			is.close();
			resultel = sb.toString();
						
		} catch (Exception e) {
			Log.e("getFILTER ", "Error limpiar string line 216  " + e.toString());
		}
		
		
		ArrayList <String>list2 = new ArrayList<String>();
		ArrayList <String>deviceidlist =new ArrayList<String>();
		ArrayList <String>statuslist =new ArrayList<String>();
		ArrayList <Integer>idlist =new ArrayList<Integer>();
		
		try {
			JSONArray jArray = new JSONArray(resultel);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				
				list2.add(json_data.getString("ntel"));
				deviceidlist.add(json_data.getString("deviceid"));
				statuslist.add(json_data.getString("status"));
				idlist.add(json_data.getInt("idu"));
				
			}

		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
			test = false;
		}
		// añadimos la String de telefonos separandolos en una array


		// limpieza de dubplicados y creacion de array de Contactos
		ArrayList<Contact> listContact = new ArrayList();
		Contact temp_cont = null;
		
		for (int i = 0; i < list2.size(); i++) {

			temp_cont = new Contact(act, list2.get(i).toString());
			
			temp_cont.setDeviceID(deviceidlist.get(i).toString());
			temp_cont.setStatus(statuslist.get(i).toString());
			temp_cont.setId(idlist.get(i));
			
			listContact.add(temp_cont);
			
		}
		
		SQLiteDatabase db = new DatabaseSQLite(act).getWritableDatabase();
		InteractSqLite sqlite = new InteractSqLite(db,act);

		
		if (sqlite.getContactCollection().isEmpty()){
			Log.i("importBD","Lista Sqlite vacia");
			
			//introduce en Sqlite
			sqlite.setListContact(listContact);
			//la devuelve ordenada por nombre
			Log.i("sqlite.getFilterPhoneListBD()","--getSQLite ordenados");
			listContact = sqlite.getContactCollection();
			//introducimos ordenada por nombre de nuevo
			sqlite.setListContact(listContact);
			
		}else{
			Log.i("importBD","Lista Sqlite  no vacia");
			
			//hacer comparacion de mysql a lista del sqlite para comprobacion de nuevos usuarios

			//introduce en Sqlite
			sqlite.setListContact(listContact);
			//la devuelve ordenada por nombre
			Log.i("sqlite.getFilterPhoneListBD()","--getSQLite ordenados");
			listContact = sqlite.getContactCollection();
			//introducimos ordenada por nombre de nuevo
			sqlite.setListContact(listContact);
			
		}

		
		
		
		
		db.close();
		Log.i("sqlite.getFilterPhoneListBD()","--fin MYSQL");
		return listContact;

	}

	

	// CREA EL ARRAI DEL SELECT DE TODOS LOS TELEFONOS DEL DISPOSITIVO
	/**
	 * @return String de selección de telefonos del telefono, retorna todos los numeros
	 * 			de telefono almacenados en tu agenda de contactos en formato csv.
	 */
	private String getSelectSqlPhones() {
		String listPhone = "";
		ContentResolver cr = act.getContentResolver(); // contenedor para acer
														// querys
		//
		// recojemos todos los numeros de telefono.
		//
		Cursor phones = cr.query(Phone.CONTENT_URI, null, null, null,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

		while (phones.moveToNext()) {
			String number = phones.getString(phones
					.getColumnIndex(Phone.NUMBER)); // recoje telefono en number

			//limpieza del number
			number = number.replace(" ", "");
			number = number.replace("-", "");
			
			if(number.length()>9){
				number=number.substring(number.length()-9, number.length());
			}
			// añadimos numeros al string para la consulta sql
			listPhone = listPhone + number + ",";

		}
		
		phones.close();
		// limpiamos el select creado
		
		listPhone = listPhone.substring(0, listPhone.length() - 1);
		
//		InteractFile.writeStringOnSD(listPhone);
		return listPhone;
	}


//	// borrado de usuario
//	public static boolean dropSelection(String phone) {
//		test = true;
//		// variables necesarios
//		String selection = phone;
//
//		InputStream is = null;
//
//		// comprueba telefono en el script si el telefono coincide y zumb is
//		// true
//		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		nameValuePairs.add(new BasicNameValuePair("selection", selection));
//
//		// http post
//		try {
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httppost = new HttpPost(LOCALHOST + "removeuser.php");
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity entity = response.getEntity();
//			is = entity.getContent();
//
//		} catch (Exception e) {
//
//			test = false;
//			Log.e("log_tag", "Error in http connection " + e.toString());
//
//		}
//		return test;
//	}
	
	// estado usuario
	public boolean updateState(String phone,String status) {
		test = true;
		// variables necesarios
		String selection = phone;

		InputStream is = null;

		// comprueba telefono en el script si el telefono coincide y zumb is
		// true
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("selection", selection));
		nameValuePairs.add(new BasicNameValuePair("status", status));

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(LOCALHOST + "updatestate.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {

			test = false;
			Log.e("log_tag", "Error in http connection " + e.toString());

		}
		return test;
	}
	public String getStatus(String phone) {
		test = true;
		// variables necesarios
		String selection = phone;
		String resultel=null;
		InputStream is = null;

		Log.i("getStatus()",":::::::::::::::::---"+ selection);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("selection", selection));

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(LOCALHOST + "getstatus.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {

			test = false;
			Log.e("log_tag", "Error in http connection " + e.toString());

		}
		
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				
				
				sb.append(line);
			}

			is.close();
			
			resultel = sb.toString();

		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		
		// parse json data
		try {
			JSONArray jArray = new JSONArray(resultel);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				Log.i("TestConnection", "status: " + json_data.getString("status"));
				resultel = json_data.getString("status");
			}

		} catch (JSONException e) {
			Log.e("TestConnection", "Error parsing data " + e.toString());
			test = false;
		}
		
		return resultel;
	}

}
