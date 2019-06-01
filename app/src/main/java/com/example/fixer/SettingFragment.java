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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fixer.model.CustomerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SettingFragment extends Fragment {
    private EditText name,phone_number,dob;
    private Button btn_edit;
    View inflatedview = null;

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private int update_controler = 0;

    private String url_update_customer = LoginActivity.getRoot() + "/api/customer/update";
    private String name_str = "";
    private String phone_str = "";
    private String dob_str = "";
//    private String address_str = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting,container,false);

        inflatedview = inflater.inflate(R.layout.fragment_setting, container, false);
        name = (EditText) inflatedview.findViewById(R.id.edText_Name);
        phone_number = (EditText) inflatedview.findViewById(R.id.editPhone);
        dob = (EditText) inflatedview.findViewById(R.id.editDob);
//        address = (EditText) inflatedview.findViewById(R.id.edText_address);
        btn_edit = (Button) inflatedview.findViewById(R.id.btn_edit);

        name.setHint(LoginActivity.getCustomerModel().getName());
        phone_number.setHint(LoginActivity.getCustomerModel().getPhone());
        dob.setHint(LoginActivity.getCustomerModel().getDob());
//        address.setHint("សូមបញ្ចូលឤស័យដ្ឋានរបស់អ្នក");

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                update_controler++;

                Log.e("update_controller", String.valueOf(update_controler));

                if (update_controler == 2) {

                    name_str = name.getText().toString();
                    phone_str = phone_number.getText().toString();
                    dob_str = dob.getText().toString();
//                    address_str = address.getText().toString();

                    Log.e("name::", name_str);
                    Log.e("phone::", phone_str);
                    Log.e("dob::", dob_str);
//                    Log.e("address::", address_str);

                    update_controler = 0;
                    btn_edit.setText("ផ្លាស់ប្តូរ");

//                    if (address_str == "" || address_str == null) {
//                        address_str = "null";
//                    }

                    HashMap data = new HashMap();
                    data.put("id", LoginActivity.getCustomerModel().getId());
                    data.put("name",name_str);
                    data.put("dob",dob_str);
                    data.put("phone",phone_str);
//                    data.put("address",address_str);

                    updateCustomer(url_update_customer, data);
                }
                else if (update_controler == 1) {

                    name.setText(LoginActivity.getCustomerModel().getName());
                    phone_number.setText(LoginActivity.getCustomerModel().getPhone());
                    dob.setText(LoginActivity.getCustomerModel().getDob());
//                    if (LoginActivity.getCustomerModel().getAddress() == "null"
//                    || LoginActivity.getCustomerModel().getAddress() == null) {
//                        address.setHint("សូមបញ្ចូលឤស័យដ្ឋានរបស់អ្នក");
//                    }
//                    else {
//                        address.setText(LoginActivity.getCustomerModel().getAddress());
//                    }

                    name.setInputType(InputType.TYPE_CLASS_TEXT);
                    phone_number.setInputType(InputType.TYPE_CLASS_NUMBER);
                    dob.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE );
//                    address.setInputType(InputType.TYPE_CLASS_TEXT);

                    btn_edit.setText("រក្សាទុក");

                    name.setFocusable(true);
                    phone_number.setFocusable(true);
                    dob.setFocusable(false);
//                    address.setFocusable(true);
                }
                else {
                    name.setHint(LoginActivity.getCustomerModel().getName());
                    phone_number.setHint(LoginActivity.getCustomerModel().getPhone());
                    dob.setHint(LoginActivity.getCustomerModel().getDob());
//                    if (LoginActivity.getCustomerModel().getAddress() == "null"
//                            || LoginActivity.getCustomerModel().getAddress() == null) {
//                        address.setHint("សូមបញ្ចូលឤស័យដ្ឋានរបស់អ្នក");
//                    }
//                    else {
//                        address.setText(LoginActivity.getCustomerModel().getAddress());
//                    }

                    name.setFocusable(false);
                    phone_number.setFocusable(false);
                    dob.setFocusable(false);
//                    address.setFocusable(false);
                }
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth
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
                dob.setText(Date);
            }
        };

        return inflatedview;
    }

//    private void updateCustomer(String url_update_customer, HashMap data) {
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//
//        JsonObjectRequest request = new JsonObjectRequest(
//                Request.Method.POST,
//                url_update_customer,
//                new JSONObject(data),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Toast.makeText(getContext(), "Update Successful!", Toast.LENGTH_LONG).show();
//
//                        name.setText("");
//                        name.setHint(name_str);
//                        phone_number.setText("");
//                        phone_number.setHint(phone_str);
//                        dob.setText("");
//                        dob.setHint(dob_str);
//                        password.setText("");
//                        password.setHint("********");
//                        btn_edit.setText("ផ្លាស់ប្តូរ");
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getContext(), "Update Error!", Toast.LENGTH_LONG).show();
//                    }
//                }
//                );
//        requestQueue.add(request);
//    }

    private void updateCustomer (String url_sendFeedback_customer, HashMap data) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url_sendFeedback_customer,
                new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Update Successful!", Toast.LENGTH_LONG).show();

                        LoginActivity.getCustomerModel().setName(name_str);
                        LoginActivity.getCustomerModel().setPhone(phone_str);
                        LoginActivity.getCustomerModel().setDob(dob_str);

//                        if (address_str == "null") {
//                            address.setHint("សូមបញ្ចូលឤស័យដ្ឋានរបស់អ្នក");
//                            LoginActivity.getCustomerModel().setAddress("null");
//                        }
//                        else {
//                            address.setHint(address_str);
//                            LoginActivity.getCustomerModel().setAddress(address_str);
//                        }

                        name.setText("");
                        phone_number.setText("");
                        dob.setText("");
//                        address.setText("");

                        name.setHint(name_str);
                        phone_number.setHint(phone_str);
                        dob.setHint(dob_str);
                  }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error::", error.toString());
                        Toast.makeText(getContext(), "Update Error!", Toast.LENGTH_LONG).show();

                        name.setText("");
                        phone_number.setText("");
                        dob.setText("");
//                        address.setText("");

                        name.setHint(name_str);
                        phone_number.setHint(phone_str);
                        dob.setHint(dob_str);
//                        address.setHint("សូមបញ្ចូលឤស័យដ្ឋានរបស់អ្នក");
                    }
                }
        );
        requestQueue.add(request);
    }
}
