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

package tsafe.server.server_gui.utils;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.MenuComponent;
import java.awt.MenuContainer;

import javax.swing.SwingUtilities;

/**
 * A replacement for the event queue that displays an hourglass cursor automatically
 * for time-intensive events.
 * <p>
 * Original source code can be found at:  
 *     http://www.javaworld.com/javaworld/javatips/jw-javatip87.html
 */
public class WaitCursorEventQueue extends EventQueue {


    //
    // MEMBER VARIABLES
    //

    private int delay;
    private WaitCursorTimer waitTimer;



    //
    // METHODS
    //

    //-------------------------------------------
    public WaitCursorEventQueue(int delay) {
        this.delay = delay;
        waitTimer = new WaitCursorTimer();
        waitTimer.setDaemon(true);
        waitTimer.start();
    }
 

    //-------------------------------------------
    protected void dispatchEvent(AWTEvent event) {
        waitTimer.startTimer(event.getSource());
        try {
            super.dispatchEvent(event);
        }
        finally {
            waitTimer.stopTimer();
        }
    }
 



    //
    // INNER CLASS
    //

 
    private class WaitCursorTimer extends Thread {
        

        //
        // MEMBER VARIABLES
        //

        private Object source;
        private Component parent;



        //
        // METHODS
        //

        //-------------------------------------------
        synchronized void startTimer(Object source) {
            this.source = source;
            notify();
        }
 

        //-------------------------------------------
        synchronized void stopTimer() {
            if (parent == null)
                interrupt();
            else {
                parent.setCursor(null);
                parent = null;
            }
        }

 
        //-------------------------------------------
        public synchronized void run() {
            while (true) {
                try {
                    //wait for notification from startTimer()
                    wait();
 
                    //wait for event processing to reach the threshold, or
                    //interruption from stopTimer()
                    wait(delay);
 
                    if (source instanceof Component)
                        parent = 
                            SwingUtilities.getRoot((Component)source);
                    else if (source instanceof MenuComponent) {
                        MenuContainer mParent =
                            ((MenuComponent)source).getParent();
                        if (mParent instanceof Component)
                            parent = SwingUtilities.getRoot(
                                                            (Component)mParent);
                    }
 
                    if (parent != null && parent.isShowing())
                        parent.setCursor(
                                         Cursor.getPredefinedCursor(
                                                                    Cursor.WAIT_CURSOR));
                }
                catch (InterruptedException ie) { }
            }
        }
    }
}
