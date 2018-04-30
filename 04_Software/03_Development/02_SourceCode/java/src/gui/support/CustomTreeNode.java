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
package gui.support;

import java.io.Serializable;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A tree implementation allowing simple tree interactions.
 *
 * @author Olimpia Popica
 * @param <T> the data type stored in the tree
 */
public class CustomTreeNode<T extends Serializable> implements Serializable {

    /**
     * Serial class version in form of MAJOR_MINOR_BUGFIX_DAY_MONTH_YEAR
     */
    private static final long serialVersionUID = 0x00_03_01_11_09_2017L;

    /**
     * The data saved in the root of the node.
     */
    private T root;

    /**
     * The parent tree of the current node.
     */
    private CustomTreeNode parent;

    /**
     * The children of the node.
     */
    private List<CustomTreeNode> children;

    /**
     * Create a new node or a new tree.
     *
     * @param root the data saved in the root of the node
     */
    public CustomTreeNode(T root) {
        this.root = root;
        this.children = new LinkedList<>();
    }

    /**
     * Return the data saved in the current node.
     *
     * @return the data saved in the root of the node
     */
    public T getRoot() {
        return root;
    }

    /**
     * Set the data of the current node.
     *
     * @param root the new data of the current node
     */
    public void setRoot(T root) {
        this.root = root;
    }

    /**
     * Get the parent of the current node.
     *
     * @return the parent node of the current one
     */
    public CustomTreeNode getParent() {
        return parent;
    }

    /**
     * Set the parent of the current node.
     *
     * @param parent the parent node of the current one
     */
    private void setParent(CustomTreeNode parent) {
        this.parent = parent;
    }

    /**
     * Add a child to the current node.
     *
     * @param child the data saved in the root of the child node
     * @return the newly created child
     */
    public CustomTreeNode addChild(T child) {
        CustomTreeNode childNode = new CustomTreeNode(child);
        childNode.parent = this;
        this.children.add(childNode);

        return childNode;
    }

    /**
     * Add a child to the current node.
     *
     * @param child the child node to be added to the current one
     */
    public void addChild(CustomTreeNode child) {
        child.setParent(this);
        this.children.add(child);
    }

    /**
     * Add a list of children nodes to the current node.
     *
     * @param children the list of children nodes to be added to the current one
     */
    public void addChildren(List<CustomTreeNode> children) {
        for (CustomTreeNode child : children) {
            child.setParent(this);
        }
        this.children.addAll(children);
    }

    /**
     * Add a list of strings as children of the current node.
     *
     * @param children the list of children strings to be added to the current
     * one (the nodes are created based on the list of input strings)
     */
    public void addChildrenStr(List<T> children) {
        for (T child : children) {
            addChild(child);
        }
    }

    /**
     * Return the child node with the given root name.
     *
     * @param rootName the data saved in the root of the searched node
     * @return the child node of the current one, containing the given root node
     */
    public CustomTreeNode getChild(T rootName) {
        for (CustomTreeNode child : children) {
            if (rootName.equals(child.getRoot())) {
                return child;
            }
        }
        return null;
    }

    /**
     * Return the child node with the given root name.
     *
     * @param rootName the string saved in the root of the searched node
     * @return the child node of the current one, containing the given root node
     */
    public CustomTreeNode getChild(String rootName) {
        for (CustomTreeNode child : children) {
            if (rootName.equalsIgnoreCase(child.getRoot().toString())) {
                return child;
            }
        }
        return null;
    }

    /**
     * Build a list of children, containing just the data from the nodes.
     *
     * @return a list containing the data of all children nodes
     */
    public List<T> getChildren() {
        List<T> childrenList = new ArrayList<>();

        children.stream().forEach(child -> childrenList.add((T) child.root));

        return childrenList;
    }

    /**
     * Build a list of children nodes.
     *
     * @return a list containing the children nodes
     */
    public List<CustomTreeNode<T>> getChildrenNodes() {
        List<CustomTreeNode<T>> childrenList = new ArrayList<>();

        children.stream().forEach(child -> childrenList.add(child));

        return childrenList;
    }

    /**
     * Remove the specified child.
     *
     * @param child the child node to be removed
     */
    public void removeChild(CustomTreeNode child) {
        children.remove(child);
    }

    /**
     * Remove all the children of the current node.
     */
    public void removeAllChildren() {
        children = new LinkedList();
    }

    /**
     * Check the list of children and return the index of the wanted child.
     *
     * @param child the child for which the index is wanted
     * @return the index of the child, in the list of children of the current
     * node
     */
    public int getIndexOfChild(CustomTreeNode child) {
        return children.indexOf(child);
    }

    /**
     * Returns the child from the specified index, from the list of children of
     * the current node.
     *
     * @param index the index of the wanted child
     * @return a node representing the child of the current node, with the
     * specified index
     */
    public CustomTreeNode getChildAt(int index) {
        return children.get(index);
    }

    /**
     * Get the number of children of the current node.
     *
     * @return the number of children of the current node
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Check if the current node is final / leaf.
     *
     * @return true if the current nod is final / has no children; false
     * otherwise
     */
    public boolean isLeaf() {
        return (children.isEmpty());
    }

    /**
     * Returns the path from the root to this node. The last element in the path
     * is the current node and the first one is the root of the tree.
     *
     * @return an object array of CustomTreeNode objects specifying the path
     * from the root node to the current node (where the first element in the
     * path is the tree root and the last element is the current node)
     */
    public Object[] getPath() {
        List path = new ArrayList();
        // add the current node
        path.add(this);

        // add all the other nodes, till the tree root
        CustomTreeNode father = getParent();
        while (father != null) {
            path.add(father);
            father = father.getParent();
        }

        // reverse the list because the tree node has to be first and the current node, last
        Collections.reverse(path);

        return path.toArray();
    }

    /**
     * Returns the path from the root to this node. The last element in the path
     * is the current node and the first one is the root of the tree.
     *
     * @return the TreePath from the tree root to the current node
     */
    public TreePath getTreePath() {
        return new TreePath(getPath());
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
