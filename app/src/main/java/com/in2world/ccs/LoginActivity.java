package com.in2world.ccs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.in2world.ccs.Database.SaveData;
import com.in2world.ccs.helper.MessageHelper;
import com.in2world.ccs.helper.ObjectUtils;
import com.in2world.ccs.helper.PermissionHelper;
import com.in2world.ccs.helper.ValidationHelper;
import com.in2world.ccs.module.Response;
import com.in2world.ccs.server.WebService;
import com.in2world.ccs.service.SIP_Service;
import com.in2world.ccs.socket.SocketIO;
import com.in2world.ccs.tools.GlobalData;
import com.in2world.ccs.ui.CallActivity;
import com.in2world.ccs.ui.DialerActivity;
import com.in2world.ccs.ui.MainActivity;
import com.in2world.ccs.ui.SipSettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import io.socket.emitter.Emitter;

import static com.in2world.ccs.helper.Constants.USER_ID;
import static com.in2world.ccs.server.WebService.RESULT;
import static com.in2world.ccs.server.WebService.StatusConnection.BAD_REQUEST;
import static com.in2world.ccs.server.WebService.StatusConnection.INTERNAL_SERVER_ERROR;
import static com.in2world.ccs.server.WebService.StatusConnection.NO_CONNECTION;
import static com.in2world.ccs.server.WebService.StatusConnection.UNAUTHORIZED;
import static com.in2world.ccs.server.WebService.StatusConnection.VALIDATION_FAILED;
import static com.in2world.ccs.service.SIP_Service.startSIPServices;
import static com.in2world.ccs.tools.GlobalData.*;


public class LoginActivity extends AppCompatActivity  implements  WebService.OnResponding{


    private static final String TAG = "LoginActivity";
    private EditText editName, editPass;
    private Button btnLogin;
    private String name, password = "";
    SipSettingsActivity sipSettingsActivity;
    private void initView() {
        editName = findViewById(R.id.edit_name);
        editPass = findViewById(R.id.edit_pass);
        btnLogin =  findViewById(R.id.btn_login);
        init();
    }

    private static LoginActivity instance = null;

    public static boolean isInstanceCreated() {
        return instance != null;
    }//met

