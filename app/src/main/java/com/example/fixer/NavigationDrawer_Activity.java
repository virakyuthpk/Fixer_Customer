package com.example.fixer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.RadioGroup;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fixer.model.CustomerModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationDrawer_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int REQUEST_LOCATION = 1;

    TextView tv_username;

    NavigationView navigationView;
    View viewHeader;
    ImageView imgCamera;
    CircleImageView imgProfile;



    Dialog dialog;
    Marker marker;

    LocationManager locationManager;
    LocationListener locationListener;

    private static CustomerModel customerModel_Nav;

    public static CustomerModel getCustomerModel_Nav() {
        return customerModel_Nav;
    }

    private String lattitude, longtitude;
    public static String auth_id = "";
    public static String auth_name = "";

    private String urlloaddata = LoginActivity.getRoot() + "/api/customer/getall";

    ImageButton btn_current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigationdrawer);

        getData(urlloaddata);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewHeader = navigationView.getHeaderView(0);

        imgCamera = (ImageView) viewHeader.findViewById(R.id.img_uploadprofile);
        imgProfile =  viewHeader.findViewById(R.id.profile_image);

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toUploadProfilePhoto();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        tv_username = headerView.findViewById(R.id.tv_username);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dialog = new Dialog(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        btn_current = findViewById(R.id.btn_current);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //get the location name from latitude and longitude
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude, 1);
                    String result = addresses.get(0).getLocality() + ":";
                    result += addresses.get(0).getCountryName();
                    LatLng latLng = new LatLng(latitude, longitude);
                    if (marker != null) {
                        marker.remove();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        marker.setVisible(false);
                    } else {
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        marker.setVisible(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    public void ShowRequestPopUp(View v) {

        TextView textClose;
        RadioGroup r_vehicle;
        EditText txt_problem, txt_phone;
        Button btn_request;

        btn_request = dialog.findViewById(R.id.btn_request);
        txt_problem = dialog.findViewById(R.id.edtext_problem);
        txt_phone = dialog.findViewById(R.id.edText_phoneNumber);
        r_vehicle = dialog.findViewById(R.id.g_vehicle);

        dialog.setContentView(R.layout.custom_popup_request_form);
        textClose = dialog.findViewById(R.id.txtclose);

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        dialog.show();
    }

    public void ShowThankyouPopUp(View v) {

        TextView textClose;
        Button btn_close;
        dialog.setContentView(R.layout.custom_pupup_thankyou);
        textClose = dialog.findViewById(R.id.txtclose2);
        btn_close = dialog.findViewById(R.id.btn_close_thankyou_popup);

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.item_homepage) {
            Intent intent = new Intent(this, NavigationDrawer_Activity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.item_setting) {
            SettingFragment settingFragment = new SettingFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.homepage, settingFragment)
                    .commit();
        } else if (id == R.id.item_callCenter) {
            String number = "085734339";
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+number));
            startActivity(callIntent);

        } else if (id == R.id.item_feedback) {
            FeedbackFragment feedbackFragment = new FeedbackFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.homepage, feedbackFragment)
                    .commit();
        } else if (id == R.id.item_helps) {
            HelpFragment helpFragment = new HelpFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.homepage, helpFragment)
                    .commit();
        } else if (id == R.id.item_logout) {

            auth_id = "";
            auth_name = "";

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    public void CheckCurrent_Locat(View v) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            getLocation();
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longtitude = String.valueOf(longi);
                LatLng latLng = new LatLng(latti, longi);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longtitude = String.valueOf(longi);
                LatLng latLng = new LatLng(latti, longi);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longtitude = String.valueOf(longi);
                LatLng latLng = new LatLng(latti, longi);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            } else {

                Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }



    class JsonRequestData extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONArray json = new JSONArray(s);
//                Log.d("Json Array :: ", json.toString());
                for (int i = 0; i < json.length(); i++) {
                    JSONObject each = json.getJSONObject(i);
                    CustomerModel customerModel = new CustomerModel(
                            each.getString("id"),
                            each.getString("name"),
                            each.getString("dob"),
                            each.getString("phone"),
                            each.getString("password"),
                            each.getString("gender"));

                    Log.e("auth_id::", auth_id);
//                    Log.e("LoginActivity.getRoot::", LoginActivity.getRoot());
//                    Log.e("auth_name::", auth_name);
//                     || auth_name.equals(customerModel.getName())
//                    Log.e("customer_id", customerModel.getId());
                    if (auth_id.equals(customerModel.getId()) || auth_name.equals(customerModel.getName())) {
                        Log.e("customer_id", customerModel.getId());
                        customerModel_Nav = customerModel;

                        tv_username.setText(customerModel_Nav.getName());
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("Json error : ", e.toString());
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
            }
        });
        requestQueue.add(request);
    }

    public void toUploadProfilePhoto(){
        Intent intent = new Intent(NavigationDrawer_Activity.this, ChangeProfilePhotoActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==5) {
//            Intent intent = getIntent();
//            byte[] byteArray = getIntent().getByteArrayExtra("image");
//            Bitmap bitmapProfile = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Bitmap bitmapProfile = (Bitmap) data.getParcelableExtra("image");
//            Log.e("Sam",);
            imgProfile.setImageBitmap(bitmapProfile);
        }

    }
}
