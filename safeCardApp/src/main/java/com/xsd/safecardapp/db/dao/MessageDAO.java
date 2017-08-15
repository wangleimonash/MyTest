package com.xsd.safecardapp.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.xsd.safecardapp.db.MessageDBHelper;
import com.xsd.safecardapp.javabean.MessageBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class MessageDAO {

	private Context mContext;
	private MessageDBHelper helper;

	public MessageDAO(Context mContext) {
		super();
		this.mContext = mContext;
		helper = new MessageDBHelper(mContext);
	}
	
	public void addAll(List<MessageBean> msgList,String username){
		SQLiteDatabase db = helper.getWritableDatabase();
		
		
		for(MessageBean msg:msgList){
			ContentValues values = new ContentValues();
			values.put("content", msg.getContent());
			values.put("createDate", msg.getCreateDate());
			values.put("title", msg.getTitle());
			values.put("type", msg.getType());
			values.put("username", username);
		    db.insert("message", null, values);
		}
		
	}
	
	public List<MessageBean> getAll(String username){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("message", null, "username=?", new String[]{username}, null, null, "_id desc");
		List<MessageBean> messageList = new ArrayList<MessageBean>();
		
		while(cursor.moveToNext()){
			MessageBean msg = new MessageBean();
			msg.setContent(cursor.getString(cursor.getColumnIndex("content")));
			msg.setCreateDate(cursor.getString(cursor.getColumnIndex("createDate")));
			msg.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			msg.setType(cursor.getString(cursor.getColumnIndex("type")));
			msg.setRead(!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("isRead"))));
			msg.setId(cursor.getInt(cursor.getColumnIndex("_id"))+"");
			messageList.add(msg);
		}
		cursor.close();
		return messageList;
	}
		
	public void setRead(int _id){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("isRead", "true");
		db.update("message", values, "_id=?", new String[]{_id+""});
	}
}
