package com.wayapp.tools;

/**
 * @author raubreak
 *
 */
public class LogBean {


	int id;
	boolean offlineLog;
	String date;
	String from;
	String to;
	String messages;
	int type=0;
	
	/**
	 * @param id
	 * @param date
	 * @param from
	 * @param to
	 * @param messages
	 * @param type
	 * @param isLoggin
	 */
	public LogBean (int id, String date, String from, String to, String messages,int type,boolean isLoggin ){
		this.date = date;
		this.from = from;
		this.to = to;
		this.messages=messages;
		this.type=type;
		this.id=id;
		this.offlineLog=isLoggin;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isOfflineLog() {
		return offlineLog;
	}
	public void setOfflineLog(boolean offlineLog) {
		this.offlineLog = offlineLog;
	}
}