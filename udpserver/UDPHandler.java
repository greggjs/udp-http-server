
 /**
  * Name: Jake Gregg
  * Instructor: Dr. Scott Campbell
  * Date: Feb 3, 2014
  * Class: CSE 617
  * Filename: UDPHandler.java
  * Description: Runs a UDP handler that determines the response
  * to send back to the requester. 
  */

package udpserver;

import java.util.*;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;

public class UDPHandler implements Runnable {
    private Logger log;
    private static DatagramSocket sock;
    private DatagramPacket pkt;
    private String request;
    // hashset to store all of our valid HTTP methods for validation purposes.
    private static HashSet<String> HTTP_METHODS =
        new HashSet<String>(Arrays.asList(
                    "GET", "POST", "PUT", "HEAD", "DELETE", "OPTIONS"));

    // ENCRYPTION PARAMS
    private static final String KEY = "KKfHCLdNdutbQ46gkDdggQ==";
    private UDPCipher cipher = new UDPCipher(KEY);
    
    /**
     * Constructor. Creates a new UDP handler that services the given request.
     */
    public UDPHandler(DatagramSocket sock, DatagramPacket pkt, byte[] request, Logger log) {
        this.sock = sock;
        this.pkt = pkt;
        this.request = cipher.decrypt(request);
        this.log = log;
    }

    /**
     * Thread execution. This method is called when the thread is woken up. 
     * It determines if the request was valid.
     */
    public void run() {
        try {
            byte sendBuffer[];

            // create response buffer and packet
            //ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //PrintWriter pw = new PrintWriter(bos);
            byte[] response = makeResponse(request);

            // make response a byte array
            //pw.print(response);
            //pw.flush();
            sendBuffer = response;
            // create datagram packet - use senders address (eg: send back to client)
            DatagramPacket sendPkt = new DatagramPacket(sendBuffer,sendBuffer.length, pkt.getSocketAddress());
            sock.send(sendPkt);

            // log the response to the person we contacted.
            log.write("Response to: " + pkt.getSocketAddress().toString());
            log.write(new String(response));

            // log when an error occurs.
        } catch (IOException err) {
            log.write("An error has occured");
            log.write(err.toString());

        }
    }

    /**
     * Determines what response to send the user based on the 
     * request that it received.
     */
    public byte[] makeResponse(String request) {
        System.out.println(request);
        String rep = request.replaceAll("\\r", "CRLF");
        System.out.println(rep);
        String[] newlines = rep.split("CRLF");
        System.out.println(Arrays.toString(newlines));
        System.out.println(newlines.length);
        // check and see if it's a valid request
        if (newlines.length < 4) {
            log.write("Error: Malformed Request");
            return makeErrorResponse(500, "Malformed Request", "This request is malformed.");
        }
        String method = "", uri  = "";
        HashMap<String, String> headers = new HashMap<String, String>();
        for (int i = 0; i < newlines.length; i++) {
            String[] header = newlines[i].split("\\s");
            if (i == 0) {
                // check the HTTP method is valid
                if (!HTTP_METHODS.contains(header[0])) {
                    log.write("Error: Invalid HTTP Method");
                    return makeErrorResponse(400, "Bad Request", "You probs need an actual HTTP Method");
                // check if it has a valid URI
                } else if (header[1].equals("")) {
                    log.write("Error: Invalid URI");
                    return makeErrorResponse(400, "Bad Request", "There ain't a URI...");
                }
                method = header[0];
                uri = header[1];
            }
        }
        Request r = new Request(method, uri, headers);
        // if it passes these checks, then send a valid response.
        log.write("Made Successful HTTP Response");
        return makeGoodResponse();
    }

    /**
     * Makes an error message to send. It takes in an Error code, status, and message
     * to display to the requester.
     */
    public byte[] makeErrorResponse(int errorCode, String errorStatus, String errorMsg) {
        String msg = "HTTP/1.1 " + errorCode + " " + errorStatus
            + "\nContent-Type:text/html\nConnection:closed\n\n"
            + "<html><body>" + errorMsg + "</body></html>\n\n";
        return cipher.encrypt(msg);
    }

    /**
     * Makes a valid HTTP response to the requester. All it does now is
     * return an HTML document that contains the current date and time.
     */
    public byte[] makeGoodResponse() {
        Date d = new Date();
        String msg = "HTTP/1.1 200 OK\nContent-Type:text/html\nConnection:closed\n\n"
            + "<html><body>"+d.toString()+"</body></html>\n\n";
        return cipher.encrypt(msg); 
    }
}
