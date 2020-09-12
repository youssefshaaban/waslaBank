package com.waslabank.wasslabank.ui.ride_details;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.databinding.ActivityRideDetailsBinding;
import com.waslabank.wasslabank.models.ChatModel;
import com.waslabank.wasslabank.models.MyRideModel;
import com.waslabank.wasslabank.models.OfferModel;
import com.waslabank.wasslabank.models.SingleRequestModel.Request;
import com.waslabank.wasslabank.models.SingleRequestModel.User;
import com.waslabank.wasslabank.models.UserModel;
import com.waslabank.wasslabank.networkUtils.Connector;
import com.waslabank.wasslabank.ui.ChatActivity;
import com.waslabank.wasslabank.ui.ConfirmRideRequestActivity;
import com.waslabank.wasslabank.ui.LiveLocationMapsActivity;
import com.waslabank.wasslabank.ui.WhereYouGoActivity;
import com.waslabank.wasslabank.utils.AppUtils;
import com.waslabank.wasslabank.utils.Helper;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class RideDetailsActivity extends AppCompatActivity implements RideDetailView, OffersListAdapter.RequestListner {
    ProgressDialog mProgressDialog;
    ActivityRideDetailsBinding binding;
    RideDetailPresnter rideDetailPresnter;
    String requestId;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_details);
        binding.offersRecycle.setLayoutManager(new LinearLayoutManager(this));
        rideDetailPresnter = new RideDetailPresnter(new WeakReference<>(this));
        userModel = Helper.getUserSharedPreferences(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.ride_details));
        }
        requestId = getIntent().getStringExtra("request_id");
        if (requestId != null)
            rideDetailPresnter.loadDatRequestDat(requestId);
    }

    @Override
    public void successAccept() {
        Toast.makeText(this, "Request Rejected Successfully", Toast.LENGTH_SHORT).show();
        rideDetailPresnter.loadDatRequestDat(requestId);
    }

    @Override
    public void successReject() {
        Toast.makeText(this, "Request Rejected Successfully", Toast.LENGTH_SHORT).show();
        rideDetailPresnter.loadDatRequestDat(requestId);
    }

    @Override
    public void navigateChatModel(ChatModel chatModel, UserModel fromUser, MyRideModel myRideModel) {
        startActivity(new Intent(this, ChatActivity.class).putExtra("chat", chatModel).putExtra("user", fromUser).putExtra("ride_2", myRideModel));
    }

    @Override
    public void successCancel() {
        Toast.makeText(this, "Request cancel Successfully", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void successReview() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Intent intent = new Intent(this, WhereYouGoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else
            super.onBackPressed();
    }

    @Override
    public void showLoading() {
        if (mProgressDialog != null)
            mProgressDialog.show();
        else
            mProgressDialog = Helper.showProgressDialog(this, getString(R.string.loading), false);
    }

    @Override
    public void hideLoading() {
        mProgressDialog.dismiss();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void setRequestData(Request requestData) {
        binding.name.setText(requestData.getUser().getName());
        binding.car.setText(requestData.getUser().getCarName());
        binding.fromPlace.setText(requestData.getAddress());
        binding.toPlace.setText(requestData.getAddressTo());
        binding.date.setText(requestData.getRequestDate());
        binding.time.setText(requestData.getRequestTime());
        binding.seatsNumber.setText(requestData.getSeats());
        binding.distance.setText(String.format(Locale.ENGLISH, "%.2f KM", Float.valueOf(requestData.getDistance())));
        Picasso.get().load(Connector.ConnectionServices.BASE_IMAGE_URL + requestData.getUser()
                .getImage()).fit()
                .error(R.drawable.im_wasla)
                .placeholder(R.drawable.im_wasla)
                .centerCrop().into(binding.profileImage);
        if (requestData.getStatus().equals("4") && requestData.getCustomer_review().equals("0")) {
            show();
        }
        if (requestData.getOffers() != null && requestData.getOffers().size() > 0) {
            binding.contentOffer.setVisibility(View.VISIBLE);
            binding.offersRecycle.setAdapter(new OffersListAdapter(requestData.getOffers(), requestData.getAddress(), requestData.getAddressTo(), RideDetailsActivity.this));
        } else {
            binding.contentOffer.setVisibility(View.GONE);
        }
        if (!requestData.getFromId().equals("0")) {
            binding.cardRequestOfferDetail.setVisibility(View.VISIBLE);
            setUserRequesterData(requestData);
        } else {
            binding.cardRequestOfferDetail.setVisibility(View.GONE);
        }
        if ((requestData.getStatus().equals("1") || requestData.getStatus().equals("2")) && !requestData.getFromId().equals("0")) {   // 1->  tripe active
            binding.actionControl.setVisibility(View.VISIBLE);
            binding.finishTrip.setVisibility(View.GONE);
            binding.cancell.setVisibility(View.VISIBLE);
            handleClicks(requestData);
        } else if (requestData.getStatus().equals("0") && requestData.getUserId().equals(userModel.getId())) {
            binding.cancell.setVisibility(View.VISIBLE);
            binding.cancell.setOnClickListener(v -> rideDetailPresnter.cancelTrip(userModel.getId(), requestData.getId()));
        } else {
            binding.cancell.setVisibility(View.GONE);
            binding.actionControl.setVisibility(View.GONE);
        }
        if (requestData.getCustomer_review().equals("0") && requestData.getStatus().equals("4") && userModel.getId().equals(requestData.getFromId())) {
            show();
        }
    }

    private void setUserRequesterData(Request userRequesterData) {
        binding.seatsNumberOffer.setText(userRequesterData.getUser_seats());
        binding.fromPlaceRequestOffer.setText(userRequesterData.getUser_address());
        binding.toPlace.setText(userRequesterData.getAddressTo());
        binding.nameRequestOffer.setText(userRequesterData.getFrom().getName());
        Picasso.get().load(Connector.ConnectionServices.BASE_IMAGE_URL + userRequesterData.getFrom()
                .getImage()).fit()
                .error(R.drawable.im_wasla)
                .placeholder(R.drawable.im_wasla)
                .centerCrop().into(binding.profileImage);
    }

    private void handleClicks(Request requestData) {
        binding.cancell.setOnClickListener(v -> {
            rideDetailPresnter.cancelTrip(userModel.getId(), requestData.getId());
        });
        binding.call.setOnClickListener(v -> {
            if (requestData.getUserId().equals(Helper.getUserSharedPreferences(this).getId())) {
                AppUtils.openCall(RideDetailsActivity.this, requestData.getFrom().getMobile());
            } else if (requestData.getFromId().equals(Helper.getUserSharedPreferences(this).getId())) {
                AppUtils.openCall(RideDetailsActivity.this, requestData.getUser().getMobile());
            }
        });
        binding.message.setOnClickListener(v -> {
            UserModel user = Helper.getUserSharedPreferences(this);
            if (user.getId().equals(requestData.getUserId())) {
                rideDetailPresnter.startChat(user.getId(), requestData.getFromId(), requestData.getId());
            } else {
                rideDetailPresnter.startChat(user.getId(), requestData.getUserId(), requestData.getId());
            }

        });
        binding.mapLiveLocation.setOnClickListener(v ->
                startActivity(new Intent(this, LiveLocationMapsActivity.class)
                        .putExtra("Request_id", "" + requestId))
        );
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
    public void onAccept(User from, OfferModel offerModel, int position) {
        rideDetailPresnter.acceptOffer(Helper.getUserSharedPreferences(this).getId(), from.getId(), offerModel.getRequestId());
    }

    @Override
    public void onReject(User from, OfferModel offerModel, int position) {
        rideDetailPresnter.reject_offer(Helper.getUserSharedPreferences(this).getId(), from.getId(), offerModel.getRequestId());
    }


    private void show() {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_rating, null);
        dialogBuilder.setView(dialogView);
        final RatingBar rating = dialogView.findViewById(R.id.rating_bar_2);
        final Button rate = dialogView.findViewById(R.id.btn_rate);
        final EditText comment = dialogView.findViewById(R.id.comment);
        rating.setIsIndicator(false);
        AlertDialog alertDialog = dialogBuilder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        alertDialog.show();
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String commentText = comment.getText().toString();
                if (rating.getRating() != 0) {
//                    if (userModel.getId().equals(rideDetailPresnter.request.getFromId())) {
//
//                    } else {
//                        rideDetailPresnter.addReview(comment.getText().toString(), rating.getRating() + "", userModel.getId(), rideDetailPresnter.request.getFromId());
//                    }
                    mProgressDialog=Helper.showProgressDialog(RideDetailsActivity.this,getString(R.string.loading),false);
                    rideDetailPresnter.addReview(comment.getText().toString(), rating.getRating() + "", userModel.getId(), rideDetailPresnter.request.getUserId());
                    alertDialog.dismiss();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.enterRate), RideDetailsActivity.this);
                }
            }
        });


    }

}