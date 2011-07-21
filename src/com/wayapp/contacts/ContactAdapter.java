package com.wayapp.contacts;

import java.util.ArrayList;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.wayapp.app.R;
import com.wayapp.services.Constants;
import com.wayapp.sql.DatabaseSQLite;
import com.wayapp.sql.InteractSqLite;
import com.wayapp.tools.TypeInfo;

/**
 * @author raubreak
 *
 */
public class ContactAdapter extends ArrayAdapter<Contact> implements TypeInfo {
	 
    
	private  Activity context;
	private	ArrayList <Contact>listname;
	private  View item;
	


	
	
		ContactAdapter(Activity context, ArrayList<Contact> listname) {
            super(context, R.layout.list_content,listname);
            this.context = context;
            this.listname = listname;
        }
 
        public View getView(int position, View convertView, ViewGroup parent) {
        	refreshList(position);
        return(item);
    }
        
        
        public void refreshList( int position){
        	
        	LayoutInflater inflater = context.getLayoutInflater();
        	item = inflater.inflate(R.layout.list_content, null);
        	SQLiteDatabase db = new DatabaseSQLite(context).getWritableDatabase();
    		InteractSqLite sqlite = new InteractSqLite(db,context);
//        	Contact contact = listname.get(position);
            
            Contact contact = sqlite.getContactCollection().get(position);
//            Log.i(contact.getName(), Integer.toString(contact.getMode()));
            /*            
             * NOMBRE
             * 
             * */
        	
        
            item.setId(contact.getId()); 
           
            TextView name = (TextView)item.findViewById(R.id.name);
   
            name.setText(contact.getName());

            
            /*
             * ULTIMO ZUMBIDO
             * 
             * */
           //formateamos fecha
        

 
            String last_zumb = contact.getLastZumb();//sqlite.getContactByPhone(contact.getPhone(),LAST_ZUMB);


            String today = Constants.getTodayString();
            String date="";
            
  
		        if(last_zumb.length()>5){
	            	int day_zumb = Integer.parseInt(last_zumb.substring(8,10)); 
		            int day_now = Integer.parseInt(today.substring(8,10)); 
		            
		            
		            
		            if (day_zumb==day_now){
		            	
		            	date = "Zumb: "+last_zumb.substring(0, 5);
		            	
		            }
		            else if (day_now==day_zumb+1){
		            	date = "Zumb: "+last_zumb.substring(0, 5) + "Ayer";
		            }
		            else{
		            	
		            	date="Zumb: "+last_zumb.substring(6, last_zumb.length());
		            }
		        }
	       
	        


            TextView zumb = (TextView)item.findViewById(R.id.last_zumb);
            zumb.setText(date);
     
            
            /*
             * ULTIMO WAYAPP
             * 
             * */
            //formateamos fecha
    
            
            String last_way = contact.getLastWayApp();//sqlite.getContactByPhone(contact.getPhone(),LAST_WAY);
            String datew="";
            
          
		        
            	if(last_way.length()>5){
		        	
		        	int day_way = Integer.parseInt(last_way.substring(8,10)); 
		            int day_now = Integer.parseInt(today.substring(8,10)); 
		            
		            
		            
		            if (day_way==day_now){
		            	
		            	datew = "Way: "+last_way.substring(0, 5);
		            	
		            }
		            else if (day_now==day_way+1){
		            	datew = "Way: "+last_way.substring(0, 5) + "Ayer";
		            }
		            else{
		            	
		            	datew="Way: "+last_way.substring(6, last_way.length());
		            }
		      
		
		
		        //Wayapp
		        TextView way = (TextView)item.findViewById(R.id.last_way);
		        way.setText(datew);
            
            }
            
            /*
             * TU POST
             * 
             * */
            
           String post = contact.getPost();//sqlite.getContactByPhone(contact.getPhone(),POST_INFO);

        	   TextView mypost = (TextView)item.findViewById(R.id.postview);
        	   mypost.setText(post);
        	   

            
            /*
             * El POST DESTINATARIO
             * 
             * */
            
         String rpost = contact.getRpost();//sqlite.getContactByPhone(contact.getPhone(),RECEPT_POST);
            
          
	        	TextView recept_post = (TextView)item.findViewById(R.id.postentry);
	           
	        	recept_post.setText(rpost);

            
            /*
             * ESTADO DEL POST QUE ENVIAS
             * 
             * */
  
         String ispost = Integer.toString(contact.getIspost());//sqlite.getContactByPhone(contact.getPhone(),ISPOST);
           if (ispost!=null){
	            
            	if (ispost.compareTo("1")==0){
	                ImageView is_post_image = (ImageView)item.findViewById(R.id.status_post);
	                is_post_image.setImageResource(R.drawable.ok);
	            }
	            else if (ispost.compareTo("0")==0){
	            	
	                ImageView is_post_image = (ImageView)item.findViewById(R.id.status_post);
	                is_post_image.setImageResource(R.drawable.no);
	            }
           
            
            }
           
           //ESTATUS
           TextView statusTxt = (TextView)item.findViewById(R.id.status);
           String status = contact.getStatus();//sqlite.getContactByPhone(contact.getPhone(),STATUS);	
           
           if (status.compareTo("null")!=0 ){
        	   statusTxt.setText(status);
           }else{
        	   statusTxt.setText("Availible");
           }
              
           QuickContactBadge photo = (QuickContactBadge) item.findViewById(R.id.quickContactBadge1);
           photo.setBackgroundResource(R.drawable.picturecont);
         
           
           ImageView mode = (ImageView)item.findViewById(R.id.ImageView01);
//           mode.setImageResource(R.drawable.jabber_offline);
           
			switch (contact.getMode()) {
	
			case Constants.STATUS_ONLINE:
					mode.setBackgroundResource(R.drawable.jabber_online);
//					mode.setImageResource(R.drawable.jabber_online);
				break;
			case Constants.PRESENCEMODE_AWAY:
					mode.setBackgroundResource(R.drawable.jabber_away);
//					mode.setImageResource(R.drawable.jabber_away);
				break;
			case Constants.STATUS_OFFLINE:
				mode.setBackgroundResource(R.drawable.jabber_offline);
//				mode.setImageResource(R.drawable.jabber_offline);
				break;
			case Constants.PRESENCEMODE_XA:
				mode.setBackgroundResource(R.drawable.jabber_xa);
//				mode.setImageResource(R.drawable.jabber_xa);
			break;
			case Constants.PRESENCEMODE_CHAT:
				mode.setBackgroundResource(R.drawable.jabber_chat);
//				mode.setImageResource(R.drawable.jabber_chat);
			break;
			}
           
           
           
           db.close();
        }
       
}