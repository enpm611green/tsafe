/*
 TSAFE Prototype: A decision support tool for air traffic controllers
 Copyright (C) 2003  Gregory D. Dennis

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package tsafe.server.parser.asdi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Maintains a connection to an ASDI feed located on a remote server.
 */
public class ServerReader extends Reader implements Serializable, Comparable {

    private final static int SERVER_TIMEOUT = 2000000; //milliseconds

    /**
     * The serialization version ID (changing this value might cause future versions
     * of this class to not be compatible with previous serialized versions).
     */
    static final long serialVersionUID = 5231997;

    //
    // MEMBER VARIABLES
    //

    /**
     * The server.
     */
    private String server;

    /**
     * The port.
     */
    private int port;

    /**
     * The actual connection to the ASDI feed server.
     */
    private transient Socket connection;

    /**
     * Flags whether the connection to the FIG file has 
     * been opened at least once.
     */
    private transient boolean hasBeenOpened;

    /**
     * A reader of the ASDI feed stream.
     */
    private transient Reader feedReader;



    //
    // CONSTRUCTORS
    //

    //-------------------------------------------
    /**
     * Constructs a new server reader.
     *
     * @param server  the remote server
     * @param port    the port to connect to
     */
    public ServerReader(String server, int port) {
        initObj(server, port);
    }


    //-------------------------------------------
    /**
     * Initializes the new server reader.
     *
     * @param server  the remote server
     * @param port    the port to connect to
     */
    private void initObj(String server, int port) {
        this.server = server;
        this.port = port;
        this.connection = null;
        this.hasBeenOpened = false;
        this.feedReader = null;
    }



    //
    // SERIALIZATION METHODS
    //

    //-------------------------------------------
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }


    //-------------------------------------------
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        initObj(server, port);
    }



    //
    // METHODS
    //

    //-------------------------------------------
    /**
     * Returns the server.
     *
     * @return  the server
     */
    public String getServer() {
        return server;
    }


    //-------------------------------------------
    /**
     * Sets the server.
     *
     * @param newServer  the new server
     */
    public void setServer(String newServer) {
        server = newServer;
    }
    

    //-------------------------------------------
    /**
     * Returns the port number.
     *
     * @return  the port number
     */
    public int getPort() {
        return port;
    }


    //-------------------------------------------
    /**
     * Sets the port number.
     *
     * @param newPort  the new port number
     */
    public void setPort(int newPort) {
        port = newPort;
    }


    //-------------------------------------------
    /**
     * Throws an error if this reader has not been properly initialized.
     *
     * @exception IOException if an error occurs
     */
    private void checkInitialization() throws IOException {
        if (!hasBeenOpened) {
            reset();
        }

        if ((feedReader == null) || (connection == null)) {
            throw new IOException("Reader has not been initialized properly or has already been closed");
        }
    }



    //
    // READER METHODS
    //

    //-------------------------------------------
    public void close() throws IOException {
        if (connection != null) {
            connection.close();
        }
        connection = null;
        
        if (feedReader != null) {
            feedReader.close();
        }
        feedReader = null;

        hasBeenOpened = true;
    }


    //-------------------------------------------
    public void reset() throws IOException {
        // Close the old connection if necessary.
        close();
        
        // Open a new connection.
        hasBeenOpened = true;

        if (server == null) {
            throw new IOException("Server cannot be null");
        }
        if (port == -1) {
            throw new IOException("No port was specified");
        }

        connection = new Socket(InetAddress.getByName(server), port);
        connection.setSoTimeout(SERVER_TIMEOUT);
        feedReader = new InputStreamReader(connection.getInputStream());
    }


    //-------------------------------------------
    public boolean ready() throws IOException {
        checkInitialization();
        return feedReader.ready();
    }


    //-------------------------------------------
    public long skip(long n) throws IOException {
        checkInitialization();
        return feedReader.skip(n);
    }
        

    //-------------------------------------------
    public void mark(int readAheadLimit) throws IOException {
        checkInitialization();
        feedReader.mark(readAheadLimit);
    }


    //-------------------------------------------
    public boolean markSupported() {
        try {
            checkInitialization();
        }
        catch (IOException ioe) {
            return false;
        }

        return feedReader.markSupported();
    }


    //-------------------------------------------
    public int read() throws IOException {
        checkInitialization();
        return feedReader.read();
    }


    //-------------------------------------------
    public int read(char[] cbuf) throws IOException {
        checkInitialization();
        return feedReader.read(cbuf);
    }


    //-------------------------------------------
    public int read(char[] cbuf, int off, int len) throws IOException {        
        checkInitialization();
        return feedReader.read(cbuf, off, len);
    }



    //
    // OBJECT METHODS
    //

    //-------------------------------------------
    public int hashCode() {
        int hashcode = 0;

        if (server != null) {
            hashcode = server.hashCode();
        }
        hashcode+= port;

        return hashcode;
    }


    //-------------------------------------------
    public boolean equals(Object o) {
        if (!(o instanceof ServerReader)) {
            return false;
        }

        ServerReader other = (ServerReader) o;
        boolean equals = true;

        try {
            equals = equals && (server.equals(other.server));
            equals = equals && (port == other.port);
        }
        catch (Exception e) {
            equals = false;
        }

        return equals;
    }


    //-------------------------------------------
    public String toString() {
        return "SERVER: " + server + "  {PORT: " + port + "}";
    }



    //
    // COMPARABLE METHODS
    //

    //-------------------------------------------
    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }

        String myString = toString();
        String otherString = o.toString();
        return (myString.compareTo(otherString));
    }

}
