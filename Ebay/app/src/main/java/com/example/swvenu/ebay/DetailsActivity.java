package com.example.swvenu.ebay;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;


public class DetailsActivity extends ActionBarActivity {
    JSONObject item;
    JSONObject basicInfo;
    JSONObject shippingInfo;
    JSONObject sellerInfo;
    String imageURL;
    String priceString;
    String location;
    String title;
    String viewItemURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent intent = getIntent();
            item = new JSONObject(intent.getStringExtra(resultActivity.ITEM_STRING));
            basicInfo = item.getJSONObject("basicInfo");
            shippingInfo = item.getJSONObject("shippingInfo");
            sellerInfo = item.getJSONObject("sellerInfo");

            setContentView(R.layout.activity_details);

            ImageView imageView = (ImageView) findViewById(R.id.bigPicture);
            viewItemURL = basicInfo.getString("viewItemURL");
            imageURL = basicInfo.getString("galleryURL");
            if(!basicInfo.getString("pictureURLSuperSize").matches(""))
                imageURL = basicInfo.getString("pictureURLSuperSize");
				
            new DownloadImageTask(imageView).execute(java.net.URLDecoder.decode(imageURL));
			
            title = java.net.URLDecoder.decode(basicInfo.getString("title"));
            ((TextView)findViewById(R.id.detailsTitle)).setText(title);
            String shipType = null;
            if(shippingInfo.getString("shippingType").matches("Free")){
                shipType = "(FREE Shipping)";
            }else if(basicInfo.has("shippingServiceCost") && !basicInfo.getString("shippingServiceCost").matches("0.0")  && !basicInfo.getString("shippingServiceCost").matches("")){
                shipType = "( $"+ basicInfo.getString("shippingServiceCost")+" for shipping)";
            }
			
            priceString = "Price: $" + basicInfo.getString("convertedCurrentPrice") + shipType;
            ((TextView)findViewById(R.id.detailsPrice)).setText(priceString);
            location = basicInfo.getString("location");
            ((TextView)findViewById(R.id.location)).setText(location);
            if(!basicInfo.getString("topRatedListing").matches("true")){
                ((ImageView) findViewById(R.id.topRated)).setVisibility(View.INVISIBLE);
            }
			
            ((TextView)findViewById(R.id.categoryname)).setText(basicInfo.getString("categoryName"));
            String condition = "N/A";
            if(!basicInfo.getString("conditionDisplayName").matches("")){
                condition = basicInfo.getString("conditionDisplayName");
            }
			
            ((TextView)findViewById(R.id.condition)).setText(condition);
            String buyFormat = "";
            if(basicInfo.getString("listingType").matches("FixedPrice") || basicInfo.getString("listingType").matches("StoreInventory")){
                buyFormat = "Buy It Now";
            }else if(basicInfo.getString("listingType").matches("Auction")){
                buyFormat =  "Auction";
            }else if(basicInfo.getString("listingType").matches("Classified")){
                buyFormat =  "Classified Ad";
            }
			
