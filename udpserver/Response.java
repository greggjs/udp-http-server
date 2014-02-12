/**
 *  Name: Jake Gregg
 *  Instructor: Dr. Scott Campbell
 *  Class: CSE 617
 *  Date: Feb 12, 2014
 *  Filename: Response.java
 *  Description: Handles all of the HTTP responses for the server.
 *  and also has some simple response generation methods for
 *  ease. It also holds a way to encrypt messages.
 */

package udpserver;

import java.util.*;
import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;

public class Response {
    private int statusCode; // response status code
    private String statusMsg; // response status message
    private HashMap<String, String> headers; // response headers (Use later)
    private String message; // message for user

    // ENCRYPTION STUFF
    private static final String KEY = "KKfHCLdNdutbQ46gkDdggQ==";
    private static UDPCipher cipher;

    /**
     *  Constructor that creates a new Response with a given status message,
     *  code, and message. Throws exceptions to the caller so errors can be
     *  logged.
     */
    public Response(int statusCode, String statusMsg, String message) throws GeneralSecurityException, IOException {
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
        this.message = message;
        this.cipher = new UDPCipher(KEY);
    }

    /**
     *  Creates a simple response with required headers and adds
     *  the message to an HTML body.
     */
    public String getSimpleResponseString() {
        return "HTTP/1.1 " + statusCode + " " + statusMsg
            + "\nContent-Type:text/html\nConnection:closed\n\n"
            + "<html><body>" + message + "</body></html>\n\n";
    }

    /**
     *  Creates a simple response with a date included in the body.
     */
    public String makeDateResponse() {
        Date d = new Date();
        return "HTTP/1.1 200 OK\nContent-Type:text/html\nConnection:closed\n\n"
            + "<html><body>"+d.toString()+"</body></html>\n\n";
    }

    /**
     *  A static method that encrypts a message into a byte array.
     */
    public static byte[] getEncryptedBytes(String msg) throws GeneralSecurityException {
        return cipher.encrypt(msg);
    }

}
