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
/*$Id: MindMapCloud.java,v 1.1.16.2 2004/10/28 05:24:53 christianfoltin Exp $*/

package freemind.modes;

import java.awt.Color;

import freemind.main.XMLElement;

public interface MindMapCloud extends MindMapLine {

	// public Color getColor();
	// public String getStyle();
	// public Stroke getStroke();
	// public int getWidth();
	// public String toString();
	/**
	 * Describes the color of the exterior of the cloud. Normally, this color is
	 * derived from the interior color.
	 */
	public Color getExteriorColor();

	/**
	 * gets iterative level which is required for painting and layout.
	 * 
	 * Cloud iterative level is kept in CloudAdapter object. It is automatically
	 * calculated during the first call of this Method (delayed initialisation).
	 * */
	public int getIterativeLevel();

	/**
	 * changes the iterative level.
	 * 
	 * When some parent node gets or loses its cloud, it should call this
	 * Method, with deltaLevel equal to 1 or -1.
	 */
	public void changeIterativeLevel(int deltaLevel);

	public XMLElement save();
}
