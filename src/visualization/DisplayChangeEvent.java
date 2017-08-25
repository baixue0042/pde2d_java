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
import java.util.EventObject;

/* -------------------------------------------------------------------------
/*
/* CLASS DisplayChangeEvent
/*
/* ------------------------------------------------------------------------- */

/** To be raised when a property of the image display has been changed */
public class DisplayChangeEvent extends EventObject {

/** Type of change in display:
 *  Coordinate X, Y, Z, the Zoom, time, color channel.
 *  So far there is no need for properties other than Z.
 */
    public static final int X = 1;
    public static final int Y = 2;
    public static final int Z = 3;
    public static final int ZOOM = 4;
    public static final int T = 5;
    public static final int CHANNEL = 6;

    private int type;
    private int value;

    public DisplayChangeEvent(Object source, int type, int value) {
        super(source);
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
