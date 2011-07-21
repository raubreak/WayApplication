/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/raubreak/Escritorio/WayAppProject/WayApplication/src/com/wayapp/services/ConnectionServiceCall.aidl
 */
package com.wayapp.services;
/**
 * @author raubreak
 *
 */
public interface ConnectionServiceCall extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.wayapp.services.ConnectionServiceCall
{
private static final java.lang.String DESCRIPTOR = "com.wayapp.services.ConnectionServiceCall";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.wayapp.services.ConnectionServiceCall interface,
 * generating a proxy if needed.
 */
public static com.wayapp.services.ConnectionServiceCall asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.wayapp.services.ConnectionServiceCall))) {
return ((com.wayapp.services.ConnectionServiceCall)iin);
}
return new com.wayapp.services.ConnectionServiceCall.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_isLoggedIn:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isLoggedIn();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setStatus:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.setStatus(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_login:
{
data.enforceInterface(DESCRIPTOR);
this.login();
reply.writeNoException();
return true;
}
case TRANSACTION_logOff:
{
data.enforceInterface(DESCRIPTOR);
this.logOff();
reply.writeNoException();
return true;
}
case TRANSACTION_connect:
{
data.enforceInterface(DESCRIPTOR);
this.connect();
reply.writeNoException();
return true;
}
case TRANSACTION_disconnect:
{
data.enforceInterface(DESCRIPTOR);
this.disconnect();
reply.writeNoException();
return true;
}
case TRANSACTION_sendMessage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.sendMessage(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_sendWayApp:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.sendWayApp(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_sendFilestream:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.sendFilestream(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getAvatar:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.getAvatar(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getRoster:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.getRoster(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_addEntry:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.util.List<java.lang.String> _arg2;
_arg2 = data.createStringArrayList();
this.addEntry(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.wayapp.services.ConnectionServiceCall
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public boolean isLoggedIn() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isLoggedIn, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setStatus(java.lang.String state, java.lang.String type, java.lang.String mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(state);
_data.writeString(type);
_data.writeString(mode);
mRemote.transact(Stub.TRANSACTION_setStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void login() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_login, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void logOff() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_logOff, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void connect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_connect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void disconnect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disconnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void sendMessage(java.lang.String user, java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(user);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_sendMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void sendWayApp(java.lang.String user, java.lang.String body, java.lang.String type) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(user);
_data.writeString(body);
_data.writeString(type);
mRemote.transact(Stub.TRANSACTION_sendWayApp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void sendFilestream(java.lang.String user, java.lang.String path) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(user);
_data.writeString(path);
mRemote.transact(Stub.TRANSACTION_sendFilestream, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void getAvatar(java.lang.String user) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(user);
mRemote.transact(Stub.TRANSACTION_getAvatar, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void getRoster(boolean newReg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((newReg)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_getRoster, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void addEntry(java.lang.String user, java.lang.String name, java.util.List<java.lang.String> groups) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(user);
_data.writeString(name);
_data.writeStringList(groups);
mRemote.transact(Stub.TRANSACTION_addEntry, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_isLoggedIn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_login = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_logOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_connect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_disconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_sendMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_sendWayApp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_sendFilestream = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getAvatar = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getRoster = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_addEntry = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
}
public boolean isLoggedIn() throws android.os.RemoteException;
public void setStatus(java.lang.String state, java.lang.String type, java.lang.String mode) throws android.os.RemoteException;
public void login() throws android.os.RemoteException;
public void logOff() throws android.os.RemoteException;
public void connect() throws android.os.RemoteException;
public void disconnect() throws android.os.RemoteException;
public void sendMessage(java.lang.String user, java.lang.String message) throws android.os.RemoteException;
public void sendWayApp(java.lang.String user, java.lang.String body, java.lang.String type) throws android.os.RemoteException;
public void sendFilestream(java.lang.String user, java.lang.String path) throws android.os.RemoteException;
public void getAvatar(java.lang.String user) throws android.os.RemoteException;
public void getRoster(boolean newReg) throws android.os.RemoteException;
public void addEntry(java.lang.String user, java.lang.String name, java.util.List<java.lang.String> groups) throws android.os.RemoteException;
}
