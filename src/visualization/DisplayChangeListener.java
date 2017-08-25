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
/* -------------------------------------------------------------------------
/*
/* INTERFACE DisplayChangeListener
/*
/* ------------------------------------------------------------------------- */

/** The Listener interface for receiving DisplayChange events.
 *  The listener can be registered to an Object issuing DisplayChange events
 *  by its addDisplayChangeListener method.
 *  So far only OpenStackWindow used by SyncWindows is such an Object.
 *  */
public interface DisplayChangeListener extends java.util.EventListener {

    public void displayChanged(DisplayChangeEvent e);
}
