package com.example.swvenu.ebay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class resultActivity extends ActionBarActivity {

    ListView list;
    JSONObject result;
    public final static String ITEM_STRING = "com.example.swvenu.ebay.itemString";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            return;
        }

        Intent intent = getIntent();
        String resultString = intent.getStringExtra(MainActivity.RESULT_STRING);

        if(resultString == null){
            return;
        }
        setContentView(R.layout.activity_result);
        ItemAdapter adapter = null;
        String[] test = new String[5];

        try {
            result = new JSONObject(resultString);
            adapter = new ItemAdapter(this, result);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        list=(ListView)findViewById(R.id.resultList);
        list.setAdapter(adapter);
        ((TextView)findViewById(R.id.heading)).setText("Results for \'"+intent.getStringExtra(MainActivity.KEYWORD)+"\'");

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              /*  if(view.getId() == R.id.imageView){
                    onImageClick(view, position);
                }else{
                    onTitleClick(view, position);
                }*/
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onTitleClick(View v, int position){
        Intent intent = new Intent(this, DetailsActivity.class);
        try {
            intent.putExtra(ITEM_STRING, result.getJSONObject("item"+position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    public void onImageClick(View v, int position){

        String url = null;
        try {
            url = result.getJSONObject("item"+position).getJSONObject("basicInfo").getString("viewItemURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse(java.net.URLDecoder.decode(url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
}
