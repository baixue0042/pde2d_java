package visualization;
/*-
 * #%L
 * Fiji distribution of ImageJ for the life sciences.
 * %%
 * Copyright (C) 2009 - 2017 Fiji developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import java.awt.AWTEventMulticaster;
import java.util.EventListener;

/* -------------------------------------------------------------------------
/*
/* CLASS IJEventMulticaster
/*
/* ------------------------------------------------------------------------- */

/**
 * Multicaster for events special to ImageJ
 * <p>
 * Example how to implement an object, which fires DisplayChangeEvents using the
 * IJEventMulticaster:
 *
 * <pre><code>
 * public mySpecialWindow extends StackWindow {
 *
 *      DisplayChangeListener dclistener = null;
 *
 *      public synchronized void addDisplayChangeListener(DisplayChangeListener l) {
 *          dclistener = IJEventMulticaster.add(dclistener, l);
 *      }
 *
 *      public synchronized void removeDisplayChangeListener(DisplayChangeListener l) {
 *          dclistener = IJEventMulticaster.remove(dclistener, l);
 *      }
 *
 *      public void myEventFiringMethod(arguments) {
 *          ... code ...
 *          if (dclistener != null) {
 *              DisplayChangeEvent dcEvent = new DisplayChangeEvent(this, DisplayChangeEvent.Z, zSlice);
 *              dclistener.displayChanged(dcEvent);
 *          }
 *          ... code ...
 *      }
 *
 *      ... other methods ...
 * }
 * </code></pre>
 *
 * To put in a new event-listener (by changing this class or extending it):
 * <p>
 * - Add the listener to the "implements" list.
 * <p>
 * - Add the methods of this listener to pass on the events (like displayChanged).
 * <p>
 * - Add the methods "add" and "remove" with the corresponding listener type.
 * <p>
 *
 * @author: code take from Sun's AWTEventMulticaster by J. Walter 2002-03-07
 */

public class IJEventMulticaster extends AWTEventMulticaster implements DisplayChangeListener {

    IJEventMulticaster(EventListener a, EventListener b) {
        super(a,b);
    }

    /**
     * Handles the DisplayChange event by invoking the
     * displayChanged methods on listener-a and listener-b.
     * @param e the DisplayChange event
     */

    public void displayChanged(DisplayChangeEvent e) {
        ((DisplayChangeListener)a).displayChanged(e);
        ((DisplayChangeListener)b).displayChanged(e);
    }
    /**
     * Adds DisplayChange-listener-a with DisplayChange-listener-b and
     * returns the resulting multicast listener.
     * @param a DisplayChange-listener-a
     * @param b DisplayChange-listener-b
     */
    public static DisplayChangeListener add(DisplayChangeListener a, DisplayChangeListener b) {
        return (DisplayChangeListener)addInternal(a, b);
    }
    /**
     * Removes the old DisplayChange-listener from DisplayChange-listener-l and
     * returns the resulting multicast listener.
     * @param l DisplayChange-listener-l
     * @param oldl the DisplayChange-listener being removed
     */
    public static DisplayChangeListener remove(DisplayChangeListener l, DisplayChangeListener oldl) {
	    return (DisplayChangeListener)removeInternal(l, oldl);
    }
}
