package com.wayapp.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.provider.BaseColumns;

/**
 * @author raubreak
 *
 */
public final class Constants implements BaseColumns {
	
	public static final String DATABASE = "wayapp.db";
	public static final int DATABASE_VERSION = 3;
	
	public static final String TABLE_SETTINGS = "settings";
	public static final String TABLE_SETTINGS_FIELD_KEY = "key";
	public static final String TABLE_SETTINGS_FIELD_VALUE = "value";

	public static final String TABLE_CONVERSATION = "openConversations";
	public static final String TABLE_CONVERSATION_FIELD_ID = "_id";
	public static final String TABLE_CONVERSATION_FIELD_DATE = "date";
	public static final String TABLE_CONVERSATION_FIELD_CHAT = "chatWith";
	public static final String TABLE_CONVERSATION_FIELD_FROM = "msgFrom";
	public static final String TABLE_CONVERSATION_FIELD_TO = "msgTo";
	public static final String TABLE_CONVERSATION_FIELD_MSG = "message";
	public static final String TABLE_CONVERSATION_FIELD_NEW = "new";
	
	public static final String TABLE_LOG = "loggedMessages";
	public static final String TABLE_LOG_FIELD_ID = "_id";
	public static final String TABLE_LOG_FIELD_DATE = "date";
	public static final String TABLE_LOG_FIELD_TIME = "time";
	public static final String TABLE_LOG_FIELD_FROM = "msgFrom";
	public static final String TABLE_LOG_FIELD_RESOURCE = "resourceName";
	public static final String TABLE_LOG_FIELD_MSG = "message";
	
	public static final String TABLE_GROUP = "groups";
	public static final String TABLE_GROUP_FIELD_ID = "_id";
	public static final String TABLE_GROUP_FIELD_GROUP = "groupName";
	public static final String TABLE_GROUP_FIELD_JID = "jid";
	public static final String TABLE_GROUP_FIELD_ORDER = "orderby";
	
	public static final String TABLE_BUDDY = "buddies";
	public static final String TABLE_BUDDY_FIELD_ID = "_id";
	public static final String TABLE_BUDDY_FIELD_JID = "jid";
	public static final String TABLE_BUDDY_FIELD_NAME = "name";
	public static final String TABLE_BUDDY_FIELD_STATUS = "status";
	public static final String TABLE_BUDDY_FIELD_PRESENCETYPE = "presenceType";
	public static final String TABLE_BUDDY_FIELD_PRESENCEMODE = "presenceMode";
	public static final String TABLE_BUDDY_FIELD_MSG = "message";
	public static final String TABLE_BUDDY_FIELD_AVATAR = "avatar";

	public static final String TABLE_STATUSMSG = "statusMessages";
	public static final String TABLE_STATUSMSG_FIELD_ID = "_id";
	public static final String TABLE_STATUSMSG_FIELD_MSG = "message";
	public static final String TABLE_STATUSMSG_FIELD_ACTIVE = "active";
	public static final String TABLE_STATUSMSG_FIELD_LASTUSED = "lastUsed";
	
	public static final String UNDEFINED_GROUP_NAME = "";
	



	public static final int NEW_MESSAGE_NOTIFICATION = 3001;
	
	// Presence Types
	public static final int PRESENCETYPE_AVAILABLE = 0; //  type = "available"
	public static final int PRESENCETYPE_UNAVAILABLE = 1; // type = "unavailable"
	public static final int PRESENCETYPE_NULL = 99;
	
	// Presence Modes
	public static final int STATUS_ONLINE = 0; //null
	public static final int PRESENCEMODE_NULL = 0; //null
	public static final int PRESENCEMODE_CHAT = 1; //"chat"
	public static final int PRESENCEMODE_AWAY = 2; //"away"
	public static final int PRESENCEMODE_XA = 3; //"xa"
	public static final int PRESENCEMODE_DND = 4; //"dnd"
	public static final int STATUS_OFFLINE = 5; //


	
	// Persistent database settings
	public static final String SETTINGS_SELECTED_GROUP = "selectedGroup";
	
	public static String getTodayString() {
		SimpleDateFormat df = new SimpleDateFormat("kk:mm - MM/dd/yy");
		return df.format(new Date());
	}
}
