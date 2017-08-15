package com.xsd.safecardapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class IsReadMsg extends SQLiteOpenHelper{

	private static final String DB_NAME = "mydata.db"; // 数据库名称
	private static final int version = 1; // 数据库版本
	
	public IsReadMsg(Context context) {
		super(context, DB_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table read(_id INTEGER PRIMARY KEY AUTOINCREMENT,time VARCHAR(20),username varchar(20));";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
