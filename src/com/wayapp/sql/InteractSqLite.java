

package com.wayapp.sql;


 
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wayapp.contacts.Contact;
 
/**
 * @author raubreak
 *
 */
public class InteractSqLite {
 
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreatelist = "CREATE TABLE if not exists Contactos (id INTEGER, ntel TEXT, name TEXT, photoid TEXT, way TEXT, zumb TEXT , deviceid TEXT, post TEXT , rpost TEXT , ispost TEXT ,status TEXT,waylock TEXT, zumblock TEXT, postlock TEXT, statuslock TEXT, mode TEXT)";
//    int type; 
    Context act;
    SQLiteDatabase dbw;
    //constructor
    public InteractSqLite(SQLiteDatabase db, Context contexto) {
    	this.dbw=db;
        this.act=contexto;
    }
 
   
    /*
     * 
     * retorna array del sqlite
     * 
     * 
     * */
    
    //retorna informacion deseada del contacto buscandolo por el parametro deseado
    
 public String getContactByPhone(String PHONE, int TYPE_INFO){
    	
    	
    	String info=null;
    	
		Cursor c = dbw.rawQuery("SELECT id,ntel,name,photoid,way,zumb,deviceid,post,rpost,ispost,status from Contactos",null);
		int i =0;
		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya m�s registros
		     do {

		    	 if( PHONE.compareTo(c.getString(1))==0){

		    		 
		    		  if (TYPE_INFO==2){
			    		 info = c.getString(2);
			    	 }
			    	 else if (TYPE_INFO==3){
			    		 info = c.getString(3); 
			    	 }		    	 
			    	 else if (TYPE_INFO==4){
			    		 info = c.getString(4); 
			    	 }
			    	 else if (TYPE_INFO==5){
			    		 info = c.getString(5); 
			    	 }
			    	 else if (TYPE_INFO==6){
			    		 info = c.getString(6); 
			    	 }
			    	 else if (TYPE_INFO==7){
			    		 info = c.getString(7); 
			    	 }
			    	 else if (TYPE_INFO==8){
			    		 info = c.getString(8); 
			    	 }
			    	 else if (TYPE_INFO==9){
			    		 info = c.getString(9); 
			    	 } 
			    	 else if (TYPE_INFO==10){
			    		 info = c.getString(10); 
			    	 }

			    	 else if (TYPE_INFO==-1){
			    		 
			    		 info = Integer.toString(i);
			    	 }

		    	 }
		    	 
		    	 i++;
		     } while(c.moveToNext());
		}
		c.close();
    	return info;	
    }
 

	 
 	//retorna verdadero si esta en la bdlite, falso si no esta
 	public boolean isInBdLite(String phone){
 		boolean res = false;
		Cursor c = dbw.rawQuery("SELECT id,ntel from Contactos",null);

		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya m�s registros
		     do {
		    	 //comparamos
		    	 if( phone.compareTo(c.getString(1))==0){
	
		    		 res=true;
			      }
		     } while(c.moveToNext());
		}
 		
		c.close();
 		return res;
 	}
    
      
    
    public Contact getContact (int id){
    	
    	Contact contact = null;
    	ArrayList<Contact> contactCollection = getContactCollection();
    	
    	for (Contact contact_temp : contactCollection){
    		
    		if(contact_temp.getId()==id){
    			contact=contact_temp;
    		}
    	}
    	return contact;
    }
    
    public Contact getContactByPhone (String phone){
    	
    	Contact contact = null;
    	ArrayList<Contact> contactCollection = getContactCollection();
    	
    	for (Contact contact_temp : contactCollection){
    		
    		if(contact_temp.getPhone().compareTo(phone)==0){
    			contact=contact_temp;
    		}
    	}
    	return contact;
    }
    

