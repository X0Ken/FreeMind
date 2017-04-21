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
/*$Id: SchemeEdgeModel.java,v 1.3 2001/03/24 22:45:46 ponder Exp $*/

package freemind.modes.schememode;

import freemind.main.FreeMindMain;
import freemind.modes.EdgeAdapter;
import freemind.modes.MindMapNode;

public class SchemeEdgeModel extends EdgeAdapter {

	public SchemeEdgeModel(MindMapNode node, FreeMindMain frame) {
		super(node, frame);
	}
}
