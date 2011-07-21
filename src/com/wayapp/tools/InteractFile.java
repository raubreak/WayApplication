

package com.wayapp.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.wayapp.app.WayApplication;

/**
 * @author raubreak
 *
 */
public class InteractFile extends Activity{

	private static String phone = "FUERA";
	
	
	//escribe en un txt el numero de telefono
		
	//genera Fichero en SD con los datos recogidos en la ARRAY que le intorduzcas por parametro
	public static boolean generateListPhoneOnSD(ArrayList listPhone){
		   
		boolean res=true;
		try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "WayApp");
	        if (!root.exists()) {
	            root.mkdirs();
	        }

	        File gpxfile = new File(root, "listphone.txt");
	        PrintWriter writer = new PrintWriter(gpxfile);
	        
	        for (int i =0; i<listPhone.size();i++)
	        {
	        	writer.append((String) listPhone.get(i) + "\n");
	        	
	        }
	        
	       
	        writer.flush();
	        writer.close();
	        
	    }
		
	    catch(IOException e)
	    {
	        res=false;
	    }
	    
	    return res;
	   }  
	
	//lee lista de contactos filtrada de la SD
	public static ArrayList readListPhoneOnSD(){
		   
		ArrayList listPhone = new ArrayList();
		try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "WayApp");
	        File file= new File(root, "listphone.txt");
        	FileInputStream fileIS = new FileInputStream(file);
        	BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS)); 
        	
        	phone = buf.readLine();  
        	if (phone!=null){
        		listPhone.add(phone);
        	}
        	while ((phone = buf.readLine()) != null) {
                
        		listPhone.add(phone);
            }
        	buf.close();
	    
	    }
	
	    catch(IOException e)
	    {
	    	phone="Excepcion!!";
	    }
	    
	    return listPhone;
	   }  
		
	//comprueba en SD los numeros que esten en la lista de telefono filtrada de BD
	//unicamente se puede utilizar despues de crear el fichero de filtroBD de contactos listphone.txt
	public static boolean verifyPhoneListOnSD(String phone){
		
		ArrayList list = new ArrayList();
		list =readListPhoneOnSD();
		boolean res = false;
		
		for (int i=0;i<list.size();i++){
			
			//es igual
			if (phone.compareTo(list.get(i).toString())==0)
			{
				res=true;
			}
			
		}
		
		
		return res;
	}
		
	//scribe string en txtSD
	public static boolean writeStringOnSD(String select){
		   
		boolean res=true;
		try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "WayApp");
	        if (!root.exists()) {
	            root.mkdirs();
	        }

	        File gpxfile = new File(root, "select.txt");
	        PrintWriter writer = new PrintWriter(gpxfile);
	        writer.append(select);
	        writer.flush();
	        writer.close();
	        
	    }
		
	    catch(IOException e)
	    {
	        res=false;
	    }
	    
	    return res;
	   }  
	
	
	public static String readStringOnSD(){
		 
		String select= null;
		
		try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "WayApp");
	        File file= new File(root, "select.txt");
        	FileInputStream fileIS = new FileInputStream(file);
        	BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS)); 
        	select = buf.readLine();  
        	buf.close();
	    }
		
	    catch(IOException e)
	    {
	    	Log.e("readSD", e.toString());
	    }
	    
	   
	    return select;
	   }  
	
	
	//scribe log en txtSD
	public static boolean writeContactLog( String way_date, String zum_date, String post){
		   
		boolean res=true;
		try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "WayApp/TopInfo");
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        
	        if (way_date!=null){
		        File waylog = new File(root, "waylog.txt");
		        PrintWriter writer = new PrintWriter(waylog);
		        writer.append(way_date);
		        writer.flush();
		        writer.close();
	        }
	        
	        if (zum_date!=null){
		        File zumblog = new File(root, "zumblog.txt");
		        PrintWriter writer = new PrintWriter(zumblog);
		        writer.append(zum_date);
		        writer.flush();
		        writer.close();
	        }
	        
	        if (post!=null){
		        File postlog = new File(root, "postlog.txt");
		        PrintWriter writer = new PrintWriter(postlog);
		        writer.append(post);
		        writer.flush();
		        writer.close();
	        }
	        

	    }
		
		
		
		
	    catch(IOException e)
	    {
	        res=false;
	    }
	    
	    return res;
	   }  
	
	
	
	//retorna la ultiam linea del fichero de logs 1- waylog 2-zumlog 3-POSTLOG 4-RECEPTLOG
	public static String readLineLog( int TYPE){
		 
		String select= null;
		
		try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "WayApp/TopInfo");
	        
	        switch (TYPE){
	        	
	        case 1:
	        	 
	        	File waylog = new File(root, "waylog.txt");
	        	FileInputStream fileIS = new FileInputStream(waylog);
	          	BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS)); 
	          	select = buf.readLine();  
	          	buf.close();
	        	
	        	break;
	        case 2:
	        	
		        File zumblog = new File(root, "zumblog.txt");
	          	FileInputStream fileIS2 = new FileInputStream(zumblog);
	          	BufferedReader buf2 = new BufferedReader(new InputStreamReader(fileIS2)); 
	          	select = buf2.readLine();  
	          	buf2.close();
	        	break;
	        case 3:
	        	 
		        File postlog = new File(root, "postlog.txt");
	          	FileInputStream fileIS3 = new FileInputStream(postlog);
	          	BufferedReader buf3 = new BufferedReader(new InputStreamReader(fileIS3)); 
	          	select = buf3.readLine();  
	          	buf3.close();
	        	break;    
	        case 4:
	        	 
		        File rpostlog = new File(root, "postlog.txt");
	          	FileInputStream fileIS4 = new FileInputStream(rpostlog);
	          	BufferedReader buf4 = new BufferedReader(new InputStreamReader(fileIS4)); 
	          	select = buf4.readLine();  
	          	buf4.close();
	        	break;  
	        	
	        case 5:
	        	 
		        File ispostlog = new File(root, "ispostlog.txt");
	          	FileInputStream fileIS5 = new FileInputStream(ispostlog);
	          	BufferedReader buf5 = new BufferedReader(new InputStreamReader(fileIS5)); 
	          	select = buf5.readLine();  
	          	buf5.close();
	        	break;  
	        }
	        
	      
        	
	    }
			
	    catch(IOException e)
	    {
	    	select="";
	    }
	    
	   
	    return select;
	   }  

	

}
