package com.wayapp.tools;

import android.view.Menu;

/**
 * @author raubreak
 *
 */
public interface TypeInfo {

	// IP-XMPP-SERVER
	static final String HOST_XMPP = "87.219.196.43";
	//MySql-Server
	public static String IP = "wayapp.xeill.net";

	// typeinfo
	static final int PHONE = 1;
	static final int NAME = 2;
	static final int PHOTO_ID = 3;
	static final int LAST_WAY = 4;
	static final int LAST_ZUMB = 5;
	static final int DEVICE_ID = 6;
	static final int POST_INFO = 7;
	static final int RECEPT_POST = 8;
	static final int ISPOST = 9;
	static final int STATUS = 10;
	static final int WAYLOCK = 11;
	static final int ZUMBLOCK = 12;
	static final int POSTLOCK = 13;
	static final int STATUSLOCK = 14;
	static final int MODE = 15;
	static final int FILE_RECEIVED = 16;

	
	
	
	//typesendpush
	static final int WAY = 0;
	static final int ZUMB = 1;
	static final int POST = 2;
	static final int WAYOK = 3;
	static final int SENDLOCFAIL = 4;
	static final int ZUMBOK = 5;

	// sd
	final static int WAYLOG = 1;
	final static int ZUMBLOG = 2;
	final static int POSTLOG = 3;
	final static int RECEPTLOG = 4;
	final static int ISPOSTLOG = 5;

	// dialog
	static final int DIALOG_YES_NO_MESSAGE = 1;
	static final int DIALOG_LIST = 3;
	static final int DIALOG_TEXT_ENTRY = 7;

	// menu
	static final int SEND_ID = Menu.FIRST;
	static final int LOC_ID = Menu.FIRST + 1;
	static final int WAYAPP = Menu.FIRST + 2;
	static final int COMP_ZUMB = Menu.FIRST + 3;
	static final int MORE = Menu.FIRST + 4;
	static final int EXIT_ID = Menu.FIRST + 5;

	//voice
	static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	
	static final String DEFAULT_STATUS ="Using WayApp!";
	
	//MenuContact Chat
	
	static final int INFO=0;
	static final int LOCK =1;
	
	//TYPE MESSAGE
	static final int MESSAGE_CHAT =0;
	static final int MESSAGE_FILE =1;
	static final int MESSAGE_PHOTO =2;
	static final int MESSAGE_AUDIO =3;
	
	//pregress roaster n refresh contacts
	
	static final int REFRESH_CHAT =0;
	static final int REFRESH_ROSTER =1;
}
