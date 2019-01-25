package com.vijay.saurabh.getconnect;

public class CreateUsers {

   private String name , password , code , issharing , lat , lng , imageurl  , userid;
    public CreateUsers(String name, String password, String code, String isSharing, String lat, String lng, String imageUrl , String userid) {
        this.name = name;
        this.password = password;
        this.code = code;
        this.userid = userid ;
        this.issharing = isSharing;
        this.lat = lat;
        this.lng = lng;
        this.imageurl = imageUrl;

    }
    public CreateUsers()
    {

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIssharing() {
        return issharing;
    }

    public void setIssharing(String issharing) {
        this.issharing = issharing;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


}
