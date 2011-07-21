package com.wayapp.sql;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wayapp.services.Constants;

/**
 * @author raubreak
 *
 */
public class DatabaseSQLite extends SQLiteOpenHelper {

  private static final String TAG = "SQLiteConnector";

  public DatabaseSQLite(Context context) {
    super(context, Constants.DATABASE, null, Constants.DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
	  db.execSQL("CREATE TABLE " + Constants.TABLE_SETTINGS + " ("
			  + Constants.TABLE_SETTINGS_FIELD_KEY + " TEXT PRIMARY KEY, "
			  + Constants.TABLE_SETTINGS_FIELD_VALUE + " TEXT);"
			  );
	  db.execSQL("CREATE TABLE " + Constants.TABLE_CONVERSATION + " ("
			  + Constants.TABLE_CONVERSATION_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			  + Constants.TABLE_CONVERSATION_FIELD_DATE + " INT,"
			  + Constants.TABLE_CONVERSATION_FIELD_CHAT + " TEXT,"
			  + Constants.TABLE_CONVERSATION_FIELD_FROM + " TEXT,"
			  + Constants.TABLE_CONVERSATION_FIELD_TO + " TEXT,"
			  + Constants.TABLE_CONVERSATION_FIELD_MSG + " TEXT,"
			  + Constants.TABLE_CONVERSATION_FIELD_NEW + " INT);"
			  );
	  db.execSQL("CREATE TABLE " + Constants.TABLE_LOG + " ("
			  + Constants.TABLE_LOG_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			  + Constants.TABLE_LOG_FIELD_DATE + " INT,"
			  + Constants.TABLE_LOG_FIELD_TIME + " TIME,"
			  + Constants.TABLE_LOG_FIELD_FROM + " TEXT,"
			  + Constants.TABLE_LOG_FIELD_RESOURCE + " TEXT,"
			  + Constants.TABLE_LOG_FIELD_MSG + " TEXT);"
			  );
	  db.execSQL("CREATE TABLE " + Constants.TABLE_BUDDY + " ("
			  + Constants.TABLE_BUDDY_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			  + Constants.TABLE_BUDDY_FIELD_JID + " TEXT,"
			  + Constants.TABLE_BUDDY_FIELD_NAME + " TEXT,"
			  + Constants.TABLE_BUDDY_FIELD_STATUS + " TEXT,"
			  + Constants.TABLE_BUDDY_FIELD_PRESENCETYPE + " INTEGER,"
			  + Constants.TABLE_BUDDY_FIELD_PRESENCEMODE + " INTEGER,"
			  + Constants.TABLE_BUDDY_FIELD_MSG + " TEXT);"
			  );
	  db.execSQL("CREATE TABLE " + Constants.TABLE_GROUP + " ("
			  + Constants.TABLE_GROUP_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			  + Constants.TABLE_GROUP_FIELD_GROUP + " TEXT,"
			  + Constants.TABLE_GROUP_FIELD_JID + " TEXT,"
			  + Constants.TABLE_GROUP_FIELD_ORDER + " INTEGER);"
			  );
	  db.execSQL("CREATE TABLE " + Constants.TABLE_STATUSMSG + " ("
			  + Constants.TABLE_STATUSMSG_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			  + Constants.TABLE_STATUSMSG_FIELD_MSG + " TEXT,"
			  + Constants.TABLE_STATUSMSG_FIELD_ACTIVE + " TEXT,"
			  + Constants.TABLE_STATUSMSG_FIELD_LASTUSED + " INT);"
			  );
	  
	  	String sqlCreatephone = "CREATE TABLE if not exists MyPhone (ntel TEXT , reg TEXT, state TEXT)";
	    String sqlCreatelist = "CREATE TABLE if not exists Contactos (id INTEGER, ntel TEXT, name TEXT, photoid TEXT, way TEXT, zumb TEXT , deviceid TEXT, post TEXT , rpost TEXT , ispost TEXT ,status TEXT,waylock TEXT, zumblock TEXT, postlock TEXT, statuslock TEXT)";
  		db.execSQL(sqlCreatephone);
  		db.execSQL(sqlCreatelist);
  		
	  ContentValues cv = new ContentValues();
	  cv.put(Constants.TABLE_STATUSMSG_FIELD_MSG, "");
	  cv.put(Constants.TABLE_STATUSMSG_FIELD_ACTIVE, true);
	  db.insert(Constants.TABLE_STATUSMSG, null, cv);

    Log.i(TAG, "Tables created");

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // upgrade verze 1 na 2
	if (oldVersion<2) {
		Log.i(TAG, "Upgrade sqlite from version 1 to version 2");
		db.execSQL("ALTER TABLE " + Constants.TABLE_GROUP + " ADD COLUMN " + Constants.TABLE_GROUP_FIELD_ORDER + " INTEGER;");
	}
  }

public void dropAllTables() {
	SQLiteDatabase db = getWritableDatabase();
	db.execSQL("DROP TABLE " + Constants.TABLE_CONVERSATION+ ";");
	db.execSQL("DROP TABLE " + Constants.TABLE_LOG +";");
	db.execSQL("DROP TABLE " + Constants.TABLE_BUDDY+ ";");
	db.execSQL("DROP TABLE " + Constants.TABLE_GROUP+ ";");
	db.execSQL("DROP TABLE " + Constants.TABLE_STATUSMSG+ ";");
	onCreate(db);
	db.close();
}
}
