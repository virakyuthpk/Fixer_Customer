package com.example.fixer;

import android.content.Intent;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fixer.model.CustomerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText name_or_phone, password;
    private Button btn_login, btn_register;
    static CustomerModel customerModel;

    public static CustomerModel getCustomerModel() {
        return customerModel;
    }

    String nameOrphone_str = null;
    String password_str=null;
    private static String root = "http://172.23.15.75:8000";

    public static String getRoot() {
        return root;
    }

    private String urlsendlogin = root + "/api/customer/login";

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

                nameOrphone_str = name_or_phone.getText().toString();
                password_str = password.getText().toString();

                if (TextUtils.isEmpty(nameOrphone_str)) {
                    name_or_phone.setError("សូមបញ្ចូលឈ្មោះរបស់អ្នក!");
                    name_or_phone.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password_str)) {
                    password.setError("សូមបញ្ចូលពាក្យសម្ងាត់របស់អ្នក!");
                    password.requestFocus();
                    return;
                }
                loginCustomer(urlsendlogin);
            }
        });
    }

    protected void loginCustomer(String url)
    {
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            JSONObject auth_user = json.getJSONObject(0);
                            customerModel = new CustomerModel(
                                    auth_user.getString("id"),
                                    auth_user.getString("name"),
                                    auth_user.getString("dob"),
                                    auth_user.getString("phone"),
                                    auth_user.getString("password"),
                                    auth_user.getString("gender"));

                            Intent intent = new Intent(getApplicationContext(), NavigationDrawer_Activity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error::", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nameOrphone", nameOrphone_str);
                params.put("password", password_str);

                return params;
            }
        };
        requestQueue.add(eventoReq);
    }
}