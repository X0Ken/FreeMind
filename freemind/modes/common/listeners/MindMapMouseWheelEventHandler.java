/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 09.11.2005
 */

package freemind.modes.common.listeners;

import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;
import java.util.Set;

import freemind.controller.Controller;
import freemind.main.FreeMind;
import freemind.modes.ControllerAdapter;
import freemind.modes.mindmapmode.actions.MindMapActions.MouseWheelEventHandler;
import freemind.preferences.FreemindPropertyListener;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * 
 */
public class MindMapMouseWheelEventHandler implements MouseWheelListener {

	private static int SCROLL_SKIPS = 8;
	private static final int HORIZONTAL_SCROLL_MASK = InputEvent.SHIFT_MASK
			| InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK
			| InputEvent.BUTTON3_MASK;
	private static final int ZOOM_MASK = InputEvent.CTRL_MASK;
	// |= oldX >=0 iff we are in the drag

	private static java.util.logging.Logger logger = null;

	/**
	 *
	 */
	public MindMapMouseWheelEventHandler(ControllerAdapter controller) {
		super();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		Controller.addPropertyChangeListener(new FreemindPropertyListener() {

			public void propertyChanged(String propertyName, String newValue,
					String oldValue) {
				if (propertyName.equals(FreeMind.RESOURCES_WHEEL_VELOCITY)) {
					SCROLL_SKIPS = Integer.parseInt(newValue);
				}
			}
		});
		SCROLL_SKIPS = controller.getFrame().getIntProperty(
				FreeMind.RESOURCES_WHEEL_VELOCITY, 8);
		logger.info("Setting SCROLL_SKIPS to " + SCROLL_SKIPS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController.MouseWheelEventHandler#handleMouseWheelEvent
	 * (java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		MapView mapView = (MapView) e.getSource();
		ControllerAdapter mController = (ControllerAdapter) mapView.getModel()
				.getModeController();
		if (mController.isBlocked()) {
			return; // block the scroll during edit (PN)
		}
		Set registeredMouseWheelEventHandler = mController
				.getRegisteredMouseWheelEventHandler();
		for (Iterator i = registeredMouseWheelEventHandler.iterator(); i
				.hasNext();) {
			MouseWheelEventHandler handler = (MouseWheelEventHandler) i.next();
			boolean result = handler.handleMouseWheelEvent(e);
			if (result) {
				// event was consumed:
				return;
			}
		}

		if ((e.getModifiers() & ZOOM_MASK) != 0) {
			// fc, 18.11.2003: when control pressed, then the zoom is changed.
			float newZoomFactor = 1f + Math.abs((float) e.getWheelRotation()) / 10f;
			if (e.getWheelRotation() < 0)
				newZoomFactor = 1 / newZoomFactor;
			final float oldZoom = ((MapView) e.getComponent()).getZoom();
			float newZoom = oldZoom / newZoomFactor;
			// round the value due to possible rounding problems.
			newZoom = (float) Math.rint(newZoom * 1000f) / 1000f;
			newZoom = Math.max(1f / 32f, newZoom);
			newZoom = Math.min(32f, newZoom);
			if (newZoom != oldZoom) {
				mController.getController().setZoom(newZoom);
			}
			// end zoomchange
		} else if ((e.getModifiers() & HORIZONTAL_SCROLL_MASK) != 0) {
			((MapView) e.getComponent()).scrollBy(
					SCROLL_SKIPS * e.getWheelRotation(), 0);
		} else {
			((MapView) e.getComponent()).scrollBy(0,
					SCROLL_SKIPS * e.getWheelRotation());
		}
	}

}