            ((TextView)findViewById(R.id.buyingformat)).setText(buyFormat);
            ((TextView)findViewById(R.id.username)).setText(sellerInfo.getString("sellerUserName"));
            ((TextView)findViewById(R.id.feedbackscore)).setText(sellerInfo.getString("feedbackScore"));
            ((TextView)findViewById(R.id.positivefeedback)).setText(sellerInfo.getString("positiveFeedbackPercent"));
            ((TextView)findViewById(R.id.feedbackrating)).setText(sellerInfo.getString("feedbackRatingStar"));
            ((TextView)findViewById(R.id.store)).setText(sellerInfo.getString("sellerStoreURL"));
            ((TextView)findViewById(R.id.shippingtype)).setText(shippingInfo.getString("shippingType"));
            ((TextView)findViewById(R.id.shippinglocation)).setText(shippingInfo.getString("shipToLocations"));
            ((TextView)findViewById(R.id.handlingtime)).setText(shippingInfo.getString("handlingTime"));
            ImageView topRated = ((ImageView) findViewById(R.id.toprateddetail));
            if(basicInfo.getString("topRatedListing").matches("true")) {
                topRated.setImageResource(R.drawable.ic_action_accept);
                topRated.setColorFilter(R.color.light_green);
            }else{
                topRated.setImageResource(R.drawable.ic_action_remove);
                topRated.setColorFilter(R.color.light_red);
            }
            ImageView expedited = ((ImageView) findViewById(R.id.expeditedshipping));
            if(shippingInfo.getString("expeditedShipping").matches("true")) {
                expedited.setImageResource(R.drawable.ic_action_accept);
            }else{
                expedited.setImageResource(R.drawable.ic_action_remove);
            }
            ImageView oneday = ((ImageView) findViewById(R.id.onedayshipping));
            if(shippingInfo.getString("oneDayShippingAvailable").matches("true")) {
                oneday.setImageResource(R.drawable.ic_action_accept);
            }else{
                oneday.setImageResource(R.drawable.ic_action_remove);
            }
            ImageView returnAccepted = ((ImageView) findViewById(R.id.returnsaccepted));
            if(shippingInfo.getString("returnsAccepted").matches("true")) {
                returnAccepted.setImageResource(R.drawable.ic_action_accept);
            }else{
                returnAccepted.setImageResource(R.drawable.ic_action_remove);
            }



        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void basicInfoClick(View v){
        ((ToggleButton)findViewById(R.id.BasicInfoButton)).setChecked(true);
        ((ToggleButton)findViewById(R.id.SellerInfoButton)).setChecked(false);
        ((ToggleButton)findViewById(R.id.ShippingInfoButton)).setChecked(false);
        ((LinearLayout)findViewById(R.id.tab1)).setVisibility(LinearLayout.VISIBLE);
        ((LinearLayout)findViewById(R.id.tab2)).setVisibility(LinearLayout.GONE);
        ((LinearLayout)findViewById(R.id.tab3)).setVisibility(LinearLayout.GONE);
    }

    public void sellerInfoClick(View v){
        ((ToggleButton)findViewById(R.id.BasicInfoButton)).setChecked(false);
        ((ToggleButton)findViewById(R.id.SellerInfoButton)).setChecked(true);
        ((ToggleButton)findViewById(R.id.ShippingInfoButton)).setChecked(false);
        ((LinearLayout)findViewById(R.id.tab1)).setVisibility(LinearLayout.GONE);
        ((LinearLayout)findViewById(R.id.tab2)).setVisibility(LinearLayout.VISIBLE);
        ((LinearLayout)findViewById(R.id.tab3)).setVisibility(LinearLayout.GONE);
    }

    public void shippingInfoClick(View v){
        ((ToggleButton)findViewById(R.id.BasicInfoButton)).setChecked(false);
        ((ToggleButton)findViewById(R.id.SellerInfoButton)).setChecked(false);
        ((ToggleButton)findViewById(R.id.ShippingInfoButton)).setChecked(true);
        ((LinearLayout)findViewById(R.id.tab1)).setVisibility(LinearLayout.GONE);
        ((LinearLayout)findViewById(R.id.tab2)).setVisibility(LinearLayout.GONE);
        ((LinearLayout)findViewById(R.id.tab3)).setVisibility(LinearLayout.VISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_details, menu);
        android.support.v7.app.ActionBar actionBar =  getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
        }
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

    public void onBuyClick(View v){

        Uri uri = Uri.parse(java.net.URLDecoder.decode(viewItemURL));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public void onFbClick(View v){
        FacebookSdk.sdkInitialize(getApplicationContext());
        CallbackManager callbackManager;
        ShareDialog shareDialog;
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        ShareLinkContent content = null;
        try {
            content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(java.net.URLDecoder.decode(basicInfo.getString("viewItemURL"))))
                            .setContentTitle(title)
                            .setContentDescription(priceString + location)
                            .setImageUrl(Uri.parse(java.net.URLDecoder.decode(imageURL)))
                            .build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Context context = getApplicationContext();
                CharSequence text = "success";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onCancel() {
                Context context = getApplicationContext();
                CharSequence text = "cancelled";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onError(FacebookException e) {
                Context context = getApplicationContext();
                CharSequence text = "Error";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        if (shareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(content);
        }

    }
}
