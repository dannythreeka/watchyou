package com.example.a55123;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.a55123.support.FixDataProvider;
import com.example.a55123.support.NewListDataSQL;
import com.example.a55123.support.NewPersonalDataSQL;
import com.example.a55123.*;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ScheduleContent extends Activity implements OnClickListener{
	
	private EditText edittime;
	private EditText edittitle;
	private EditText editnote;
	private TextView datetext1;
	private TextView datetext2;
    private Button pOk, pdelete;
    private int pYear, pMonth, pDay;
    private int pHour, pMinute;
    private int checksum;
    private Bundle bundle;
    private Spinner mSpinnerA, mSpinnerB, mSpinnerC;
    private int stype, sstar, sring;
    private String _ID;
    private String[] data_in_base;
    
    SQLiteDatabase db;
    public String db_name = "MainPageSQL";
	public String table_name = "newtable";
	NewListDataSQL helper = new NewListDataSQL(ScheduleContent.this, db_name);
	
	SQLiteDatabase db_personal;
	public String db_name_personal = "PersonalSQL";
	public String table_name_personal = "personaldata";
	NewPersonalDataSQL personaldata_helper = new NewPersonalDataSQL(ScheduleContent.this, db_name_personal);
	
    private FixDataProvider FDP = new FixDataProvider();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_content);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		db = helper.getWritableDatabase();
		db_personal = personaldata_helper.getReadableDatabase();
		data_in_base = myData();
		
		 bundle = getIntent().getExtras();
		 ArrayList<Integer> box;
		 box = bundle.getIntegerArrayList("dateinfo");
		 pYear=box.get(0);
		 pMonth=box.get(1);
		 pDay=box.get(2);
		 // checksum 0=new one, 1=reverse
		 checksum=bundle.getInt("CheckSum");
		// ------set title --------------------------------------------------------------------------------
		edittitle = (EditText)findViewById(R.id.scedit1); 
		//--------------------------------------------------------------------------------------------------
		
		//--------------set remind time----------------------------------------------------------
		edittime = (EditText)findViewById(R.id.scedittime1);
		edittime.setOnClickListener(this);
		//----------------------------------------------------------------------------------------
		
		//-----set note----------------------------------------------------------------------------------------------
		editnote = (EditText)findViewById(R.id.MultieditText1);
		//-------------------------------------------------------------------------------------------------------------
		
		//---------set current date-------------------------------------------------------------------------------------
		datetext1 = (TextView)findViewById(R.id.scedit2);
		datetext1.setText(new StringBuilder().append(pYear).append(" / ").
      	      append(pMonth).append(" / ").append(pDay));
		datetext1.setOnClickListener(this);
		//--------------------------------------------------------------------------------------------------------------
		
		//-----set remind date------------------------------------------------------------------------------------------
		datetext2 = (TextView)findViewById(R.id.scedit3);
		datetext2.setText(new StringBuilder().append(pYear).append(" / ").
      	      append(pMonth).append(" / ").append(pDay));
		datetext2.setOnClickListener(this);
		//-------------------------------------------------------------------------------------------------------------
		
		//--------Spinner ---------------------------------------------------------------------//
		mSpinnerA = (Spinner)findViewById(R.id.spinner1);
		this.loadDataForSpinnerA();
		mSpinnerA.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView parent, View v, int position, long id) {
                    // parent = parent view
                    // position = Selected index = parent.getSelectedItemPosition()
                    // id = row id
            	stype = mSpinnerA.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView parent) {}
		});
		mSpinnerB = (Spinner)findViewById(R.id.spinner2);
		this.loadDataForSpinnerB();
		mSpinnerB.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView parent, View v, int position, long id) {
            	sstar = mSpinnerB.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView parent) {}
		});
		mSpinnerC = (Spinner)findViewById(R.id.spinner3);
		this.loadDataForSpinnerC();
		mSpinnerC.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView parent, View v, int position, long id) {
            	sring = mSpinnerC.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView parent) {}
		});
		//-----------------------------------------------------------------------------//
		//check if there is review one
		
		pOk = (Button)findViewById(R.id.scbutton);	
		pOk.setOnClickListener(this);
		
		
		
		if(checksum == 1){
			ArrayList<Integer> box2 = bundle.getIntegerArrayList("dateinfo");
			 pYear=box2.get(0);
			 pMonth=box2.get(1);
			 pDay=box2.get(2);
			String[] str = bundle.getStringArray("Item_Detail");
			edittitle.setText(str[0]);
			datetext1.setText(new StringBuilder().append(pYear).append(" / ").
      	      append(pMonth).append(" / ").append(pDay));
			datetext2.setText(str[1]);
			edittime.setText(str[2]);
			editnote.setText(str[3]);
			mSpinnerA.setSelection( Integer.parseInt(str[4]));
			mSpinnerB.setSelection( Integer.parseInt(str[5]));
			mSpinnerC.setSelection( Integer.parseInt(str[6]));
			_ID=str[7];
			pdelete = (Button)findViewById(R.id.delete_button);
			pdelete.setOnClickListener(this);
			pdelete.setVisibility(View.VISIBLE);
			Toast.makeText(ScheduleContent.this,"Review Mode", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(ScheduleContent.this,DDatePickerActivity.class);
		switch(v.getId()){
			case R.id.scedittime1:
				showTimePickerDialog(); 
				break;
				
			case R.id.scedit2:
				showDatePickerDialog();
				break;
				
			case R.id.scedit3:
				showDatePickerDialogremind();
				break;
				
			case R.id.scbutton:
				if(!TextUtils.isEmpty(edittitle.getText().toString())){
				 	String stitle = edittitle.getText().toString();
	            	String sreminddate = datetext2.getText().toString();
	            	String sremindtime = edittime.getText().toString();
	            	String snote = editnote.getText().toString();
	            	/*int stype = (int) mSpinnerA.getSelectedItemPosition();
	            	int sstar = (int) mSpinnerB.getSelectedItemPosition();
	            	int sring = (int) mSpinnerC.getSelectedItemPosition();*/
	            	
	            	//寫進SQLite
	        	   ContentValues cv = new ContentValues();
	        	   cv.put("title", stitle);
	        	   cv.put("year", pYear);
	        	   cv.put("month", pMonth);
	        	   cv.put("day", pDay);
	        	   cv.put("reminddate", sreminddate);
	        	   cv.put("remindtime", sremindtime);
	        	   cv.put("note",snote);
	        	   cv.put("type",stype);
	        	   cv.put("star",sstar);
	        	   cv.put("ring",sring);
	        	   
		    	   if(checksum == 0){
		    		   cv.put("submit", "false");
		    		   cv.put("accept", "false");
		    		   cv.put("schedule_ID_web", "null");
		        	   long long1 = db.insert(table_name, "", cv);
		        	   
		        	   if (long1 == -1) {
		        		   Toast.makeText(ScheduleContent.this,"fail！", Toast.LENGTH_SHORT).show();
		        	   }
		        	   else{    
		        		   Toast.makeText(ScheduleContent.this,"Success!", Toast.LENGTH_SHORT).show();
		        		   post(); //post to Webserver
		        	   }
		        	   
		        	   
		        	}
		        	else{
		        		
		        		long long1 = db.update(table_name, cv, "_ID=" + _ID, null);
		        		
		    		   if (long1 == -1) {
		        		   Toast.makeText(ScheduleContent.this,"Change Fail！", Toast.LENGTH_SHORT).show();
		        		   //ScheduleContent.this.finish(); 
		        	   }
		        	   else{    
		        		   Toast.makeText(ScheduleContent.this,"Change Success!", Toast.LENGTH_SHORT).show();
		        		   //ScheduleContent.this.finish(); 
		        	   }
		        	}
		    	    
	        	    setResult(Activity.RESULT_OK,intent);
	        	    finish();
				}
				else{
					Toast.makeText(ScheduleContent.this,"Title is empty", Toast.LENGTH_SHORT).show();
				}
	    	   	break;
    	   
			case R.id.delete_button:
				long long1 = db.delete(table_name, "_ID=" + _ID, null);
				if (long1 == -1) {
	        		   Toast.makeText(ScheduleContent.this,"Delete Fail！", Toast.LENGTH_SHORT).show();
	        		   //ScheduleContent.this.finish(); 
				}
				else{    
	        		   Toast.makeText(ScheduleContent.this,"Delete Success!", Toast.LENGTH_SHORT).show();
	        		   //ScheduleContent.this.finish(); 
				}
				 //Intent i_delete = new Intent(ScheduleContent.this,DDatePickerActivity.class);
				 setResult(Activity.RESULT_OK,intent);
				 finish();
				break;
		}
		
	}  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule_content, menu);
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
	
	private void loadDataForSpinnerA() {
		List<String> spinnerList = FDP.GetTypeList();
		ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
		this.mSpinnerA.setAdapter(myAdapter);
	}
	private void loadDataForSpinnerB() {
		List<String> spinnerList = FDP.GetStarList();
		ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
		this.mSpinnerB.setAdapter(myAdapter);
	}
	private void loadDataForSpinnerC() {
		List<String> spinnerList = FDP.GetRingList();
		ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
		this.mSpinnerC.setAdapter(myAdapter);
	}
	
	public String[] myTitle(String s){
		Cursor cursor = db.rawQuery(s, null);
		String[] sNote = new String[cursor.getCount()];
		  
		int rows_num = cursor.getCount();
		if(rows_num != 0) {
			  cursor.moveToFirst();   
			  for(int i=0; i<rows_num; i++){
				  String strCr = cursor.getString(0);
				  sNote[i]=strCr;
				  cursor.moveToNext();
			  }
		 }
		 cursor.close(); 
		 //dbHelper.close();
		 return sNote;
	}
    
    public String[] myTime(String s){
		Cursor cursor = db.rawQuery(s, null);
		String[] sNote = new String[cursor.getCount()];
		  
		int rows_num = cursor.getCount();
		if(rows_num != 0) {
			  cursor.moveToFirst(); 
			  for(int i=0; i<rows_num; i++){
				  String strCr = cursor.getString(0);
				  if(strCr != null){
					  sNote[i]=strCr;
				  }
				  else{
					  sNote[i]="nnn";
				  }
				  cursor.moveToNext();
			  }
		 }
		 cursor.close();
		 return sNote;
	}
    public void setlistview(String title, String time){
    	String[] str = myTitle(title);
    	String[] str2 = myTime(time);
    	String[] final_string = new String[str.length];
    	for(int i=0 ; i<str.length ; i++){
    		if(str2[i]=="nnn"){
    			final_string[i]=str[i];
    		}
    		else{
    			final_string[i]=str2[i]+" "+str[i];
    		}
    	}
    	ListView temp = (ListView)findViewById(R.id.listview);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(new DDatePickerActivity(), 
        		android.R.layout.simple_expandable_list_item_1, final_string);
        temp.setAdapter(listAdapter);
    }
    
    public void showDatePickerDialog() {  
    	
  	  DatePickerDialog dpd = new DatePickerDialog(this,  
  	    new DatePickerDialog.OnDateSetListener() { 
  		  @Override
  	     public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
  			  pYear=year;
  			  pMonth=monthOfYear;
  			  pDay=dayOfMonth;
  			  datetext1.setText(new StringBuilder().append(pYear).append(" / ").
  		      	      append(pMonth).append(" / ").append(pDay));
  	     }  
  	    }, pYear, pMonth, pDay); 
  	  dpd.show();
	 } 
    
    public void showDatePickerDialogremind() {  
    	
    	  DatePickerDialog dpd = new DatePickerDialog(this,  
    	    new DatePickerDialog.OnDateSetListener() { 
    		  @Override
    	     public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
    			 int Year=year;
    			 int Month=monthOfYear;
    			 int Day=dayOfMonth;
    			  datetext2.setText(new StringBuilder().append(Year).append(" / ").
    		      	      append(Month).append(" / ").append(Day));
    	     }  
    	    }, pYear, pMonth, pDay); 
    	  dpd.show();
	 }  
    	  
	 public void showTimePickerDialog() {  

	  TimePickerDialog tpd = new TimePickerDialog(this,  
	    new TimePickerDialog.OnTimeSetListener() {
		  @Override
	     public void onTimeSet(TimePicker view, int hourOfDay,  int minute) {  
	    	 pHour= hourOfDay;
	    	 pMinute=minute;
	    	 if(pHour <= 9){
	    		 if(pMinute < 10){
	    			 edittime.setText("0"+ pHour + ":" + "0" + pMinute);
	    		 }
	    		 else{
	    			 edittime.setText("0"+ pHour + ":" + pMinute);
	    		 }
	    	 }
	    	 else{
	    		 if(pMinute < 10){
	    			 edittime.setText(pHour + ":" + "0" + pMinute);
	    		 }
	    		 else{
	    			 edittime.setText(pHour + ":" + pMinute);
	    		 }
	    	 }
	     }  
	    }, pHour, pMinute, false);  
	  tpd.show();  
	 }
	//------------------------post new schedule to WebServer --------------------------------------------------------------
	 private void post() {  /// post to webserver
	    	
	    	StringBuilder sb = new StringBuilder();

			String http = "http://watchyou.herokuapp.com/schedules";
			HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(http);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setUseCaches(false);
				urlConnection.setConnectTimeout(10000);
				urlConnection.setReadTimeout(10000);
				urlConnection.setRequestProperty("Content-Type", "application/json");

				urlConnection.connect();

				// Create JSONObject here
				OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
				JSONObject jsonParam = formJSON();
				out.write(jsonParam.toString());
				out.close();

				int HttpResult = urlConnection.getResponseCode();
				if (HttpResult == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();
				} 
				else {
					System.out.println(urlConnection.getResponseMessage());
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (urlConnection != null)
					urlConnection.disconnect();
			}
	    }
	    
	    public JSONObject formJSON(){
	    	Map<String, JSONObject> params = new HashMap<String, JSONObject>();
			Map<String, String> params_nested = new HashMap<String, String>();
			params_nested.put("userID", data_in_base[4]);
			params_nested.put("title", edittitle.getText().toString());
			params_nested.put("year", Integer.toString(pYear));
			params_nested.put("month", Integer.toString(pMonth));
			params_nested.put("day", Integer.toString(pDay));
			params_nested.put("remind_date", datetext2.getText().toString());
			params_nested.put("remind_time", edittime.getText().toString());
			params_nested.put("note", editnote.getText().toString());
			params_nested.put("category", Integer.toString(stype));// this one is type
			params_nested.put("star", Integer.toString(sstar));
			params_nested.put("ring", Integer.toString(sring));
			 // 1 = true (submit already), 0 = false (haven't submit)
			params_nested.put("submit", "false");
			params_nested.put("accept", "false");
			params_nested.put("image", null);
			JSONObject json_nested = new JSONObject(params_nested);
			params.put("schedule", json_nested);
			JSONObject json_f = new JSONObject(params);
			
			return json_f;
	    }
	    //--------------------------------------------------------------------------------------
	    //------------------Delete Schedule at Webserver------------------------------------------------------------------
	    public void delete_schedule(String UserID, String id) throws JSONException{
			StringBuilder sb = new StringBuilder();
			String http = "http://localhost:3000/schedules/delete"+ "/"+ UserID+ "/"+ id;
			HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(http);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("DELETE");
				urlConnection.setUseCaches(false);
				urlConnection.setConnectTimeout(10000);
				urlConnection.setReadTimeout(10000);
				urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				urlConnection.connect();
				// Create JSONObject here
				int HttpResult = urlConnection.getResponseCode();
				if (HttpResult == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							urlConnection.getInputStream(), "utf-8"));
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();
					System.out.println("" + sb.toString());
				} else {
					System.out.println(urlConnection.getResponseMessage());
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (urlConnection != null)
					urlConnection.disconnect();
			}
		}
	    //------------------------------------------------------------------------------------------------------
	    //------------------------------------------get personal data from database -------------------------------
		public String[] myData(){
	    	String keeplogin = "select _ID, name, email, password, webserverID from personaldata ";
			Cursor cursor = db_personal.rawQuery(keeplogin, null);
			String[] sNote = new String[5];
			  
			int rows_num = cursor.getCount();
			if(rows_num != 0) {
				  cursor.moveToFirst();   
				  for(int i=0; i<5; i++){
					  String strCr = cursor.getString(i);
					  sNote[i]=strCr;  
				  }
				  cursor.moveToNext();
			 }
			 cursor.close();
			 Log.e("str0",sNote[0]);
			 Log.e("str1",sNote[1]);
			 Log.e("str2",sNote[2]);
			 Log.e("str3",sNote[3]);
			 Log.e("str4",sNote[4]);
			 return sNote;
		}
		 //------------------------------------------------------------------------------------------------------

	
}
