package com.rsostream.tcp.resources;


import com.kumuluz.ee.discovery.annotations.RegisterService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@RegisterService
@ApplicationPath("v1") // Good practice: add application version here
public class TCPApplication extends Application {
}
