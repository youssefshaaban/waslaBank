package com.waslabank.wasslabank.ui.find_ride.request_ride;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import se.arbitur.geocoding.ReverseGeocoding;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.databinding.ActivityRequestRideDetailBinding;
import com.waslabank.wasslabank.models.SingleRequestModel.Request;
import com.waslabank.wasslabank.models.UserModel;
import com.waslabank.wasslabank.networkUtils.Connector;
import com.waslabank.wasslabank.ui.MapActivity;
import com.waslabank.wasslabank.utils.AppUtils;
import com.waslabank.wasslabank.utils.Helper;
import com.waslabank.wasslabank.utils.LocationHelper;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

// startActivityForResult(new Intent(WhereYouGoActivity.this, MapActivity.class), 2)
public class RequestRideDetailActivity extends AppCompatActivity implements RequestRideDetailView, LocationHelper.LocationListner {
    ProgressDialog mProgressDialog;
    RequestRideDetailPresnter requestRideDetailPresnter;
    ActivityRequestRideDetailBinding binding;
    UserModel mUserModel;
    LocationHelper locationHelper;
    Double mLonfrom, mLatfrom;
    String mAddressFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_ride_detail);
        requestRideDetailPresnter = new RequestRideDetailPresnter(new WeakReference(this));
        mUserModel = Helper.getUserSharedPreferences(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.confirm_ride_request));
        }
        locationHelper = new LocationHelper(this, this);
        locationHelper.init();
        String request_id = getIntent().getStringExtra("request_id");
        if (request_id != null)
            requestRideDetailPresnter.loadDatRequestDat(request_id);
    }

    @Override
    public void showLoading() {
        mProgressDialog = Helper.showProgressDialog(this, getString(R.string.loading), false);
    }

    @Override
    public void hideLoading() {
        mProgressDialog.dismiss();
    }

    @Override
    public void setRequestData(Request requestData) {
        binding.name.setText(requestData.getUser().getName());
        binding.car.setText(requestData.getUser().getCarName());
        binding.fromPlace.setText(requestData.getAddress());
        binding.toPlace.setText(requestData.getAddressTo());
        binding.date.setText(requestData.getRequestDate());
        binding.time.setText(requestData.getRequestTime());
        binding.distance.setText(String.format(Locale.ENGLISH, "%.2f KM", Float.valueOf(requestData.getDistance())));
        String seats = requestData.getSeats();
        if (seats!=null&&Integer.parseInt(seats)>0)
            binding.numberButton.setRange(1, Integer.parseInt(seats));
        else
            binding.numberButton.setRange(1, 4);
        binding.picked.setOnClickListener(v ->
                startActivityForResult(new Intent(this, MapActivity.class), 2));
        if (requestData.getSeats() != null && requestData.getSeats().equals("0"))
            binding.numberButton.setNumber(String.valueOf(1));
        else
            binding.numberButton.setNumber(String.valueOf(requestData.getSeats()));
        Picasso.get().load(Connector.ConnectionServices.BASE_IMAGE_URL + requestData.getUser()
                .getImage()).fit()
                .error(R.drawable.im_wasla)
                .placeholder(R.drawable.im_wasla)
                .centerCrop().into(binding.profileImage);
        binding.confirmButton.setOnClickListener(v -> {
            if (mLatfrom != null && mLatfrom != 0) {
                requestRideDetailPresnter.confirmRequest(mLonfrom + "", mLatfrom + "", binding.numberButton.getNumber(), mAddressFrom);
            } else {
                Toast.makeText(this, getString(R.string.picked_location_from), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && Activity.RESULT_OK == resultCode) {
            mAddressFrom = data.getStringExtra("address");
            mLatfrom = data.getDoubleExtra("lat", 0);
            mLonfrom = data.getDoubleExtra("lon", 0);
            binding.picked.setText(mAddressFrom);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showNetworkError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void shoMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void successConfirm() {
        setResult(Activity.RESULT_OK);
        onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }


    @Override
    public void getLocation(Location location) {
        if (location != null) {
            setAddress(location);
            locationHelper.stopLocationUpdate(this);
        } else {
            locationHelper.init();
        }

    }

    @Override
    public void sendRequsestPermission() {

    }

    public void setAddress(Location location) {
        new ReverseGeocoding(location.getLatitude(), location.getLongitude(), AppUtils.GOOGLE_KEY)
                .setLanguage("en")
                .fetch(new se.arbitur.geocoding.Callback() {
                    @Override
                    public void onResponse(se.arbitur.geocoding.Response response) {
                        mAddressFrom = response.getResults()[0].getFormattedAddress();
                        binding.picked.setText(mAddressFrom);
                    }

                    @Override
                    public void onFailed(se.arbitur.geocoding.Response response, IOException e) {

                    }
                });
    }
}