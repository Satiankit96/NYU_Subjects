package com.example.Peer.Appliances.AppliancesImpl;

import com.example.Peer.Appliances.Appliances;
import com.example.Peer.Models.DataResponse;
import com.example.Peer.Models.Status;
import org.springframework.stereotype.Component;

@Component
public class Light implements Appliances {

    int intenstiy;
    Status status;
    Long lastUpdateTime;

    public Light(){
        this.intenstiy = 0;
        this.status = Status.OFF;
        this.lastUpdateTime = 0L;
    }
    @Override
    public void turnOnOFf(String str) {
        if(str.equalsIgnoreCase("off")) {
            if(Status.OFF.equals(this.status)) {
                System.out.println("Light is Already OFF");
            } else {
                this.status = Status.OFF;
            }
        } else if(str.equalsIgnoreCase("on")) {
            if(Status.ON.equals(this.status)) {
                System.out.println("Light is Already ON");
            } else {
                this.status = Status.ON;
            }
        }
    }

    @Override
    public void setValue(String value) {
        int val = Integer.parseInt(value);
        if(val == 0) {
            this.status = Status.OFF;
            this.intenstiy = 0;
        } else if(val == 1) {
            this.status = Status.ON;
            this.intenstiy = 30;
        } else if(val == 2) {
            this.status = Status.ON;
            this.intenstiy = 60;
        } else if(val > 2 ) {
            this.status = Status.ON;
            this.intenstiy = 100;
        }
        System.out.println("Value of Intensity of Light has been changed to: "+val);
    }

    @Override
    public DataResponse fetchAllData() {
        DataResponse dataResponse = new DataResponse();
        dataResponse.setName("Light");
        dataResponse.setValue(intenstiy);
        dataResponse.setId(1);
        dataResponse.setStatus(!status.equals(Status.OFF));
        return dataResponse;
    }

    public int getIntenstiy() {
        return intenstiy;
    }

    public void setIntenstiy(int intenstiy) {
        this.intenstiy = intenstiy;
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
