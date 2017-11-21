/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attributes;

import gui.support.CustomTreeNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Olimpia Popica
 * @param <T>
 */
public class CustomTreeModel<T extends Serializable> implements TreeModel {

    /**
     * The root node of the tree.
     */
    private final CustomTreeNode<T> root;

    /**
     * The tree listeners which are updating the tree based on the performed
     * actions.
     */
    private final List<TreeModelListener> treeModelListeners;

    /**
     * Tree related interactions: rename node, add new node, remove node etc.
     */
    public enum TreeActions {
        NODE_RENAMED,
        NODE_INSERTED,
        NODE_REMOVED,
        TREE_STRUCTURE_CHANGED
    }

    /**
     * Creates a new tree model which has the specified node as a root.
     *
     * @param root the nodes which will be the root of the tree model
     */
    public CustomTreeModel(CustomTreeNode root) {
        this.root = root;
        treeModelListeners = new ArrayList<>();
    }

    @Override
    public Object getChild(Object parent, int index) {
        CustomTreeNode p = (CustomTreeNode) parent;

        return p.getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((CustomTreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((CustomTreeNode) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        if (path != null) {
            // update the name of the node, for it has changed
            CustomTreeNode node = (CustomTreeNode) path.getLastPathComponent();
            node.setRoot((T) newValue);

            // fire the corresponding event
            CustomTreeNode parent = node.getParent();
            if (parent != null) {
                fireTreeStructureChanged(parent, new int[]{parent.getIndexOfChild(node)}, new Object[]{node}, TreeActions.NODE_RENAMED);
            }
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        CustomTreeNode p = (CustomTreeNode) parent;
        return p.getIndexOfChild((CustomTreeNode) child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    public void reload() {
        root.removeAllChildren();
        fireTreeStructureChanged(root, null, null, TreeActions.TREE_STRUCTURE_CHANGED);
    }

    /**
     * Generate events specific to the user actions and trigger the internal
     * management of the tree.
     *
     * @param parent the parent node of the changed nodes (rename, add, remove)
     * @param indices the indexes of the changed nodes
     * @param nodes the affected nodes; the nodes which are changed
     * @param action the action related to the node: rename, add, remove
     */
    public void fireTreeStructureChanged(CustomTreeNode parent, int[] indices, Object[] nodes, TreeActions action) {
        TreeModelEvent eventInsRem = new TreeModelEvent(this, parent.getPath(), indices, nodes);
        TreeModelEvent eventStructChange = new TreeModelEvent(this, new Object[]{parent});

        for (TreeModelListener lis : treeModelListeners) {
            switch (action) {
                case NODE_RENAMED:
                    lis.treeNodesChanged(eventInsRem);
                    break;

                case NODE_INSERTED:
                    lis.treeNodesInserted(eventInsRem);
                    break;

                case NODE_REMOVED:
                    lis.treeNodesRemoved(eventInsRem);
                    break;

                case TREE_STRUCTURE_CHANGED:
                    lis.treeStructureChanged(eventStructChange);
                    break;

            }
        }
    }
}
