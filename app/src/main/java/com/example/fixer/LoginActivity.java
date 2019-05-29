package com.example.fixer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fixer.model.CustomerModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText name_or_phone, password;
    private Button btn_login, btn_register;

    private String index_customer;

    private static String root = "http://192.168.15.1:8000";

    public static String getRoot() {
        return root;
    }

    private String urlloaddata = root + "/api/customer/getall";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name_or_phone = findViewById(R.id.txtNameOrPhone);
        password = findViewById(R.id.txtPassword);

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(name_or_phone.getText().toString())) {
                    name_or_phone.setError("សូមបញ្ចូលឈ្មោះរបស់អ្នក!");
                    name_or_phone.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError("សូមបញ្ចូលពាក្យសម្ងាត់របស់អ្នក!");
                    password.requestFocus();
                    return;
                }

                getData(urlloaddata);
            }
        });
    }

    private void getData(final String urlloaddata) {
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, urlloaddata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e("All Data ::", response.toString());
                new JsonRequestData().execute(urlloaddata);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ::", error.toString());
                Intent intent = new Intent(getApplicationContext(), NavigationDrawer_Activity.class);
                startActivity(intent);
            }
        });
        requestQueue.add(request);
    }

    class JsonRequestData extends AsyncTask<String , Void, String> {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            boolean login_success = false;
            String nameORphone = name_or_phone.getText().toString();
            String passWord = password.getText().toString();

            try {
                JSONArray json = new JSONArray(s);
                Log.d("Json Array :: ", json.toString());
                for(int i = 0; i <json.length(); i++){
                    JSONObject each = json.getJSONObject(i);
                    CustomerModel customerModel = new CustomerModel(
                            each.getString("id"),
                            each.getString("name"),
                            each.getString("dob"),
                            each.getString("phone"),
                            each.getString("password"),
                            each.getString("gender"));
//                    Log.i("Customer :: ", String.valueOf(customerModel));
//                    customerModels.add(customerModel);
                    if (nameORphone.equals(customerModel.getName()) && passWord.equals(customerModel.getPassword()) ||
                            nameORphone.equals(customerModel.getPhone()) && passWord.equals(customerModel.getPassword())) {
                        login_success = true;
                        index_customer = customerModel.getId();
                        break;
                    }
                    else {
//                        Log.i("name from model", customerModel.getName());
//                        Log.i("phone from model", customerModel.getPhone());
//                        Log.i("Edit passWord", passWord);
//                        Log.i("Edit nameORphone", nameORphone);
                        login_success = false;
                    }
//                    Log.e("Login_Success :: ", String.valueOf(login_success));
                }

                if (login_success == true) {
                    NavigationDrawer_Activity.auth_id = index_customer;
                    Intent intent = new Intent(getApplicationContext(),NavigationDrawer_Activity.class);
//                    Log.e("index_customer ::", index_customer);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Sorry your Name or password isn't match!, Please try again!\n or Register a new account!", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Log.e("Json error : ",e.toString());
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                if (con.getResponseCode() == 200) {
                    InputStream is = con.getInputStream();
                    while (true) {
                        int data = is.read();
                        if (data == -1)
                            break;
                        else
                            result += (char) data;
                    }
                }
                con.disconnect();
            } catch (Exception ex) {
                Log.e("Connection Fail : ", ex.toString());
            }
            return result;
        }

    }
}