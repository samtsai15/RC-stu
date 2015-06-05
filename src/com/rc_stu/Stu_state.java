package com.rc_stu;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.view.ViewGroup.LayoutParams;
import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Stu_state extends Activity {
	private static final String NAMESPACE = "http://tempuri.org/";
	private static final String URL = "http://163.14.70.47:80/SNQuery.asmx";
	private static final String METHOD_NAME = "named";
	private Button btn_query;
	private TextView Sid;
	private TextView Scou;
	private TextView State;
	private ImageView chicken;
	private dbclass dbHlp;
	private SharedPreferences sp;
	private String ap_mac = "";// ���β{�bAP��MACADDRESS

	private String macText = "";// ���Τ����macaddress
	private String check = "";
	private String course_id = "";
	private String course_name = "";

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
		setContentView(R.layout.stu_state);
		// ������
		// http://www.2cto.com/kf/201402/281526.html
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		read_userInfo();// Ū���p����������
		btn_query = (Button) findViewById(R.id.button1);
		btn_query.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Intent intent = new Intent(Stu_state.this, Stu_Query.class);
				startActivity(intent);*/
			}
		});

		chicken = (ImageView) findViewById(R.id.image_chicken);
		Sid = (TextView) findViewById(R.id.sid);
		Scou = (TextView) findViewById(R.id.scourse);
		State = (TextView) findViewById(R.id.state);
		get_mac();// ���o���mac_address&&AP��MACADDRESS
		// ///////////////////////////////////////////////////////////����
		// macText = "��";
		// ap_mac="first";

		// ///////////////////////////////////////////////////////////����
		if (pass_value.level != 1) {
			change_image();// ����
		}
		connect_sql_Named();
		if (check.equals("0") || check.equals("2")) {
			Sid.setText("�Ǹ��G" + pass_value.Uid.getText().toString());
			Scou.setText("�ҵ{�G" + course_name);
			if (check.equals("0")) {
				State.setText("����");
				pass_value.level++;// ���żW�[
				if (pass_value.level > 7)
					pass_value.level = 7;
				save_userInfo();// �s��
				change_image();// ����
			} else {
				State.setText("���");
				save_userInfo();// �s��
				change_image();// ����(�������)
			}
		} else if (check.equals("1")) {
			Sid.setText("�Ǹ��G" + pass_value.Uid.getText().toString());
			Scou.setText("�ҵ{�G" + "�L");
			State.setText("�L�I�W");
		} else if (check.equals("Q")) // �����I�W����
		{
			Sid.setText("�Ǹ��G" + pass_value.Uid.getText().toString());
			Scou.setText("�ҵ{�G" + course_name);
			State.setText("����");
		} else
			State.setText("no");

		// btn_query.setText(check);
		//databaseinsert();
	}

	public void databaseinsert() {
		//String Scou_ = Scou.getText().toString().trim();
		//String State_ = State.getText().toString().trim();

		StringBuilder sb = new StringBuilder();
		Site site = new Site(Sid.getText().toString().trim(), Scou.getText().toString().trim());
		long rowId = dbHlp.insertDB(site);
		if (rowId != -1) {
			sb.append(getString(R.string.insert_success));

		} else {

			sb.append(getString(R.string.insert_fail));
		}
		Toast.makeText(Stu_state.this, sb, Toast.LENGTH_SHORT).show();
	}

	public void save_userInfo() {
		sp = this.getSharedPreferences("userInfo", Context.MODE_APPEND);
		Editor editor = sp.edit();
		editor.putString("level", Integer.toString(pass_value.level));
		editor.commit();

	}

	public void read_userInfo() {

		sp = this.getSharedPreferences("userInfo", Context.MODE_APPEND);
		// Editor editor = sp.edit();
		if (pass_value.level == 1 && sp.getString("level", null) == null) {
		} else {
			pass_value.level = Integer.parseInt(sp.getString("level", null));
		}
	}

	public void change_image() {
		int tett = pass_value.level;
		String uri = "@drawable/k" + Integer.toString(pass_value.level);
		chicken.setImageResource(getResources().getIdentifier(uri, null,
				getPackageName()));

	}

	@Override
	public void onResume() {
		super.onResume();
		if (dbHlp == null)
			dbHlp = new dbclass(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (dbHlp != null) {
			dbHlp.close();
			dbHlp = null;
		}
	}

	public void connect_sql_Named() {
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("id", pass_value.Uid.getText().toString());
			request.addProperty("pw", pass_value.Upw.getText().toString());
			request.addProperty("mac", macText);
			request.addProperty("apmac", ap_mac);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;// �YWS����J�Ѽƥ����n�[�o�@��_�hWS�S����
			envelope.setOutputSoapObject(request);
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.call((NAMESPACE + METHOD_NAME), envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			check = response.toString().split("_")[0];
			course_id = response.toString().split("_")[1];
			course_name = response.toString().split("_")[2];
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "���~", Toast.LENGTH_LONG).show();
		}
	}

	// ���o���mac_address&&AP��MACADDRESS
	public void get_mac() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		macText = info.getMacAddress();
		ap_mac = info.getBSSID();
	}
	public void backDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(Stu_state.this);
		dialog.setMessage("�аݭn����App?");
		dialog.setTitle("����");
		dialog.setPositiveButton("�T�{", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		    	Stu_state.this.finish();	
				android.os.Process.killProcess(android.os.Process.myPid());
		    }  
		}); 
		dialog.setNegativeButton("����", new DialogInterface.OnClickListener() {  
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
