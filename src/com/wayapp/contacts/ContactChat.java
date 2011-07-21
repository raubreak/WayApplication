package com.wayapp.contacts;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wayapp.app.R;
import com.wayapp.services.ConnectionServiceCall;
import com.wayapp.services.Constants;
import com.wayapp.services.WayAppService;
import com.wayapp.sql.DatabaseSQLite;
import com.wayapp.sql.InteractSqLite;
import com.wayapp.tools.LogBean;
import com.wayapp.tools.TypeInfo;

/**
 * @author raubreak
 *
 */
public class ContactChat extends Activity implements TypeInfo {



	private int USER_ID ;
	 // base
//	private TestConnection testConnection = new TestConnection(this);

	private Vector <LogBean>list = new Vector <LogBean>();
	private ChatAdapter adapter;
	private ListView lst;
	private String phone;
	private String name;
	private String myphone;
	private String jid;
	private Contact contact;
	private DateFormat df;
	private Calendar lastStamp;
	private Button sendbutton;
	private boolean isLoggin=false;
	private static final int PICKFILE_RESULT_CODE = 1;

	private String post;
	//server
	private Intent aimConServ;
	private ConnectionServiceCall service;
	private ServiceConnection callConnectService = null;
	private BroadcastReceiver presenceBcr;
	private BroadcastReceiver loggerReceiver;
	public BroadcastReceiver csr;
	private IntentFilter f;
	private EditText messagetxt;
	private TextView chatnameview;
	
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chatcontact);
		prefs = getSharedPreferences("preferences",Context.MODE_PRIVATE);

		chatnameview = (TextView) findViewById(R.id.chatnameview);
		lst = (ListView) findViewById(R.id.listView1);
		lst.setDividerHeight(0);
		
		SQLiteDatabase db = new DatabaseSQLite(ContactChat.this).getWritableDatabase();
		InteractSqLite sqlite = new InteractSqLite(db,ContactChat.this);
		//recogemos posticion del flag creando contacto para la activity

		
		Log.i("IntentChat",Integer.toString(getIntent().getFlags()));
		if (getIntent().getFlags()!=1){
			contact = sqlite.getContactByPhone(getIntent().getStringExtra("phone"));
			name = contact.getName();
			phone = contact.getPhone();
			myphone =  prefs.getString("myphone", null);
			jid= contact.getJid();
		}

			else{
			String user = getIntent().getStringExtra("username");
			Integer id = getIntent().getFlags();
			contact = new Contact(id,user);
			name = contact.getName();
			phone = contact.getPhone();
			jid = contact.getJid();
		}
		
		chatnameview.setText(name);
//		Toast.makeText(this, name, Toast.LENGTH_LONG).show();
		
		df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
		//servicio
		aimConServ =  new Intent(this,WayAppService.class);
		//metodo inicializa el algoritmo de send-post
		sendPostInit();
		
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		try {
			service.setStatus(prefs.getString("status", DEFAULT_STATUS), "available", "away");
		} catch (Exception e) {
			Log.e("ContactList - pause", e.toString());
		}
		Log.i("ContactChat", "Activity Paused");
