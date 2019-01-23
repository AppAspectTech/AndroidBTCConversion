package com.appaspect.btcrate.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appaspect.btcrate.R;
import com.appaspect.btcrate.data.Currency_Data;
import com.appaspect.btcrate.data.prefs.SharedPreferenceUtils;
import com.appaspect.btcrate.ui.splash.Splash_Activity;
import com.appaspect.btcrate.ui.start.StartActivity;
import com.appaspect.btcrate.utils.AppConstants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import ir.alirezabdn.wp7progress.WP7ProgressBar;

public class CurrencyListActivity extends AppCompatActivity {

    private long REFRESH_LENGTH=20*1000;
    private Timer timer;
    private TimerTask timerTask;
    private ArrayList<Currency_Data>  currency_list=new ArrayList<Currency_Data>();
    private RecyclerView recyclerView;
    private CurrencyListAdapter currencyListAdapter;
    private List<String> items;
    private TextView lbl_hello_title,txt_user_name,lbl_select_currencies;
    private WP7ProgressBar wp7progressBar;
    private  int counter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (AppConstants.sharedPreferenceUtils == null)
        {
            AppConstants.sharedPreferenceUtils = SharedPreferenceUtils.getInstance(this);
        }

        String str_Selected_Currency=AppConstants.sharedPreferenceUtils.getStringValue(SharedPreferenceUtils.KEY_Selected_Currency,null);
        if(!TextUtils.isEmpty(str_Selected_Currency))
        {
            items = Arrays.asList(str_Selected_Currency.split("\\s*,\\s*"));

            for (int i = 0; i < items.size(); i++)
            {


                String str_code=items.get(i);
                int pos = new ArrayList<String>(Arrays.asList(AppConstants.currency_list)).indexOf(str_code);
                String str_symbol=AppConstants.currency_symbol_list[pos];

                Currency_Data currency_data=new Currency_Data();
                currency_data.setStr_currency_code(str_code);
                currency_data.setStr_currency_symbol(str_symbol);
                currency_data.setStr_diff_percentage("");
                currency_data.setStr_diff_price("");
                currency_data.setStr_price("");

                currency_list.add(currency_data);

            }
        }

        counter=currency_list.size();

        wp7progressBar = (WP7ProgressBar)findViewById(R.id.wp7progressBar);
        lbl_hello_title= (TextView) findViewById(R.id.lbl_hello_title);
                txt_user_name= (TextView) findViewById(R.id.txt_user_name);
        lbl_select_currencies= (TextView) findViewById(R.id.lbl_select_currencies);


