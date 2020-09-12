package com.waslabank.wasslabank.models;

import com.google.gson.annotations.SerializedName;
import com.waslabank.wasslabank.models.SingleRequestModel.Request;

import java.io.Serializable;

public class MyRideModel implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("city_id")
    private String cityId;
    @SerializedName("address")
    private String address;
    @SerializedName("longitude")
    private String lon;
    @SerializedName("latitude")
    private String lat;
    @SerializedName("longitude_to")
    private String lonTo;
    @SerializedName("latitude_to")
    private String latTo;
    @SerializedName("status")
    private String status;
    @SerializedName("created")
    private String created;
    @SerializedName("updated")
    private String updated;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("address_to")
    private String addressTo;
    @SerializedName("city_id_to")
    private String cityTo;
    @SerializedName("request_time")
    private String requestTime;
    @SerializedName("views")
    private String view;
    @SerializedName("request_date")
    private String requestDate;
    @SerializedName("longitude_update")
    private String lonUpdate;
    @SerializedName("latitude_update")
    private String latUpdate;
    private String fromId;
    @SerializedName("user")
    private UserModel user;
    @SerializedName("upcoming")
    private boolean upcoming;
    @SerializedName("distance")
    private String distance = "0.0";
    @SerializedName("seats")
    private String seats;
    @SerializedName("start")
    private String start;
    @SerializedName("picked")
    private String picked;
    @SerializedName("duration")
    private String duration;


    public String getPicked() {
        return picked;
    }

    public void setPicked(String picked) {
        this.picked = picked;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public MyRideModel() {
    }

    public MyRideModel(String id, String cityId, String address, String lon, String lat, String lonTo, String latTo
            , String status, String created, String updated, String userId, String addressTo, String cityTo
            , String requestTime, String view, String requestDate, String lonUpdate, String latUpdate
            , String fromId, UserModel user, boolean upcoming, String distance, String seats, String start,String picked) {
        this.id = id;
        this.cityId = cityId;
        this.address = address;
        this.lon = lon;
        this.lat = lat;
        this.lonTo = lonTo;
        this.latTo = latTo;
        this.status = status;
        this.created = created;
        this.updated = updated;
        this.userId = userId;
        this.addressTo = addressTo;
        this.cityTo = cityTo;
        this.requestTime = requestTime;
        this.view = view;
        this.requestDate = requestDate;
        this.lonUpdate = lonUpdate;
        this.latUpdate = latUpdate;
        this.fromId = fromId;
        this.user = user;
        this.upcoming = upcoming;
        this.distance = distance;
        this.seats = seats;
        this.start = start;
        this.picked=picked;
    }


    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public boolean isUpcoming() {
        return upcoming;
    }

    public void setUpcoming(boolean upcoming) {
        this.upcoming = upcoming;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLonTo() {
        return lonTo;
    }

    public void setLonTo(String lonTo) {
        this.lonTo = lonTo;
    }

    public String getLatTo() {
        return latTo;
    }

    public void setLatTo(String latTo) {
        this.latTo = latTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public String getCityTo() {
        return cityTo;
    }

    public void setCityTo(String cityTo) {
        this.cityTo = cityTo;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getLonUpdate() {
        return lonUpdate;
    }

    public void setLonUpdate(String lonUpdate) {
        this.lonUpdate = lonUpdate;
    }

    public String getLatUpdate() {
        return latUpdate;
    }

    public void setLatUpdate(String latUpdate) {
        this.latUpdate = latUpdate;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getDistance() {
        return distance;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


//    public MyRideModel(String id, String cityId, String address, String lon, String lat, String lonTo, String latTo
//            , String status, String created, String updated, String userId, String addressTo, String cityTo
//            , String requestTime, String view, String requestDate, String lonUpdate, String latUpdate
//            , String fromId, UserModel user, boolean upcoming, String distance, String seats, String start)
    public static MyRideModel returnRidleModel(Request request){
        return new  MyRideModel(
               request.getId(),
               request.getCityId(),
                request.getAddress(),
                request.getLongitude(),
                request.getLatitude(),
                request.getLongitudeTo(),
                request.getLatitudeTo(),request.getStatus(),
                request.getCreated(),request.getUpdated(),
                request.getUserId(),request.getAddressTo(),
                request.getCityIdTo(),
                request.getRequestTime(),
                request.getViews(),
                request.getRequestDate(),
                request.getLongitudeUpdate(),
                request.getLatitudeUpdate(),
                request.getFromId(),
                request.getUser(),request.isUpcoming(),
                request.getDistance(),request.getSeats(),request.getStart(),
                request.getPicked()
        );
    }
}
