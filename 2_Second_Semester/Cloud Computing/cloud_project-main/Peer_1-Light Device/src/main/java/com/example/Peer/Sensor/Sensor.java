package com.example.Peer.Sensor;

import com.example.Peer.Models.DataResponse;

public interface Sensor {

    public void analyseData(String value, String onOff);

    public void printData();

    public void setData(String value, String onOff);

    void updateData(String value);

    DataResponse fetchAllData();
}
