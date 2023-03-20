package com.example.Peer.Sensor.SensorImpl;

import com.example.Peer.Models.DataResponse;
import com.example.Peer.Models.Status;
import com.example.Peer.Sensor.Sensor;
import org.springframework.beans.factory.annotation.Autowired;

public class TemperatureSensor implements Sensor {

    int lastData;
    public TemperatureSensor() {
        this.lastData = 0;
    }

    @Autowired
    com.example.Peer.Configuration.appConfig appConfig;
    @Override
    public void analyseData(String value, String onOff) {
        if(System.currentTimeMillis()-appConfig.fetchLight().getLastUpdateTime() > 60*1000) {
            appConfig.fetchAC().setValue(value);
            appConfig.fetchAC().turnOnOFf(onOff);
            appConfig.fetchAC().setLastUpdateTime(System.currentTimeMillis());
        } else {
            System.out.println("Value can't be override");
        }
    }

    @Override
    public void printData() {
        System.out.println(appConfig.fetchAC().getTemperature()+":  "+appConfig.fetchAC().getStatus());
    }

    @Override
    public void setData(String value, String onOff) {
        appConfig.fetchAC().setTemperature(Integer.parseInt(value));
        appConfig.fetchAC().setStatus(onOff.equalsIgnoreCase("off")? Status.OFF:Status.ON);
        appConfig.fetchAC().setLastUpdateTime(System.currentTimeMillis());
    }

    @Override
    public void updateData(String value) {
        setLastData(Integer.parseInt(value));
    }

    @Override
    public DataResponse fetchAllData() {
        DataResponse dataResponse = new DataResponse();
        dataResponse.setName("TemperatureSensor");
        dataResponse.setValue(lastData);
        dataResponse.setStatus(true);
        return dataResponse;
    }

    public int getLastData() {
        return lastData;
    }

    public void setLastData(int lastData) {
        this.lastData = lastData;
    }
}
