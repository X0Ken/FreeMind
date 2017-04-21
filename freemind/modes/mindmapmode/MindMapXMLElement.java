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


package freemind.modes.mindmapmode;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Vector;

import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.modes.ArrowLinkAdapter;
import freemind.modes.ArrowLinkTarget;
import freemind.modes.CloudAdapter;
import freemind.modes.EdgeAdapter;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.XMLElementAdapter;

public class MindMapXMLElement extends XMLElementAdapter {

	// Logging:
	private static java.util.logging.Logger logger;

	public MindMapXMLElement(ModeController pModeController) {
		super(pModeController);
		init();
	}

	protected MindMapXMLElement(ModeController pModeController,
			Vector ArrowLinkAdapters, HashMap IDToTarget) {
		super(pModeController, ArrowLinkAdapters, IDToTarget);
		init();
	}

	/**
     *
     */
	private void init() {
		if (logger == null) {
			logger = getFrame().getLogger(this.getClass().getName());
		}
	}

	/** abstract method to create elements of my type (factory). */
	protected XMLElement createAnotherElement() {
		// We do not need to initialize the things of XMLElement.
		return new MindMapXMLElement(mModeController, mArrowLinkAdapters,
				mIdToTarget);
	}

	protected NodeAdapter createNodeAdapter(FreeMindMain frame, String nodeClass) {
		if (nodeClass == null) {
			return new MindMapNodeModel(frame, getMap());
		}
		// reflection:
		try {
			// construct class loader:
			ClassLoader loader = this.getClass().getClassLoader();
			// constructed.
			Class nodeJavaClass = Class.forName(nodeClass, true, loader);
			Class[] constrArgs = new Class[] { Object.class,
					FreeMindMain.class, MindMap.class };
			Object[] constrObjs = new Object[] { null, frame, getMap() };
			Constructor constructor = nodeJavaClass.getConstructor(constrArgs);
			NodeAdapter nodeImplementor = (NodeAdapter) constructor
					.newInstance(constrObjs);
			return nodeImplementor;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e,
					"Error occurred loading node implementor: " + nodeClass);
			// the best we can do is to return the normal class:
			NodeAdapter node = new MindMapNodeModel(frame, getMap());
			return node;
		}
	}

	protected EdgeAdapter createEdgeAdapter(NodeAdapter node, FreeMindMain frame) {
		return new MindMapEdgeModel(node, frame);
	}

	protected CloudAdapter createCloudAdapter(NodeAdapter node,
			FreeMindMain frame) {
		return new MindMapCloudModel(node, frame);
	}

	protected ArrowLinkAdapter createArrowLinkAdapter(NodeAdapter source,
			NodeAdapter target, FreeMindMain frame) {
		return new MindMapArrowLinkModel(source, target, frame);
	}

	protected ArrowLinkTarget createArrowLinkTarget(NodeAdapter source,
			NodeAdapter target, FreeMindMain frame) {
		return new ArrowLinkTarget(source, target, frame);
	}
	
	protected NodeAdapter createEncryptedNode(String additionalInfo) {
		NodeAdapter node = createNodeAdapter(frame,
				EncryptedMindMapNode.class.getName());
		setUserObject(node);
		copyAttributesToNode(node);
		node.setAdditionalInfo(additionalInfo);
		return node;
	}

}