//		unbindFromService();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
			bindToService();
			SQLiteDatabase db = new DatabaseSQLite(ContactChat.this).getWritableDatabase();
			bindToService();
			printMessages(getMessages(db,false));
			db.close();	
			Log.i("ContactChat", "Activity Resume");
			
		super.onResume();
		
	}
		
	
	
	private void sendPostInit(){
		
		messagetxt = (EditText) findViewById(R.id.terxtpost2);
		// set up button
		sendbutton = (Button) findViewById(R.id.postbutton2);
		
		sendbutton.setEnabled(false);
		//listener del teclado
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(messagetxt.getWindowToken(), 0);
		
		
		messagetxt.addTextChangedListener(new TextWatcher(){

			public void afterTextChanged(Editable arg0) {	

				
			}
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
//				
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				String msg = messagetxt.getText().toString();
				if (msg.compareTo("")==0){
					sendbutton.setEnabled(false);
				}
				else{
					sendbutton.setText("Post!");
					sendbutton.setEnabled(true);
				}
//				Toast.makeText(ContactChat.this, "escribiendo...", Toast.LENGTH_SHORT).show();

			}});
		
		sendbutton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

							
				if (sendbutton.getText().toString().compareTo("Back")!=0){

					// ---> //ponemos a 0 conforme aun no se sabe si a
					// llegado
					SQLiteDatabase db = new DatabaseSQLite(ContactChat.this).getWritableDatabase();
					InteractSqLite sqlite = new InteractSqLite(db,ContactChat.this);
					
					// sqlitesd.insertLog(phone,name,ISPOST, "0");
					// //<----sqliteSD ispost dejarlo en interna

					// enviamos el post al contacto
					post = messagetxt.getText().toString();
					// ---> //actualizamos sqlite para mostrar por pantalla
					// la info
		            sendMessage();
		            
		            //actualiza DB
		            sqlite.updateContact(phone, ISPOST, "0");
		            sqlite.updateContact(phone, POST_INFO, post);
//					sqlitesd.insertLog(phone, name, POST_INFO, post);// <----sqliteSD
					// flush message text
					db.close();
					sendbutton.setEnabled(false);

				}
				
			}
		});

	}
	
	private void printMessages(final Cursor c) {
		runOnUiThread(new Runnable() {
			public void run() {
			
				// TODO Improve the date comparison of the method
				Date currentDate = new Date();
				Calendar today = Calendar.getInstance();
				today.clear();
				today.set(currentDate.getYear(), currentDate.getMonth(), currentDate.getDate());
				Calendar compareCal = Calendar.getInstance();
			
				
				if(c!=null) {
					
					c.moveToFirst();
					int id;
					String date;
					String from;
					String messages;
					
					while(!c.isAfterLast()) {			
						Date temp = new Date(c.getLong(c.getColumnIndex(Constants.TABLE_CONVERSATION_FIELD_DATE)));
						compareCal.clear();
						compareCal.set(temp.getYear(), temp.getMonth(), temp.getDate());
						
						id = c.getInt(0);
						from = c.getString(2);
						messages = c.getString(4);
						int type=0;
						if (from.equals("me")){
							type=1;
						}
						
						if(lastStamp == null || today.compareTo(lastStamp)!=0) {
							date = temp.toLocaleString();
						} else {
							date = df.format(temp.getTime());
						}
					
						LogBean log = new LogBean (id,date,from,jid,messages,type,true);
						list.add(log);
						lastStamp = compareCal;
						c.moveToNext();
						
					}

				} else {
					
					String messages = messagetxt.getText().toString();
					String date;
					
					if(lastStamp == null || today.compareTo(lastStamp)!=0) {
						date = currentDate.toLocaleString();
					} else {
						date = df.format(currentDate);
					}

					LogBean log = new LogBean (0,date,"me",jid,messages,1,isLoggin);		
					list.add(log);
					lastStamp = today;
					
				}
		
				
				
				adapter= new ChatAdapter(ContactChat.this,list);
				lst.setAdapter(adapter);
//				lst.smoothScrollToPosition(list.size());
				lst.setSelection(list.size());
				

			}
		});

	}
	
	private Cursor getMessages( SQLiteDatabase db ,boolean justNewMessages) {
		
		final String table = Constants.TABLE_CONVERSATION;
																				//index
		final String[] columns = { Constants.TABLE_CONVERSATION_FIELD_ID,		//0
								   Constants.TABLE_CONVERSATION_FIELD_DATE,		//1
								   Constants.TABLE_CONVERSATION_FIELD_FROM,		//2
								   Constants.TABLE_CONVERSATION_FIELD_TO,		//3
								   Constants.TABLE_CONVERSATION_FIELD_MSG		//4
			};
		
		String selection;
		
		if(justNewMessages) {
			selection = "(" + Constants.TABLE_CONVERSATION_FIELD_FROM + " = '" + jid +"' or "
							+ Constants.TABLE_CONVERSATION_FIELD_TO + " = '" + jid + "') and "
							+ Constants.TABLE_CONVERSATION_FIELD_NEW + " = '1'";
		} else {
			selection = "(" + Constants.TABLE_CONVERSATION_FIELD_FROM + " = '" + jid +"' or "
			 				+ Constants.TABLE_CONVERSATION_FIELD_TO + " = '" + jid + "')";
		}
		
		final String[] selectionArgs = null;
		final String groupBy = null;
		final String having = null;
		final String orderBy = Constants.TABLE_CONVERSATION_FIELD_DATE;
		
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		
		
	}
	
	private boolean sendMessage() {
		if(messagetxt.getText().length() > 0)
		{
			try {
					if (isLoggin){
						service.sendMessage(jid,messagetxt.getText().toString());
					}else{
						Toast.makeText(ContactChat.this, "Enviando...", Toast.LENGTH_SHORT).show();
					}
					printMessages(null);
					messagetxt.setText("");
//	    	  		try{
//	    	  			adapter.notifyDataSetChanged();
//	    	  		}catch(Exception e){
//	    	  			e.printStackTrace();
//	    	  		}
	
			} catch (RemoteException e) {
				return false;
			}
		}
		else
			return false;
		
		return true;
	}
	
	
	private void setUnread() {
		ContentValues cv = new ContentValues();
		cv.put(Constants.TABLE_CONVERSATION_FIELD_NEW, 0);
		SQLiteDatabase db = new DatabaseSQLite(this).getWritableDatabase();
		db.update(Constants.TABLE_CONVERSATION,
				  cv,
				  Constants.TABLE_CONVERSATION_FIELD_FROM+"='"+jid+"' or "+Constants.TABLE_CONVERSATION_FIELD_TO+"='"+jid+"'",
				  null);
		db.close();
	}
	
	
	
	// MENU DE OPCIONES
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item = menu.add(0, INFO, 0, "informacion");
		item.setIcon(R.drawable.act1);
		item = menu.add(0, LOCK, 0, "Restricciones");
		item.setIcon(R.drawable.zum1);
		item = menu.add(0, MORE, 0, "Enviar fichero");
		item.setIcon(R.drawable.more1);
		
		return true;
	}
	
	// SELECIIONADOR DE LOS ITEMS DEL MENU
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {

		// ATUALIZACION LISTA
		case INFO:

			final Dialog infoDialog = new Dialog(ContactChat.this);
			infoDialog.setContentView(R.layout.post_dialog);
			infoDialog.setTitle(
					"Información Contacto" );
			TextView txtvalues = (TextView) infoDialog.findViewById(R.id.textView1);
			
			txtvalues.setText(
					"   Nombre : " + contact.getName() + "\n" +
					"   Telefono : " + contact.getPhone() + "\n\n" 
					 	);

			
			
			infoDialog.setCancelable(true);
			
			final EditText txt = (EditText) infoDialog.findViewById(R.id.message);
			txt.setVisibility(View.INVISIBLE);

			// set up button
			Button buttonOk = (Button) infoDialog.findViewById(R.id.postbutton);
			buttonOk.setText("Volver");
			buttonOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					infoDialog.dismiss();
				}
			});
			infoDialog.show();
			
			
			return true;
		case LOCK:
			
			final Dialog lockDialog = new Dialog(ContactChat.this);
			lockDialog.setContentView(R.layout.locklayout);
			lockDialog.setTitle("Restricciones" );
			
			 CheckBox checkway = (CheckBox) lockDialog.findViewById(R.id.checkBox1);
			 CheckBox checkzumb = (CheckBox) lockDialog.findViewById(R.id.checkBox2);
			 CheckBox checkpost = (CheckBox) lockDialog.findViewById(R.id.checkBox3);
			 CheckBox checkstatus = (CheckBox) lockDialog.findViewById(R.id.checkBox4);

			 checkway.setChecked(contact.isLockway());
			 checkzumb.setChecked(contact.isLockzumb());
			 checkpost.setChecked(contact.isLockpost());
			 checkstatus.setChecked(contact.isLockstatus());
			 
					
			checkway.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					contact.setLock(WAY,isChecked);	
