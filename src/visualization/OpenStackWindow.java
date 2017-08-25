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
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.StackWindow;

import java.awt.Image;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseWheelEvent;

/* -------------------------------------------------------------------------
/*
/* CLASS OpenStackWindow
/*
/* ------------------------------------------------------------------------- */

/** StackWindow, which issues DispChanged Events
 *  to registered Listeners, when the displayed slice has been changed.
 */
public class OpenStackWindow extends StackWindow {
    DisplayChangeListener displayChangeListener = null;

    public OpenStackWindow(ImagePlus imp) {
        super(imp);
        if (ij!=null) {
            Image img = ij.getIconImage();
            if (img!=null) setIconImage(img);
        }
    }

    public OpenStackWindow(ImagePlus imp, ImageCanvas ic) {
        super(imp, ic);
        if (ij!=null) {
            Image img = ij.getIconImage();
            if (img!=null) setIconImage(img);
        }
    }
    
    public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
        super.adjustmentValueChanged(e);
        if (!running2)
		sendDisplayChangeEvent(e.getSource());
    }

    /** Handles changing slice by MouseWheel */   
    public void mouseWheelMoved(MouseWheelEvent event) {
        super.mouseWheelMoved(event);
        sendDisplayChangeEvent(event.getSource());
    }

    public void sendDisplayChangeEvent(Object source) {
	if (displayChangeListener == null)
		return;
	final DisplayChangeEvent event;
	if (source == cSelector)
		event = new DisplayChangeEvent(this, DisplayChangeEvent.CHANNEL, cSelector.getValue());
	else if (source == tSelector)
		event = new DisplayChangeEvent(this, DisplayChangeEvent.T, tSelector.getValue());
	else /* default to z */
		event = new DisplayChangeEvent(this, DisplayChangeEvent.Z, zSelector.getValue());
        displayChangeListener.displayChanged(event);
    }

    public synchronized void addDisplayChangeListener(DisplayChangeListener l) {
        displayChangeListener = IJEventMulticaster.add(displayChangeListener, l);
    }
    public synchronized void removeDisplayChangeListener(DisplayChangeListener l) {
        displayChangeListener = IJEventMulticaster.remove(displayChangeListener, l);
    }

}
