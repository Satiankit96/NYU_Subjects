package com.example.Peer.Sensor.SensorImpl;

import com.example.Peer.Models.DataResponse;
import com.example.Peer.Models.Status;
import com.example.Peer.Sensor.Sensor;
import org.springframework.beans.factory.annotation.Autowired;

public class MotionSensor implements Sensor {

    int lastData;
    public MotionSensor(){
        this.lastData = 0;
    }

    @Autowired
    com.example.Peer.Configuration.appConfig appConfig;
    @Override
    public void analyseData(String value, String onOff) {
        if(System.currentTimeMillis()-appConfig.fetchLight().getLastUpdateTime() > 60*1000) {
            appConfig.fetchLight().setValue(value);
            appConfig.fetchLight().turnOnOFf(onOff);
        } else {
            System.out.println("Value can't be override");
        }
    }

    @Override
    public void printData() {
        System.out.println(appConfig.fetchLight().getIntenstiy()+":  "+appConfig.fetchLight().getStatus());
    }

    @Override
    public void setData(String value, String onOff) {
        appConfig.fetchLight().setIntenstiy(Integer.parseInt(value));
        appConfig.fetchLight().setStatus(onOff.equalsIgnoreCase("off")? Status.OFF:Status.ON);
        appConfig.fetchLight().setLastUpdateTime(System.currentTimeMillis());
    }

    @Override
    public void updateData(String value) {
        setLastData(Integer.parseInt(value));
    }

    @Override
    public DataResponse fetchAllData() {
        DataResponse dataResponse = new DataResponse();
        dataResponse.setName("MotionSensor");
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
