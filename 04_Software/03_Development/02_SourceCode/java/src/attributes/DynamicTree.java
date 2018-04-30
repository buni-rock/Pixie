/*
 * The MIT License
 *
 * Copyright 2017 Olimpia Popica, Benone Aligica
 *
 * Contact: contact[a(t)]annotate[(d){o}t]zone
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package attributes;

import common.Icons;
import observers.NotifyObservers;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.Observer;

/**
 *
 * @author Olimpia Popica
 */
public class DynamicTree extends JPanel {

    /**
     * The root node of the tree.
     */
    private final gui.support.CustomTreeNode rootNode;

    /**
     * A custom implementation of the tree model for the JTree.
     */
    private final transient CustomTreeModel treeModel;

    /**
     * The visual tree, displaying all the information of the tree.
     */
    private final JTree tree;

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final transient NotifyObservers observable = new NotifyObservers();

    /**
     * The toolkit is used to play a sound notifying the user that its last
     * operation was not successful.
     */
    private final transient Toolkit toolkit = Toolkit.getDefaultToolkit();
    private int treeDepth;

    /**
     * Create a panel on which is displayed a JTree.
     *
     * @param rootName the name of the tree root
     * @param treeDepth the maximum depth of the tree
     */
    public DynamicTree(gui.support.CustomTreeNode rootName, int treeDepth) {
        super(new GridLayout(1, 0));

        this.rootNode = rootName;
        this.treeDepth = treeDepth;

        treeModel = new CustomTreeModel(rootNode);
        tree = new JTree(treeModel);

        configureTree();
    }

    /**
     * Remove all nodes except the root node.
     */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();

        // select the root, in order to have all the time one entry selected
        tree.setSelectionPath(rootNode.getTreePath());
    }

    /**
     * Remove the currently selected node.
     */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();

        if (currentSelection != null) {
            gui.support.CustomTreeNode currentNode = (gui.support.CustomTreeNode) (currentSelection.getLastPathComponent());

            gui.support.CustomTreeNode parent = currentNode.getParent();
            if (parent != null) {
                int index = parent.getIndexOfChild(currentNode);
                parent.removeChild(currentNode);

                // update gui
                treeModel.fireTreeStructureChanged(parent, new int[]{index}, new Object[]{currentNode}, CustomTreeModel.TreeActions.NODE_REMOVED);

                // select a brother, or the parent; it is wanted to have always an entry selected
                if (parent.isLeaf()) {
                    tree.setSelectionPath(parent.getTreePath());
                } else {
                    tree.setSelectionPath(parent.getChildAt(0).getTreePath());
                }
                return;
            }
        }
        // Either there was no selection, or the root was selected.
        toolkit.beep();
    }

    /**
     * Add child to the currently selected node.
     *
     * @param child the node to be added to the selected node
     * @return the node which was inserted in the tree
     */
    public gui.support.CustomTreeNode addObject(String child) {
        gui.support.CustomTreeNode parentNode;
        gui.support.CustomTreeNode childNode;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else // the tree has a limited depth!
        {
            if (parentPath.getPath().length < treeDepth) {
                parentNode = (gui.support.CustomTreeNode) (parentPath.getLastPathComponent());
            } else {
                // Either there was no selection, or the root was selected.
                toolkit.beep();
                return null;
            }
        }

        // add the child to the list of children
        childNode = parentNode.addChild(child);

        // update gui
        treeModel.fireTreeStructureChanged(parentNode,
                new int[]{parentNode.getIndexOfChild(childNode)},
                new Object[]{childNode},
                CustomTreeModel.TreeActions.NODE_INSERTED);

        tree.expandPath(parentNode.getTreePath());
        tree.scrollPathToVisible(childNode.getTreePath());
        return childNode;
    }

    /**
     * Add child to the specified parent node.
     *
     * @param parent the parent to which the node should be added
     * @param child the node to be added to the selected node
     * @return the node which was inserted in the tree
     */
    public gui.support.CustomTreeNode addObject(gui.support.CustomTreeNode parent, String child) {
        if (parent == null) {
            parent = rootNode;
        }

        return parent.addChild(child);
    }

    /**
     * Allows another module to put an observer into the current module.
     *
     * @param o - the observer to be added
     */
    public void addObserver(Observer o) {
        observable.addObserver(o);
    }

    /**
     * Get the data stored in the selected node.
     *
     * @return a string representing the data stored in the selected node
     */
    public String getSelection() {
        if (tree.getSelectionPath() != null) {
            return tree.getSelectionPath().getLastPathComponent().toString();
        }
        return "";
    }

    /**
     * Expand all the rows of the tree.
     */
    public void expandRows() {
        for (int index = 0; index < tree.getRowCount(); index++) {
            tree.expandRow(index);
        }
    }

    /**
     * Collapse all the rows of the tree.
     */
    public void collapseRows() {
        for (int index = 0; index < tree.getRowCount(); index++) {
            tree.collapseRow(index);
        }
    }

    /**
     * Configure the tree specific options wanted in the application and add it
     * to the current panel.
     */
    private void configureTree() {
        tree.setEditable(true);

        tree.setShowsRootHandles(true);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.addTreeSelectionListener((javax.swing.event.TreeSelectionEvent evt) -> observable.notifyObservers());

        changeTreeIcons();

        this.add(new JScrollPane(tree));
    }

    /**
     * Update the tree small icons: expanded/collapsed/leaf.
     */
    private void changeTreeIcons() {
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        renderer.setClosedIcon(Icons.CLOSED_ICON_16X16);
        renderer.setOpenIcon(Icons.OPEN_ICON_16X16);
        renderer.setLeafIcon(Icons.LEAF_ICON_16X16);
    }

    /**
     * Get the root node of the tree.
     *
     * @return the root node of the tree
     */
    public gui.support.CustomTreeNode getRootNode() {
        return rootNode;
    }

    /**
     * Select the root node of the tree.
     */
    public void selectRoot() {
        tree.setSelectionPath(rootNode.getTreePath());
    }

}
