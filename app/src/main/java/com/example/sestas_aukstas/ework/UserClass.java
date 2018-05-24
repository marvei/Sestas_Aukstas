package com.example.sestas_aukstas.ework;

/**
 * Created by Martynas on 5/23/2018.
 */

public class UserClass {
    public String userID;
    public String userName;
    public String userMail;

    public UserClass(String uID, String uName, String uMail){
        this.userID = uID;
        this.userName = uName;
        this.userMail = uMail;
    }

    public String getUserName(){
        if (userName != null){
            return userName;
        }
        return "";
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserMail(){
        if (userMail != null){
            return userMail;
        }
        return "";
    }

    public void setUserMail(String uMail){
        this.userMail = uMail;
    }

    public String getUserID(){
        if (userID != null){
            return userID;
        }
        return "";
    }
}
