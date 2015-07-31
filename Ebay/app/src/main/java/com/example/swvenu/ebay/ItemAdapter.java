package com.example.swvenu.ebay;

/**
 * Created by swvenu on 20-04-2015.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class ItemAdapter extends ArrayAdapter<String> {

    private Activity context;
    private JSONObject jsonResult;


    public ItemAdapter(Activity context, JSONObject jsonResult) {
        super(context, R.layout.result_view, new String[5]);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.jsonResult = jsonResult;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.result_view, null,true);

        TextView title = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView price = (TextView) rowView.findViewById(R.id.price);
        try {
            JSONObject item = jsonResult.getJSONObject("item"+position);
            JSONObject basicInfo = item.getJSONObject("basicInfo");
            JSONObject shippingInfo = item.getJSONObject("shippingInfo");

            title.setText(java.net.URLDecoder.decode(basicInfo.getString("title")));
            new DownloadImageTask(imageView).execute(java.net.URLDecoder.decode(basicInfo.getString("galleryURL")));
            String shipType = "";
            if(shippingInfo.getString("shippingType").matches("Free")){
                shipType = "(FREE Shipping)";
            }else if(basicInfo.has("shippingServiceCost") && !basicInfo.getString("shippingServiceCost").matches("0.0")  && !basicInfo.getString("shippingServiceCost").matches("")){
                shipType = "( $"+ basicInfo.getString("shippingServiceCost")+" for shipping)";
            }
            price.setText("Price: $"+basicInfo.getString("convertedCurrentPrice")+shipType);
            imageView.setTag(Integer.valueOf(position));
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Integer rowPosition = (Integer)v.getTag();
                    resultActivity r = (resultActivity) v.getContext();
                    r.onImageClick(v, rowPosition);

                }
            });
            price.setTag(Integer.valueOf(position));
            price.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Integer rowPosition = (Integer)v.getTag();
                    resultActivity r = (resultActivity) v.getContext();
                    r.onTitleClick(v, rowPosition);

                }
            });
            title.setTag(Integer.valueOf(position));
            title.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Integer rowPosition = (Integer)v.getTag();
                    resultActivity r = (resultActivity) v.getContext();
                    r.onTitleClick(v, rowPosition);

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rowView;


    };
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }

}


