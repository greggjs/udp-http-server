
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

public class UDPHandler implements Runnable {
    private Logger log;
    private static DatagramSocket sock;
    private DatagramPacket pkt;
    private String request;
    // hashset to store all of our valid HTTP methods for validation purposes.
    private static HashSet<String> HTTP_METHODS =
        new HashSet<String>(Arrays.asList(
                    "GET", "POST", "PUT", "HEAD", "DELETE", "OPTIONS"));

    /**
     * Constructor. Creates a new UDP handler that services the given request.
     */
    public UDPHandler(DatagramSocket sock, DatagramPacket pkt, String request, Logger log) {
        this.sock = sock;
        this.pkt = pkt;
        this.request = request;
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
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(bos);
            String response = makeResponse(request);

            // make response a byte array
            pw.print(response);
            pw.flush();
            sendBuffer = bos.toByteArray();

            // create datagram packet - use senders address (eg: send back to client)
            DatagramPacket sendPkt = new DatagramPacket(sendBuffer,sendBuffer.length, pkt.getSocketAddress());
            sock.send(sendPkt);

            // log the response to the person we contacted.
            log.write("Response to: " + pkt.getSocketAddress().toString());
            log.write(response);

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
    public String makeResponse(String request) {
        String[] lines = request.split("\\s");
        // check and see if it's a valid request
        if (lines.length < 10) {
            log.write("Error: Malformed Request");
            return makeErrorResponse(400, "Bad Request", "This request is malformed.");
            // check the HTTP method is valid
        } else if (!HTTP_METHODS.contains(lines[0])) {
            log.write("Error: Invalid HTTP Method");
            return makeErrorResponse(400, "Bad Request", "You probs need an actual HTTP Method");
            // check if it has a valid URI
        } else if (lines[1].equals("")) {
            log.write("Error: Invalid URI");
            return makeErrorResponse(400, "Bad Request", "There ain't a URI...");
        }
        // if it passes these checks, then send a valid response.
        log.write("Made Successful HTTP Response");
        return makeGoodResponse();
    }

    /**
     * Makes an error message to send. It takes in an Error code, status, and message
     * to display to the requester.
     */
    public String makeErrorResponse(int errorCode, String errorStatus, String errorMsg) {
        return "HTTP/1.1 " + errorCode + " " + errorStatus
            + "\nContent-Type:text/html\nConnection:closed\n\n"
            + "<html><body>" + errorMsg + "</body></html>\n\n";
    }

    /**
     * Makes a valid HTTP response to the requester. All it does now is
     * return an HTML document that contains the current date and time.
     */
    public String makeGoodResponse() {
        Date d = new Date();
        return "HTTP/1.1 200 OK\nContent-Type:text/html\nConnection:closed\n\n"
            + "<html><body>"+d.toString()+"</body></html>\n\n";
    }
}
