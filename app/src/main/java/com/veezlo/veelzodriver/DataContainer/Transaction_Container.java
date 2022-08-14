package com.veezlo.veelzodriver.DataContainer;

public class Transaction_Container {
    String mobile;
    String amount;
    String date;
    String TranID;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTranID() {
        return TranID;
    }

    public void setTranID(String tranID) {
        TranID = tranID;
    }
}