//        
    public ArrayList<Contact> getContactCollection(){
    	ArrayList<Contact> listPhone = new ArrayList<Contact>();
//    	 Log.i("sqlite.getContactCollection","--recogiendo valores de SQLITE");
    	try{
    	Cursor c = null;
    	
    	try{
    		 c = dbw.rawQuery(
    				 "SELECT id,ntel,name,photoid,way,zumb,deviceid,post,rpost,ispost,status,waylock,zumblock ,postlock,statuslock,mode " +
    		 		"from Contactos " +
    		 		"order by name ASC",null);
		}catch(Exception e){
			Log.i("Database Cursor", e.toString());
		}
		
		if (c.moveToFirst()) {
//		     //Recorremos el cursor hasta que no haya m�s registros
		     do {
		    	 //retorna una array de toda la sqlite de la informacion deseada
//		    	 Log.i("Post",c.getString(2));
		    	 
		    	 Contact contact = new Contact(
		    			 c.getString(0),
		    			 c.getString(1),
		    			 c.getString(2),
		    			 c.getString(3),
		    			 c.getString(4),
		    			 c.getString(5),
		    			 c.getString(6),
		    			 c.getString(7),
		    			 c.getString(8),
		    			 c.getString(9),
		    			 c.getString(10),
		    			 Boolean.parseBoolean(c.getString(11)),
		    			 Boolean.parseBoolean(c.getString(12)),
		    			 Boolean.parseBoolean(c.getString(13)),
		    			 Boolean.parseBoolean(c.getString(14)),
		    			 c.getString(15)
		    	 );
		    	 listPhone.add(contact);
		    	 
		     } while(c.moveToNext());
		}
		
    			
		c.close();
		
		
//		for (Contact contact: listPhone){
//			Log.i("LISPHONE GETCONTACT--->", contact.toContactString());	
//		}
		
    	}catch(Exception ef){
    		Log.i("DATABASE------------>>>>",ef.toString());
    	}
    	return listPhone;
    }
    
    public void setListContact(ArrayList <Contact>listPhone){
    	
    	Log.i("sqlite.setListContact","introduciendo valores en SQLITE");
		//sqlite
	 	dbw.execSQL("DROP TABLE IF EXISTS Contactos");
		dbw.execSQL(sqlCreatelist);		
       
		

		
		//base de datos abierta
        if(dbw != null)
        {
 	
	        	for (Contact contact : listPhone){	
	            	dbw.execSQL("INSERT INTO Contactos (id,ntel,name,photoid,way,zumb,post,rpost,status,ispost,waylock,zumblock ,postlock,statuslock,mode,deviceid) " +
	                        "VALUES ("
	            			+contact.getId()+",'"
	                        +contact.getPhone()+"','"
	                        +contact.getName()+"','"
	                        +contact.getPhotoId()+"','"
	                        +contact.getLastWayApp()+"','"
	                        +contact.getLastZumb()+"','"
	                        +contact.getPost()+"','"
	                        +contact.getRpost()+"','"
	                        +contact.getStatus()+"','"
	                        +contact.getIspost()+"','"
	                        +Boolean.toString(contact.isLockway())+"','"
	                        +Boolean.toString(contact.isLockzumb())+"','"
	                        +Boolean.toString(contact.isLockpost())+"','"
	                        +Boolean.toString(contact.isLockstatus())+"','"
	                        +contact.getMode()+"','"
	                        +contact.getDeviceID()+"')");
	        	}
        
    
            
            //Cerramos la base de datos
   
        }	
    	
    }
    

    
    /*
     * 
     * Telefono propio en BDLite
     * 
     * intro , reotrna de bdlite
     * 
     * */
        
    //update de contacto en sql
    public boolean updateContact(String PHONE, int TYPE_INFO , String VALUE){
		
		//sqlite
		boolean res = false;
		Cursor c = dbw.rawQuery("SELECT id,ntel,name,photoid,way,zumb,deviceid,post,rpost,ispost,status,waylock , zumblock , postlock , statuslock from Contactos",null);
			
		
		
		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya m�s registros
		     do {
	 
		    	 
		    	 if( PHONE.compareTo(c.getString(1))==0){

		    		 
		    		  if (TYPE_INFO==2){
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET name = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");
		    			  res = true;
			    	 }
			    	 else if (TYPE_INFO==3){
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET photoid = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");
		    			  res = true;
			    	 }		    	 
			    	 else if (TYPE_INFO==4){
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET way = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");
		    			  res = true;
			    	 }
			    	 else if (TYPE_INFO==5){
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET zumb = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");
		    			  
		    			  res = true;
		    			  
			    	 }
			    	 else if (TYPE_INFO==7){
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET post = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");
		    			  

		    			  res = true;
		    			  
			    	 }
			    	 else if (TYPE_INFO==8){
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET rpost = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");

		    			  res = true;
		    			  
			    	 }
			    	 else if (TYPE_INFO==9){
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET ispost = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");

		    			  res = true;
		    			  
			    	 }
			    	 else if (TYPE_INFO==10){
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET status = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");

		    			  res = true;
		    			  
			    	 }
			    	 else if (TYPE_INFO==11){
			    		 Log.i("WAYLOCK",VALUE);
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET waylock = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");

		    			  res = true;
		    			  
			    	 }
			    	 else if (TYPE_INFO==12){
			    		 Log.i("ZUMBLOCK",VALUE);
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET zumblock = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");

		    			  res = true;
		    			  
			    	 }
			    	 else if (TYPE_INFO==13){
			    		 Log.i("LOCKPOST",VALUE);
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET postlock = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");

		    			  res = true;
		    			  
			    	 }
			    	 else if (TYPE_INFO==14){
			    		 Log.i("STATUSLOCK",VALUE);
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET statuslock = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");

		    			  res = true;
		    			  
			    	 }
			    	 else if (TYPE_INFO==15){
		    			  dbw.execSQL("UPDATE Contactos " +
		                           "SET mode = '" + VALUE
		                           +"' WHERE ntel = '" + PHONE + "'");

		    			  res = true;
		    			  
			    	 }
		    		  

		    	 }
		    	 

		     } while(c.moveToNext());
		}
			c.close();
		
		  return res;
    	
    }
    
    
}