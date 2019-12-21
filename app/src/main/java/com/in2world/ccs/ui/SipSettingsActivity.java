package com.in2world.ccs.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.R;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.tools.GlobalData;

public class SipSettingsActivity extends AppCompatActivity {
    private EditText editUsername, editDomain, editpssword;

    private void initView() {

        editUsername = findViewById(R.id.edit_username);
        editDomain = findViewById(R.id.edit_domain);
        editpssword = findViewById(R.id.edit_apssword);
        init();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sip_settings);

        initView();
    }

    private void init() {
        if (SaveData.getInstance().isKeyExists(GlobalData.KEY_SIP_username)) {
            GlobalData.SIP_username = SaveData.getInstance().getString(GlobalData.KEY_SIP_username);
            editUsername.setText(GlobalData.SIP_username);
        }
        if (SaveData.getInstance().isKeyExists(GlobalData.KEY_SIP_domain)) {
            GlobalData.SIP_domain = SaveData.getInstance().getString(GlobalData.KEY_SIP_domain);
            editDomain.setText(GlobalData.SIP_domain);
        }
        if (SaveData.getInstance().isKeyExists(GlobalData.KEY_SIP_password)) {
            GlobalData.SIP_password = SaveData.getInstance().getString(GlobalData.KEY_SIP_password);
            editpssword.setText(GlobalData.SIP_password);
        }

    }

    public void Save(View view) {

        if (ValidationHelper.validString(editUsername.getText().toString())) {
            GlobalData.SIP_username = editUsername.getText().toString();
            SaveData.getInstance().saveString(GlobalData.KEY_SIP_username, GlobalData.SIP_username);
        } else {
            editUsername.setError(getResources().getString(R.string.enter_username));
            return;
        }

        if (ValidationHelper.validString(editDomain.getText().toString())) {
            GlobalData.SIP_domain = editDomain.getText().toString();
            SaveData.getInstance().saveString(GlobalData.KEY_SIP_domain, GlobalData.SIP_domain);
        } else {
            editDomain.setError(getResources().getString(R.string.enter_domain));
            return;
        }

        if (ValidationHelper.validString(editpssword.getText().toString())) {
            GlobalData.SIP_password = editpssword.getText().toString();
            SaveData.getInstance().saveString(GlobalData.KEY_SIP_password, GlobalData.SIP_password);
        } else {
            editpssword.setError(getResources().getString(R.string.enter_password));
            return;
        }

        finish();

    }

    public void Close(View view) {
        finish();
    }
}
