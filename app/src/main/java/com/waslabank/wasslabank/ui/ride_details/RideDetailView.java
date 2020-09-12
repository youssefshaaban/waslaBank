package com.waslabank.wasslabank.ui.ride_details;

import com.waslabank.wasslabank.models.ChatModel;
import com.waslabank.wasslabank.models.MyRideModel;
import com.waslabank.wasslabank.models.UserModel;
import com.waslabank.wasslabank.ui.base_ride.BaseRideView;

public interface RideDetailView extends BaseRideView {
    void successAccept();
    void successReject();
    void navigateChatModel(ChatModel chatModel, UserModel fromUser, MyRideModel myRideModel);
    void successCancel();
    void successReview();
}
