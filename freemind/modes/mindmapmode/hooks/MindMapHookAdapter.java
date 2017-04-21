/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2005  Christian Foltin <christianfoltin@users.sourceforge.net>
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


package freemind.modes.mindmapmode.hooks;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.mindmapmode.MindMapController;

/** */
public class MindMapHookAdapter extends ModeControllerHookAdapter {

	/**
     *
     */
	public MindMapHookAdapter() {
		super();

	}

	public MindMapController getMindMapController() {
		return (MindMapController) getController();
	}
	
	public JMenuItem addAccelerator(JMenuItem menuItem, String key) {
		String keyProp = getMindMapController().getFrame().getProperty(key);
		if(keyProp == null) {
			logger.warning("Keystroke to " + key + " not found.");
		}
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyProp);
		menuItem.setAccelerator(keyStroke);
		menuItem.getAction().putValue(Action.ACCELERATOR_KEY, keyStroke);
		return menuItem;
	}



}