        lbl_hello_title.setVisibility(TextView.GONE);
        txt_user_name.setText(getString(R.string.btc_one));
        lbl_select_currencies.setText(getString(R.string.currencies_list));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setVisibility(TextView.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currencyListAdapter = new CurrencyListAdapter(currency_list, this);
        recyclerView.setAdapter(currencyListAdapter);
        currencyListAdapter.updateData(currency_list);
        display_data();


        if (timer != null)
            timer.cancel();

        timerTask = new TimerTask() {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        display_data();
                    }
                });

            }
        };
        timer = new Timer();
        timer.schedule(timerTask, REFRESH_LENGTH,REFRESH_LENGTH);
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart "," onRestart");
        finish();
    }


    public  void display_data()
    {
        counter=currency_list.size();

        if(AppConstants.isConnectionAvailable(this))
        {


            wp7progressBar.setVisibility(WP7ProgressBar.VISIBLE);
            // for showing
            wp7progressBar.showProgressBar();

            for (int i = 0; i < currency_list.size(); i++)
            {
                Currency_Data currency_data=currency_list.get(i);
                call_ws_for_get_data(currency_data.getStr_currency_code());

            }

        }
        else
        {
            wp7progressBar.setVisibility(WP7ProgressBar.GONE);

            Toast.makeText(this,getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();


        }
    }

    class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.NewsViewHolder> {

        private ArrayList<Currency_Data>  currency_list_adpt;
        private Context context;


        public CurrencyListAdapter(ArrayList<Currency_Data>  currency_list, Context context) {

            currency_list_adpt = currency_list;
            this.context = context;

        }

        public void updateData(ArrayList<Currency_Data>  currency_list) {

            currency_list_adpt = currency_list;
            notifyDataSetChanged();

        }

        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {

            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = li.inflate(R.layout.item_currency_list, parent, false);

            return new NewsViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(NewsViewHolder holder1, final int position) {


            try
            {
                holder1.itemView.setTag(""+position);

                Currency_Data currency_data=  currency_list_adpt.get(position);
                String str_price="";

                str_price=currency_data.getStr_diff_price();

                if(TextUtils.isEmpty(str_price))
                {

                }
                else if( Double.parseDouble(currency_data.getStr_diff_price())<0)
                {
                     str_price= currency_data.getStr_currency_symbol()+currency_data.getStr_diff_price()+"("+currency_data.getStr_diff_percentage()+"%)";
                    holder1.txt_diff.setTextColor(this.context.getResources().getColor(R.color.text_red_color));
                }
                else
                {
                     str_price= currency_data.getStr_currency_symbol()+"+"+currency_data.getStr_diff_price()+"(+"+currency_data.getStr_diff_percentage()+"%)";
                    holder1.txt_diff.setTextColor(this.context.getResources().getColor(R.color.text_green_color));
                }


                if(!TextUtils.isEmpty(str_price))
                {
                    holder1.txt_diff.setText(str_price);
                    holder1.txt_price.setText(currency_data.getStr_price());
                }
                else
                {
                    holder1.txt_diff.setText("");
                    holder1.txt_price.setText("");
                }



                holder1.txt_currency_symbol.setText(currency_data.getStr_currency_symbol());
                holder1.txt_currency_code.setText(currency_data.getStr_currency_code());


            }
            catch (Exception e)
            {
                Log.e("onBindViewHolder",""+e.toString());

            }



        }

        @Override
        public int getItemCount() {

            return currency_list_adpt.size();
        }


        class NewsViewHolder extends RecyclerView.ViewHolder {

            private TextView txt_currency_symbol,txt_price,txt_diff,txt_currency_code;
            private View itemView;

            public NewsViewHolder(View itemView) {

                super(itemView);
                this.itemView = itemView;
                txt_currency_symbol = (TextView) itemView.findViewById(R.id.txt_currency_symbol);
                txt_price  = (TextView) itemView.findViewById(R.id.txt_price);
                txt_diff = (TextView) itemView.findViewById(R.id.txt_diff);
                txt_currency_code  = (TextView) itemView.findViewById(R.id.txt_currency_code);

            }
        }
    }

    public void  call_ws_for_get_data(String str_code)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC"+str_code, new JsonHttpResponseHandler() {

            @Override
            public void onStart()
            {
                // called before request is started
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                // called when response HTTP status is "200 OK"



                try {

                    if(response!=null)
                    {
                        Log.e("AsyncHttpClient onSuccess JSONObject ",response.toString());

                        if(response.has("display_symbol"))
                        {
                            String  str_last="",str_percent_hour="",str_price_hour="";
                            String  str_display_symbol=response.getString("display_symbol");

                            String[] str_symbol= str_display_symbol.split("-");

                            String str_symbol_0=str_symbol[0];
                            String str_symbol_1=str_symbol[1];

                            Log.e("str_symbol 0 ",str_symbol_0);
                            Log.e("str_symbol 1 ",str_symbol_1);

                            int pos =  items.indexOf(str_symbol_1);

                            if(response.has("last"))
                            {
                                 str_last=response.getString("last");
                            }

                            if(response.has("changes"))
                            {
                                JSONObject  changes_obj=response.getJSONObject("changes");

                                if(changes_obj.has("percent"))
                                {
                                    JSONObject  percent_obj=changes_obj.getJSONObject("percent");

                                    if(percent_obj.has("hour"))
                                    {
                                          str_percent_hour=percent_obj.getString("hour");
                                    }
                                }

                                if(changes_obj.has("price"))
                                {
                                    JSONObject  price_obj=changes_obj.getJSONObject("price");

                                    if(price_obj.has("hour"))
                                    {
                                          str_price_hour=price_obj.getString("hour");
                                    }
                                }
                            }

                            Currency_Data currency_data=currency_list.get(pos);
                            currency_data.setStr_price(str_last);
                            currency_data.setStr_diff_percentage(str_percent_hour);
                            currency_data.setStr_diff_price(str_price_hour);

                            currency_list.set(pos,currency_data);
                            currencyListAdapter.notifyItemChanged(pos);
                        }
                    }

                }
                catch (JSONException e)
                {

                }
                catch (Exception e)
                {

                }

                counter=counter-1;

                if(counter<=0)
                {
                    recyclerView.setVisibility(TextView.VISIBLE);
                    wp7progressBar.setVisibility(WP7ProgressBar.GONE);
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                // called when response HTTP status is "200 OK"


                Log.e("AsyncHttpClient onSuccess JSONArray ",timeline.toString());
                // Do something with the response

            }
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response)
//            {
//               // called when response HTTP status is "200 OK"
//            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e)
            {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.e("AsyncHttpClient onFailure ",errorResponse);
                counter=counter-1;
                if(counter<=0)
                {

                    wp7progressBar.setVisibility(WP7ProgressBar.GONE);
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Log.e("AsyncHttpClient onRetry ",retryNo+"");
            }
        });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        if (timer != null)
            timer.cancel();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null)
            timer.cancel();

        finish();
    }

}
