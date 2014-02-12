package udpserver;

import java.util.*;
import java.io.*;
import java.net.*;

public class Request {
    private HashMap<String, String> headers;
    private String method;
    private String uri;

    public Request(String method, String uri, HashMap<String, String> headers) {
        this.headers = headers;
        this.method = method;
        this.uri = uri;
    }
}
