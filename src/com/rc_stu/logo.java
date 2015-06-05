package com.rc_stu;



import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
public class logo extends Activity{
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        // TODO Auto-generated method stub
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.logo);
	        
	        ActionBar act = getActionBar();
	        act.hide();
	      
	        
	        new Thread(new Runnable(){
	        	
	        
	    		
	            @Override
	            public void run() {
	                // TODO Auto-generated method stub
	                try {
	                    Thread.sleep(3000);//這邊可以做你想預先載入的資料
	                    //接下來轉跳到app的主畫面
	                    startActivity(new Intent().setClass(logo.this, MainActivity.class));
	                    logo.this.finish();
	                } catch (InterruptedException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	            
	        }).start();
	    }

}
