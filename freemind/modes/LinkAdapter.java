/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: LinkAdapter.java,v 1.3.18.2.4.1 2007/05/06 21:12:19 christianfoltin Exp $*/

package freemind.modes;

import java.awt.Color;

import freemind.controller.Controller;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.preferences.FreemindPropertyListener;

public abstract class LinkAdapter extends LineAdapter implements MindMapLink {

	public static final String RESOURCES_STANDARDLINKSTYLE = "standardlinkstyle";
	private static Color standardColor = null;
	private static String standardStyle = null;
	private static LinkAdapterListener listener = null;

	String destinationLabel;
	String referenceText;
	MindMapNode source;
	private String uniqueId;

	public LinkAdapter(MindMapNode source, MindMapNode target,
			FreeMindMain frame) {
		super(target, frame);
		this.source = source;
		destinationLabel = null;
		referenceText = null;
		if (listener == null) {
			listener = new LinkAdapterListener();
			Controller.addPropertyChangeListener(listener);
		}
	}

	public String getDestinationLabel() {
		return destinationLabel;
	}

	public String getReferenceText() {
		return referenceText;
	}

	public MindMapNode getSource() {
		return source;
	}

	public void setSource(MindMapNode source) {
		this.source = source;
	}

	public void setDestinationLabel(String destinationLabel) {
		this.destinationLabel = destinationLabel;
	}

	public void setReferenceText(String referenceText) {
		this.referenceText = referenceText;
	}

	// public Object clone() {
	// try {
	// return super.clone();
	// } catch(java.lang.CloneNotSupportedException e) {
	// return null;
	// }
	// }

	/**
	 * @return Returns the uniqueId.
	 */
	public String getUniqueId() {
		return uniqueId;
	}

	/**
	 * @param uniqueId
	 *            The uniqueID to set.
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	protected Color getStandardColor() {
		return standardColor;
	}

	protected void setStandardColor(Color standardColor) {
		LinkAdapter.standardColor = standardColor;
	}

	protected String getStandardStyle() {
		return standardStyle;
	}

	protected void setStandardStyle(String standardStyle) {
		LinkAdapter.standardStyle = standardStyle;
	}

	protected String getStandardColorPropertyString() {
		return FreeMind.RESOURCES_LINK_COLOR;
	}

	protected String getStandardStylePropertyString() {
		return RESOURCES_STANDARDLINKSTYLE;
	}

	protected static class LinkAdapterListener implements
			FreemindPropertyListener {
		public void propertyChanged(String propertyName, String newValue,
				String oldValue) {
			if (propertyName.equals(FreeMind.RESOURCES_LINK_COLOR)) {
				LinkAdapter.standardColor = Tools.xmlToColor(newValue);
			}
			if (propertyName.equals(RESOURCES_STANDARDLINKSTYLE)) {
				LinkAdapter.standardStyle = newValue;
			}
		}
	}
}