//					Toast.makeText(ContactChat.this,Boolean.toString(isChecked) + " way" ,Toast.LENGTH_LONG).show();
					//insertado en BD
					SQLiteDatabase db = new DatabaseSQLite(ContactChat.this).getWritableDatabase();
					InteractSqLite sqlite = new InteractSqLite(db,ContactChat.this);
					sqlite.updateContact(phone, WAYLOCK, Boolean.toString(isChecked));
					db.close();
//					sqlitesd.insertLog(phone, name, WAYLOCK, Boolean.toString(isChecked));
//					sqlitesd.getContactsCollection();
				}});
			
			
			checkzumb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					contact.setLock(ZUMB,isChecked);
//					Toast.makeText(ContactChat.this,Boolean.toString(isChecked) + " zumb" ,Toast.LENGTH_LONG).show();
					//insertado en BD
					SQLiteDatabase db = new DatabaseSQLite(ContactChat.this).getWritableDatabase();
					InteractSqLite sqlite = new InteractSqLite(db,ContactChat.this);
					sqlite.updateContact(phone, ZUMBLOCK, Boolean.toString(isChecked));
					db.close();
//					sqlitesd.insertLog(phone, name, ZUMBLOCK, Boolean.toString(isChecked));
//					sqlitesd.getContactsCollection();
				}});
			checkpost.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					contact.setLock(POST_INFO,isChecked);
//					Toast.makeText(ContactChat.this,Boolean.toString(isChecked) + " post" ,Toast.LENGTH_LONG).show();
					//insertado en BD
					SQLiteDatabase db = new DatabaseSQLite(ContactChat.this).getWritableDatabase();
					InteractSqLite sqlite = new InteractSqLite(db,ContactChat.this);
					sqlite.updateContact(phone, POSTLOCK, Boolean.toString(isChecked));
					db.close();

				}});
			checkstatus.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					contact.setLock(STATUS,isChecked);
					
