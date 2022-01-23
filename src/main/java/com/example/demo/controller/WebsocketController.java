package com.example.demo.controller;

import com.example.demo.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author nuc
 */
@RestController
@RequestMapping("/open/socket")
public class WebsocketController {


    @Autowired
    private WebSocketServer webSocketServer;

    @PostMapping("/onReceive")
    public void onReceive(String id, String pwd) throws IOException {
            webSocketServer.putAllInfo(id);
    }
}
