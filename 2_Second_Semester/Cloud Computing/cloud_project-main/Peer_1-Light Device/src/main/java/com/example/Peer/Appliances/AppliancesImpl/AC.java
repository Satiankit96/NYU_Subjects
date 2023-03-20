package com.example.Peer.Appliances.AppliancesImpl;

import com.example.Peer.Appliances.Appliances;
import com.example.Peer.Models.DataResponse;
import com.example.Peer.Models.Status;
import org.springframework.stereotype.Component;

@Component
public class AC implements Appliances {

    int temperature;
    Status status;
    Long lastUpdateTime;
    public AC() {
        this.temperature = 24;
        this.status = Status.OFF;
        this.lastUpdateTime = 0L;
    }
    @Override
    public void turnOnOFf(String str) {
        if(str.equalsIgnoreCase("off")) {
            if(Status.OFF.equals(this.status)) {
                System.out.println("AC is Already OFF");
            } else {
                this.status = Status.OFF;
            }
        } else if(str.equalsIgnoreCase("on")) {
            if(Status.ON.equals(this.status)) {
                System.out.println("AC is Already ON");
            } else {
                this.status = Status.ON;
            }
        }
    }

    @Override
    public void setValue(String value) {
        int val = Integer.parseInt(value);
        if(val >= 28) {
            this.status = Status.ON;
            this.temperature = 24;
        } else {
            this.status = Status.OFF;
            this.temperature = 0;
        }
        System.out.println("AC new temperature is: "+val);
    }

    @Override
    public DataResponse fetchAllData() {
        DataResponse dataResponse = new DataResponse();
        dataResponse.setName("AC");
        dataResponse.setValue(temperature);
        dataResponse.setStatus(!status.equals(Status.OFF));
        return dataResponse;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
