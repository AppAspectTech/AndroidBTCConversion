package com.appaspect.btcrate.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appaspect.btcrate.R;
import com.appaspect.btcrate.data.prefs.SharedPreferenceUtils;
import com.appaspect.btcrate.ui.main.SelectCurrencyActivity;
import com.appaspect.btcrate.utils.AppConstants;

public class StartActivity extends AppCompatActivity implements  View.OnClickListener{

    private TextInputLayout input_layout_user_name;
    private EditText input_user_name;
    private Button btn_start;
    private String str_User_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (AppConstants.sharedPreferenceUtils == null)
        {
            AppConstants.sharedPreferenceUtils = SharedPreferenceUtils.getInstance(this);
        }

         input_layout_user_name=(TextInputLayout)findViewById(R.id.input_layout_user_name);
         input_user_name=(EditText)findViewById(R.id.input_user_name);
         btn_start=(Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_start)
        {
            str_User_Name=input_user_name.getText().toString().trim();

            if(TextUtils.isEmpty(str_User_Name))
            {
                Toast.makeText(StartActivity.this, getString(R.string.please_enter_your_name),Toast.LENGTH_SHORT).show();
            }
            else
            {

                 AppConstants.sharedPreferenceUtils.setValue(SharedPreferenceUtils.KEY_User_Name,str_User_Name);
                Intent mainIntent = new Intent(StartActivity.this, SelectCurrencyActivity.class);
                startActivity(mainIntent);
                finish();
            }

        }
    }
}
