package com.example.Peer.Service;

import com.example.Peer.Appliances.Appliances;
import com.example.Peer.Appliances.AppliancesImpl.AC;
import com.example.Peer.Configuration.appConfig;
import com.example.Peer.Models.DataResponse;
import com.example.Peer.Sensor.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Service {
    @Autowired
    com.example.Peer.Configuration.appConfig appConfig;
    public void analyseSensor(String sensorType, String value, String onOff, String device) {
        Sensor sensor = fetchAppliance(sensorType);
        sensor.printData();
        if(!device.equalsIgnoreCase("mobile")) {
            sensor.updateData(value);
            if(sensor.equals(appConfig.fetchMotionSensor())) {
                return;
            }
            sensor.analyseData(value, onOff);
        } else {
            sensor.setData(value, onOff);
        }
        sensor.printData();
    }

    public Sensor fetchAppliance(String sensorType) {
        if(sensorType.equalsIgnoreCase("motion")) {
            return appConfig.fetchMotionSensor();
        } else {
            return appConfig.fetchTemperatureSensor();
        }
    }

    public List<DataResponse> getData() {
        List<DataResponse> data = new ArrayList<>();
        data.add(appConfig.fetchAC().fetchAllData());
//        data.add(appConfig.fetchLight().fetchAllData());
        data.add(appConfig.fetchTemperatureSensor().fetchAllData());
//        data.add(appConfig.fetchMotionSensor().fetchAllData());
        return data;
    }
}
