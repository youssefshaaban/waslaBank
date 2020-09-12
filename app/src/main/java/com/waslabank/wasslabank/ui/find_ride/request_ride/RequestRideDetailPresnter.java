package com.waslabank.wasslabank.ui.find_ride.request_ride;

import com.google.android.gms.maps.model.LatLng;
import com.waslabank.wasslabank.models.BasicObject;
import com.waslabank.wasslabank.models.SingleRequestModel.GeneralObjectRequest;
import com.waslabank.wasslabank.models.SingleRequestModel.Request;
import com.waslabank.wasslabank.networkUtils.NetworkClient;
import com.waslabank.wasslabank.ui.MyApp;
import com.waslabank.wasslabank.utils.AppUtils;
import com.waslabank.wasslabank.utils.Helper;

import java.io.IOException;
import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.arbitur.geocoding.ReverseGeocoding;

public class RequestRideDetailPresnter {
    WeakReference<RequestRideDetailView> requestRideDetailView;

    Request request;

    public RequestRideDetailPresnter(WeakReference<RequestRideDetailView> requestRideDetailView) {
        this.requestRideDetailView=requestRideDetailView;
    }


    public void loadDatRequestDat(String requestId) {
        requestRideDetailView.get().showLoading();
        NetworkClient.getApiService().getSingleRequest(requestId).enqueue(new Callback<GeneralObjectRequest>() {
            @Override
            public void onResponse(Call<GeneralObjectRequest> call, Response<GeneralObjectRequest> response) {
                GeneralObjectRequest body = response.body();
                if (body.getRequest() != null){
                    request=body.getRequest();
                    requestRideDetailView.get().setRequestData(request);
                }
                requestRideDetailView.get().hideLoading();
            }

            @Override
            public void onFailure(Call<GeneralObjectRequest> call, Throwable t) {
                requestRideDetailView.get().hideLoading();
            }
        });
    }
    public void confirmRequest(String longtitude,String latitude,String seats,String address) {
        requestRideDetailView.get().showLoading();
        NetworkClient.getApiService().send_offer(
                request.getId(),request.getUserId(),longtitude,latitude,address,100+""
                , Helper.getUserSharedPreferences(MyApp.getAppContext()).getId(),seats

        ).enqueue(new Callback<BasicObject>() {
            @Override
            public void onResponse(Call<BasicObject> call, Response<BasicObject> response) {
                BasicObject body = response.body();
                if (body.getStatus()){
                    requestRideDetailView.get().successConfirm();
                }
                requestRideDetailView.get().hideLoading();
            }

            @Override
            public void onFailure(Call<BasicObject> call, Throwable t) {
                requestRideDetailView.get().hideLoading();
            }
        });
    }


}
