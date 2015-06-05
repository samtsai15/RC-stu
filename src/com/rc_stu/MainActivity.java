package com.rc_stu;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button btn_next;
	private static final String NAMESPACE = "http://tempuri.org/";
	private static final String URL = "http://163.14.70.47:80/SNQuery.asmx";
	private static final String METHOD_NAME1 = "info";
	private static final String METHOD_NAME2 = "Update_mac";
	private SharedPreferences sp;
	private String sql_mac = "QQ";// 用來承接回傳回來的判斷，yy代表帳號密碼錯誤，xx代表已經註冊，已註冊者則回傳資料庫的macadd
	// private String macText = "";// 取用手機的macadd
	private String checktext = "";// 用來確認的回傳值，0代表註冊寫入資料成功，1代表註冊寫入資料失敗

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backDialog();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 不推薦
		// http://www.2cto.com/kf/201402/281526.html
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		ActionBar actionBar = getActionBar();
		actionBar.hide();
		findview();
		read_userInfo(); // 讀userInfo.xml
		get_mac();// 取得手機mac_address

		btn_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connect_sql_get_mac();
				// btn_next.setText(sql_mac);
				if (sql_mac.equals("yy")) {
					Toast.makeText(v.getContext(), "帳號密碼錯誤", Toast.LENGTH_LONG)
							.show();
				} else if (sql_mac.equals("xx")) {
					// ///////////////////////////////////////////////////////////測試
					// pass_value.macText= "成";
					// ///////////////////////////////////////////////////////////測試
					connect_sql_insert_mac();
					if (checktext.equals("1"))
						Toast.makeText(v.getContext(), "錯誤", Toast.LENGTH_LONG)
								.show();
					else {
						save_userInfo();

						Intent intent = new Intent(MainActivity.this,
								Stu_state.class);
						startActivity(intent);
						MainActivity.this.finish();
					}
				} else {
					// ///////////////////////////////////////////////////////////測試
					// pass_value.macText ="成";
					// ///////////////////////////////////////////////////////////測試
					if (!sql_mac.equals(pass_value.macText))
						Toast.makeText(v.getContext(), "請用註冊手機登入",
								Toast.LENGTH_LONG).show();
					else {
						save_userInfo();
						Intent intent = new Intent(MainActivity.this,
								Stu_state.class);
						startActivity(intent);
						MainActivity.this.finish();
					}
				}
			}
		});

		// pass_value.Uid = Uid.getText().toString();
		// pass_value.Upw = Upw.getText().toString();
	}

	public void findview() {
		pass_value.Uid = (EditText) findViewById(R.id.uid);
		pass_value.Upw = (EditText) findViewById(R.id.upw);
		btn_next = (Button) findViewById(R.id.button1);

	}

	public void save_userInfo() {
		sp = this.getSharedPreferences("userInfo", Context.MODE_APPEND);
		Editor editor = sp.edit();
		editor.putString("Uid", pass_value.Uid.getText().toString());
		editor.putString("Upw", pass_value.Upw.getText().toString());
		editor.commit();
	}

	public void read_userInfo() {

		sp = this.getSharedPreferences("userInfo", Context.MODE_APPEND);
		// Editor editor = sp.edit();
		pass_value.Uid.setText(sp.getString("Uid", null));
		pass_value.Upw.setText(sp.getString("Upw", null));

		if (!pass_value.Uid.getText().toString().equals("")
				&& !pass_value.Upw.getText().toString().equals("")) {
			Intent intent = new Intent(MainActivity.this, Stu_state.class);
			startActivity(intent);
			MainActivity.this.finish();
		}
	}

	public void connect_sql_get_mac() {
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
			request.addProperty("id", pass_value.Uid.getText().toString());
			request.addProperty("pw", pass_value.Upw.getText().toString());
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;// 嚙磐WS嚙踝蕭嚙踝蕭J嚙諸數伐蕭嚙踝蕭嚙緯嚙稼嚙緻嚙瑾嚙踝蕭_嚙篁WS嚙磅嚙踝蕭嚙踝蕭
			envelope.setOutputSoapObject(request);
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.call((NAMESPACE + METHOD_NAME1), envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			sql_mac = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			sql_mac = "11";
			// ResultOutput.setText("ccc");
		}
	}

	public void connect_sql_insert_mac() {
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
			request.addProperty("id", pass_value.Uid.getText().toString());
			request.addProperty("pw", pass_value.Upw.getText().toString());
			request.addProperty("mac", pass_value.macText);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;// 嚙磐WS嚙踝蕭嚙踝蕭J嚙諸數伐蕭嚙踝蕭嚙緯嚙稼嚙緻嚙瑾嚙踝蕭_嚙篁WS嚙磅嚙踝蕭嚙踝蕭
			envelope.setOutputSoapObject(request);
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.call((NAMESPACE + METHOD_NAME2), envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			checktext = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			sql_mac = "22";
			// ResultOutput.setText("ccc");
		}
	}

	// 取得手機mac_address
	public void get_mac() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		pass_value.macText = info.getMacAddress();
	}

	public void backDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setMessage("請問要關閉App?");
		dialog.setTitle("提示");
		dialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		    	System.exit(0);
		    }  
		}); 
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		     
		    }  
		}); 
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
