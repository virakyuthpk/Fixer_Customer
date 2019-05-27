package com.example.fixer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fixer.model.CustomerModel;

import org.json.JSONObject;

import java.util.HashMap;

public class FeedbackFragment extends Fragment {

    EditText editText_feedback;
    Button button_sendfeedback;

    private String url_sendFeedback_customer = LoginActivity.getRoot() + "/api/feedback/insert";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback,container,false);

        editText_feedback = (EditText) v.findViewById(R.id.edit_feedback);
        button_sendfeedback = (Button) v.findViewById(R.id.btn_sendfeedback);

        final CustomerModel auth_user = NavigationDrawer_Activity.getCustomerModel_Nav();

        button_sendfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String feedback_str = editText_feedback.getText().toString();

                if (TextUtils.isEmpty(feedback_str)) {
                    editText_feedback.setError("ប្រសិនបើអ្នកចង់ផ្ញើរមតិត្រលប់មកកាន់យើងខ្ញំុសូមសរសេរអ្វីមួយក្នុងប្រអប់ (ប្រហែលជាយ៉ាងហោចណាស់មួយពាក្យ)។");
                    editText_feedback.requestFocus();
                    return;
                }

                HashMap data = new HashMap();
                data.put("customer_id", auth_user.getId());
                data.put("text_feedback", feedback_str);
                data.put("fixer_id", null);

//                Log.e("customer_id::", auth_user.getId());
//                Log.e("Text Feedback::", feedback_str);

                sendFeedback(url_sendFeedback_customer, data);
            }
        });

        return v;
    }

    private void sendFeedback (String url_sendFeedback_customer, HashMap data) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url_sendFeedback_customer,
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
