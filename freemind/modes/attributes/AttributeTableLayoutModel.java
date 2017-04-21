/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
/*
 * Created on 24.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

/**
 * @author Dimitri Polivaev 24.07.2005
 */
public class AttributeTableLayoutModel {
	public static final String SHOW_SELECTED = "selected";
	public static final String SHOW_ALL = "extended";
	public static final String HIDE_ALL = "hide";
	public static final int DEFAULT_COLUMN_WIDTH = 75;
	private int[] width = { DEFAULT_COLUMN_WIDTH, DEFAULT_COLUMN_WIDTH };

	private EventListenerList listenerList = null;
	ChangeEvent changeEvent = null;
	ColumnWidthChangeEvent[] layoutChangeEvent = { null, null };

	public AttributeTableLayoutModel() {
		super();
	}

	public int getColumnWidth(int col) {
		return width[col];
	}

	public void setColumnWidth(int col, int width) {
		if (this.width[col] != width) {
			this.width[col] = width;
			fireColumnWidthChanged(col);
		}
	}

	/**
	 * @param listenerList
	 *            The listenerList to set.
	 */
	private void setListenerList(EventListenerList listenerList) {
		this.listenerList = listenerList;
	}

	/**
	 * @return Returns the listenerList.
	 */
	private EventListenerList getListenerList() {
		if (listenerList == null)
			listenerList = new EventListenerList();
		return listenerList;
	}

	public void addColumnWidthChangeListener(ColumnWidthChangeListener l) {
		getListenerList().add(ColumnWidthChangeListener.class, l);
	}

	public void removeColumnWidthChangeListener(ColumnWidthChangeListener l) {
		getListenerList().remove(ColumnWidthChangeListener.class, l);
	}

	// Notify all listeners that have registered interest for
	// notification on this event type. The event instance
	// is lazily created using the parameters passed into
	// the fire method.

	protected void fireColumnWidthChanged(int col) {
		// Guaranteed to return a non-null array
		Object[] listeners = getListenerList().getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ColumnWidthChangeListener.class) {
				// Lazily create the event:
				if (layoutChangeEvent[col] == null)
					layoutChangeEvent[col] = new ColumnWidthChangeEvent(this,
							col);
				((ColumnWidthChangeListener) listeners[i + 1])
						.columnWidthChanged(layoutChangeEvent[col]);
			}
		}
	}
}
