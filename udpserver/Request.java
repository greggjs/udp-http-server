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
    // Map to hold all header types and values of request
    private HashMap<String, String> headers;
    // HTTP method of request
    private String method;
    // URI of request
    private String uri;
    // Set of HTTP methods available 
    private static HashSet<String> HTTP_METHODS =
        new HashSet<String>(Arrays.asList(
                    "GET", "POST", "PUT", "HEAD", "DELETE", "OPTIONS"));

    /**
     *  Exception class to handle if there is a bad 
     *  request and hold all error information.
     */
    public class RequestException extends Exception {
        int errorCode; // status code (400)
        String errorStatus; // error status ('bad request)
        String msg; // message for requester
        /**
         *  Default Constructor
         */
        public RequestException() {}
        
        /**
         *  Constructor that creates a new ReqeustException with a given
         *  code, status, and message.
         */
        public RequestException(String message, int errorCode, String errorStatus) {
            super(message);
            this.errorCode = errorCode;
            this.errorStatus = errorStatus;
            this.msg = message;
        }
    }

    /**
     *  Constructur that creates a new Request with a given 
     *  String array of a received request line. 
     *  Handles all forms of bad and malformed requests and 
     *  throws thme to the caller.
     */
    public Request(String[] lines) throws RequestException {
        // check and see if it's a valid request
        if (lines.length < 4) {
            throw new RequestException("This request is malformed.", 400, "Bad Request");
        }
        this.headers = new HashMap<String, String>();
        // Make request object
        for (int i = 0; lines[i].length() > 1; i++) {
            String[] header = lines[i].split("\\s");
            if (i == 0) {
                // check the HTTP method is valid
                if (!HTTP_METHODS.contains(header[0])) {
                    throw new RequestException("You probs need an actual HTTP Method", 400, "Bad Request");
                    // check if it has a valid URI
                } else if (header[1].equals("")) {
                    throw new RequestException("There ain't a URI...", 400, "Bad Request");
                }
                // Add the method and URI of the request
                this.method = header[0];
                this.uri = header[1];
            // Add each Header to the dictionary 
            } else if (header.length != 0) {
                headers.put(header[0], header[1]);
            }
        }
        // If there is no Host field, throw an error.
        if (!headers.containsKey("Host:")) {
            throw new RequestException("No Host Specified", 400, "Bad Request");
        }
    }

}
