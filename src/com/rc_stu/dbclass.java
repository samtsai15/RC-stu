package com.rc_stu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
 

//http://jim690701.blogspot.tw/2012/06/android-sqlite.html
public class dbclass extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "site";
	private static final String TABLE_NAME = "student";
	
	
	private static final String DATABASE_CREATE_TABLE =
			
			"CREATE TABLE " + TABLE_NAME + " ( " +
			" id TEXT NOT NULL, " +
			" name TEXT NOT NULL,PRIMARY KEY (id)); " ;
		
	private static final String COL_id="id";
	private static final String COL_name = "name";
	
	//private static final String COL_state = "state";
	
	
     
  private static final int VERSION = 1;
   
         
  public dbclass(Context context, String name, CursorFactory factory,int version) {
   super(context, DATABASE_NAME, factory, version);
  }
 
  public dbclass(Context context) { 
   this(context, DATABASE_NAME, null, VERSION); 
   } 
   
   public dbclass(Context context, String name, int version) {  
    this(context, name, null, version);  
      }  
    

  @Override
  public void onCreate(SQLiteDatabase db) {
  
            db.execSQL(DATABASE_CREATE_TABLE);
  }
 
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	  db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
  }
   
  
  public long insertDB(Site site){
	  SQLiteDatabase db = getWritableDatabase();
	  ContentValues values = new ContentValues();
	  values.put(COL_id, site.getId());
	  values.put(COL_name, site.getName());
	  //values.put(COL_state, site.getState());
	
	  long rowId = db.insert(TABLE_NAME, null, values);
	  db.close();
	  return rowId;
  }
  
  public String queryDB(){
	  SQLiteDatabase db = getReadableDatabase();
	  //String sql = "SELECT * FROM"+TABLE_NAME;
	  Cursor cursor = db.rawQuery("SELECT * FROM student",null);
	  String columnCount =""+cursor.getCount();
	 String stu = "";
	  while (cursor.moveToNext()){
		  
		String stuID = cursor.getString(0);
		String stuName = cursor.getString(1);
		
		  stu = stu +stuID +"  "+stuName+"\n";
		  
	  }
	  cursor.close();
	  db.close();
	return stu;
	  
  }
  
  
  
  @Override   
   public void onOpen(SQLiteDatabase db) {     
           super.onOpen(db);       
 
       } 
 
   @Override
        public synchronized void close() {
            super.close();
        }
 
}