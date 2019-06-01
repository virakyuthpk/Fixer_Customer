package com.example.fixer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fixer.model.CustomerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Year;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class RegisterActivity extends AppCompatActivity {

    private EditText txt_birth, txt_name, txt_phone, txt_password, txt_confirm_password;
    private Button btn_signup;
    private RadioGroup r_gender;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    String name, phone, birth, gender, password, confirm_password;

    private String url_register_customer = LoginActivity.getRoot() + "/api/customer/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_signup = findViewById(R.id.btn_signup);
        txt_birth = findViewById(R.id.txt_birth);
        txt_name = findViewById(R.id.txt_Name);
        txt_phone = findViewById(R.id.txt_phone);
        r_gender = findViewById(R.id.g_gender);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = txt_name.getText().toString();
                phone = txt_phone.getText().toString();
                birth = txt_birth.getText().toString();
                gender = ((RadioButton)findViewById(r_gender.getCheckedRadioButtonId())).getText().toString();
                password = txt_password.getText().toString();
                confirm_password = txt_confirm_password.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    txt_name.setError("សូមបញ្ចូលឈ្មោះរបស់អ្នក!");
                    txt_name.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    txt_phone.setError("សូមបញ្ចូលលេខទូរស័ព្ទរបស់អ្នក!");
                    txt_phone.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(birth)) {
                    txt_birth.setError("សូមបញ្ចូលថ្ងៃខែឆ្នាំកំណើតរបស់អ្នក!");
                    txt_birth.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    txt_password.setError("សូមបញ្ចូលពាក្យសម្ងាត់របស់អ្នក!");
                    txt_password.requestFocus();
                    return;
                }
                if (!confirm_password.equals(password)) {
                    txt_confirm_password.setError("សូមទោស, ពាក្យសម្ងាត់បញ្ជាក់របស់អ្នកមិនត្រូវគ្នា!");
                    txt_confirm_password.requestFocus();
                    return;
                }

                registerCustomer(url_register_customer);
            }
        });

        txt_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , onDateSetListener, year, month, dayOfMonth);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String Date =  year + "-" + month + "-" + dayOfMonth;
                txt_birth.setText(Date);
            }
        };
    }

    protected void registerCustomer(String url)
    {
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray json = new JSONArray(response);
                            JSONObject auth_user = json.getJSONObject(0);
                            LoginActivity.customerModel = new CustomerModel(
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
                            Toast.makeText(getApplicationContext(), "Sorry this username is already existed!", Toast.LENGTH_LONG).show();
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
                params.put("name", name);
                params.put("dob", birth);
                params.put("phone", phone);
                params.put("password", password);
                params.put("gender", gender);

                return params;
            }
        };
        requestQueue.add(eventoReq);
    }

}
