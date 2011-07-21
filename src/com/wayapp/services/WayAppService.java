package com.wayapp.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;

import com.wayapp.app.R;
import com.wayapp.contacts.Contact;
import com.wayapp.contacts.ContactChat;
import com.wayapp.contacts.ContactList;
import com.wayapp.location.LocationPosition;
import com.wayapp.sql.DatabaseSQLite;
import com.wayapp.sql.InteractSqLite;
import com.wayapp.tools.TypeInfo;

/**
 * @author raubreak
 *
 */
public class WayAppService extends Service implements TypeInfo{

	
	  //configuracion servidor y user (está en local)
		private String host = HOST_XMPP;
		private String user ;
		private String password = "admin";
		private String status ;
		
		private static final String TAG = "WayAppService";

//		private SharedPreferences prefs;

		private ConnectionConfiguration cc = null;
		public static XMPPConnection con = null;
		private Roster roster = null;
		private WakeLock wl;
		private Vibrator vibrator;
		
		public BroadcastReceiver filereceived;
				
		private WayAppService acs = this;

		private static final String CERT_DIR = "/system/etc/security/";
		private static final String CERT_FILE = "cacerts.bks";
		boolean isreg;
		
		//notification
		private String NOTIFICATION_FIRST ;
		private String NOTIFICATION_SECOND ;
		
		private SharedPreferences prefs;
		private FileTransferManager fileTransferM;
//		private OutgoingFileTransfer outFiletransfer;
		/*
		 * 
		 * 
		 * DECLARACION LISTENERS
		 * 
		 * 
		 * */
		 ServiceDiscoveryManager servicemanager;
		