    public static LoginActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance= this;
        initView();
    }

    private void init() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.w(TAG, "init: recent_token "+recent_token);
        if(isToken() && isProfile() && checkMyData()) {
            //  new WebService(WebService.RequestAPI.USERS,null,this);
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogIn();
            }
        });
    }

    public void LogIn() {
        name = editName.getText().toString();
        password = editPass.getText().toString();
        if (!validate())
            return;
        if (!checkPermission())
            return;
        // if (!checkMyData()) {
        //     updatePreferences();
        //     return;
        // }
        HashMap<String, String> params = new HashMap<>();
        params.put("username",name);
        params.put("password",password);
        Toast.makeText(LoginActivity.this, "waiting...", Toast.LENGTH_SHORT).show();
        new WebService(WebService.RequestAPI.LOGIN,params,this);
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
                if (!checkMyData()) {
                    LogIn();
                }
                else
                    Toast.makeText(this, "Check Permission App", Toast.LENGTH_SHORT).show();
        }
    }

    public void Change(View view) {
        updatePreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance= null;
    }

    @Override
    public void onResponding(WebService.RequestAPI requestAPI, boolean IsSuccess, WebService.StatusConnection statusConnection, HashMap<String, Object> objectResult) {
        Log.w(TAG, "onResponding: requestAPI " + requestAPI.toString());
        Log.w(TAG, "onResponding: statusConnection " + statusConnection);
        Log.w(TAG, "onResponding: dataResult " + objectResult.toString());
        try {
            if (requestAPI.equals(WebService.RequestAPI.LOGIN)) {
                if (IsSuccess) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));
                    if (result.getResult() == Response.SUCCESS) {
                        TOKEN_VALUE = result.getDataResponse().getToken();
                        SaveData.getInstance().saveString(TOKEN_KEY, TOKEN_VALUE);
                        mProfile = result.getDataResponse().getUser();

                        Log.d(TAG, "sendIdToSocket: saveDataSIP "+saveDataSIP(mProfile.getPhoneNumberPbx()
                                ,mProfile.getPhoneNumberPbx()
                                ,mProfile.getPhoneNumberPbx()));
                        startSIPServices(this);
                        SocketIO.init();
                        String recent_token = FirebaseInstanceId.getInstance().getToken();
                        if (ValidationHelper.validString(recent_token)) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("fcm_token", recent_token);
                            params.put(USER_ID,""+mProfile.getId());
                            mProfile.setFcmToken(recent_token);
                            new WebService(WebService.RequestAPI.USER_UPDATE, params, this);
                        }else {
                            new WebService(WebService.RequestAPI.USERS,null,this);
                        }
                        SaveData.getInstance().saveObject(PROFILE_KEY, mProfile);
                    }
                } else if (statusConnection == NO_CONNECTION) {
                    Toast.makeText(this, getResources().getString(R.string.NO_CONNECTION), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == BAD_REQUEST) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == UNAUTHORIZED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == VALIDATION_FAILED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == INTERNAL_SERVER_ERROR) {
                    Toast.makeText(this, "حدث خطأ في الخادم ... جاري الأصلاح LOGIN ", Toast.LENGTH_SHORT).show();
                }
            }else if (requestAPI.equals(WebService.RequestAPI.USER_UPDATE)) {
                if (IsSuccess) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));

                    new WebService(WebService.RequestAPI.USERS,null,this);

                } else if (statusConnection == NO_CONNECTION) {
                    Toast.makeText(this, getResources().getString(R.string.NO_CONNECTION), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == BAD_REQUEST) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));

                    new WebService(WebService.RequestAPI.USERS,null,this);

                } else if (statusConnection == UNAUTHORIZED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == VALIDATION_FAILED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == INTERNAL_SERVER_ERROR) {
                    Toast.makeText(this, "حدث خطأ في الخادم ... جاري الأصلاح USER_UPDATE ", Toast.LENGTH_SHORT).show();
                }
            }else if (requestAPI.equals(WebService.RequestAPI.USERS)) {
                if (IsSuccess) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));
                    if (result.getResult() == Response.SUCCESS) {
                        userList = result.getDataResponse().getUserList();
                        if (ValidationHelper.validList(userList)) {
                            Log.e(TAG, "onResponding: size "+userList.size());
                            HashMap<String, String> params = new HashMap<>();
                            params.put(USER_ID,""+mProfile.getId());
                            new WebService(WebService.RequestAPI.GROUPS_USER,params,this);
                        }
                    }
                } else if (statusConnection == NO_CONNECTION) {
                    Toast.makeText(this, getResources().getString(R.string.NO_CONNECTION), Toast.LENGTH_SHORT).show();
                } else if (statusConnection == BAD_REQUEST) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());
                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();
                } else if (statusConnection == UNAUTHORIZED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());
                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();
                } else if (statusConnection == VALIDATION_FAILED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == INTERNAL_SERVER_ERROR) {
                    Toast.makeText(this, "حدث خطأ في الخادم ... جاري الأصلاح USERS ", Toast.LENGTH_SHORT).show();
                }
            }else if (requestAPI.equals(WebService.RequestAPI.GROUPS_USER)) {
                if (IsSuccess) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + ObjectUtils.deserializeObjectToString(result));
                    if (result.getResult() == Response.SUCCESS) {
                        groupList = result.getDataResponse().getGroupUserList();
                        if (ValidationHelper.validList(groupList)) {
                            Log.e(TAG, "onResponding: size "+groupList.size() );
                            sendIdToSocket();
                        }
                    }
                } else if (statusConnection == NO_CONNECTION) {
                    Toast.makeText(this, getResources().getString(R.string.NO_CONNECTION), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == BAD_REQUEST) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == UNAUTHORIZED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());

                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();

                } else if (statusConnection == VALIDATION_FAILED) {
                    Response result = new Gson().fromJson(Objects.requireNonNull(objectResult.get(RESULT)).toString(), new TypeToken<Response>() {
                    }.getType());
                    Log.w(TAG, "onResponding: result " + result.toString());
                    Toast.makeText(this, result.getErrors(), Toast.LENGTH_SHORT).show();
                } else if (statusConnection == INTERNAL_SERVER_ERROR) {
                    Toast.makeText(this, "حدث خطأ في الخادم ... جاري الأصلاح GROUPS ", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onResponding: Exception " + e.getMessage());
            Log.d(TAG, "onResponding: Exception getLocalizedMessage " + e.getLocalizedMessage());
            MessageHelper.AppDialog(this, R.string.error_connection, e.getMessage());
        }
    }

    private void sendIdToSocket() {
        JSONObject dataJSON = new JSONObject();
        try {
            dataJSON.put("senderID",mProfile.getId());
            Log.d(TAG, "LogIn: data "+dataJSON.toString());
            SocketIO.getInstance().getSocket().emit("auth", dataJSON);
            Log.d(TAG, "sendIdToSocket: isToken "+isToken());
            Log.d(TAG, "sendIdToSocket: isProfile "+isProfile());
            Log.d(TAG, "sendIdToSocket: checkMyData "+checkMyData());
            if(isToken() && isProfile() && checkMyData()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class Message{
        String username = "";
        String message = "";

        public Message(String username, String message) {
            this.username = username;
            this.message = message;
        }
    }

    public void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
