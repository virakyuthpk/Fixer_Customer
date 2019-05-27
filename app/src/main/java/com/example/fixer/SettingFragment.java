package com.example.fixer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fixer.model.CustomerModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

public class SettingFragment extends Fragment {
    private EditText name,phone_number,dob ,password;
    private Button btn_edit;
    View inflatedview = null;

    private int update_controler = 0;

    private String url_update_customer = LoginActivity.getRoot() + "/api/customer/update";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting,container,false);

        inflatedview = inflater.inflate(R.layout.fragment_setting, container, false);
        name = (EditText) inflatedview.findViewById(R.id.edText_Name);
        phone_number = (EditText) inflatedview.findViewById(R.id.editPhone);
        dob = (EditText) inflatedview.findViewById(R.id.editDob);
        password = (EditText) inflatedview.findViewById(R.id.edText_password);
        btn_edit = (Button) inflatedview.findViewById(R.id.btn_edit);

        final CustomerModel auth_user = NavigationDrawer_Activity.getCustomerModel_Nav();

        name.setHint(auth_user.getName());
        phone_number.setHint(auth_user.getPhone());
        dob.setHint(auth_user.getDob());

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setInputType(InputType.TYPE_CLASS_TEXT);
                phone_number.setInputType(InputType.TYPE_CLASS_NUMBER);
                dob.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE );
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btn_edit.setText("រក្សាទុក");
                update_controler++;
                Log.e("update_controller", String.valueOf(update_controler));

                if (update_controler == 2) {

                    String name_str = name.getText().toString();
                    String phone_str = phone_number.getText().toString();
                    String dob_str = dob.getText().toString();
                    String password_str = password.getText().toString();

                    Log.e("name::", name_str);
                    Log.e("phone::", phone_str);
                    Log.e("dob::", dob_str);
                    Log.e("password::", password_str);

                    btn_edit.setText("ផ្លាស់ប្តូរ");
                    update_controler = 0;

                    name.setText("");
                    name.setHint(name_str);
                    phone_number.setText("");
                    phone_number.setHint(phone_str);
                    dob.setText("");
                    dob.setHint(dob_str);
                    password.setText("");
                    password.setHint("********");

                    HashMap data = new HashMap();
                    data.put("id", auth_user.getId());

                    if (name_str == "") {
                        data.put("name", auth_user.getName());
                    }
                    else {
                        data.put("name", name_str);
                    }

                    if (dob_str == "") {
                        data.put("dob", auth_user.getDob());
                    }
                    else {
                        data.put("dob", dob_str);
                    }

                    if (phone_str == "") {
                        data.put("phone", auth_user.getPhone());
                    }
                    else {
                        data.put("phone", phone_str);
                    }

                    if (password_str == "********") {
                        data.put("password", auth_user.getPassword());
                        Log.e("password if::", auth_user.getPassword());
                    }
                    else {
                        data.put("password",password_str);
                        Log.e("password else::", password_str);
                    }

                    updateCustomer(url_update_customer, data);
                }
            }
        });

        return inflatedview;
    }

    private void updateCustomer(String url_update_customer, HashMap data) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url_update_customer,
                new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Update Successful!", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Update Error!", Toast.LENGTH_LONG).show();
                    }
                }
                );
        requestQueue.add(request);
    }
}
