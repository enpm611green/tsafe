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
import java.io.Reader;
import java.util.Calendar;

import tsafe.server.calculation.Calculator;
import tsafe.server.database.DatabaseInterface;
import tsafe.server.parser.ParserInterface;

/**
 * This class connects to the ASDI feed and calls the actual parser
 */
public class ASDIParser extends ParserInterface {
    
    /** Parses the ASDI messages */
    private MessageExtractor messageExtractor;

    /**
     * Constructs an ASDIParser to read from the feed source, and update the database accordingly.
     * This Calculator is used to interpret some of the feed messages.
     */
    public ASDIParser(Reader source, DatabaseInterface tsafeDB, Calculator calc) {
        super(source, tsafeDB);
        this.messageExtractor = new MessageExtractor(tsafeDB, calc);
    }

    /**
     * Reads from the feed and executes a single update on the database.
     * Returns true if there are more updates to be executed.
     *@throws IOException - if there is an error reading from the feed
     */
    public boolean executeUpdate() throws IOException {

        String line = this.feedReader.readLine();
        if (line == null) return false;
        Calendar cal  = Calendar.getInstance();

        /**
          * Message time stamps don't give month and year, so we will
          * use the month and year at the time the message was received
          */
        try {
            Message msg = new Message(line, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
            this.messageExtractor.extractMessage(msg);
        } catch (RuntimeException e) {
            System.out.println();
            System.out.println("ERROR PARSING MESSAGE");
            System.out.println(line);
            e.printStackTrace();
        }

        return true;
   }
}

