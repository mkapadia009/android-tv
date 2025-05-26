package com.app.itaptv.structure;

public class DeviceDetailsData {
    String Brand="";
    String DeviceID="";
    String Model="";
    String ID="";
    int SDK;
    String Manufacture="";
    String User="";
    String Type="";
    int Base;
    String Incremental="";
    String Board="";
    String Host="";
    String FingerPrint="";
    String VersionCode="";

    public DeviceDetailsData(String brand, String deviceID, String model, String ID, int SDK, String manufacture, String user, String type, int base, String incremental, String board, String host, String fingerPrint, String versionCode) {
        Brand = brand;
        DeviceID = deviceID;
        Model = model;
        this.ID = ID;
        this.SDK = SDK;
        Manufacture = manufacture;
        User = user;
        Type = type;
        Base = base;
        Incremental = incremental;
        Board = board;
        Host = host;
        FingerPrint = fingerPrint;
        VersionCode = versionCode;
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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
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
}