		private final ConnectionServiceCall.Stub binder = new ConnectionServiceCall.Stub() {


			public void connect() {
				connectToServer();
			}
			public void disconnect() throws RemoteException {
				disconnectFromServer();
			}


			public boolean isLoggedIn() throws RemoteException {
				return isUserLoggedIn();
			}


			public void logOff() throws RemoteException {
				// not used
			}


			public void login() throws RemoteException {
				// not used
				try {
					if (con!=null){
						con.login(user, password,"WayApplication");
					}
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}

			public void setStatus(String state, String type,String mode) throws RemoteException {
				setPresenceState(state,type,mode);
			}


			public void sendMessage(String user, String message) throws RemoteException {
				sendMessagePacket(user, message);
			}
			
			public void getAvatar(String user) throws RemoteException {
				loadAvatar(user);
			}
			
			public void getRoster(boolean newReg) {
				acs.sendRoster(newReg);
				
			}
			
			public void addEntry(String user, String name, List<String> groups) {
				acs.addEntry(user, name, groups);
			}

			public void sendWayApp(String user,String body, String type)
					throws RemoteException {
				sendWayAppPacket(user,body, type);
				// TODO Auto-generated method stub
				
			}
			
			public void sendFilestream(String user,String path) throws RemoteException {
				// TODO Auto-generated method stub
				sendFileStream(user,path);
			}
		};

		/**
		 * 
		 */
		
		
		
		private ConnectionListener connectionListener = new ConnectionListener() {


			public void connectionClosed() {
				Intent intent = new Intent("com.wayapp.wayappim.CONNECTION_CLOSED");
				sendBroadcast(intent);
				//      if (wl.isHeld()){
				//    	  wl.release();
				//      }
				
			}


			public void connectionClosedOnError(Exception e) {
				Intent intent = new Intent("com.wayapp.wayappim.CONNECTION_FAILED");
				sendBroadcast(intent);
				//      if (wl.isHeld()){
				//    	  wl.release();
				//      }
			}


			public void reconnectingIn(int seconds) {
				// TODO Auto-generated method stub
				Log.i(TAG, "Reconnect in "+Integer.toString(seconds));
				connectToServer();
			}


			public void reconnectionFailed(Exception e) {
				// TODO Auto-generated method stub
				Log.i(TAG, "Reconnect failed!");
			}


			public void reconnectionSuccessful() {
				// TODO Auto-generated method stub
				Log.i(TAG, "Reconnect OK!");
			}

		};

		private FileTransferListener filetrasferListener = new FileTransferListener(){
			
			public void fileTransferRequest(final FileTransferRequest arg0) {
				// TODO Auto-generated method stub
				Log.i("FILETRANSFER", "::::::::::::::::::::::::::::RECEIVED!");

			
//				ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(con);
//		        if (sdm == null){
//		            sdm = new ServiceDiscoveryManager(con);
//		        }
//		        FileTransferNegotiator.setServiceEnabled(con, true);
				
        		IncomingFileTransfer incomingFile = arg0.accept();						
	  			String path = "/sdcard/download/"+incomingFile.getFileName();
				Log.i("incomingFile - >", incomingFile.getFileName());
				Log.i("incomingFile - >", incomingFile.getPeer());
				Log.i("incomingFile - >", Long.toString(incomingFile.getFileSize()));
				

				Message msg = new Message(incomingFile.getPeer(),Type.chat);
				msg.setFrom(incomingFile.getPeer());
				msg.setBody(incomingFile.getFileName() + " ("+Long.toString(incomingFile.getFileSize())+")");
				msg.setSubject(Integer.toString(FILE_RECEIVED));
				
				
				notificationFilter(msg);

				
				try {
					incomingFile.recieveFile(new File(path));
					while(!incomingFile.isDone()) {
			            if(incomingFile.getStatus().equals(Status.error)) {
			                  System.out.println("ERROR!!! " + incomingFile.getError());
			            } else {
			                  System.out.println(incomingFile.getStatus());
			                  System.out.println(incomingFile.getProgress());
			            }
			            Thread.sleep(1000);
			      }
//							
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					Log.e("incomingFile",e.toString());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	
				

				
				
			}
			
		};
		
		private PacketListener msgListener = new PacketListener() {
			public void processPacket(Packet packet) {
				
								
				Log.i(TAG, "message received " + packet.getFrom());
				//message
				Message msg = (Message)packet;

				HashMap<String, String> msgInfo  = getMessageInfo(msg);
				//notificacion
				notificationFilter(msg);
				Intent intent = new Intent("com.wayapp.wayappim.NEW_MESSAGE");
				intent.putExtra("test", msgInfo.get("body"));
				intent.putExtra("jid", msgInfo.get("user"));
				intent.putExtra("username", msgInfo.get("username"));
				intent.putExtra("message", msg.getBody());
				intent.putExtra("type", msg.getSubject());
				sendOrderedBroadcast(intent, null);

			}
		};
		
		private PacketListener presenceListener = new PacketListener() {
			public void processPacket(Packet packet) {
				Presence presence = (Presence) packet;
				
				Log.i(TAG, "presence received " + presence.getFrom());
				String type = presence.getType().name();
				Mode mode = presence.getMode();

				int typeValue = getType(type);
				int modeValue = getMode(mode);

				
				Intent intent = new Intent("com.wayapp.wayappim.PRESENCE_CHANGED");
				intent.putExtra("jid", StringUtils.parseBareAddress(presence.getFrom()));
				intent.putExtra("resourceName", StringUtils.parseResource(presence.getFrom()));
				intent.putExtra("resourcePriority", presence.getPriority());
				intent.putExtra("presenceType", typeValue);
				intent.putExtra("presenceMode", modeValue);
				intent.putExtra("presenceMessage", presence.getStatus());
				sendOrderedBroadcast(intent, null);
			}
		};
		
		
//-------------------------------------------------------------------------------------------
		/* (non-Javadoc)
		 * @see android.app.Service#onCreate()
		 * 
		 * ONCREATE::::::::
		 * 
		 */
		
		@Override
		public void onCreate() {
			super.onCreate();
						
			prefs = getSharedPreferences("preferences",Context.MODE_PRIVATE);	
			user = prefs.getString("myphone", null);
			isreg = prefs.contains("myphone");
//			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			Log.i(TAG, "WayApp Connection Service created");
			connectToServer();
			
		

		}

		/* (non-Javadoc)
		 * @see android.app.Service#onStart(android.content.Intent, int)
		 */
		@Override
		public void onStart(Intent intent, int startId) {
			super.onStart(intent, startId);
			
			Log.i(TAG, "WayApp Service started");
			
		}

		/* (non-Javadoc)
		 * @see android.app.Service#onBind(android.content.Intent)
		 */
		@Override
		public IBinder onBind(Intent intent) {
			return binder;
		}

		
		
///-----------------------------------------------------------------------------------------/
		
		
		public void connectToServer() {
			
			final boolean newReg = prefs.getBoolean("newReg", true);
			
			if (newReg){
				Intent intent = new Intent("com.wayapp.wayappim.ROSTER_PRESENCE_START");
				intent.putExtra("type", REFRESH_ROSTER);
				sendBroadcast(intent);
			}
			//----------------------------->>>> //configuracion previa del providermanager
		    System.setProperty("java.net.preferIPv6Addresses", "false");
		    configure(ProviderManager.getInstance());
		//----------------------------->>>>
	
			cc = new ConnectionConfiguration(host, 5222);
			cc.setTruststorePath(CERT_DIR+CERT_FILE);
			con = new XMPPConnection(cc);	
			Thread task= new Thread() {
				public void run() {
					try {
						con.connect();
						if(newReg){
							AccountManager ac = new AccountManager(con);
							Log.i("Connected to server ",con.getHost() + ":" + con.getPort());
							try{
								Log.i("ACCOUNTCREATION","añadiendo " + user +" ....");
								ac.createAccount(user, "admin");
			    				Log.i("ACCOUNTCREATION- Service", "Creada cuenta con exito...");
							}catch(Exception e){
			    				Log.i("ACCOUNTCREATION- Service","ya estaba registrado en XMPP");
							}
						}
						con.login(user, password,"WayApplication");	 
						Log.i("Loggin",con.getUser());	
						Intent isLogged = new Intent("com.wayapp.wayappim.USER_LOGGED");
						sendBroadcast(isLogged);

					} catch (Exception xe) {
						Log.e(TAG, "Could not connect to server: " + xe.getLocalizedMessage());
//						setAllContactsOffline();
						return;
					}
					
					try {
						//packet and presence listeners
						Log.i(TAG, "add connection listeners");
						con.addPacketListener(msgListener, new PacketTypeFilter(Message.class));
						con.addPacketListener(presenceListener, new PacketTypeFilter(Presence.class));						
						//file listener

						fileTransferM = new FileTransferManager(con);
						Log.i(TAG, "add file listeners");
						fileTransferM.addFileTransferListener(filetrasferListener);
						//connection listener
						con.addConnectionListener(connectionListener);
						
					}
					catch(IllegalStateException e) {
						Log.e(TAG, e.getLocalizedMessage());
						return;
					}

					try {
						roster = con.getRoster();
						//añade la publicacion de todos los contactos
						if (newReg){
							sendRoster(newReg);
						}
				
						OfflineMessageManager omm = new OfflineMessageManager(con);						
						Log.i("OfflineMessageManager","Offline Messages -> " + Integer.toString(omm.getMessageCount()));
						try {
							omm.deleteMessages();
						} catch (XMPPException e) {
					 		Log.e(TAG, "Could not delete offline messages.");
						}

					} catch(Exception e) {
						e.printStackTrace();
					}
//					try{
//						sendFileStream(con.getUser());
//					}catch(Exception e){
//						Log.e("WayappService","sendFileStream()" + e.toString());
//					}
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean("newReg", false);
					editor.commit();
					
				}
			};
			task.start();
		}

		
		
		
		/**
		 * @param fromUser
		 * @param msg
		 * @param phone
		 */
		public void notifyUser(String fromUser, String msg, String phone) {						
			
			NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			Notification n = new Notification(R.drawable.icon,NOTIFICATION_FIRST, System.currentTimeMillis());
			int flag=0;
			
			Intent i=null;
			if (phone!=null){
				if (phone.compareTo("web")==0){
					i = new Intent(this,ContactChat.class);
					i.putExtra("newReceived", true);
					i.putExtra("username", fromUser);
					i.putExtra("type", "web");
					Log.i("Service - phoneNotify", phone);
					i.setFlags(1);
					flag=1;
				}else{
					i = new Intent(this,ContactChat.class);
					i.putExtra("newReceived", true);
					i.putExtra("phone", phone);
					i.putExtra("type", "chat");
					i.setFlags(0);
				}
			}
			else{
				i = new Intent(this,ContactList.class);
				i.putExtra("newReceived", true);
				i.putExtra("phone", phone);
				i.setFlags(0);
			}
			
			PendingIntent pi = PendingIntent.getActivity(this, 0, i, flag);
			n.setLatestEventInfo(this,NOTIFICATION_SECOND, msg, pi);

			
				n.defaults |= Notification.DEFAULT_SOUND;

				n.ledARGB = 0xffff69b4;
				n.ledOnMS = 300;
				n.ledOffMS = 1000;
				n.flags |= Notification.FLAG_SHOW_LIGHTS;
				n.flags |= Notification.FLAG_AUTO_CANCEL;
				vibrator.vibrate(new long[] { 10L, 100L}, -1);


			nm.notify(Constants.NEW_MESSAGE_NOTIFICATION, n);

			Log.i(TAG, "message received - notify done");
		}

		/**
		 * @param msg
		 */
		public void notificationFilter(Message msg){

			//Bases de datos
			SQLiteDatabase db = new DatabaseSQLite(WayAppService.this).getWritableDatabase();
			InteractSqLite sqlite = new InteractSqLite(db,WayAppService.this);			
						
			//recgemos contacto
			String username = msg.getFrom().substring(0,msg.getFrom().indexOf('@'));
			Contact contact = sqlite.getContactByPhone(username);
			//filtramos tipo recibido
		
			
			
			Integer type = -1;
			String msge = msg.getBody();
			String user = username;
			String phone = null;

			
			
			if (msg.getSubject()!=null){
				try{
					type = Integer.parseInt(msg.getSubject());	
				}catch(Exception e){
					Log.e("notificationFilter", "no type wayapp subject" + e.toString());
				}
			}

			if (contact!=null){
				Log.e("notificationFilter","Contacto existente - " + contact.getName()+"-" + type.toString());
				user=contact.getName();
				phone = contact.getPhone();
				if (type ==-1){
					type = POST;
				}
			}else{
				Log.e("notificationFilter","Contacto Nulo");

			}
	
			HashMap<String, String> msgInfo=null;
			try{
				msgInfo  = getMessageInfo(msg);
			}catch (Exception e){
				Log.i("msgInfo ERROR" , e.toString());
			}
			
			
			switch(type){
			case WAY:
				phone = null;
				if (!contact.isLockway()){

					LocationPosition locationPosition = new LocationPosition(WayAppService.this);
					Location loc = locationPosition.getLocation();
					
					if (loc != null) {
						String MY_LOCATION = "http://maps.google.com/maps?daddr="
								+ Double.toString(loc.getLatitude()) + ","
								+ Double.toString(loc.getLongitude());
						
						
						sendWayAppPacket(msg.getFrom(),MY_LOCATION,Integer.toString(WAYOK));

						Log.i("DENTRO DE WAY", "enviado! "+ MY_LOCATION);
						NOTIFICATION_FIRST = user +" solicita WayApp!";
						NOTIFICATION_SECOND = "WayApp!";
						msge = "WayApp a " + user +" ha sido enviado.";
					}else{
						sendWayAppPacket(user,"",Integer.toString(SENDLOCFAIL));
						NOTIFICATION_FIRST = user +" solicita WayApp!";
						NOTIFICATION_SECOND = "WayApp!";
						msge = "WayApp a " + user +" - localización no disp.";
					}
					notifyUser(user ,msge, phone);
					
				}else{
					NOTIFICATION_FIRST = user +" solicita WayApp!";
					NOTIFICATION_SECOND = "WayApp! no enviado";
					msge = "WayApp a " + user + " está actualmente bloqueado." ;
					notifyUser(user ,msge, phone);
				}	
				break;
			case ZUMB:
				phone = null;
				NOTIFICATION_FIRST = user +" hace ZumB!";
				NOTIFICATION_SECOND = "Zumb!";
				msge = "Zumb de " + user ;
				notifyUser(user ,msge, phone);
				sendWayAppPacket(msg.getFrom(),msge,Integer.toString(ZUMBOK));
				break;
			case POST:
				NOTIFICATION_FIRST = "Post "+user+" : "+ msge;
				NOTIFICATION_SECOND = "Post de " + user;
				notifyUser(user ,msge, phone);
				sqlite.updateContact(contact.getPhone(), RECEPT_POST, msg.getBody());
				sendWayAppPacket(msg.getFrom(),msge,Integer.toString(ISPOST));
				
				queueIncomingMessage(msgInfo);
				break;
			case WAYOK:
				phone = null;
				Log.i("WAYOK- Recibido"," recibido!" + msge);
				NOTIFICATION_FIRST ="WayApp de " +user +" recibido.";
				NOTIFICATION_SECOND = "WayApp recibido";
				Uri uri = Uri.parse(msge);
				startActivity(new Intent(android.content.Intent.ACTION_VIEW, uri)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				
				msge = "WayApp de " + user +"recibido!";
				notifyUser(user ,msge, phone);
				sqlite.updateContact(contact.getPhone(), LAST_WAY, Constants.getTodayString());
//				sqlitesd.insertLog(contact.getPhone(), contact.getName() , LAST_WAY, null);
				break;
				
			case SENDLOCFAIL:
				phone = null;
				NOTIFICATION_FIRST = user +" WayApp no disponible";
				NOTIFICATION_SECOND = "WayApp!";
				msge = "WayApp de " + user +" - localización no disp.";
				notifyUser(user ,msge, phone);
				break;
				
			case ZUMBOK:
				sqlite.updateContact(contact.getPhone(), LAST_ZUMB, Constants.getTodayString());
				break;
			case ISPOST:
				sqlite.updateContact(contact.getPhone(), ISPOST, "1");
				break;
			case FILE_RECEIVED:
				
				Log.i("FILE_RECEIVED","notificacion:::::::::::::::::::::::::::::::::::::::::::");
				NOTIFICATION_FIRST = user+" : "+ msg.getBody();
				NOTIFICATION_SECOND = "Fichero de " + user;
				notifyUser(user ,msge, phone);
//				sqlite.updateContact(contact.getPhone(), RECEPT_POST, msg.getBody());
				
				break;
			default:
				phone="web";
				NOTIFICATION_FIRST = username;
				NOTIFICATION_SECOND = "Mensage web : " + username;
				notifyUser(user ,msge, phone);
				queueIncomingMessage(msgInfo);
				break;
		}
			
			db.close();	
		}

		public void disconnectFromServer() {
			new Thread() {
				public void run() {
					if(con != null) {
						try{
							con.disconnect();
						}
						catch (Exception e){
							e.printStackTrace();
						}
					}
					if (wl.isHeld()){
						wl.release();
					}
				}
			}.start();

		}

		public void updateRoster() {
			new Thread() {
				public void run() {
					if(con != null && con.getUser()!=null) {
						roster = con.getRoster();
					}
				}
			}.start();

		}


		private void sendRoster(final boolean newReg) {

			/* Load roster group and entry data */
			Collection<RosterGroup> rGroups  = roster.getGroups();			
			/*addEntryes*/
			SQLiteDatabase db = new DatabaseSQLite(WayAppService.this).getWritableDatabase();
			InteractSqLite sqlite = new InteractSqLite(db,WayAppService.this);		
			final ArrayList<Contact> contactList = sqlite.getContactCollection();
			db.close();
//			addEntries(contactList, rGroups);
				   
			final List<String> groupList = new ArrayList<String>();
			for (RosterGroup group : rGroups){
				groupList.add(group.getName());
			}
			
			final String[] grps = new String[groupList.size()];
			for (int i =0; i<grps.length;i++){
				grps[i] = groupList.get(i).toString();
			}

			new Thread() {
				@Override
				public void run() {
					if(con != null && con.getUser() != null) {
						for (Contact contact : contactList){
							try {
								Log.i("WayAppService","add: " + contact.getJid() + " - in - " + groupList.toString());
								roster.createEntry(contact.getJid(), contact.getPhone(), grps);
							} catch (XMPPException e) {
								Log.e(TAG, "Can't add contact: " + contact.getJid(), e);
							}
							
						}
						Intent intent = new Intent("com.wayapp.wayappim.ROSTER_PRESENCE_STOP");
						sendBroadcast(intent);
						

					}
				}
			}.start();
			
		}

		
		
		public boolean isUserLoggedIn() {
			if(con!=null && con.getUser()!=null) {
				return true;
			}
			return false;
		}
		
		protected void loadAvatar(final String user) {
			new Thread() {
				@Override
				public void run() {
					if(con != null && con.getUser() != null) {
						try {
							VCard vCard = new VCard();
							vCard.load(con, user);
							
//							String avatarHash = vCard.getAvatarHash();
							byte[] avatarBytes = vCard.getAvatar();
							
							Bitmap avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);

							// Resize avatar
							int width = avatar.getWidth();
							int height = avatar.getHeight();
							
							int newWidth;
							int newHeight;
							float scaleX;
							float scaleY;
							
							if(width > height) {
								newWidth = 32;
								scaleX = ((float)newWidth / width);
								newHeight = Math.round(height * scaleX);
								scaleY = ((float)newHeight / height);
							}
							else if(height > width) {
								newHeight = 32;
								scaleY = ((float)newHeight / height);
								newWidth = Math.round(width * scaleY);
								scaleX = ((float)newWidth / width);
							}
							else {
								newWidth = 32;
								newHeight = 32;
								scaleX = ((float)newWidth / width);
								scaleY = ((float)newHeight / height);
							}

							Matrix matrix = new Matrix();
							matrix.postScale(scaleX, scaleY); 
							Bitmap resizedAvatar = Bitmap.createBitmap(avatar, 0, 0, width, height, matrix, true);
							
							// Convert resized Avatar to PNG format and save it to byte array
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							resizedAvatar.compress(CompressFormat.PNG, 100, bos);
//							byte[] resizedAvatarBytes = bos.toByteArray();
							
						}
						catch(XMPPException e) {
							Log.e(TAG, e.getMessage());
						}
					}
				}
			}.start();
		}

		
		protected void sendFileStream(String userparam, String path){

			
			ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(con);
	        if (sdm == null){
	            sdm = new ServiceDiscoveryManager(con);
	        }
	        FileTransferNegotiator.setServiceEnabled(con, true);
	        
	        
	        
			final String user =userparam +"/WayApplication";
//			String path = "/sdcard/matrix.txt";
	        Log.i("WayAppService","Get file to intent path .... [" + path +"]" );
	        final File finalFile = new File(path);
			Log.i("WayAppService","File to byte[] ....["+finalFile.toURI().toString()+"]"  );
	        byte[] filebytes = new byte[(int)finalFile.length()];
	        try {
				FileInputStream fis = new FileInputStream(finalFile);
				fis.read(filebytes);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				  Log.e("WayAppService","ParseFile to byte[]",e );
			}
			
			new Thread() {
				@Override
				public void run() {
					if(con != null &&con.getUser() != null) {
						Log.i("WayAppService","sending packet ....to: " + user);
						fileTransferM = new FileTransferManager(con);
						OutgoingFileTransfer outFiletransfer= fileTransferM.createOutgoingFileTransfer(user);
						try {
							outFiletransfer.sendFile(finalFile, user);
							
							
							while(!outFiletransfer.isDone()) {
					            if(outFiletransfer.getStatus().equals(Status.error)) {
					                  System.out.println("ERROR!!! " + outFiletransfer.getError());
					            } else {
					                  System.out.println(outFiletransfer.getStatus());
					                  System.out.println(outFiletransfer.getProgress());
					            }
					            try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					      }
							
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							Log.e("sendFile", e.toString());
						}
						
						
//						logOutgoingMessage("Send -> " + finalFile.getName(), user);
					}
				}
			}.start();
			

	
				// TODO Auto-generated catch block
			
			Log.i("sendFile ","Enviado  ->>");


		}
		
		protected void sendWayAppPacket(String user,String body, String type) {
			Log.i("SendWay", "Enviado - " + type);
			final Message msg = new Message(user,Type.chat);
			msg.setSubject(type);
			msg.setBody(body);
			new Thread() {
				@Override
				public void run() {
					if(con != null && con.getUser() != null) {
						con.sendPacket(msg);
						Log.i("SendWayApp","["+ msg.getTo() + "] - [" + msg.getSubject()+"]");
					}
				}
			}.start();
		}
		
		protected void sendMessagePacket(String user, String message) {
			
			final Message msg = new Message(user,Message.Type.chat);
			msg.setBody(message);
			msg.setSubject(Integer.toString(POST));
			new Thread() {
				@Override
				public void run() {
					if(con != null && con.getUser() != null) {
						con.sendPacket(msg);
						Log.i("WayAppService.sendMessagePacket()","["+ msg.getTo() + "] - [" + msg.getBody()+"]");
						logOutgoingMessage(msg.getBody(), msg.getTo());
					}
				}
			}.start();
		}
		
		protected void addEntry(String user, String name, List<String> groups) {
			
			final String jid = user;
			final String alias = name;
			final String[] grps = new String[groups.size()];

			for (int i =0; i<grps.length;i++){
				grps[i] = groups.get(i).toString();
				Log.i("Array -addEntry",grps[i]);
			}
			
			
			new Thread() {
				@Override
				public void run() {
					if(con != null && con.getUser() != null) {
						try {
							roster.createEntry(jid, alias, grps);
						} catch (XMPPException e) {
							Log.e(TAG, "Can't add contact: ", e);
						}
					}
				}
			}.start();
		}
		
		
		protected void addEntries(final ArrayList<Contact> contactList, Collection<RosterGroup> rGroups) {

			final List<String> groupList = new ArrayList<String>();
			for (RosterGroup group : rGroups){
				groupList.add(group.getName());
			}
			
			final String[] grps = new String[groupList.size()];
			for (int i =0; i<grps.length;i++){
				grps[i] = groupList.get(i).toString();
				Log.i("Array -addEntry",grps[i]);
			}

			new Thread() {
				@Override
				public void run() {
					if(con != null && con.getUser() != null) {
						for (Contact contact : contactList){
							try {
								Log.i("WayAppService","add: " + contact.getJid() + " - in - " + groupList.toString());
								roster.createEntry(contact.getJid(), contact.getPhone(), grps);
							} catch (XMPPException e) {
								Log.e(TAG, "Can't add contact: " + contact.getJid(), e);
							}
						}
					}
				}
			}.start();
		}

		public HashMap<String, String> getMessageInfo(Message msg) {
			/* Build the user id, username, resource */

			String userWithRes = msg.getFrom(); //plain JabberID (width resource)
			int slash = userWithRes.lastIndexOf("/"); // select index of the separator of Jabber ID and resource
			String resource; // only the resource name
			String user; // only the Jabber id

			// Check if there was a separator (should always be) and assign the proper values
			if(slash != -1) {
				resource = userWithRes.substring(slash+1);
				user = userWithRes.substring(0,slash);
			} else {
				resource = "unknown";
				user = userWithRes;
			}

			
			String userName = user; // Alias, if there is one

//			RosterEntry re = roster.getEntry(user);
//			if((re.getName()) != null) {
//				userName = re.getName();
//			}
			
			String body = msg.getBody(); // Message

			java.util.Date date = null; //getTimestamp(packet); currently not working, so ignoring it and use the current time.

			if (date == null) {
				date = new java.util.Date();
			} 

			long time = date.getTime();
			HashMap<String,String> list = new HashMap<String,String>();
			list.put("user", user);
			list.put("resource", resource);
			list.put("username", userName);
			list.put("body", body);
			list.put("time", String.valueOf(time));

			return list;
		}

		// NOT WORKING -> seems to be a bug.
		public static java.util.Date getDelayedStamp(final Packet packet) {

			DelayInformation delay = (DelayInformation) packet.getExtension("jabber:x:delay");

			if (delay != null) {
				return delay.getStamp();
			}
			return null;
		}


		private void queueIncomingMessage(final HashMap<String,String> msgInfo) {
			
			SQLiteDatabase db = new DatabaseSQLite(acs).getWritableDatabase();
			long time = Long.parseLong(msgInfo.get("time"));
			ContentValues val = new ContentValues();

//			if(prefs.getBoolean("prefLogMessagesKey", true)) {
				val.put(Constants.TABLE_LOG_FIELD_DATE, new Date(time).toString());
				val.put(Constants.TABLE_LOG_FIELD_TIME, new Time(time).toString());
				val.put(Constants.TABLE_LOG_FIELD_FROM, msgInfo.get("user"));
				val.put(Constants.TABLE_LOG_FIELD_RESOURCE, msgInfo.get("resource"));
				val.put(Constants.TABLE_LOG_FIELD_MSG, msgInfo.get("body").trim());
				db.insert(Constants.TABLE_LOG, null , val);
//			}
			val.clear();
			val.put(Constants.TABLE_CONVERSATION_FIELD_DATE, time);
			val.put(Constants.TABLE_CONVERSATION_FIELD_CHAT, msgInfo.get("user"));
			val.put(Constants.TABLE_CONVERSATION_FIELD_FROM, msgInfo.get("user"));
			val.put(Constants.TABLE_CONVERSATION_FIELD_TO, "me");
			val.put(Constants.TABLE_CONVERSATION_FIELD_MSG, msgInfo.get("body").trim());
			val.put(Constants.TABLE_CONVERSATION_FIELD_NEW, 1);
			db.insert(Constants.TABLE_CONVERSATION, null , val);
			db.close();
		}

		private void logOutgoingMessage(final String body, final String to) {

			SQLiteDatabase db = new DatabaseSQLite(acs).getWritableDatabase();

			String user = con.getUser();
			int slash = user.lastIndexOf("/");
			String resource = user.substring(slash+1);
			user = user.substring(0,slash);

			long time = System.currentTimeMillis();

			ContentValues val = new ContentValues();

//			if(prefs.getBoolean("prefLogMessagesKey", true)) {
				val.put(Constants.TABLE_LOG_FIELD_DATE, new Date(time).toString());
				val.put(Constants.TABLE_LOG_FIELD_TIME, new Time(time).toString());
				val.put(Constants.TABLE_LOG_FIELD_FROM, user);
				val.put(Constants.TABLE_LOG_FIELD_RESOURCE, resource);
				val.put(Constants.TABLE_LOG_FIELD_MSG, body.trim());
				db.insert(Constants.TABLE_LOG, null , val);
//			}

			val.clear();
			val.put(Constants.TABLE_CONVERSATION_FIELD_DATE, time);
			val.put(Constants.TABLE_CONVERSATION_FIELD_CHAT, to);
			val.put(Constants.TABLE_CONVERSATION_FIELD_FROM, "me");
			val.put(Constants.TABLE_CONVERSATION_FIELD_TO, to);
			val.put(Constants.TABLE_CONVERSATION_FIELD_MSG, body.trim());
			val.put(Constants.TABLE_CONVERSATION_FIELD_NEW, 0);
			db.insert(Constants.TABLE_CONVERSATION, null , val);

			db.close();
		}


		public void setPresenceState(final String state, final String type, final String mode) {
			new Thread() {
				public void run() {
					if(con.getUser()!=null) {
						Presence presence = new Presence(Presence.Type.valueOf(type));
						if(state != null) presence.setStatus(state);
						presence.setMode(Presence.Mode.valueOf(mode));
						presence.setPriority(5);
						con.sendPacket(presence);
					}
				}
			}.start();
		}

		public int getType(String type) {
			if(type == "available") {
				return Constants.PRESENCETYPE_AVAILABLE;
			} else if ( type == "unavailable") {
				return Constants.PRESENCETYPE_UNAVAILABLE;
			}
			return Constants.PRESENCETYPE_NULL;
		}

		public int getMode(Mode mode) {
			if (mode == null) {
				return Constants.PRESENCEMODE_NULL;
			} else if(mode.name() == "away") {
				return Constants.PRESENCEMODE_AWAY;
			} else if (mode.name() == "xa") {
				return Constants.PRESENCEMODE_XA;
			} else if (mode.name() == "chat") {
				return Constants.PRESENCEMODE_CHAT;
			} else if (mode.name() == "dnd") {
				return Constants.PRESENCEMODE_DND;
			}
			return Constants.PRESENCEMODE_NULL;
		}
		 //metodo que configura todos los parametros del ProviderManager
	    private void configure(ProviderManager pm) {
	    //  Private Data Storage
		    pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
		     
		    //  Time
		    try {
		    pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		    } catch (ClassNotFoundException e) {
		    Log.w("---", "Can't load class for org.jivesoftware.smackx.packet.Time");
		    }
		     
		    //  XHTML
		    pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
		     
		    //  Roster Exchange
		    pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());
		    //  Message Events
		    pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());
		    //  Chat State
		    pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		    pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		    pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		    pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		    pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		     
		    //   FileTransfer
		    pm.addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());

		    //  Group Chat Invitations
		    pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());
		    //  Service Discovery # Items
		    pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
		    //  Service Discovery # Info
		    pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
		    //  Data Forms
		    pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());
		    //  MUC User
		    pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());
		    //  MUC Admin
		    pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
		    //  MUC Owner
		    pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
		    //  Delayed Delivery
		    pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());
		    //  Version
		    try {
		    pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		    } catch (ClassNotFoundException e) {
		    Log.w("--->>", "Can't load class for org.jivesoftware.smackx.packet.Version");
		    }
		    //  VCard
		    pm.addIQProvider("vCard","vcard-temp", new VCardProvider());
		    //  Offline Message Requests
		    pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
		    //  Offline Message Indicator
		    pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
		    //  Last Activity
		    pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());
		    //  User Search
		    pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());
		    //  SharedGroupsInfo
		    pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
		    //  JEP-33: Extended Stanza Addressing
		    pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());
	    }
	    
	    
	}
