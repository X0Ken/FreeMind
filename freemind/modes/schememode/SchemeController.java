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


package freemind.modes.schememode;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import freemind.controller.MenuBar;
import freemind.controller.StructuredMenuHolder;
import freemind.extensions.HookFactory;
import freemind.modes.FreeMindFileDialog;
import freemind.modes.MapAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.common.actions.NewMapAction;
import freemind.modes.viewmodes.ViewControllerAdapter;

public class SchemeController extends ViewControllerAdapter {

	Action newMap = new NewMapAction(this);
	Action open = new OpenAction(this);
	Action save = new SaveAction(this);
	Action saveAs = new SaveAsAction(this);
	Action evaluate = new EvaluateAction();
	// Action edit = new EditAction(this);
	private JPopupMenu popupmenu = new SchemePopupMenu(this);

	public SchemeController(Mode mode) {
		super(mode);
	}

	public MapAdapter newModel(ModeController modeController) {
		return new SchemeMapModel(getFrame(), modeController);
	}

	public MindMapNode newNode(Object userObject, MindMap map) {
		return new SchemeNodeModel(getFrame(), map);
	}

	// //private
	// private MindMap getModel() {
	// return (MindMap)getController().getModel();
	// }

	public boolean saveAs() {
		FreeMindFileDialog chooser = getFileChooser();
		int returnVal = chooser.showSaveDialog(getView());
		if (returnVal == JFileChooser.APPROVE_OPTION) {// ok pressed
			File f = chooser.getSelectedFile();
			// Force the extension to be .mm
			// String ext = Tools.getExtension(f.getName());
			// if(!ext.equals(freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION_WITHOUT_DOT))
			// {
			// f = new
			// File(f.getParent(),f.getName()+freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION);
			// }
			save(f);
			// Update the name of the map
			updateMapModuleName();
			return true;
		}
		return false;
	}

	public JPopupMenu getPopupMenu() {
		return this.popupmenu;
	}

	private class EvaluateAction extends AbstractAction {
		EvaluateAction() {
			super(getController().getResourceString("scheme_evaluate"));
		}

		public void actionPerformed(ActionEvent e) {
			String rawCode = ((SchemeMapModel) getMap()).getCode().trim();
			System.out.println(rawCode);
			StringTokenizer code = new StringTokenizer(rawCode, ",");
			String output = "Output: \n";
			// while(code.hasMoreTokens()) {
			// output = output + (SI.eval(code.nextToken()).toString())+"\n";
			// }
			JOptionPane.showMessageDialog(getView(), output);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#updateMenus(freemind.controller.
	 * StructuredMenuHolder)
	 */
	public void updateMenus(StructuredMenuHolder holder) {
		holder.addAction(newMap, MenuBar.FILE_MENU + "open/new");
		holder.addAction(open, MenuBar.FILE_MENU + "open/open");
		holder.addAction(save, MenuBar.FILE_MENU + "open/save");
		holder.addAction(saveAs, MenuBar.FILE_MENU + "open/saveAs");

		// JMenuItem editItem = holder.addAction(edit,
		// MenuBar.EDIT_MENU+"edit/editItem");
		// editItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getAdjustableProperty("keystroke_edit")));
		// JMenuItem addNewItem = holder.addAction(addNew,
		// MenuBar.EDIT_MENU+"edit/newItem");
		// addNewItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getAdjustableProperty("keystroke_add")));
		// JMenuItem removeItem = holder.addAction(remove,
		// MenuBar.EDIT_MENU+"edit/removeItem");
		// removeItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getAdjustableProperty("keystroke_remove")));
		holder.addAction(evaluate, MenuBar.EDIT_MENU + "edit/evaluate");
		holder.addAction(toggleFolded, MenuBar.EDIT_MENU + "edit/toggleFolded");

	}

	public HookFactory getHookFactory() {
		throw new IllegalArgumentException("Not implemented yet.");
	}

}
