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

package tsafe.common_datastructures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A SID, or Standard Intrument Departure, is a standard route for departing an airport.
 * It is represented as a basic route plus a number of alternative transitions that can be
 * appened to the end of the route. Here's an example abstract value for SID we will call EXSID1:<p>
 *
 * Basic Route: <a, b, c>
 * Transition 1: <c, d, e>
 * Transition 2: <c, x>
 * Transition 3: <c, l, m n>
 * <p>
 *
 * A flight that is following this SID, is either following the path <a, b, c>, <a, b, c, d, e>,
 * <a, b, c, x>, or <a, b, c, l, m, n>. Thus, a SID with n transitions represents n+1 possible routes.
 * The route that is intended is the one whose last element is the same as the element following the
 * SID in the filed flight route. Using the above example, if the flight route read f.EXSID.e, the flight
 * would be expected to fly the route <f, a, b, c, d, e>; and a route that read f.EXSID1.c would stand
 * for <f, a, b, c>.<p>
 *
 * The last element of the basic route is referred to as the <b>transition fix</b>.
 * In the above example, the transition fix is c.
 */
public class Sid {

    // The sid's id
    private String id;

    // The sid's basic route
    private Route basic;

    // The last fix in the basic route
    private Fix transitionFix;

    // Maps fixes to the route the leads to that fix
    private Map last2Route = new HashMap();

    /**
     * Constructs a new SID with the given parameters
     */ 
    public Sid(String id, Route basic) {
        this.id = id;
        this.basic = basic;
        this.transitionFix = basic.lastFix();
        last2Route.put(transitionFix, basic);
    }

    /**
     * Returns the sid id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the basic route
     */
    public Route basicRoute() {
        return basic;
    }

    /**
     * Returns the route that ends with the given fix
     */
    public Route routeTo(Fix last) {
        return (Route)last2Route.get(last);
    }

    /**
     * Returns the set of all routes this SID represents
     *
     *@returns a set containing the basic route plus a full transition route for
     * each transition. Each full transition route is the basic route followed by
     * one of the transitions. 
     */
    public Set allRoutes() {
        HashSet routes = new HashSet(last2Route.values());
        return routes;
    }

    /**
     * Add an alternate transition to append to the basic route
     * @throws IllegalArugmentException if the first fix of the added<br>
     *         transition does not equal the last fix of the basic route
     */
    public void addTransition(Route transition) {     
        if (!transition.firstFix().equals(transitionFix)) {
            throw new IllegalArgumentException("Sid " + id + ":" +
                                               " first fix of transition " + transition +
                                               " is not last fix of basic route " + basic);
        }

        // Add this transition to the map
        Route fullRoute = appendTransition(transition);
        last2Route.put(fullRoute.lastFix(), fullRoute);
        if (!fullRoute.lastFix().equals(transition.lastFix())) {
            throw new RuntimeException("transition appended wrong");
        }
    }
    
    private Route appendTransition(Route transition) {
        Route fullRoute = new Route();

        // Add all the basic route fixes to the full route
        Iterator fixIter = basic.fixIterator();
        while(fixIter.hasNext()) {
            fullRoute.addFix((Fix)fixIter.next());
        }

        // Add all but the first fix from the transition
        // That fix should be the last fix of the basic route
        fixIter = transition.fixIterator();
        fixIter.next();
        while(fixIter.hasNext()) {
            fullRoute.addFix((Fix)fixIter.next());
        }

        return fullRoute;
    }
}
