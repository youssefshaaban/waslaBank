package com.waslabank.wasslabank.ui.find_ride;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;

import com.android.volley.VolleyError;
import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.adapters.RidesAdapter;
import com.waslabank.wasslabank.models.RideModel;
import com.waslabank.wasslabank.networkUtils.Connector;
import com.waslabank.wasslabank.ui.find_ride.request_ride.RequestRideDetailActivity;
import com.waslabank.wasslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindRideActivity extends AppCompatActivity {

    private final String TAG = FindRideActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rides_recycler)
    RecyclerView mRidesRecycler;

    RidesAdapter mRidesAdapter;
    ArrayList<RideModel> mRides;

    Connector mConnector;
    ProgressDialog mProgressDialog;

    double mLatTo;
    double mLonTo;
    double mLatFrom;
    double mLonFrom;
    String date;
    Integer Seats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbar.setNavigationOnClickListener(v -> finish());
        /////
        Log.d("TTTT", "onCreate: Here");
        ///
        if (getIntent() != null) {
            mLatFrom = getIntent().getDoubleExtra("latFrom", 0);
            mLonTo = getIntent().getDoubleExtra("lonTo", 0);
            mLatTo = getIntent().getDoubleExtra("latTo", 0);
            mLonFrom = getIntent().getDoubleExtra("lonFrom", 0);
            Seats = getIntent().getIntExtra("Seats", 1);
            date = getIntent().getStringExtra("date");

        }

        mRides = new ArrayList<>();

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                mRides.clear();
                if (Connector.checkStatus(response)) {
                    mRides.addAll(Connector.getRequests(response));
                    mRidesAdapter.notifyDataSetChanged();
                } else {
                    mRides.addAll(new ArrayList<>());
                    mRidesAdapter.notifyDataSetChanged();
                    Helper.showSnackBarMessage(getString(R.string.no_results), FindRideActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Log.d("Error_", "onError: " + error.getMessage());
                Helper.showSnackBarMessage(getString(R.string.error), FindRideActivity.this);
            }
        });


        mRidesAdapter = new RidesAdapter(mRides, this, Seats, this, new RidesAdapter.OnItemClicked() {
            @Override
            public void setOnItemClicked(int position) {

            }

            @Override
            public void onRequestRide(RideModel rideModel) {
                startActivityForResult(new Intent(FindRideActivity.this, RequestRideDetailActivity.class)
                        .putExtra("request_id", rideModel.getId()), 1);
            }
        });

        mRidesRecycler.setHasFixedSize(true);
        mRidesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRidesRecycler.setAdapter(mRidesAdapter);

        mProgressDialog = Helper.showProgressDialog(this, getString(R.string.loading), false);
        //mConnector.getRequest(TAG,"https://code-grow.com/waslabank/api/get_requests?lat_from=13123131231&long_from=132311231231&lat_to=13123131231&long_to=132311231231" );
        mConnector.getRequest(TAG, Connector.ConnectionServices.BaseURL + "waslabank/api/get_requests?lat_from=" + mLatFrom + "&long_from=" + mLonFrom
                + "&lat_to=" + mLatTo + "&long_to=" + mLonTo + "&user_id=" + Helper.getUserSharedPreferences(this).getId() + "&type=" + "search&date=" + date);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
           onBackPressed();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
