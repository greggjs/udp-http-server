package udpserver;

import java.util.*;
import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;

public class Response {
    private int statusCode;
    private String statusMsg;
    private HashMap<String, String> headers;
    private String message;
    private static final String KEY = "KKfHCLdNdutbQ46gkDdggQ==";
    private static UDPCipher cipher = new UDPCipher(KEY);


    public Response(int statusCode, String statusMsg, String message) {
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
        this.message = message;
    }

    public String getSimpleResponseString() {
        return "HTTP/1.1 " + statusCode + " " + statusMsg
            + "\nContent-Type:text/html\nConnection:closed\n\n"
            + "<html><body>" + message + "</body></html>\n\n";
    }

    public String makeDateResponse() {
        Date d = new Date();
        return "HTTP/1.1 200 OK\nContent-Type:text/html\nConnection:closed\n\n"
            + "<html><body>"+d.toString()+"</body></html>\n\n";
    }

    public static byte[] getEncryptedBytes(String msg) throws GeneralSecurityException {
        return cipher.encrypt(msg);
    }

}
