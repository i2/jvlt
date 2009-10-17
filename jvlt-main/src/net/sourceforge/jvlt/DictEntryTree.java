package net.sourceforge.jvlt;

import java.awt.Component;
import java.awt.Font;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

class DictEntryTree extends JTree {
	private static final long serialVersionUID = 1L;
	
	private static class TreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;

		private static final Font orth_font =
			JVLT.getConfig().getFontProperty("ui_orth_font");
		private static final Font pron_font =
			JVLT.getConfig().getFontProperty("ui_pron_font");
		
		public Component getTreeCellRendererComponent(
				JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			
			if (node.getUserObject() instanceof Entry) {
				Entry entry = (Entry) node.getUserObject();
				
				StringBuffer html_buffer = new StringBuffer();
				html_buffer.append("<html>");
				if (orth_font == null) {
					html_buffer.append("<span>");
				} else {
					html_buffer.append(
							"<span style=\"font-family:" + orth_font.getFamily()
							+ "; font-size: " + orth_font.getSize() + "\">");
				}
				html_buffer.append(entry.getOrthography());
				html_buffer.append(" </span>");
				if (pron_font == null) {
					html_buffer.append("<span>");
				} else {
					html_buffer.append(
							"<span style=\"font-family:" + pron_font.getFamily()
							+ "; font-size: " + pron_font.getSize() + "\">");
				}
				html_buffer.append("("
						+ Utils.arrayToString(entry.getPronunciations()) + ")");
				html_buffer.append("</span>");
				html_buffer.append("</html>");
				
				return super.getTreeCellRendererComponent(
						tree, html_buffer.toString(),
						selected, expanded, leaf, row, hasFocus);
			} else
				return super.getTreeCellRendererComponent(
						tree, value,
						selected, expanded, leaf, row, hasFocus);
		}
	}
	
	public DictEntryTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		DefaultTreeModel model = new DefaultTreeModel(root);
		setModel (model);
		setRootVisible(false);
		setLargeModel(true);
		
		setCellRenderer(new TreeCellRenderer());
	}
	
	public Entry[] getEntries()	{
		ArrayList<Entry> entries = new ArrayList<Entry>();
		TreeModel model = getModel();
		Object root = model.getRoot();
		int child_count = model.getChildCount(root);
		for (int i=0; i<child_count; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				model.getChild(root, i);
			entries.add((Entry) node.getUserObject());
		}
		
		return entries.toArray(new Entry[0]);
	}
	
	public boolean containsEntry(Entry entry) {
		return (getEntryNode(entry) != null);
	}
	
	public boolean containsSense(Sense sense) {
		return (getSenseNode(sense) != null);
	}
	
	public Entry getSelectedEntry() {
		Object obj = getSelectedObject();
		if (obj == null)
			return null;
		
		if (obj instanceof Entry)
			return (Entry) obj;
		else
			return null;
	}
	
	public Sense getSelectedSense()	{
		Object obj = getSelectedObject();
		if (obj == null)
			return null;
		
		if (obj instanceof Sense)
			return (Sense) obj;
		else
			return null;
	}
	
	public void setSelectedSense (Sense sense){
		DefaultMutableTreeNode node = getSenseNode (sense);
		if (node != null)
			setSelectedNode(node);
	}
	
	public void setSelectedEntry (Entry entry) {
		DefaultMutableTreeNode node = getEntryNode (entry);
		if (node != null)
			setSelectedNode(node);
	}
	
	public void setSenses(Sense[] senses) {
		if (senses.length == 0)
			return;
		
		clear();

		TreeSet<Entry> entryset = new TreeSet<Entry>();
		for (int i=0; i<senses.length; i++)
			entryset.add(senses[i].getParent());

		ArrayList<DefaultMutableTreeNode> entry_nodes =
			new ArrayList<DefaultMutableTreeNode>();
		Iterator<Entry> eit=entryset.iterator();
		while (eit.hasNext()) {
			Entry entry = eit.next();
			DefaultMutableTreeNode entrynode = addObject (
				entry, (DefaultMutableTreeNode) getModel().getRoot());
			entry_nodes.add(entrynode);
			
			Sense[] esenses = entry.getSenses();
			for (int i=0; i<esenses.length; i++)
				for (int j=0; j<senses.length; j++)
					if (senses[j] == esenses[i]) // "pointer" equality.
						addObject(esenses[i], entrynode);
		}
		
		expandAll(true);
	}
	
	public void removeSense (Sense sense) {
		DefaultMutableTreeNode node = getSenseNode(sense);
		if (node == null)
			return;
		
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		DefaultMutableTreeNode entry_node = (DefaultMutableTreeNode)
			node.getParent();
		model.removeNodeFromParent(node);
		if (model.getChildCount(entry_node) == 0)
			model.removeNodeFromParent(entry_node);
		updateUI();
	}
	
	public void setEntries (Entry[] entries) {
		clear();
		
		for (int i=0; i<entries.length; i++)
			addEntry(entries[i]);
	}

	public void addEntry(Entry entry) {
		DefaultMutableTreeNode entrynode = addObject(
				entry, (DefaultMutableTreeNode) getModel().getRoot());
			
		Sense[] senses = entry.getSenses();
		for (int j=0; j<senses.length; j++)
			addObject(senses[j], entrynode);
		
		expandPath(new TreePath(entrynode.getPath()));
	}

	public void removeEntry(Entry entry) {
		DefaultMutableTreeNode node = getEntryNode(entry);
		if (node == null)
			return;

		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.removeNodeFromParent(node);
		updateUI();
	}
	
	public void clear()	{
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
			
		root.removeAllChildren();
		model.reload();
	}
	
	public Object getSelectedObject() {
		TreePath path = getSelectionPath();
		if (path==null)
			return null;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			path.getLastPathComponent();
			
		return node.getUserObject();
	}
	
	private DefaultMutableTreeNode getSenseNode (Sense sense) {
		TreeModel model = getModel();
		Object root = model.getRoot();
		int entry_count = model.getChildCount(root);
		for (int i=0; i<entry_count; i++) {
			Object child = model.getChild(root, i);
			int sense_count = model.getChildCount(child);
			for (int j=0; j<sense_count; j++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					model.getChild(child, j);
				if (node.getUserObject() == sense)
						return node;
			}
		}
		
		return null;
	}
	
	private DefaultMutableTreeNode getEntryNode (Entry entry) {
		TreeModel model = getModel();
		Object root = model.getRoot();
		int child_count = model.getChildCount(root);
		for (int i=0; i<child_count; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				model.getChild(root, i);
			if (node.getUserObject() == entry)
				return node;
		}
		
		return null;
	}
	
	private DefaultMutableTreeNode addObject (
		Object object, DefaultMutableTreeNode parent) {
		DefaultTreeModel model = (DefaultTreeModel) getModel();

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(object);
		model.insertNodeInto(node, parent, parent.getChildCount());
		//parent.add(node);
		
		return node;
	}
	
	private void setSelectedNode(DefaultMutableTreeNode node) {
		TreePath path = new TreePath(node.getPath());
		setSelectionPath(path);
		scrollPathToVisible(path);
	}
	
	private void expandAll(boolean expand) {
		DefaultMutableTreeNode root =
			(DefaultMutableTreeNode) getModel().getRoot();
	
		// Traverse tree from root
		expandAll(new TreePath(root), expand);
	}
	
	private void expandAll(TreePath parent, boolean expand) {
		// Traverse children
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (int i=0; i<node.getChildCount(); i++) {
				DefaultMutableTreeNode n =
					(DefaultMutableTreeNode) node.getChildAt(i);
				TreePath path = parent.pathByAddingChild(n);
				expandAll(path, expand);
			}
		}
	
		// Expansion or collapse must be done bottom-up
		if (expand)
			expandPath(parent);
		else
			collapsePath(parent);
	}
}

