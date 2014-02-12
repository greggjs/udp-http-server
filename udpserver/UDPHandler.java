
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
import java.security.GeneralSecurityException;

public class UDPHandler implements Runnable {
    private Logger log;
    private static DatagramSocket sock;
    private DatagramPacket pkt;
    private String request;

    // ENCRYPTION PARAMS
    private static final String KEY = "KKfHCLdNdutbQ46gkDdggQ==";
    private UDPCipher cipher = new UDPCipher(KEY);

    /**
     * Constructor. Creates a new UDP handler that services the given request.
     */
    public UDPHandler(DatagramSocket sock, DatagramPacket pkt, byte[] request, Logger log) {
        this.sock = sock;
        this.pkt = pkt;
        this.log = log;
        try {
            this.request = cipher.decrypt(request);
        } catch (GeneralSecurityException err) {
            log.write("Error: " + err.toString());
        }
    }

    /**
     * Thread execution. This method is called when the thread is woken up. 
     * It determines if the request was valid.
     */
    public void run() {
        try {
            byte sendBuffer[] = makeResponse(request);

            // create datagram packet - use senders address (eg: send back to client)
            DatagramPacket sendPkt = new DatagramPacket(sendBuffer,sendBuffer.length, pkt.getSocketAddress());
            sock.send(sendPkt);

            // log the response to the person we contacted.
            log.write("Response to: " + pkt.getSocketAddress().toString());
            log.write(new String(sendBuffer));

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
        String rep = request.replaceAll("\\r", "CRLF");
        String[] newlines = rep.split("CRLF");
        Response resp;
        try {
            Request req = new Request(newlines);
            log.write("Made Successful HTTP Response");
            resp = new Response(200, "OK", (new Date()).toString());
        } catch (Request.RequestException err) {
            log.write("Error: " + err.errorStatus + ": " + err.msg);
            resp = new Response(err.errorCode, err.errorStatus, err.msg);
        }
        try {
            return Response.getEncryptedBytes(resp.getSimpleResponseString());
        } catch (GeneralSecurityException err) {
            log.write("Error: " + err.toString());
            return resp.getSimpleResponseString().getBytes();
        }
    }

}
