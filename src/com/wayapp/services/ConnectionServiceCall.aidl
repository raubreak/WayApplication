package com.wayapp.services;

/**
 * @author raubreak
 *
 */
interface ConnectionServiceCall {
	
	boolean isLoggedIn();
	void setStatus(String state, String type, String mode);
	void login();
	void logOff();
	void connect();
	void disconnect();
	void sendMessage(String user, String message );
	void sendWayApp(String user,String body, String type);
	void sendFilestream(String user, String path);
	void getAvatar(String user);
	void getRoster(boolean newReg);
	void addEntry(String user, String name, in List<String> groups);
}