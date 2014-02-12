/**
 * Name: Jake Gregg
 * Instructor: Dr. Scott Campbell
 * Date: Feb 3, 2014
 * Class: CSE 617
 * Filename: UDP.java
 * Description: Runs a UDP server on the specified port. 
 * When a request is received, it wakes a UDPHandler 
 * thread to handle the request.
 */

package udpserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDP {
    // Socket to listen for packets
    DatagramSocket sock;
    // packet object for client packets
    DatagramPacket pkt;
    // port we listen on in our server
    static final int PORT = 8100;
    // logger we use to write down actions taken by the server
    static Logger log = new Logger("udp.log");
    // thread pool size and thread pool object.
    static final int THREAD_SIZE = 5;
    static ExecutorService pool = Executors.newFixedThreadPool(THREAD_SIZE);
    
    /**
     * Entry point. Creates a new server and runs it.
     */
    public static void main(String args[]) {
        new UDP(PORT).Main();
    }

    /**
     * Construtor. Takes a port to run on.
     */
    public UDP(int port) {
        try {
            sock = new DatagramSocket(port);
        } catch (IOException err) {
            System.err.println("error creating socket " + err);
            log.write("Error: " + err);
            System.exit(-1);
        }
    }
    
    /**
     * The main body of the server. Loops forever and receieves 
     * UDP requests. Sends back a valid HTTP response based 
     * on if the request was valid.
     */
    public void Main() {
        byte recvBuffer[]; // receiving buffer

        while (true) {  
            try {
                // get packet
                recvBuffer = new byte[4096]; //store packet here
                pkt = new DatagramPacket(recvBuffer,recvBuffer.length);
                sock.receive(pkt);
                byte[] reqarr = Arrays.copyOfRange(pkt.getData(), pkt.getOffset(), pkt.getLength());
                 
                // log who contacted the server and what they sent
                log.write("Request from: " + pkt.getSocketAddress().toString());
                
                // create a handler to handle the request.
                Runnable handler = new UDPHandler(sock, pkt, reqarr, log);
                pool.execute(handler);

            // log when an error occurs.
            } catch (IOException err) {
                log.write("An error has occured");
                log.write(err.toString());
            }
        }
    }
}
