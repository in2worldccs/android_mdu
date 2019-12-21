package com.in2world.ccs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.helper.PermissionHelper;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.tools.GlobalData;
import com.in2world.ccs.ui.CallActivity;
import com.in2world.ccs.ui.SipSettingsActivity;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    private EditText editName, editPass;
    private String username, domain, password1 = "";

    private String name, password = "";

    private void initView() {

        editName = findViewById(R.id.edit_name);
        editPass = findViewById(R.id.edit_pass);
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

        if (!ValidationHelper.validString(name)) {
            editName.setError(getResources().getString(R.string.enter_name));
            return;
        }

        if (!ValidationHelper.validString(password)) {
            editName.setError(getResources().getString(R.string.enter_password));
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        username = prefs.getString("namePref", "");
        domain = prefs.getString("domainPref", "");
        password1 = prefs.getString("passPref", "");


        if (SaveData.getInstance().isKeyExists(GlobalData.KEY_SIP_username)) {
            GlobalData.SIP_username = SaveData.getInstance().getString(GlobalData.KEY_SIP_username);
        } else {
            updatePreferences();
            return;
        }

        if (SaveData.getInstance().isKeyExists(GlobalData.KEY_SIP_domain)) {
            GlobalData.SIP_domain = SaveData.getInstance().getString(GlobalData.KEY_SIP_domain);
        } else {
            updatePreferences();
            return;
        }

        if (SaveData.getInstance().isKeyExists(GlobalData.KEY_SIP_password)) {
            GlobalData.SIP_password = SaveData.getInstance().getString(GlobalData.KEY_SIP_password);
        } else {
            updatePreferences();
            return;
        }


        if (!checkPermission())
            return;


        startActivity(new Intent(this, CallActivity.class));

    }

    public void updatePreferences() {
        Toast.makeText(this, "enter your data", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, SipSettingsActivity.class));

    }


    private boolean checkPermission(){

        if (!PermissionHelper.isPermissionGranted(this,PermissionHelper.PERMISSION_RECORD_AUDIO)
             && !PermissionHelper.isPermissionGranted(this,PermissionHelper.PERMISSION_RECORD_AUDIO))
            return true;

        PermissionHelper.checkAllPermission(this,PermissionHelper.PERMISSIONS_LIST_AUDIO,PermissionHelper.CODE_PERMISSION);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.CODE_PERMISSION){
            if (!PermissionHelper.isPermissionGranted(this,PermissionHelper.PERMISSION_RECORD_AUDIO)
                    && !PermissionHelper.isPermissionGranted(this,PermissionHelper.PERMISSION_USE_SIP))
                startActivity(new Intent(this, CallActivity.class));
               else
                Toast.makeText(this, "Check Permission App", Toast.LENGTH_SHORT).show();
        }
    }
}
