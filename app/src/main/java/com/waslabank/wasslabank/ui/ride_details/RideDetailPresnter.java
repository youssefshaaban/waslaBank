package com.waslabank.wasslabank.ui.ride_details;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.waslabank.wasslabank.models.BasicObject;
import com.waslabank.wasslabank.models.ChatModel;
import com.waslabank.wasslabank.models.MyRideModel;
import com.waslabank.wasslabank.models.SingleRequestModel.GeneralObjectRequest;
import com.waslabank.wasslabank.models.SingleRequestModel.Request;
import com.waslabank.wasslabank.models.UserModel;
import com.waslabank.wasslabank.networkUtils.Connector;
import com.waslabank.wasslabank.networkUtils.NetworkClient;
import com.waslabank.wasslabank.ui.ConfirmRideRequestActivity;
import com.waslabank.wasslabank.ui.MyApp;
import com.waslabank.wasslabank.ui.find_ride.request_ride.RequestRideDetailView;
import com.waslabank.wasslabank.utils.Helper;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideDetailPresnter {
    WeakReference<RideDetailView> requestRideDetailView;

    Request request;

    public RideDetailPresnter(WeakReference<RideDetailView> requestRideDetailView) {
        this.requestRideDetailView = requestRideDetailView;
    }


    public void loadDatRequestDat(String requestId) {
        requestRideDetailView.get().showLoading();
        NetworkClient.getApiService().getSingleRequest(requestId).enqueue(new Callback<GeneralObjectRequest>() {
            @Override
            public void onResponse(Call<GeneralObjectRequest> call, Response<GeneralObjectRequest> response) {
                GeneralObjectRequest body = response.body();
                if (body.getRequest() != null) {
                    request = body.getRequest();
                    requestRideDetailView.get().setRequestData(request);
                }
                requestRideDetailView.get().hideLoading();
            }

            @Override
            public void onFailure(Call<GeneralObjectRequest> call, Throwable t) {
                requestRideDetailView.get().hideLoading();
                requestRideDetailView.get().showNetworkError(t);
            }
        });
    }

    public void reject_offer(String userId, String fromId, String Id) {
        requestRideDetailView.get().showLoading();
        NetworkClient.getApiService().reject_offer(userId, fromId, Id).enqueue(new Callback<GeneralObjectRequest>() {
            @Override
            public void onResponse(Call<GeneralObjectRequest> call, Response<GeneralObjectRequest> response) {
                Log.d("TTTT", "onResponse: raw" + response.raw());
                Log.d("TTTT", "onResponse: raw" + response.toString());
                requestRideDetailView.get().hideLoading();

                if (response.raw().code() == 500) {
                    requestRideDetailView.get().shoMessage("Internal server");
                } else if (response.code() == 200) {
                    GeneralObjectRequest generalObjectRequest = response.body();
                    if (generalObjectRequest.getStatus())
                        requestRideDetailView.get().successReject();
                }
            }

            @Override
            public void onFailure(Call<GeneralObjectRequest> call, Throwable t) {
                requestRideDetailView.get().hideLoading();
                requestRideDetailView.get().showNetworkError(t);

            }
        });
    }


    public void cancelTrip(String userId, String Id) {
        requestRideDetailView.get().showLoading();
        NetworkClient.getApiService().cancelTrip(userId, Id).enqueue(new Callback<GeneralObjectRequest>() {
            @Override
            public void onResponse(Call<GeneralObjectRequest> call, Response<GeneralObjectRequest> response) {
                Log.d("TTTT", "onResponse: raw" + response.raw());
                Log.d("TTTT", "onResponse: raw" + response.toString());
                requestRideDetailView.get().hideLoading();
                if (response.raw().code() == 500) {
                    requestRideDetailView.get().shoMessage("Internal server");
                } else if (response.code() == 200) {
                    GeneralObjectRequest generalObjectRequest = response.body();
                    if (generalObjectRequest.getStatus()) {
                        requestRideDetailView.get().successCancel();
                    }

                }
            }

            @Override
            public void onFailure(Call<GeneralObjectRequest> call, Throwable t) {
                requestRideDetailView.get().hideLoading();
                requestRideDetailView.get().showNetworkError(t);

            }
        });
    }


    public void acceptOffer(String userId, String fromId, String Id) {
        requestRideDetailView.get().showLoading();
        NetworkClient.getApiService().accept_offer(userId, fromId, Id).enqueue(new Callback<GeneralObjectRequest>() {
            @Override
            public void onResponse(Call<GeneralObjectRequest> call, Response<GeneralObjectRequest> response) {
                Log.d("TTTT", "onResponse: raw" + response.raw());
                Log.d("TTTT", "onResponse: raw" + response.toString());
                requestRideDetailView.get().hideLoading();
                if (response.raw().code() == 500) {
                    requestRideDetailView.get().shoMessage("Internal server");
                } else if (response.code() == 200) {
                    GeneralObjectRequest generalObjectRequest = response.body();
                    if (generalObjectRequest.getStatus())
                        requestRideDetailView.get().successAccept();
                }
//                if (generalObjectRequest.getStatus()) {
//                    Toast.makeText(ConfirmRideRequestActivity.this, "Request Rejected Successfully", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
            }

            @Override
            public void onFailure(Call<GeneralObjectRequest> call, Throwable t) {
                requestRideDetailView.get().hideLoading();
                requestRideDetailView.get().showNetworkError(t);

            }
        });
    }

    public void startChat(String userId, String to_id, String Id) {
        requestRideDetailView.get().showLoading();
        NetworkClient.getApiService().start_chat(userId, to_id, "", "request", Id).enqueue(new Callback<ChatModel>() {
            @Override
            public void onResponse(Call<ChatModel> call, Response<ChatModel> response) {
                Log.d("TTTT", "onResponse: raw" + response.raw());
                Log.d("TTTT", "onResponse: raw" + response.toString());
                requestRideDetailView.get().hideLoading();
                if (response.raw().code() == 500) {
                    requestRideDetailView.get().shoMessage("Internal server");
                } else if (response.code() == 200 && response.body() != null) {
                    ChatModel mChatModel;
                    UserModel mUserModel = Helper.getUserSharedPreferences(MyApp.getAppContext());
                    if (mUserModel.getId().equals(request.getUserId()))
                        mChatModel = Connector.getChatModelWithChatId(response.body().getChatId(), request.getFrom().getName(), request.getFromId(), mUserModel.getId());
                    else
                        mChatModel = Connector.getChatModelWithChatId(response.body().getChatId(), request.getUser().getName(), request.getUserId(), mUserModel.getId());
                    requestRideDetailView.get().navigateChatModel(mChatModel, request.getFrom(), MyRideModel.returnRidleModel(request));
                }
            }

            @Override
            public void onFailure(Call<ChatModel> call, Throwable t) {
                requestRideDetailView.get().hideLoading();
                requestRideDetailView.get().showNetworkError(t);
            }
        });
    }

    public void addReview(String comment, String rate, String fromId, String userId) {

        requestRideDetailView.get().showLoading();
        NetworkClient.getApiService().addComment(comment, rate, request.getId(), userId, fromId)
                .enqueue(new Callback<BasicObject>() {
                    @Override
                    public void onResponse(Call<BasicObject> call, Response<BasicObject> response) {
                        requestRideDetailView.get().hideLoading();
                        if (response.body().getStatus()) {
                            requestRideDetailView.get().successReview();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicObject> call, Throwable t) {
                        requestRideDetailView.get().hideLoading();
                        requestRideDetailView.get().shoMessage(t.getMessage());
                    }
                });
    }
}
