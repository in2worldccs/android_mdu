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
import com.in2world.ccs.ui.MainActivity;
import com.in2world.ccs.ui.SipSettingsActivity;

import static com.in2world.ccs.tools.GlobalData.*;


public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    private EditText editName, editPass;

    private String name, password = "";

    SipSettingsActivity sipSettingsActivity;
    MainActivity mainActivity = new MainActivity();
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



        if (!validate())
            return;


        if (!checkMyData()) {
            updatePreferences();
            return;
        }

        if (!checkPermission())
            return;

        RootApplcation.getmRootApplcation().init(this);
        startActivity(new Intent(this, MainActivity.class));
    }

    public void updatePreferences() {
        Toast.makeText(this, "enter your data", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, SipSettingsActivity.class));

    }



    public boolean validate() {

        if (!ValidationHelper.validString(name)) {
            editName.setError(getResources().getString(R.string.enter_name));
            return false;
        }

        if (!ValidationHelper.validString(password)) {
            editPass.setError(getResources().getString(R.string.enter_password));
            return false;
        }

        return true;
    }


    private boolean checkPermission() {

        if (!PermissionHelper.isPermissionGranted(this, PermissionHelper.PERMISSION_RECORD_AUDIO)
                && !PermissionHelper.isPermissionGranted(this, PermissionHelper.PERMISSION_RECORD_AUDIO))
            return true;

        PermissionHelper.checkAllPermission(this, PermissionHelper.PERMISSIONS_LIST_AUDIO, PermissionHelper.CODE_PERMISSION);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.CODE_PERMISSION) {
            if (!PermissionHelper.isPermissionGranted(this, PermissionHelper.PERMISSION_RECORD_AUDIO)
                    && !PermissionHelper.isPermissionGranted(this, PermissionHelper.PERMISSION_USE_SIP))
                startActivity(new Intent(this, MainActivity.class));
            else
                Toast.makeText(this, "Check Permission App", Toast.LENGTH_SHORT).show();
        }
    }

    public void Change(View view) {
        updatePreferences();
    }
}
