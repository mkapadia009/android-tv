package com.app.itaptv.roomDatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "AnalyticsData")
public class AnalyticsData {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    String activityType = "";
    String entityId = "";
    String entityTitle = "";
    String entityType = "";
    String duration = "";

    //Location details
    String latitude = "";
    String longitude = "";
    String city = "";
    String state = "";

    //DeviceDetailsData
    String Brand = "";
    String DeviceID = "";
    String Model = "";
    int SDK;
    String Manufacture = "";
    String UserDevice = "";
    String Type = "";
    int Base;
    String Incremental = "";
    String Board = "";
    String Host = "";
    String FingerPrint = "";
    String VersionCode = "";

    //User Data
    int userId;
    String user;
    String userContact;
    String createdOn = "";

    public AnalyticsData(String activityType, String duration,int userId, String user, String userContact, String createdOn) {
        this.activityType = activityType;

        this.duration = duration;
        this.userId = userId;
        this.user = user;
        this.userContact = userContact;
        this.createdOn = createdOn;
    }

    public AnalyticsData(String activityType,String entityId, String entityTitle, String entityType, String duration, String latitude, String longitude, String city, String state, String brand, String deviceID, String model, int SDK, String manufacture, String userDevice, String type, int base, String incremental, String board, String host, String fingerPrint, String versionCode,int userId, String user, String userContact, String createdOn) {
        this.activityType = activityType;
        this.entityId = entityId;
        this.entityTitle = entityTitle;
        this.entityType = entityType;
        this.duration = duration;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.state = state;
        this.Brand = brand;
        this.DeviceID = deviceID;
        this.Model = model;
        this.SDK = SDK;
        this.Manufacture = manufacture;
        this.UserDevice = userDevice;
        this.Type = type;
        this.Base = base;
        this.Incremental = incremental;
        this.Board = board;
        this.Host = host;
        this.FingerPrint = fingerPrint;
        this.VersionCode = versionCode;
        this.userId = userId;
        this.user = user;
        this.userContact = userContact;
        this.createdOn = createdOn;
    }

    @Ignore


    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getEntityTitle() {
        return entityTitle;
    }

    public void setEntityTitle(String entityTitle) {
        this.entityTitle = entityTitle;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public int getSDK() {
        return SDK;
    }

    public void setSDK(int SDK) {
        this.SDK = SDK;
    }

    public String getManufacture() {
        return Manufacture;
    }

    public void setManufacture(String manufacture) {
        Manufacture = manufacture;
    }

    public String getUserDevice() {
        return UserDevice;
    }

    public void setUserDevice(String userDevice) {
        UserDevice = userDevice;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getBase() {
        return Base;
    }

    public void setBase(int base) {
        Base = base;
    }

    public String getIncremental() {
        return Incremental;
    }

    public void setIncremental(String incremental) {
        Incremental = incremental;
    }

    public String getBoard() {
        return Board;
    }

    public void setBoard(String board) {
        Board = board;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public String getFingerPrint() {
        return FingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        FingerPrint = fingerPrint;
    }

    public String getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(String versionCode) {
        VersionCode = versionCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
