package com.waslabank.wasslabank.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserModel implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("last_login")
    @Expose
    private Object lastLogin;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("license")
    @Expose
    private String license;
    @SerializedName("car_image")
    @Expose
    private String carImage;
    @SerializedName("national_id")
    @Expose
    private String nationalId;
    @SerializedName("car_id")
    @Expose
    private String carId;
    @SerializedName("color_id")
    @Expose
    private String colorId;
    @SerializedName("device")
    @Expose
    private String device;
    @SerializedName("badge")
    @Expose
    private String badge;
    @SerializedName("car_name")
    @Expose
    private String carName;
    @SerializedName("ref_id")
    @Expose
    private String refId;
    @SerializedName("credit")
    @Expose
    private String credit;
    @SerializedName("blocked")
    @Expose
    private String blocked;
    @SerializedName("country")
    private String country;
    private int orders = 0;
    private int comments = 0;
    private boolean friend = false;


    public UserModel() {
    }

    public UserModel(String name, String username, String token, String password, String mobile, String longitude, String latitude, String cityId, String country, String gender, String image, String role, String id, int orders, int comments, String rating, String status, String carName, boolean friend) {
        this.name = name;
        this.username = username;
        this.token = token;
        this.password = password;
        this.mobile = mobile;
        this.longitude = longitude;
        this.latitude = latitude;
        this.cityId = cityId;
        this.country = country;
        this.gender = gender;
        this.image = image;
        this.role = role;
        this.id = id;
        this.orders = orders;
        this.comments = comments;
        this.rating = rating;
        this.status = status;
        this.carName = carName;
        this.friend = friend;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public UserModel(String name, String username, String token, String password, String mobile, String longitude, String latitude, String cityId, String country, String gender, String image, String role, String id, int orders, int comments, String rating, String status, String carName,String refId,String credit) {
        this.name = name;
        this.username = username;
        this.token = token;
        this.password = password;
        this.mobile = mobile;
        this.longitude = longitude;
        this.latitude = latitude;
        this.cityId = cityId;
        this.country = country;
        this.gender = gender;
        this.image = image;
        this.role = role;
        this.id = id;
        this.orders = orders;
        this.comments = comments;
        this.rating = rating;
        this.status = status;
        this.carName = carName;
        this.refId = refId;
        this.credit = credit;
    }

    public UserModel(String mobile) {
        this.mobile = mobile;
    }

    public UserModel(String name, String id, String rating, String carName) {
        this.name = name;
        this.id = id;
        this.rating = rating;
        this.carName = carName;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
}
