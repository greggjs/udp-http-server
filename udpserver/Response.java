package udpserver;

import java.util.*;
import java.io.*;
import java.net.*;

public class Response {
    private int statusCode;
    private String statusMsg;
    private HashMap<String, String> headers;
    private String message;

    public Response(int statusCode, String statusMsg, 
            HashMap<String, String> headers, String message) {
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
        this.headers = headers;
        this.message = message;
    }


}
