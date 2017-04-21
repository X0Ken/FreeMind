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
/*$Id: MindMapEdge.java,v 1.9.18.1.16.2 2007/06/20 21:52:42 dpolivaev Exp $*/

package freemind.modes;

import freemind.main.XMLElement;

public interface MindMapEdge extends MindMapLine {

	// public Color getColor();
	// public String getStyle();
	// public Stroke getStroke();
	// public int getWidth();
	// public String toString();
	// public void setTarget(MindMapNode node);
	public XMLElement save();

	// returns false if and only if the style is inherited from parent
	boolean hasStyle();
	
	int getStyleAsInt();
}
