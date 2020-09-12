package com.waslabank.wasslabank.ui.base_ride;

import com.waslabank.wasslabank.models.SingleRequestModel.Request;

public interface BaseRideView {
    void  showLoading();
    void  hideLoading();
    void setRequestData(Request requestData);
    void showNetworkError(Throwable throwable);
    void shoMessage(String message);
}
