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
 * Created on 12.06.2005
 *
 */
package freemind.view.mindmapview.attributeview;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import freemind.main.Resources;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.AttributeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.attributes.AttributeTableModel;
import freemind.modes.attributes.ColumnWidthChangeEvent;
import freemind.modes.attributes.ColumnWidthChangeListener;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

/**
 * @author dimitri 12.06.2005
 */
public class AttributeTable extends JTable implements ColumnWidthChangeListener {
	private static final int MAX_HEIGTH = 300;
	private static final int MAX_WIDTH = 600;

	static private class MyFocusListener implements FocusListener {
		private AttributeTable focusedTable;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		public void focusGained(final FocusEvent event) {
			final Component source = (Component) event.getSource();
			final Component oppositeComponent = event.getOppositeComponent();
			if (source instanceof AttributeTable) {
				focusedTable = (AttributeTable) source;
			} else {
				focusedTable = (AttributeTable) SwingUtilities
						.getAncestorOfClass(AttributeTable.class, source);
			}
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					if (focusedTable != null) {
						final Component newNodeViewInFocus = SwingUtilities
								.getAncestorOfClass(NodeView.class,
										focusedTable);
						if (newNodeViewInFocus != null) {
							NodeView viewer = (NodeView) newNodeViewInFocus;
							if (viewer != viewer.getMap().getSelected()) {
								viewer.getMap().selectAsTheOnlyOneSelected(
										viewer);
							}
						}
					}
				}
			});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
		 */
		public void focusLost(FocusEvent event) {
			Component oppositeComponent = event.getOppositeComponent();
			Component newTable = SwingUtilities.getAncestorOfClass(
					AttributeTable.class, oppositeComponent);
			if (focusedTable != null && focusedTable != newTable) {
				if (focusedTable.isEditing()) {
					focusedTable.getCellEditor().stopCellEditing();
				}
				if (!focusedTable.attributeView.isPopupShown()) {
					final AttributeView attributeView = focusedTable
							.getAttributeView();
					final String currentAttributeViewType = attributeView
							.getNode().getMap().getRegistry().getAttributes()
							.getAttributeViewType();
					if (attributeView.getViewType() != currentAttributeViewType) {
						attributeView.stateChanged(null);
					}
				}
				focusedTable = null;
			}
		}

	}

	static private class HeaderMouseListener extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			JTableHeader header = (JTableHeader) e.getSource();
			AttributeTable table = (AttributeTable) header.getTable();
			float zoom = table.attributeView.getMapView().getZoom();
			Dimension preferredScrollableViewportSize = table
					.getPreferredScrollableViewportSize();
			JViewport port = (JViewport) table.getParent();
			Dimension extentSize = port.getExtentSize();
			if (preferredScrollableViewportSize.width != extentSize.width) {
				AttributeTableModel model = (AttributeTableModel) table
						.getModel();
				for (int col = 0; col < table.getColumnCount(); col++) {
					int modelColumnWidth = model.getColumnWidth(col);
					int currentColumnWidth = (int) (table.getColumnModel()
							.getColumn(col).getWidth() / zoom);
					if (modelColumnWidth != currentColumnWidth) {
						model.setColumnWidth(col, currentColumnWidth);
					}
				}
			}
		}
	}

	static private MyFocusListener focusListener = new MyFocusListener();
	static private MouseListener componentListener = new HeaderMouseListener();
	private int highRowIndex = 0;
	static private ComboBoxModel defaultComboBoxModel = null;
	static private AttributeTableCellRenderer dtcr = new AttributeTableCellRenderer();
	private AttributeView attributeView;
	private static final int EXTRA_HEIGHT = 4;
	private static final float TABLE_ROW_HEIGHT = 4;
	private static final Dimension prefHeaderSize = new Dimension(1, 8);
	private static Logger mLogger = null;

	AttributeTable(AttributeView attributeView) {
		super();
		if (mLogger == null) {
			mLogger = Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		this.attributeView = attributeView;
		addFocusListener(focusListener);
		final MindMapNode model = attributeView.getNodeView().getModel();
		final AttributeController attributeController = model.getMap()
				.getRegistry().getModeController().getAttributeController();
		if (attributeController != null) {
			getTableHeader().addMouseListener(componentListener);
		} else {
			getTableHeader().setResizingAllowed(false);
		}
		setModel(attributeView.getCurrentAttributeTableModel());
		updateFontSize(this, 1F);
		updateColumnWidths();
		setAutoResizeMode(AUTO_RESIZE_OFF);
		getTableHeader().setReorderingAllowed(false);
		getTableHeader().setPreferredSize(prefHeaderSize);
		int h = getRowHeight();
		updateRowHeights();
		setRowSelectionAllowed(false);
		putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		String text = getValueAt(row, column).toString();
		dtcr.setText(text);
		int prefWidth = dtcr.getPreferredSize().width;
		int width = getColumnModel().getColumn(column).getWidth();
		if (prefWidth > width) {
			dtcr.setToolTipText(text);
		} else {
			dtcr.setToolTipText(null);
		}
		return dtcr;
	}

	public TableCellEditor getCellEditor(int row, int column) {
		JComboBox comboBox = new JComboBox();
		DefaultCellEditor dce = new DefaultCellEditor(comboBox);
		return dce;
	}

	/**
     * 
     */
	public Component prepareEditor(TableCellEditor tce, int row, int col) {
		ComboBoxModel model;
		JComboBox comboBox = (JComboBox) ((DefaultCellEditor) tce)
				.getComponent();
		MindMapNode node = getAttributeTableModel().getNode();
		AttributeRegistry attributes = node.getMap().getRegistry()
				.getAttributes();
		switch (col) {
		case 0:
			model = attributes.getComboBoxModel();
			comboBox.setEditable(!attributes.isRestricted());
			break;
		case 1:
			String attrName = getAttributeTableModel().getValueAt(row, 0)
					.toString();
			model = attributes.getDefaultComboBoxModel(attrName);
			comboBox.setEditable(!attributes.isRestricted(attrName));
			break;
		default:
			model = getDefaultComboBoxModel();
		}
		comboBox.setModel(model);
		model.setSelectedItem(getValueAt(row, col));

		comboBox.addFocusListener(focusListener);
		comboBox.getEditor().getEditorComponent()
				.addFocusListener(focusListener);
		Component editor = super.prepareEditor(tce, row, col);
		updateFontSize(editor, getZoom());
		return editor;
	}

	public Dimension getPreferredScrollableViewportSize() {
		if (!isValid())
			validate();
		float zoom = getZoom();
		Dimension dimension = super.getPreferredSize();
		dimension.width = Math.min((int) (MAX_WIDTH * zoom), dimension.width);
		dimension.height = Math.min((int) (MAX_HEIGTH * zoom)
				- getTableHeaderHeight(), dimension.height);
		return dimension;
	}

	static ComboBoxModel getDefaultComboBoxModel() {
		if (defaultComboBoxModel == null) {
			defaultComboBoxModel = new DefaultComboBoxModel();
		}
		return defaultComboBoxModel;
	}

	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
			boolean extend) {
		int rowCount = getRowCount();
		if (rowCount == 0)
			return;
		if (rowIndex >= rowCount) {
			rowIndex = 0;
			columnIndex = 0;
		}
		changeSelectedRowHeight(rowIndex);
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	private void changeSelectedRowHeight(int rowIndex) {
		if (highRowIndex != rowIndex) {
			if (highRowIndex < getRowCount()) {
				int h = getRowHeight(highRowIndex);
				setRowHeight(highRowIndex, h - EXTRA_HEIGHT);
			}
			int h = getRowHeight(rowIndex);
			setRowHeight(rowIndex, h + EXTRA_HEIGHT);
			highRowIndex = rowIndex;
		}
	}

	/**
     * 
     */
	void updateAttributeTable() {
		updateFontSize(this, 1F);
		updateRowHeights();
		updateColumnWidths();
	}

	private void updateColumnWidths() {
		float zoom = getZoom();
		for (int i = 0; i < 2; i++) {
			int width = (int) (getAttributeTableModel().getColumnWidth(i) * zoom);
			getColumnModel().getColumn(i).setPreferredWidth(width);
		}
	}

	private void updateRowHeights() {
		int rowCount = getRowCount();
		if (rowCount == 0)
			return;

		int constHeight = getTableHeaderHeight() + EXTRA_HEIGHT;
		float zoom = getZoom();
		float fontSize = getFontSize();
		float tableRowHeight = fontSize + zoom * TABLE_ROW_HEIGHT;
		int newHeight = (int) ((tableRowHeight * rowCount + (zoom - 1)
				* constHeight) / rowCount);
		if (newHeight < 1) {
			newHeight = 1;
		}
		int highRowsNumber = (int) ((tableRowHeight - newHeight) * rowCount);
		for (int i = 0; i < highRowsNumber; i++) {
			setRowHeight(i, 1 + newHeight
					+ (i == highRowIndex ? EXTRA_HEIGHT : 0));
		}
		for (int i = highRowsNumber; i < rowCount; i++) {
			setRowHeight(i, newHeight + (i == highRowIndex ? EXTRA_HEIGHT : 0));
		}
	}

	int getTableHeaderHeight() {
		final JTableHeader tableHeader = getTableHeader();
		return tableHeader != null ? tableHeader.getPreferredSize().height : 0;
	}

	private void updateFontSize(Component c, float zoom) {
		// 1) Determine font
		Font font = c.getFont();
		if (font != null) {
			float oldFontSize = font.getSize2D();
			float newFontSize = getFontSize() * zoom;
			if (oldFontSize != newFontSize) {
				font = font.deriveFont(newFontSize);
				c.setFont(font);
			}
		}
	}

	float getZoom() {
		return attributeView.getMapView().getZoom();
	}

	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (getParent() == null)
			return;

		if (e.getType() == TableModelEvent.DELETE
				&& e.getFirstRow() == highRowIndex
				&& e.getFirstRow() == getRowCount() && e.getFirstRow() != 0) {
			changeSelection(e.getFirstRow() - 1, 0, false, false);
		} else {
			updateRowHeights();
		}

		MapView map = getAttributeView().getNodeView().getMap();
		getParent().getParent().invalidate();
		map.getModel().nodeChanged(getAttributeView().getNode());
	}

	public void viewRemoved() {
		getModel().removeTableModelListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#removeNotify()
	 */
	public void removeNotify() {
		// TODO Auto-generated method stub
		super.removeNotify();
	}

	private float getFontSize() {
		return (attributeView.getNodeView().getModel().getMap().getRegistry()
				.getAttributes().getFontSize());
	}

	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
	}

	private void removeListenerFromEditor() {
		JComboBox comboBox = (JComboBox) getEditorComponent();
		comboBox.removeFocusListener(focusListener);
		comboBox.getEditor().getEditorComponent()
				.removeFocusListener(focusListener);
		comboBox.setModel(new DefaultComboBoxModel());
	}

	public void removeEditor() {
		removeListenerFromEditor();
		getAttributeTableModel().editingCanceled();
		super.removeEditor();
	}

	/**
	 * @return Returns the currentModel.
	 */
	public AttributeTableModelDecoratorAdapter getAttributeTableModel() {
		return (AttributeTableModelDecoratorAdapter) getModel();
	}

	public AttributeView getAttributeView() {
		return attributeView;
	}

	/**
     * 
     */
	public void setOptimalColumnWidths() {
		Component comp = null;
		int cellWidth = 0;
		int maxCellWidth = 2 * (int) (Math.ceil(getFontSize()
				+ TABLE_ROW_HEIGHT));
		for (int col = 0; col < 2; col++) {
			for (int row = 0; row < getRowCount(); row++) {
				comp = dtcr.getTableCellRendererComponent(this,
						getValueAt(row, col), false, false, row, col);
				cellWidth = comp.getPreferredSize().width;
				maxCellWidth = Math.max(cellWidth, maxCellWidth);
			}
			getAttributeTableModel().setColumnWidth(col, maxCellWidth + 1);
		}
	}

	/**
     */
	public void insertRow(int row) {
		if (getModel() instanceof ExtendedAttributeTableModelDecorator) {
			ExtendedAttributeTableModelDecorator model = (ExtendedAttributeTableModelDecorator) getModel();
			if (isEditing() && getCellEditor() != null
					&& !getCellEditor().stopCellEditing()) {
				return;
			}
			model.insertRow(row);
			changeSelection(row, 0, false, false);
			if (editCellAt(row, 0))
				getEditorComponent().requestFocus();
		}
	}

	/**
     */
	public void removeRow(int row) {
		if (getModel() instanceof ExtendedAttributeTableModelDecorator) {
			ExtendedAttributeTableModelDecorator model = (ExtendedAttributeTableModelDecorator) getModel();
			model.removeRow(row);
		}
	}

	/**
     */
	public void moveRowUp(int row) {
		if (getModel() instanceof ExtendedAttributeTableModelDecorator) {
			ExtendedAttributeTableModelDecorator model = (ExtendedAttributeTableModelDecorator) getModel();
			model.moveRowUp(row);
		}
	}

	/**
     */
	public void moveRowDown(int row) {
		if (getModel() instanceof ExtendedAttributeTableModelDecorator) {
			ExtendedAttributeTableModelDecorator model = (ExtendedAttributeTableModelDecorator) getModel();
			model.moveRowDown(row);
		}
	}

	public void columnWidthChanged(ColumnWidthChangeEvent event) {
		float zoom = getZoom();
		int col = event.getColumnNumber();
		AttributeTableLayoutModel layoutModel = (AttributeTableLayoutModel) event
				.getSource();
		int width = layoutModel.getColumnWidth(col);
		getColumnModel().getColumn(col).setPreferredWidth((int) (width * zoom));
		getAttributeView().getNode().getMap()
				.nodeChanged(getAttributeView().getNode());
	}

	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
			int condition, boolean pressed) {
		mLogger.info("AttributeTable.processKeyBinding");
		if (ks.getKeyCode() == KeyEvent.VK_TAB && e.getModifiers() == 0
				&& pressed && getSelectedColumn() == 1
				&& getSelectedRow() == getRowCount() - 1
				&& getModel() instanceof ExtendedAttributeTableModelDecorator) {
			insertRow(getRowCount());
			return true;
		}
		if (ks.getKeyCode() == KeyEvent.VK_ESCAPE && e.getModifiers() == 0
				&& pressed) {
			attributeView.getNodeView().requestFocus();
			return true;
		}
		boolean retValue = super.processKeyBinding(ks, e, condition, pressed);
		// Start editing when a key is typed. UI classes can disable this
		// behavior
		// by setting the client property JTable.autoStartsEdit to
		// Boolean.FALSE.
		if (!retValue
				&& condition == WHEN_FOCUSED
				&& isFocusOwner()
				&& ks.getKeyCode() != KeyEvent.VK_TAB
				&& e != null
				&& e.getID() == KeyEvent.KEY_PRESSED
				&& !e.isActionKey()
				&& e.getKeyChar() != KeyEvent.CHAR_UNDEFINED
				&& 0 == (e.getModifiers() & (KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK))) {
			// We do not have a binding for the event.
			// Try to install the editor
			int leadRow = getSelectionModel().getLeadSelectionIndex();
			int leadColumn = getColumnModel().getSelectionModel()
					.getLeadSelectionIndex();
			if (leadRow != -1 && leadColumn != -1 && !isEditing()) {
				if (!editCellAt(leadRow, leadColumn)) {
					return false;
				}
			}
			Component editorComponent = getEditorComponent();
			// If the editorComponent is a JComboBox, pass the event to it.
			if (editorComponent instanceof JComboBox) {
				JComboBox comboBox = (JComboBox) editorComponent;
				if (comboBox.isEditable()) {
					ComboBoxEditor editor = comboBox.getEditor();
					editor.selectAll(); // to enable overwrite
					KeyEvent keyEv;
					keyEv = new KeyEvent(editor.getEditorComponent(),
							KeyEvent.KEY_TYPED, e.getWhen(), e.getModifiers(),
							KeyEvent.VK_UNDEFINED, e.getKeyChar(),
							KeyEvent.KEY_LOCATION_UNKNOWN);
					retValue = SwingUtilities.processKeyBindings(keyEv);
				} else {
					editorComponent.requestFocus();
					retValue = true;
				}
			}
		}
		if (ks.getKeyCode() == KeyEvent.VK_SPACE) {
			return true;
		}
		return retValue;
	}

	public boolean isVisible() {
		return attributeView.areAttributesVisible();
	}
}
