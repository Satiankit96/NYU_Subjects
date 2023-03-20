package com.example.Peer.Configuration;

import com.example.Peer.Appliances.Appliances;
import com.example.Peer.Appliances.AppliancesImpl.AC;
import com.example.Peer.Appliances.AppliancesImpl.Light;
import com.example.Peer.Sensor.Sensor;
import com.example.Peer.Sensor.SensorImpl.MotionSensor;
import com.example.Peer.Sensor.SensorImpl.TemperatureSensor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class appConfig {

    @Bean()
    public Sensor fetchTemperatureSensor(){
        return new TemperatureSensor();
    }


    @Bean()
    public MotionSensor fetchMotionSensor(){
        return new MotionSensor();
    }

    @Bean()
    public AC fetchAC(){
        return new AC();
    }

    @Bean()
    public Light fetchLight(){
        return new Light();
    }
}
