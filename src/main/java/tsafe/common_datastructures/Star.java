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
 * A STAR, or Standard Terminal Arrival Route, is a standard route for arriving at an airport.
 * It is represented as a basic route plus a number of alternative transitions that can be
 * appened to the beginning of the route. Here's an example abstract value for STAR we will call EXSTAR1:<p>
 *
 * Basic Route: <x, y, z>
 * Transition 1: <a, b, x>
 * Transition 2: <c, x>
 * Transition 3: <l, m, n x>
 * <p>
 *
 * A flight that is following this STAR, is either following the path <x, y, z>, <a, b, x, y, z>,
 * <c, x, y, z>, or <l, m, n, x, y, z>. Thus, a STAR with n transitions represents n+1 possible routes.
 * The route that is intended is the one whose first element is the same as the element preceding the
 * STAR in the filed flight route. Using the above example, if the flight route read a.EXSTAR1.f, the flight
 * would be expected to fly the route <a, b, x, y, z, f>; and a route that read x.EXSTAR1.f would stand
 * for <x, y, z, f>.<p>
 *
 * The first element of the basic route is referred to as the <b>transition fix</b>.
 * In the above example, the transition fix is x.
 */
public class Star {

    // The star's id
    private String id;

    // The star's basic route
    private Route basic;

    // The first fix in the basic route
    private Fix transitionFix;

    // Mas fixes to the route that leaves from that fix
    private Map first2Route = new HashMap();

    /**
     * Constructs a new STAR with the given parameters
     */
    public Star(String id, Route basic) {
        this.id = id;
        this.basic = basic;
        this.transitionFix = basic.firstFix();
        first2Route.put(transitionFix, basic);
    }

    /**
     * Returns the star id
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
     * Returns the route that ends with the given destination fix
     */
    public Route routeFrom(Fix first) {
        return (Route)first2Route.get(first);
    }

    /**
        * Returns the set of all routes this SID represents
     *
     *@returns a set containing the basic route plus a full transition route for
     * each transition. Each full transition route is the basic route followed by
     * one of a transition.
     */
    public Set allRoutes() {
        HashSet routes = new HashSet(first2Route.values());
        return routes;
    }

    /**
     * Add an alternate transition to append to the basic route
     * @throws IllegalArugmentException if the first fix of the added<br>
     *         transition does not equal the last fix of the basic route
     */
    public void addTransition(Route transition) {
        if (!transition.lastFix().equals(transitionFix)) {
            throw new IllegalArgumentException("Star " + id + ":" +
                                               " last fix of transition " + transition +
                                               " is not first fix of basic route " + basic);
        }

        // Add this transition to the map
        Route fullRoute = appendTransition(transition);
        first2Route.put(fullRoute.firstFix(), fullRoute);
        if (!fullRoute.firstFix().equals(transition.firstFix())) {
            throw new RuntimeException("transition appended wrong");
        }
    }

    private Route appendTransition(Route transition) {
        Route fullRoute = new Route();

        // Add all the fixes of the transition to the basic route
        Iterator fixIter = transition.fixIterator();
        while(fixIter.hasNext()) {
            fullRoute.addFix((Fix)fixIter.next());
        }

        // Add all but the first fix of the basic route
        fixIter = basic.fixIterator();
        fixIter.next();
        while(fixIter.hasNext()) {
            fullRoute.addFix((Fix)fixIter.next());
        }

        return fullRoute;
    }
}
