
package com.wayapp.contacts;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wayapp.app.R;
import com.wayapp.location.LocationPosition;
import com.wayapp.services.ConnectionServiceCall;
import com.wayapp.services.Constants;
import com.wayapp.services.WayAppService;
import com.wayapp.sql.DatabaseSQLite;
import com.wayapp.sql.InteractSqLite;
import com.wayapp.sql.TestConnection;
import com.wayapp.tools.TypeInfo;

/**
 * @author raubreak
 *
 */
public class ContactList extends ListActivity implements TypeInfo{



	// comprovacion de registrado
	private ProgressDialog pd;
	private SharedPreferences prefs;
	
	private TestConnection testConnection = new TestConnection(this);

	// dialog
	public static int LIST_SELECT_ID = -1;

	public static ArrayList <Contact>endlist;
	private LocationPosition locationPosition = new LocationPosition(this);
	//server
	private Intent aimConServ;
	private ConnectionServiceCall service;
	private ServiceConnection callConnectService = null;
	private BroadcastReceiver connectionClosedReceiver;
	private BroadcastReceiver connectionFailedReceiver;
	private BroadcastReceiver loggerReceiver;
	private BroadcastReceiver presenceBcr;
	private BroadcastReceiver ProgressStartReceiver;
	private BroadcastReceiver ProgressStopReceiver;
	public BroadcastReceiver csr;
	private IntentFilter f;
	private ProgressBar pbar =null;
	
	private TextView spinner1;
	private ListView lst ;
	private ContactAdapter adaptador;
	
	//myJid
	private String jid;
	private String status;
	
	private Intent chatIntent;

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts);	
		prefs = getSharedPreferences("preferences",Context.MODE_PRIVATE);
		spinner1 = (TextView) this.findViewById(R.id.fliper1);
		pd= new ProgressDialog(this);
  		pd.setCancelable(true);
  		pd.setIndeterminate(true);  		
  		pbar = (ProgressBar) findViewById(R.id.progressBar1);
		

  		
  		// si no esta registrado
		if (prefs.getBoolean("newReg", true)) {
			SharedPreferences.Editor editor = prefs.edit();
			status= DEFAULT_STATUS;
			try{
				testConnection.getFilterPhoneListBD();
				status = testConnection.getStatus(prefs.getString("myphone", ""));
				Log.i("ContactList", "myphone ->" + prefs.getString("myphone", "") +" - "+ status);
				
			}catch(Exception e){
				Toast.makeText(ContactList.this, "Actualmente no se encuentran contactos",Toast.LENGTH_SHORT).show();
				Log.e("ListContact--->> ", e.toString());
			}
			if (status.compareTo("null")==0){
				Log.i("STATUS",":::::::::::ES null");
				status=DEFAULT_STATUS;
			}
			editor.putString("status", status);
			editor.commit();
			try {
				Log.i("STATUS","ENVIO STATUS FIRST TIME :::::::::::" + status);
				service.setStatus(prefs.getString("status", DEFAULT_STATUS), "available", "available");
				Log.i("refreshAdaptor -resume","refesco");
				adaptador.notifyDataSetChanged();
			} catch (Exception e) {
				Log.e("refreshAdaptor setstatus - onCreate", e.toString());
			}
			
			Toast.makeText(ContactList.this, "Hazle WayApp a un amigo!!", Toast.LENGTH_LONG).show();
		}
	
		// servicio
		aimConServ = new Intent(this, WayAppService.class);
		startService(aimConServ);
		
		//inicializa la lista de contactos
		initContactList();	
		//declara eventos y listeners de componentes
		setEvents();
		
		registerForContextMenu(getListView());
		
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.ListActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			if (service!=null){
				service.setStatus(prefs.getString("status", DEFAULT_STATUS), "available", "xa");
			}
		} catch (Exception e) {
			Log.e("ContactList - destroy", e.toString());
		}

		Log.i("ContactList", "Activity Destroy");
	
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		
		try {
			service.setStatus(prefs.getString("status", DEFAULT_STATUS), "available", "away");
		} catch (Exception e) {
			Log.e("ContactList - pause", e.toString());
		}
		Log.i("ContactList", "Activity Paused");
		unbindFromService();
		super.onPause();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		bindToService();
		try {
			service.setStatus(prefs.getString("status", DEFAULT_STATUS), "available", "available");
			Log.i("refreshAdaptor -resume","refesco");
			adaptador.notifyDataSetChanged();
		} catch (Exception e) {
			Log.e("refreshAdaptor - resume", e.toString());
		}
		Log.i("ContactList", "Activity Resume");
		super.onResume();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		
		try {
			service.setStatus(prefs.getString("status", DEFAULT_STATUS), "available", "available");
		} catch (Exception e) {
			Log.e("ContactList - restart", e.toString());
		}
		Log.i("ContactList", "Activity Restart");
		super.onRestart();
	}
	
	
