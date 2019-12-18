package com.ibrahem.ccs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ibrahem.ccs.helper.ValidationHelper;
import com.ibrahem.ccs.ui.CallActivity;
import com.ibrahem.ccs.ui.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editName,editPass;

    private String name,password = "";

    private void initView(){

        editName =  findViewById(R.id.edit_name);
        editPass =  findViewById(R.id.edit_pass);
        init();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void init() {


    }

    public void LogIn(View view) {

        name = editName.getText().toString();
        password = editPass.getText().toString();

        if (!ValidationHelper.validString(name)){
            editName.setError(getResources().getString(R.string.enter_name));
            return;
        }

        if (!ValidationHelper.validString(password)){
            editName.setError(getResources().getString(R.string.enter_password));
            return;
        }

        startActivity(new Intent(this, MainActivity.class));



    }
}
