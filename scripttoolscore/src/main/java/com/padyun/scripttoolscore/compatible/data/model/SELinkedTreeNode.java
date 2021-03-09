package com.padyun.scripttoolscore.compatible.data.model;

/**
 * Created by daiepngfei on 2/16/19
 */
public class SELinkedTreeNode<D> {
    private D data;
    private boolean isEntity;
    private boolean isMoveable;
    private boolean isDeleteable;
    private boolean isTail;
    private boolean ishead;
    private int orderPosition, childrenOrderPosition;
    private SELinkedTreeNode<D> parentNode, childrenHead, childrenTail;
    private SELinkedTreeNode<D> orderedPreNode, orderedNextNode;

    public boolean isRoot() {
        return parentNode == null;
    }

    public boolean isLeaf() {
        return childrenHead == null;
    }

    public SELinkedTreeNode<D> getParentNode() {
        return parentNode;
    }

    public  void getBranchNodes() {

    }

    public   void getRootNode() {
    }

    public   void getOrderedNodes() {
    }

    public void getOrderedNodesInner() {
    }

    public void orderNodes() {
    }

    public   void addNode() {
    }

    public  void resetThesePositions() {
    }

    public   void delete() {
    }

    public  void getOrderedEndNodePosition() {
    }

    public void isOrdered() {
    }

    public void getOrderedNodePosition() {
    }

    public  void getNodePosition() {
    }

    public void positionOf() {
    }

    public D getData() {
        return data;
    }

}