////_--------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 */
	public void initContactList(){
		
		// mostramos LIsta
		try {
			SQLiteDatabase db = new DatabaseSQLite(this).getWritableDatabase();
			InteractSqLite sqlite = new InteractSqLite(db,this);
			endlist=sqlite.getContactCollection();
			db.close();
			updateContactList(endlist);
			
		} catch (Exception e) {
			Log.e("ENDLIST-SQLITE", e.toString());
		}
	}
	
	/**
	 * 
	 */
	public void setEvents(){
		// layout Post
		// listener lista
		lst = (ListView) findViewById(android.R.id.list);
		// dialog
		// click del list
		lst.setFocusable(true);		
		lst.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> a, View v, int position,long id) {
		
//				Toast.makeText(ContactList.this,Integer.toString(v.getId()), Toast.LENGTH_LONG).show();
				SQLiteDatabase db = new DatabaseSQLite(ContactList.this).getWritableDatabase();
				InteractSqLite sqlite = new InteractSqLite(db,ContactList.this);
				Contact contact = sqlite.getContact(v.getId());
				db.close();
		        chatIntent = new Intent(ContactList.this, ContactChat.class);
				chatIntent.putExtra("jid", contact.getJid());
				chatIntent.putExtra("phone", contact.getPhone());
				chatIntent.putExtra("type", "chat");
				startActivity(chatIntent);
			}

		});
		// dejar apretado
		lst.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				showDialog(arg1.getId());

				return false;
			}
		});

	}

	/*
	 * 
	 * Menu principal activity
	 *
	 * MENU DE OPCIONES
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item = menu.add(0, SEND_ID, 0, "Actualiza");
		item.setIcon(R.drawable.act1);
		item = menu.add(0, COMP_ZUMB, 0, "ZumB Voice");
		item.setIcon(R.drawable.zum1);
		item = menu.add(0, MORE, 0, "Mas");
		item.setIcon(R.drawable.more1);
		item = menu.add(0, LOC_ID, 0, "Mi direccion");
		item.setIcon(R.drawable.globe1);
		item = menu.add(0, WAYAPP, 0, "Mi Estado");
		item.setIcon(R.drawable.way);
		item = menu.add(0, EXIT_ID, 0, "Salir");
		item.setIcon(R.drawable.exit1);

		return true;
	}

	// SELECIIONADOR DE LOS ITEMS DEL MENU
	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {

		// ATUALIZACION LISTA
		case SEND_ID:
			
			Intent intent = new Intent("com.wayapp.wayappim.ROSTER_PRESENCE_START");
			intent.putExtra("type", REFRESH_CHAT);
			sendBroadcast(intent);
			
			Thread t = new Thread() {
				public void run() {
					// actualizacion lista
					if (testConnection.updateContactList()){
						Log.i("refreshList","Hay cambios en la lista");
						initContactList();
					}
					try{	
						if (service!=null){
							//envia la finalizacion añade suscripciones
//							while(!service.isLoggedIn()){
//								Thread.sleep(1000);
//							}
							service.getRoster(false);
						}
					}catch(Exception e){
						Log.e("refreshListThread",e.toString());
					}
					
				}
			};
			t.start();

			return true;

			// POSICIONAMIENTO
		case LOC_ID:

			Location loc = locationPosition.getLocation();

			if (loc != null) {
				Toast.makeText(
						this,
						"Direccion: " + locationPosition.getAddress(this, loc)
								+ "\n" + "Longitud: "
								+ Double.toString(loc.getLongitude()) + "\n"
								+ "Latitud: "
								+ Double.toString(loc.getLatitude()),
						Toast.LENGTH_LONG).show();

//				String mylocation = "http://maps.google.com/maps?saddr="
//						+ Double.toString(loc.getLatitude()) + ","
//						+ Double.toString(loc.getLongitude());

			} else {
				Toast.makeText(this, "Direccion no disponible",
						Toast.LENGTH_SHORT).show();

			}

			return true;

		case COMP_ZUMB:

			// reconocimiento de voz
			Toast.makeText(ContactList.this, "Habla!", Toast.LENGTH_SHORT)
					.show();

			return true;

			//status
		case WAYAPP:

			final Dialog status_dialog = new Dialog(this);
			// set up dialog
			// final Dialog dialog = new Dialog(ContactList.this);
			status_dialog.setContentView(R.layout.post_dialog);
			status_dialog.setTitle("Edita tu estado");
			status_dialog.setCancelable(true);
			// there are a lot of settings, for dialog, check them all out!

			final EditText Status = (EditText) status_dialog.findViewById(R.id.message);
			Status.setText(prefs.getString("status", DEFAULT_STATUS));
			// set up button
			Button button = (Button) status_dialog.findViewById(R.id.postbutton);
			button.setText("Cambia");
			button.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					final String value = Status.getText().toString();
					Thread t = new Thread (){
						public void run(){
						try {
							
							service.setStatus(value, "available", "available");
						} catch (Exception e) {
							Log.e("ContactList", e.toString());
							
						}
						testConnection.updateState(prefs.getString("myphone", null), value);
						}
					};
					t.start();
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("status", value);
					editor.commit();
					status_dialog.dismiss();
					
				}
			});
			// now that the dialog is set up, it's time to show it
			status_dialog.show();

			return true;

		case MORE:		
						
			return true;
		case EXIT_ID:
			System.exit(0);
			return true;

		}

		return super.onMenuItemSelected(featureId, item);
	}

	/*
	 * Llamada principal dela lista
	 */

	// refresco de array en listView
	/**
	 * @param endlist
	 */
	public void updateContactList(ArrayList <Contact>endlist) {

		ListView lst = (ListView) findViewById(android.R.id.list);
		adaptador = new ContactAdapter(this, endlist);
		lst.setAdapter(adaptador);
		getListView().setAdapter(adaptador);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(final int id) {

			return new AlertDialog.Builder(ContactList.this).
						setIcon(R.drawable.way).
						setTitle("Comparte !").setItems(R.array.select_dialog_items,
					new DialogInterface.OnClickListener() {	
							public void onClick(DialogInterface dialog, int which) {
								SQLiteDatabase db = new DatabaseSQLite(ContactList.this).getWritableDatabase();
								InteractSqLite sqlite = new InteractSqLite(db,ContactList.this);
								Contact contact = sqlite.getContact(id);
								db.close();						
								jid = contact.getJid();
		
								switch (which) {
								//info
									case 0:
										final Dialog infoDialog = new Dialog(ContactList.this);
										infoDialog.setContentView(R.layout.post_dialog);
										infoDialog.setTitle("Información Contacto" );
										TextView txtvalues = (TextView) infoDialog.findViewById(R.id.textView1);
										
										txtvalues.setText(
												"   Nombre : " + contact.getName() + "\n" +
												"   Telefono : " + contact.getPhone() + "\n");

										
										
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
										
									break;
								
								// WAYAPP
									case 1:
										
							
										Thread t = new Thread (){
											public void run(){
												try {
													while(!service.isLoggedIn()){	
														Thread.sleep(2000);
													}							
													service.sendWayApp(jid,"",Integer.toString(WAY));
												} catch (Exception e) {
													Log.i("WayAppSend-Fail", e.toString());
												}
											}
										};
										t.start();
										break;
									// ZUMBIDO
									case 2:
										
										Thread tz = new Thread (){
											public void run(){
												try {
													
													while(!service.isLoggedIn()){					
														Thread.sleep(2000);
													}
													service.sendWayApp(jid,"", Integer.toString(ZUMB));
								
												} catch (Exception e) {
													Log.i("ZumbSend-Fail", e.toString());
												}
											}
										};
										tz.run();
										break;
							}

						}
					}).create();

	}
	

	//listener de mensajeria
	/**
	 * @return
	 */
	private boolean bindToService() {
    	if(callConnectService == null) {
    		callConnectService = new ServiceConnection() {
    			public void onServiceConnected(ComponentName name, IBinder binder) {
    				service = ConnectionServiceCall.Stub.asInterface(binder);
    				
    				Log.i("ConectionReveicer", "Inicializado Service -" +service.toString() );
    				
    				try {
						if(service != null && service.isLoggedIn()) {
							try {
								spinner1.setText("En linea");
								pbar.setVisibility(View.INVISIBLE);
								String value= prefs.getString("status", DEFAULT_STATUS);
								service.setStatus(value, "available", "available");
							} catch (Exception e) {
								Log.e("ContactList - start", e.toString());
							}
						}else{
							// servicio
							pbar.setVisibility(View.VISIBLE);
							spinner1.setText("Conectando...");
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
        		String inJid = intent.getStringExtra("jid");
        		
 
    			try{
    				adaptador.notifyDataSetChanged();
    			}catch(Exception e){
    				Log.e("refreshAdaptor",e.toString());
    			}

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

    			  String jid = intent.getStringExtra("jid");
    			  String resourceName = intent.getStringExtra("resourceName");
    			  Integer resourcePriority = intent.getIntExtra("resourcePriority", 0);
    			  Integer presenceType = intent.getIntExtra("presenceType", Constants.PRESENCETYPE_NULL);
    			  Integer presenceMode = intent.getIntExtra("presenceMode", Constants.PRESENCEMODE_NULL);
    	    	  String presenceMessage = intent.getStringExtra("presenceMessage");
    	    	  String avatarHash = intent.getStringExtra("avatarHash");

    	    	String phone =jid;
    	    	if (jid.contains("@")){
    	    		phone = jid.substring(0, jid.indexOf('@'));
    	    	}
				SQLiteDatabase db = new DatabaseSQLite(ContactList.this).getWritableDatabase();
				InteractSqLite sqlite = new InteractSqLite(db, ContactList.this);
  				if (presenceMessage!=null){
  					sqlite.updateContact(phone, STATUS, presenceMessage);
  				}
  				
  				Log.i("ContactList", "Presence received " + jid);
				sqlite.updateContact(phone, MODE, Integer.toString(presenceMode)); 
				db.close();

  				try {
  					Log.e("refreshAdaptor","refesco");
  					adaptador.notifyDataSetChanged();
  				} catch (Exception e) {
  					Log.e("refreshAdaptor", e.toString());
  				}
    	    	  
    	      }
    	    };
    	    
    	    registerReceiver(presenceBcr, new IntentFilter("com.wayapp.wayappim.PRESENCE_CHANGED"));
    	    
    	    connectionClosedReceiver = new BroadcastReceiver() {
    	        @Override
    	        public void onReceive(Context context, Intent intent) {
    	        	 Log.i("connectionClosedReceiver","::::::::::::::::::::ClosedConnection!");
    	        	 Toast.makeText(ContactList.this,"Sin conexión",	Toast.LENGTH_LONG).show();
						pbar.setVisibility(View.VISIBLE);
						spinner1.setText("Conectando...");
    	        }
    	      };

    	      registerReceiver(connectionClosedReceiver, new IntentFilter("com.wayapp.wayappim.CONNECTION_CLOSED"));
    	      
    	      connectionFailedReceiver = new BroadcastReceiver() {
    	          @Override
    	          public void onReceive(Context context, Intent intent) {
    	        	  
					Log.i("connectionFailedReceiver","::::::::::::::::::::FailedConnection!");
					Toast.makeText(ContactList.this, "Sin conexión",Toast.LENGTH_LONG).show();
					pbar.setVisibility(View.VISIBLE);
					spinner1.setText("Conectando...");

    	          }
    	        };
    	      registerReceiver(connectionFailedReceiver, new IntentFilter("com.wayapp.wayappim.CONNECTION_FAILED"));

    	  	ProgressStartReceiver = new BroadcastReceiver() {
    	  		@Override
    	  		public void onReceive(Context context, Intent intent) {
    	  		
    	  			Log.i("Type","::::::::::::::::::::->" + Integer.toString(intent.getIntExtra("type",-1)));

	    	  		if (intent.getIntExtra("type",-1)==REFRESH_ROSTER){
		    	  		Log.i("rosterProgressStartReceiver","::::::::::::::::::::Dentro!");
						pd.setMessage("Actualizando contactos... \nEsta operación solo se ejecuta la primera vez.");
						
	    	  		}
	    	  		if  (intent.getIntExtra("type",-1)==REFRESH_CHAT) {
		    	  		Log.i("rosterProgressStartReceiver","::::::::::::::::::::Dentro!");
						pd.setMessage("Actualizando contactos...");
	    	  		}
	    	  		pd.setTitle("Espere...");
	    	  		pd.show();
    	  		}
    	  	};
    	  	registerReceiver(ProgressStartReceiver, new IntentFilter("com.wayapp.wayappim.ROSTER_PRESENCE_START"));
    	  	
    	  	ProgressStopReceiver = new BroadcastReceiver() {
    	  		@Override
    	  		public void onReceive(Context context, Intent intent) {

    	  			Log.i("rosterProgressStopReceiver","::::::::::::::::::::Dentro!");
//	    	  		if (pd!=null ){
						if(pd.isShowing()){
  	  			
		    	  			pd.dismiss();
							Toast.makeText(ContactList.this, "Lista actualizada",Toast.LENGTH_SHORT).show();
							try{
								adaptador.notifyDataSetChanged();
							}catch(Exception e){
								Log.e("Contact List ", "progressReceiver adaptor fail");
							}
						
						}
//	    	  		}
    	  		}
    	  	};
    	  	registerReceiver(ProgressStopReceiver, new IntentFilter("com.wayapp.wayappim.ROSTER_PRESENCE_STOP"));
    	  	
    	  	loggerReceiver = new BroadcastReceiver() {
    	  		@Override
    	  		public void onReceive(Context context, Intent intent) {
    	  			spinner1.setText("En linea");
    	  			pbar.setVisibility(View.INVISIBLE);
					status = prefs.getString("status", DEFAULT_STATUS);
					Log.i("Login Receiver", status);
					try {
						service.setStatus(status, "available", "available");
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	  		}
    	  	};
    	  	registerReceiver(loggerReceiver, new IntentFilter("com.wayapp.wayappim.USER_LOGGED"));
    	    return bound;
    	    
    	    
    }
	
	  /**
	 * 
	 */
	private void unbindFromService() {
		    if(callConnectService!=null) {
		    	unregisterReceiver(ProgressStartReceiver);
		    	unregisterReceiver(ProgressStopReceiver);
		    	unregisterReceiver(presenceBcr);
		    	unregisterReceiver(connectionClosedReceiver);
		    	unregisterReceiver(connectionFailedReceiver);
		    	unregisterReceiver(loggerReceiver);
		    	unregisterReceiver(csr);
		    	unbindService(callConnectService);
		    	
		    }
		  }


	/*
	 * End Class
	 */

}
