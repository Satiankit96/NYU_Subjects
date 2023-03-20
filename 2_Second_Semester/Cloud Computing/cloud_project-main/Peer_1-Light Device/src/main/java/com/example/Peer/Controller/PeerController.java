package com.example.Peer.Controller;

import com.example.Peer.Models.DataResponse;
import com.example.Peer.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping(value = "/peer", produces= MediaType.APPLICATION_JSON_VALUE)
public class PeerController {

    @Autowired
    Service service;
    @PutMapping(value = "/putData")
    public ResponseEntity<String> putData(@RequestParam(name = "sensorType") String sensorType,
                                          @RequestParam(name = "value") String value,
                                          @RequestParam(name = "onOff") String onOff,
                                          @RequestParam(name = "device") String device) {

        service.analyseSensor(sensorType, value, onOff, device);
        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Access-Control-Allow-Origin", "*");
//        responseHeaders.set("Access-Control-Allow-Credentials", "true");
        responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        responseHeaders.set("Access-Control-Max-Age", "3600");
        responseHeaders.set("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, Origin");

        return ResponseEntity.ok().headers(responseHeaders).body("ok");
    }

    @GetMapping(value = "/getData")
    public ResponseEntity<List<DataResponse>> getData() {

        List<DataResponse> data = service.getData();
        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Access-Control-Allow-Origin", "*");
//        responseHeaders.set("Access-Control-Allow-Credentials", "true");
        responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        responseHeaders.set("Access-Control-Max-Age", "3600");
        responseHeaders.set("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, Origin");
        return ResponseEntity.ok().headers(responseHeaders).body(data);
    }


}
