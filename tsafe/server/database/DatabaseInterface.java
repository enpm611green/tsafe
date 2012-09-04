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

package tsafe.server.database;

import java.util.Collection;
import java.util.Iterator;

import tsafe.common_datastructures.Airway;
import tsafe.common_datastructures.Fix;
import tsafe.common_datastructures.Flight;
import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.Route;
import tsafe.common_datastructures.Sid;
import tsafe.common_datastructures.Star;

/**
* DatabaseInterface.java
* An interface through which to manage a database for Tsafe
*/
public abstract class DatabaseInterface {
  

   /**
    * Construct a database with the given relevant bounds
    */
   public DatabaseInterface() {
   }  

   public abstract void insertFlight(Flight f);
   public abstract void updateFlight(Flight f);
   public abstract void deleteFlight(String aircraftId);
   public abstract Flight selectFlight(String aircraftId);
   public abstract Collection selectFlightsInBounds(LatLonBounds bounds);
   
   public abstract void insertFix(Fix f);
   public abstract void deleteFix(String fixId);
   public abstract Fix selectFix(String fixId);
   public abstract Collection selectFixesInBounds();
  
   // Airways    
   public abstract void insertAirway(Airway a);
   public abstract void deleteAirway(String awyId);
   public abstract Airway selectAirway(String awyId);
   public abstract Collection selectAirwaysInBounds();

   // Sids
   public abstract void insertSid(Sid sid);
   public abstract void deleteSid(String sidId);
   public abstract Sid selectSid(String sidId);
   public abstract Collection selectSidsInBounds();

   // Stars
   public abstract void insertStar(Star star);
   public abstract void deleteStar(String starId);
   public abstract Star selectStar(String starId);
   public abstract Collection selectStarsInBounds();


   /** Returns true if the route is within bounds */
   protected final boolean routeInBounds(Route r, LatLonBounds bounds) {
       boolean inBounds = false;
       Iterator fixIter = r.fixIterator();

       while (fixIter.hasNext()) {
           Fix f = (Fix)fixIter.next();
           if (bounds.contains(f)) {
               inBounds = true;
               break;
           }
       }

       return inBounds;
   }
}
