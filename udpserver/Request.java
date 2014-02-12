/**
 * Name: Jake Gregg
 * Instructor: Dr. Scott Campbell
 * Date: Feb 12, 2014
 * Class: CSE 617
 * Filename: Request.java
 * Description: Object for creating and handling errors with 
 * Requests within the UDP server.
 */

package udpserver;

import java.util.*;
import java.io.*;
import java.net.*;

public class Request {
    private HashMap<String, String> headers;
    private String method;
    private String uri;
    private Logger log;
    private static HashSet<String> HTTP_METHODS =
        new HashSet<String>(Arrays.asList(
                    "GET", "POST", "PUT", "HEAD", "DELETE", "OPTIONS"));

    public class RequestException extends Exception {
        int errorCode;
        String errorStatus;
        String msg;
        public RequestException() {}
        public RequestException(String message, int errorCode, String errorStatus) {
            super(message);
            this.errorCode = errorCode;
            this.errorStatus = errorStatus;
            this.msg = message;
        }
    }

    public Request(String[] lines, Logger log) throws RequestException {
        // check and see if it's a valid request
        if (lines.length < 4) {
            log.write("Error: Malformed Request");
            throw new RequestException("This request is malformed.", 500, "Malformed Request");
        }
        this.headers = new HashMap<String, String>();
        // Make request object
        for (int i = 0; lines[i].length() > 1; i++) {
            String[] header = lines[i].split("\\s");
            if (i == 0) {
                // check the HTTP method is valid
                if (!HTTP_METHODS.contains(header[0])) {
                    log.write("Error: Invalid HTTP Method");
                    throw new RequestException("You probs need an actual HTTP Method", 400, "Bad Request");
                    // check if it has a valid URI
                } else if (header[1].equals("")) {
                    log.write("Error: Invalid URI");
                    throw new RequestException("There ain't a URI...", 400, "Bad Request");
                }
                // Add the method and URI of the request
                this.method = header[0];
                this.uri = header[1];
            // Add each Header to the dictionary 
            } else if (header.length != 0) {
                System.out.println("Header: " + header[1] + "\nValue: " + header[2]);
                headers.put(header[1], header[2]);
            }
        }
        if (!headers.containsKey("Host:")) {
            throw new RequestException("No Host Specified", 400, "Bad Request");
        }
    }

}
