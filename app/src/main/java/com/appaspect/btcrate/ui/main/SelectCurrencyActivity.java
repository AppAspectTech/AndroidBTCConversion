package com.appaspect.btcrate.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appaspect.btcrate.R;
import com.appaspect.btcrate.data.prefs.SharedPreferenceUtils;
import com.appaspect.btcrate.utils.AppConstants;

public class SelectCurrencyActivity extends AppCompatActivity implements  View.OnClickListener{

    private boolean[]  array_selected;
    private RecyclerView recyclerView;
    private CurrencyListAdapter customAdapter;
    private int counter=0;
    private Button btn_next;
    private TextView txt_user_name;
    private String str_User_Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);

        if (AppConstants.sharedPreferenceUtils == null)
        {
            AppConstants.sharedPreferenceUtils = SharedPreferenceUtils.getInstance(this);
        }

        str_User_Name= AppConstants.sharedPreferenceUtils.getStringValue(SharedPreferenceUtils.KEY_User_Name,null);

        txt_user_name=(TextView)findViewById(R.id.txt_user_name);

        if(!TextUtils.isEmpty(str_User_Name))
        {
            txt_user_name.setText(str_User_Name+"!");
        }

        btn_next=(Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView


    }

    @Override
    protected void onResume()
    {
        super.onResume();

        counter=0;
        array_selected=new boolean[AppConstants.currency_list.length];
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        customAdapter = new CurrencyListAdapter(SelectCurrencyActivity.this, AppConstants.currency_list);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_next)
        {


            String str_currency="";
            if(counter<=0)
            {
                Toast.makeText(SelectCurrencyActivity.this, getString(R.string.please_select_the_currency_minimum),Toast.LENGTH_SHORT).show();
            }
            else
            {

                for (int i = 0; i < AppConstants.currency_list.length; i++)
                {

                    if(array_selected[i])
                    {
                        if(TextUtils.isEmpty(str_currency))
                        {
                            str_currency=AppConstants.currency_list[i];
                        }
                        else
                        {
                            str_currency=str_currency+","+AppConstants.currency_list[i];
                        }

                    }
                }

                if(!TextUtils.isEmpty(str_currency))
                {
                    AppConstants.sharedPreferenceUtils.setValue(SharedPreferenceUtils.KEY_Selected_Currency,str_currency);
                    Intent mainIntent = new Intent(SelectCurrencyActivity.this, CurrencyListActivity.class);
                    startActivity(mainIntent);
                    //finish();
                }


            }

        }
    }

    class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder> {
        private String[] currency_list_adpt;
        private Context context;

        public CurrencyListAdapter(Context context, String[] currency_list) {
            this.context = context;
            this.currency_list_adpt = currency_list;

        }

        @Override
        public CurrencyListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
           // View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_country, viewGroup, false);

            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = li.inflate(R.layout.item_select_country, viewGroup, false);


            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CurrencyListAdapter.ViewHolder viewHolder, int position)
        {
            viewHolder.itemView.setTag(""+position);
            viewHolder.txt_currency_code.setText(currency_list_adpt[position]);
            boolean selected_value=array_selected[position];

            if(selected_value)
            {
                viewHolder.ll_currency_code.setBackgroundResource(R.drawable.currency_selected);
                viewHolder.txt_currency_code.setTextColor(this.context.getResources().getColor(R.color.text_white_color));
            }
            else
            {
                viewHolder.ll_currency_code.setBackgroundResource(R.drawable.currency_unselected);
                viewHolder.txt_currency_code.setTextColor(this.context.getResources().getColor(R.color.text_black_color));
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {

                        String str_tag=v.getTag().toString();
                        int position=Integer.parseInt(str_tag);

                        boolean selected_value=array_selected[position];

                        if(selected_value)
                        {
                            array_selected[position]=false;
                            counter=counter-1;
                        }
                        else
                        {
                            if(counter>=4)
                            {
                                Toast.makeText(SelectCurrencyActivity.this, getString(R.string.please_select_the_currency_maximum),Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                array_selected[position]=true;
                                counter=counter+1;
                            }


                        }

                      //  notifyDataSetChanged();
                        customAdapter.notifyItemChanged(position);

                    } catch (Exception e)
                    {
                        // TODO: handle exception

                    }

                    // More_URLS
                }
            });

        }

        @Override
        public int getItemCount() {
            return currency_list_adpt.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView txt_currency_code;
            private LinearLayout ll_currency_code;
            private  View itemView;

            public ViewHolder(View view) {
                super(view);
                this.itemView = view;
                txt_currency_code = (TextView)view.findViewById(R.id.txt_currency_code);
                ll_currency_code = (LinearLayout) view.findViewById(R.id.ll_currency_code);
            }
        }

    }

    
}
