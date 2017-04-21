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
 * Created on 12.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter.condition;

import freemind.controller.Controller;
import freemind.main.Resources;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.AttributeTableModel;

/**
 * @author Dimitri Polivaev 12.07.2005
 */
public class AttributeNotExistsCondition extends NodeCondition {
	static final String ATTRIBUTE = "attribute";
	static final String NAME = "attribute_not_exists_condition";
	private String attribute;

	/**
     */
	public AttributeNotExistsCondition(String attribute) {
		super();
		this.attribute = attribute;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.filter.condition.Condition#checkNode(freemind.modes
	 * .MindMapNode)
	 */
	public boolean checkNode(Controller c, MindMapNode node) {
		AttributeTableModel attributes = node.getAttributes();
		for (int i = 0; i < attributes.getRowCount(); i++) {
			if (attributes.getValueAt(i, 0).equals(attribute))
				return false;
		}
		return true;
	}

	public void save(XMLElement element) {
		XMLElement child = new XMLElement();
		child.setName(NAME);
		super.saveAttributes(child);
		child.setAttribute(ATTRIBUTE, attribute);
		element.addChild(child);
	}

	static Condition load(XMLElement element) {
		return new AttributeNotExistsCondition(
				element.getStringAttribute(ATTRIBUTE));
	}

	protected String createDesctiption() {
		final String simpleCondition = Resources.getInstance()
				.getResourceString(ConditionFactory.FILTER_DOES_NOT_EXIST);
		return ConditionFactory.createDescription(attribute, simpleCondition,
				null, false);
	}
}
