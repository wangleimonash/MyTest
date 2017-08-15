package com.xsd.safecardapp.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.xsd.safecardapp.db.IsReadMsg;
import com.xsd.safecardapp.javabean.MessageBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class isReadDAO {
	
	private Context mContext;
	private IsReadMsg helper;

	public isReadDAO(Context mContext) {
		super();
		this.mContext = mContext;
		helper = new IsReadMsg(mContext);
	}
	
	public List<String> getList(String username){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("read", null, "username=?", new String[]{username}, null, null, null);
		List<String> strList = new ArrayList<String>();
		
		while(cursor.moveToNext()){
			strList.add(cursor.getString(cursor.getColumnIndex("time")));			
		}
		cursor.close();
		return strList;
	}
	
	public void add(String time,String username){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		db.beginTransaction();  //开始事务  
		
			values.put("time", time);
			values.put("username", username);
			
		    db.insert("read", null, values);
		
		db.setTransactionSuccessful();  //设置事务成功完成  
		db.endTransaction();    //结束事务  
	}
	
	public void addAll(List<String> timeList,String username){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		db.beginTransaction();  //开始事务  
		for(String str : timeList){
		values.put("time", str);
		values.put("username", username);
			
		db.insert("read", null, values);
		}
		
		db.setTransactionSuccessful();  //设置事务成功完成  
		db.endTransaction();    //结束事务  
	}
	
	

}
