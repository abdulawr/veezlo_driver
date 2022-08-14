package com.veezlo.veelzodriver.DataContainer;

import java.io.Serializable;

public class CarKmPerPrice implements Serializable {
    String id,panel_wrap_price,rear_wrap_price,full_wrap_price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPanel_wrap_price() {
        return panel_wrap_price;
    }

    public void setPanel_wrap_price(String panel_wrap_price) {
        this.panel_wrap_price = panel_wrap_price;
    }

    public String getRear_wrap_price() {
        return rear_wrap_price;
    }

    public void setRear_wrap_price(String rear_wrap_price) {
        this.rear_wrap_price = rear_wrap_price;
    }

    public String getFull_wrap_price() {
        return full_wrap_price;
    }

    public void setFull_wrap_price(String full_wrap_price) {
        this.full_wrap_price = full_wrap_price;
    }
}
