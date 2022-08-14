package com.veezlo.veelzodriver.DataContainer;

import java.io.Serializable;

public class NotificationContainer implements Serializable {

    public NotificationContainer() {

    }

    String userID,driverID,date,notID;

    public String getNotID() {
        return notID;
    }

    public void setNotID(String notID) {
        this.notID = notID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
