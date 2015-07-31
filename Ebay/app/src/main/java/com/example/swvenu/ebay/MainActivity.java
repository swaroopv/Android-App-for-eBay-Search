package com.example.swvenu.ebay;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;



public class MainActivity extends ActionBarActivity {
    public final static String RESULT_STRING = "com.example.swvenu.ebay.resultString";
    public final static String KEYWORD = "com.example.swvenu.ebay.keyword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Spinner spinner = (Spinner) findViewById(R.id.sort);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sortEntries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void search(View view) {
        // validation
        EditText keyword = (EditText) findViewById(R.id.keyword);
        TextView error = (TextView) findViewById(R.id.error);
        //Clear fields
        error.setText("");
        ((TextView) findViewById(R.id.result)).setText("");

        String search = keyword.getText().toString();
        if (search.replaceAll("\\s+", "").length() == 0) {
            error.setText(R.string.errorKeyword);
            return;
        }
        search = java.net.URLEncoder.encode(search);
        EditText fromEdit = (EditText) findViewById(R.id.from);
        EditText toEdit = (EditText) findViewById(R.id.to);

        float from = -1;
        float to = -1;

        if(!fromEdit.getText().toString().matches("")){
            from = Float.parseFloat(fromEdit.getText().toString());
        }
        if(!toEdit.getText().toString().matches("")){
            to = Float.parseFloat(toEdit.getText().toString());
        }

        if (from >= 0 && to >=0 && to < from) {
            error.setText(R.string.errorRange);
            return;
        }
        Spinner sort = (Spinner) findViewById(R.id.sort);
        int sortSelection = sort.getSelectedItemPosition();
        String sortBy = "";
        switch (sortSelection) {
            case 0:
                sortBy = "BestMatch";
                break;
            case 1:
                sortBy = "CurrentPriceHighest";
                break;
            case 2:
                sortBy = "PricePlusShippingHighest";
                break;
            case 3:
                sortBy = "PricePlusShippingLowest";
                break;
        }
        String fromValue = (from >= 0) ? from+"" : "";
        String toValue = (to >= 0) ? to+"" : "";

        myTask ebayQuery = new myTask(this);
        ebayQuery.execute("http://swvenu-env.elasticbeanstalk.com/?Keywords=" + search + "&minPrice="+ fromValue +"&maxPrice="+ toValue +"&sortOrder="+sortBy+"&entriesPerPage=5&pageNumber=1");
		//Replace above query with index.php

    }

    public void clear(View view) {

        ((EditText) findViewById(R.id.keyword)).setText("");
        ((TextView) findViewById(R.id.error)).setText("");
        ((EditText) findViewById(R.id.from)).setText("");
        ((EditText) findViewById(R.id.to)).setText("");
    }

    public void onSearchComplete(JSONObject result, String resultString) {
        try {
            String resultAck = result.getString("ack");
            TextView textResult = ((TextView) findViewById(R.id.result));
            if(resultAck.matches("Failure")) {
                textResult.setText(R.string.failure);
                return;
            }else if(resultAck.matches("No results found")){
                textResult.setText(resultAck);
                return;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, resultActivity.class);
        intent.putExtra(RESULT_STRING, resultString);
        intent.putExtra(KEYWORD, ((EditText) findViewById(R.id.keyword)).getText().toString());
        startActivity(intent);
    }
}


class myTask extends AsyncTask<String, Void, String> {

    public MainActivity activity;

    public myTask(MainActivity a) {
        this.activity = a;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            //TODO Handle problems..
        } catch (IOException e) {
            e.printStackTrace();
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            activity.onSearchComplete(new JSONObject(result), result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
