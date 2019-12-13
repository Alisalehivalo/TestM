package ir.airport.testmetar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String uri;
    EditText search;
    Button metar,tafor;
    ListView listView;
    static final String KEY_METAR="METAR";
    static final String KEY_TAF="TAF";
    static final String KEY_Raw="raw_text";
    static final String KEY_Station="station_id";
    private ProgressDialog pDialog;
    ArrayList<HashMap<String,String>> menuItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search=findViewById(R.id.Search);
        listView=findViewById(R.id.listView);
        metar=findViewById(R.id.metar);
        tafor=findViewById(R.id.tafor);

        metar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_metar=search.getText().toString();
                uri="https://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&stationString="+search_metar+"&hoursBeforeNow=1";
Log.d("URL",uri);
                menuItems=new ArrayList<>();
                new GetItems().execute();

            }
        });
        tafor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_tafor=search.getText().toString();
                uri="https://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=tafs&requestType=retrieve&format=xml&stationString="+search_tafor+"&hoursBeforeNow=5&timeType=issue&mostRecent=true";
                menuItems=new ArrayList<>();
                new GetItems1().execute();

            }
        });


    }
    private class GetItems extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog=new ProgressDialog(MainActivity.this);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            XMLparser parser=new XMLparser();
            String xml=parser.getXmlFromUrl(uri);
            Log.i("xml",xml);
            Document doc=parser.getDomElement(xml);

            NodeList nl=doc.getElementsByTagName(KEY_METAR);
            for (int i=0;i<nl.getLength();i++){
                HashMap<String,String>map=new HashMap<>();
                Element e=(Element) nl.item(i);
                map.put(KEY_Station,parser.getValue(e,KEY_Station));
                map.put(KEY_Raw,parser.getValue(e,KEY_Raw)+"=");
                menuItems.add(map);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
            ListAdapter adapter=new SimpleAdapter(MainActivity.this,menuItems,R.layout.list_item,new String[]{KEY_Station,KEY_Raw},new int[]{R.id.station,R.id.raw});
            listView.setAdapter(adapter);
        }
    }
    private class GetItems1 extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog=new ProgressDialog(MainActivity.this);
            pDialog.setMessage("loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            XMLparser parser=new XMLparser();
            String xml=parser.getXmlFromUrl(uri);
            Log.i("xml",xml);
            Document doc=parser.getDomElement(xml);

            NodeList nl=doc.getElementsByTagName(KEY_TAF);
            for (int i=0;i<nl.getLength();i++){
                HashMap<String,String>map=new HashMap<>();
                Element e=(Element) nl.item(i);
                map.put(KEY_Station,parser.getValue(e,KEY_Station));
                map.put(KEY_Raw,parser.getValue(e,KEY_Raw)+"=");
                menuItems.add(map);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
            ListAdapter adapter=new SimpleAdapter(MainActivity.this,menuItems,R.layout.list_item,new String[]{KEY_Station,KEY_Raw},new int[]{R.id.station,R.id.raw});
            listView.setAdapter(adapter);
        }
    }
}
