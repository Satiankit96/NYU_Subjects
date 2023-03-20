package com.example.Peer.Appliances;

import com.example.Peer.Models.DataResponse;

public interface Appliances {

    public void turnOnOFf(String str);

    public void setValue(String value);

    DataResponse fetchAllData();
}
