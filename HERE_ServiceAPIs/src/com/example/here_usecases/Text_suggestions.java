package com.example.here_usecases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.here.android.common.GeoCoordinate;
import com.here.android.mapping.FactoryInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.MapFactory;
import com.here.android.search.ErrorCode;
import com.here.android.search.Places;
import com.here.android.search.ResultListener;
import com.here.android.search.places.TextSuggestionRequest;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Text_suggestions extends Activity implements TextWatcher, ResultListener<List<String>> {
	
	SimpleAdapter m_adapter;
	List<java.util.Map<String, String>> m_SuggestionList = null;
	TextSuggestionRequest request = null;
	private Places map_places = null;
	private GeoCoordinate m_sydney = null;
	
	private static final double SYDNEY_lat = -33.87365;
    private static final double SYDNEY_lon = 151.20689;
    
	 @Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.text_suggestions);
	       
	       m_SuggestionList = new ArrayList<java.util.Map<String,String>>();
	       ListView lv = (ListView) findViewById(R.id.listView);
	       m_adapter = new SimpleAdapter(this, m_SuggestionList, android.R.layout.simple_list_item_1, new String[] {"suggestion"}, new int[] {android.R.id.text1});
	       lv.setAdapter(m_adapter);
	       
	       ((TextView)findViewById(R.id.geostring)).addTextChangedListener(this);
	       
	       MapFactory.initFactory(getApplicationContext(), new FactoryInitListener() {

				@Override
				public void onFactoryInitializationCompleted(InitError error) {
					// TODO Auto-generated method stub
					if(error == InitError.NONE){
						map_places = MapFactory.getPlaces();
						m_sydney = MapFactory.createGeoCoordinate(SYDNEY_lat, SYDNEY_lon);
					} else {
		            	Toast.makeText(getApplicationContext(),"MapFactory init error: " + error, Toast.LENGTH_LONG).show();
		           }	
				} 
       	 });
	       
	   }
	 

	@Override
	public void afterTextChanged(Editable arg0) {
		
		if(map_places == null){
			Toast.makeText(getApplicationContext(),"Map places not initialized!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(request != null){
 			request.cancel();
 			request = null;
 		}
		
		 m_SuggestionList = new ArrayList<java.util.Map<String,String>>();
	     ListView lv = (ListView) findViewById(R.id.listView);
	     m_adapter = new SimpleAdapter(this, m_SuggestionList, android.R.layout.simple_list_item_1, new String[] {"suggestion"}, new int[] {android.R.id.text1});
	     lv.setAdapter(m_adapter);
		
		TextView textviewDate=(TextView)findViewById(R.id.geostring);
  		String selectedText =textviewDate.getText().toString();
  		if(selectedText.length() > 0){
  			Toast.makeText(getApplicationContext(),"now requesting", Toast.LENGTH_SHORT).show();
  			request =  	map_places.createTextSuggestionRequest(m_sydney,selectedText);
  			request.execute(this);
  		}
	}

	@Override
	public void onCompleted(List<String> data, ErrorCode error) {
		if(error == ErrorCode.NONE && data != null){		   
			if(data.size() > 0){
				for(int i=0; i < data.size(); i++){
					m_SuggestionList.add(createSuggestion("suggestion", data.get(i)));
				}
			}else{
				Toast.makeText(getApplicationContext(),"No results for Suggestions", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getApplicationContext(),"Suggestion finished with error: " + error, Toast.LENGTH_LONG).show();
		}	
	}
	
	private HashMap<String, String> createSuggestion(String key, String name) {
	 	HashMap<String, String> planet = new HashMap<String, String>();
	 	planet.put(key, name);	 	 
	 	return planet;
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

}