//					Toast.makeText(ContactChat.this,Boolean.toString(isChecked) + " status" ,Toast.LENGTH_LONG).show();
					//insertado en BD
					SQLiteDatabase db = new DatabaseSQLite(ContactChat.this).getWritableDatabase();
					InteractSqLite sqlite = new InteractSqLite(db,ContactChat.this);
					sqlite.updateContact(phone, STATUSLOCK, Boolean.toString(isChecked));
					db.close();
				}});
		
			lockDialog.setCancelable(true);
			lockDialog.show();
			return true;
			
			
		case MORE:
			
			
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	        intent.setType("file/*");
	        startActivityForResult(Intent.createChooser(intent, "Selecciona archivo"),PICKFILE_RESULT_CODE);	
			return true;
			
		}

			
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	//retorno del fichero
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 // TODO Auto-generated method stub
	 switch(requestCode){
		 case PICKFILE_RESULT_CODE:
		  if(resultCode==RESULT_OK){
		  
		   String FilePath = data.getData().getPath();		  
			try {
				service.sendFilestream(jid, FilePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("ContactChat","sendFile",e);
			}	
		   		   		   
		  }
		  break;
		 }
	 }
	
	private boolean bindToService() {
    	if(callConnectService == null) {
    		callConnectService = new ServiceConnection() {
    			
    			
    			public void onServiceConnected(ComponentName name, IBinder binder) {
    				service = ConnectionServiceCall.Stub.asInterface(binder);
    				try {
    					if (service!=null){
							isLoggin = service.isLoggedIn();
							if (isLoggin){
								
							}
    					}

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				
    			}

    			public void onServiceDisconnected(ComponentName name) {
    				service = null;
    			}
    		};
    	}
    	
    	csr =  new BroadcastReceiver() {
        	@Override
        	public void onReceive(Context context, Intent intent) {
        		
        		Log.i("ContactChat"," Broadcast Receiver!");	        		
       			SQLiteDatabase db = new DatabaseSQLite(ContactChat.this).getWritableDatabase();
       			printMessages(getMessages(db,true));
       			db.close();
       			
        		String inJid = intent.getStringExtra("jid");
        		String username = intent.getStringExtra("username");;
        		if (jid.equalsIgnoreCase(inJid)){
        			csr.abortBroadcast();
        		}
        		setUnread();
        	}
        };
        f = new IntentFilter();
        f.setPriority(10);
        f.addAction("com.wayapp.wayappim.NEW_MESSAGE");
        boolean bound = bindService(aimConServ,callConnectService,BIND_AUTO_CREATE);
    	registerReceiver(csr, f);
    	//Listener de estado
    	presenceBcr = new BroadcastReceiver() {
    	      @Override
    	      public void onReceive(Context context, Intent intent) {    	  
    	    	  Log.i("ContactChat", "Presence received");
    			  String jid = intent.getStringExtra("jid");
//    			  String resourceName = intent.getStringExtra("resourceName");
//    			  Integer resourcePriority = intent.getIntExtra("resourcePriority", 0);
//    			  Integer presenceType = intent.getIntExtra("presenceType", Constants.PRESENCETYPE_NULL);
    			  Integer presenceMode = intent.getIntExtra("presenceMode", Constants.PRESENCEMODE_NULL);
    	    	  String presenceMessage = intent.getStringExtra("presenceMessage");
//    	    	  String avatarHash = intent.getStringExtra("avatarHash");	  
    	    	  
    	    	    String phone =jid;
    		    	if (jid.contains("@")){
    		    		phone = jid.substring(0, jid.indexOf('@'));
    		    	}
    				SQLiteDatabase db = new DatabaseSQLite(ContactChat.this).getWritableDatabase();
    				InteractSqLite sqlite = new InteractSqLite(db, ContactChat.this);
    				
    					if (presenceMessage!=null){
    						sqlite.updateContact(phone, STATUS, presenceMessage);
    					}
    					
    					Log.i("ContactList", phone);
    				sqlite.updateContact(phone, MODE, presenceMode.toString()); 
    				db.close();
    	    	    registerReceiver(presenceBcr, new IntentFilter("com.wayapp.wayappim.PRESENCE_CHANGED"));
    	      }
    	      
    	    };
    	    
    	    loggerReceiver = new BroadcastReceiver() {
    	  		@Override
    	  		public void onReceive(Context context, Intent intent) {
    	  			Log.i("ContactChat","En Linea....");
    	  			isLoggin = true; 
//	    	  		try{
//	    	  			adapter.notifyDataSetChanged();
//	    	  		}catch(Exception e){
//	    	  			e.printStackTrace();
//	    	  		}
    	  		}
    	  	};
    	  	registerReceiver(loggerReceiver, new IntentFilter("com.wayapp.wayappim.USER_LOGGED"));
    	    
    	return bound;
    }
	private void unbindFromService() {
	    if(callConnectService!=null) {

	    	unregisterReceiver(presenceBcr);
	    	unregisterReceiver(loggerReceiver);
	    	unregisterReceiver(csr);
	    	unbindService(callConnectService);
	    	
	    }
	  }

}
