package com.wayapp.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wayapp.contacts.ContactList;
import com.wayapp.sql.DatabaseSQLite;
import com.wayapp.sql.InteractSqLite;
import com.wayapp.sql.TestConnection;
import com.wayapp.tools.TypeInfo;

/**
 * @author raubreak
 *
 */
public class WayApplication extends Activity {



	private SharedPreferences prefs;


	// comrpueba DB
	private TestConnection testConnection;

	// creamos Base de datos type 0 crea tabla MyPhone

	public static boolean isreg = false;
	private ProgressDialog pd=null;

	// variables inciales

	private Button b1;
	private EditText numtxt;
	
	
	private static String ntel=null;
	private TelephonyManager tm;

	// idunic
	public static String mDeviceID;
	private boolean newReg = false;
	
	
	private BroadcastReceiver startProgress=null;
	private BroadcastReceiver stopProgress=null;
	private Thread t=null;
	
	//receivers
	
	// main
	@Override
	public void onCreate(Bundle savedInstanceState) {
		prefs = getSharedPreferences("preferences",Context.MODE_PRIVATE);
		
		
		super.onCreate(savedInstanceState);
		// predefiniciones ventana
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// vista layout
		setContentView(R.layout.about);
		// creacion de deviceID

		mDeviceID = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("deviceid", mDeviceID);
		editor.commit();
		testConnection = new TestConnection(this);
		
		if (!isNetworkAvailable()){
			Toast.makeText(this,"Sin conexión",	Toast.LENGTH_LONG).show();
		}
		initContent();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceivers();
	}
	
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceivers() ;

	}
	// ////-----------------------------------------------------------------------------------------

	/**
	 * 
	 */
	private void initContent() {
		// recojermos valores telefono		

		// toma valor de xml
		b1 = (Button) findViewById(R.id.entryButton);
		numtxt = (EditText) findViewById(R.id.nteltxt);

		// verificacion si el usuario esta en base de datos, sino pantalla
		// de registro
		// if (testConnection.verifiedUser(ntel)) { // <- Seria si esta
		// registrado
		if (prefs.contains("myphone")) {
			// a la lista de cotactos
			launchContactList();
			// cierro actividad para el retorno
			finish();

		} else {
			/*
			 * 
			 * AQUI ELIMINAR REGISTRO SI EXISTE DE LA BASE DE DATOS RETORNAR
			 * EL ID DE LA CNSULTA
			 */
			// notificacion NO registrado
			Toast.makeText(	WayApplication.this,
							"No estas registrado! Introduce tu numero para acceder.",
							Toast.LENGTH_LONG).show();
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("status", TypeInfo.DEFAULT_STATUS);
			editor.commit();
			numtxt.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					numtxt.setText("");
				}});	
			
			// Clica a acceder
			b1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(numtxt.getWindowToken(), 0);
					
					sendBroadcast(new Intent("com.wayapp.wayappim.START_PROGRESS"));
					t = new Thread(){
						public void run(){
							ntel = numtxt.getText().toString();
							SharedPreferences.Editor editor = prefs.edit();
							editor.putString("myphone", ntel);
							editor.commit();
							
							// comprueba que anteriormente esté en la base de datos
							if(!testConnection.verifiedUser(ntel)){
								//borrado base de datos
//									TestConnection.dropSelection(mDeviceID);
								newReg = testConnection.newReg(ntel, mDeviceID);
								
							}else{
								//nuevo reg en telefono
								newReg=true;
							}
							Intent i = new Intent("com.wayapp.wayappim.STOP_PROGRESS");
							i.putExtra("newReg", newReg);
							sendBroadcast(i);
						}
					};
					t.start();

				}

			});
		}

		

	}

	// intent de pantalla
	/**
	 * 
	 */
	protected void launchContactList() {
		Intent i = new Intent(this, ContactList.class);
		startActivity(i);
	}


	// comprueba conexion de red disponible
	/**
	 * @return
	 */
	public boolean isNetworkAvailable() {
		Context context = getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.d("NETWORK", "No network connection available");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 */
	public void registerReceivers(){
		
		startProgress = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				
				pd = new ProgressDialog(WayApplication.this);
				pd.setIndeterminate(true);
				pd.setMessage("Realizando registro.....");
				pd.show();
			}
			
		};
		
		registerReceiver(startProgress, new IntentFilter("com.wayapp.wayappim.START_PROGRESS"));
		
		stopProgress = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				newReg = intent.getBooleanExtra("newReg", false);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("newReg", newReg);
				editor.commit();
				
				if (newReg) {
					Toast.makeText(WayApplication.this,"Registro finalizado con exito",	Toast.LENGTH_LONG).show();
					// a la lista de contactos
					Log.i("Preferences:" ,
							"myphone - " +prefs.getString("myphone", "none")+
							"deviceid - "+prefs.getString("deviceid", "none")+
							"isReg - "+ prefs.getBoolean("newReg", false)							
						);
					launchContactList();
					// cierro actividad para el return
					if (pd!=null ){
						if(pd.isShowing()){
		    	  			pd.dismiss();
						}
					}
					finish();
				} else {
					Toast.makeText(WayApplication.this,"Registro no realizado, reintentando...", Toast.LENGTH_LONG)
							.show();
					if(t!=null){
						t.start();
					}
				}	
			}

		};
		registerReceiver(stopProgress,  new IntentFilter("com.wayapp.wayappim.STOP_PROGRESS"));

		
	}

	  /**
	 * 
	 */
	private void unregisterReceivers() {
		    
		if (startProgress!=null){
	    	unregisterReceiver(startProgress);
		}
		if (stopProgress!=null){
			unregisterReceiver(stopProgress);
		}
	}
}