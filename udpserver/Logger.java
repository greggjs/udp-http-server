/**
 * Name: Jake Gregg
 * Instructor: Dr. Scott Campbell
 * Date: Feb 5, 2014
 * Class: CSE 617
 * Filename: Logger.java
 * Description: Logs information to a specified file.
 */

package udpserver;

import java.io.*;
import java.util.*;
import java.text.*;

public class Logger {
    private File logfile;
    private PrintWriter pw;

    /**
     *  Constructor. It creates a new Logger with the given filename.
     */
    public Logger(String logname) {
        try {
            logfile = new File(logname);
            pw = new PrintWriter(new FileWriter(logfile, true));
        } catch (IOException err) {
            System.out.println("cannot find file");
        }
    }
    
    /**
     *  Writes to the log file, given a message. It records the date and time,
     *  as well as the message with all newlines converted to spaces, for 
     *  readability reasons.
     */
    public void write(String message) {
        pw.println("[" + (new Date()).toString() + "]: " + message.replaceAll("\\s", " ")); 
        pw.flush();
    }
}
