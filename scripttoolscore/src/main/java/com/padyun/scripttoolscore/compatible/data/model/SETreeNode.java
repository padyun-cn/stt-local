package com.padyun.scripttoolscore.compatible.data.model;


import com.uls.utilites.un.Useless;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by daiepngfei on 1/12/19
 */
@SuppressWarnings({"unused", "WeakerAccess", "BooleanMethodIsAlwaysInverted", "unchecked"})
public final class SETreeNode<D> implements Cloneable{
//    private List<SETreeNode<D>> childrenNodes;
//    private SETreeNode<D> parentNode;
//    private List<SETreeNode<D>> orderedNodes = new ArrayList<>();
//
//    private D data;
//    private int layer;
//    private SETreeNode<D> tailLeaf;
//    private SETreeNode<D> headLeaf;
//        private static final AtomicLong ID = new AtomicLong(0);
//    private boolean isRemoveable = true;
//    private boolean isVisibile = true;
//
//    public static void main(String args[]) {
//        SETreeNode<String> root = new SETreeNode<>("root0-0");
//        SETreeNode<String> l10 = new SETreeNode<>("layer1-0");
//        SETreeNode<String> l11 = new SETreeNode<>("layer1-1");
//        SETreeNode<String> l12 = new SETreeNode<>("layer1-2");
//        SETreeNode<String> l13 = new SETreeNode<>("layer1-3");
//        root.addChildrenNode(l10);
//        root.addChildrenNode(l11);
//        root.addChildrenNode(l12);
//        root.addChildrenNode(l13);
//        l12.addChildrenNode(new SETreeNode<>("layer1-2:2-0"));
//
//        root.print();
//
//        System.out.println("=========");
//        SETreeNode<String> l14 = new SETreeNode<>("layer1-4");
//        SETreeNode<String> l20 = new SETreeNode<>("layer1-0:2-0");
//        SETreeNode<String> l21 = new SETreeNode<>("layer1-0:2-1");
//        l20.addChildrenNode(new SETreeNode<>("layer1-0:3-0"));
//        l10.addChildrenNode(l21);
//        l10.addChildrenNode(l20);
//
//        root.print();
//        System.out.println("=========2");
//        SETreeNode<String> last = new SETreeNode<>("hehe").addChildrenNode(new SETreeNode<>("hehe1").addChildrenNode(new SETreeNode<>("hehe2").addChildrenNode(new SETreeNode<>("hehe3"))));
//        root.addChildrenNode(last);
//        root.print();
//        System.out.println("=========3");
//        l10.delete();
//
//        root.print();
//    }
//
//    private void print() {
//        List<SETreeNode<D>> orderedNodes = getOrderedNodes();
//        for (SETreeNode<D> d : orderedNodes) {
//            System.out.println("layer:" + d.layer + /*", pos:" + d.getOrderedNodePosition() + ", end: " + (d.getOrderedEndNodePosition() + 1) +*/ ", data: " + d.data.toString());
//        }
//
//    }
//
//    public SETreeNode(D d) {
//        this.data = d;
//        ID.incrementAndGet();
//        if (INodeDataChangedListener.class.isInstance(d)) {
//            setOnDataChangedListener((INodeDataChangedListener<D>) d);
//        }
//    }
//
//    public SETreeNode(D d, boolean removeable) {
//        this(d);
//        setRemoveable(removeable);
//    }
//
//    public void setRemoveable(boolean removeable) {
//        this.isRemoveable = removeable;
//    }
//
//    public boolean isRemoveable() {
//        return isRemoveable;
//    }
//
//    public long getId() {
//        return ID.longValue();
//    }
//
//    public boolean isRoot() {
//        return parentNode == null;
//    }
//
//    public boolean isLeaf() {
//        return childrenNodes == null || childrenNodes.size() == 0;
//    }
//
//    public SETreeNode<D> getParentNode() {
//        return parentNode;
//    }
//
//    public List<SETreeNode<D>> getChildrenNodes() {
//        return childrenNodes;
//    }
//
//    public boolean isSelfVisibile() {
//        return isVisibile;
//    }
//
//    public void collapseSetVisible() {
//        final List<SETreeNode<D>> nodes = getCurrentNodeSelfAndAllSubOrderedNodes();
//        nodes.remove(0);
//        Useless.foreach(nodes, t -> t.isVisibile = false);
//    }
//
//    public void setNodeSelfAndNextSubsVisible(boolean visibile) {
//        this.isVisibile = visibile;
//        if (this.headLeaf != null) this.headLeaf.isVisibile = visibile;
//        if (this.tailLeaf != null) this.tailLeaf.isVisibile = visibile;
//        Useless.foreach(childrenNodes, t -> t.isVisibile = visibile);
//    }
//
//    /**
//     * trash codes
//     *
//     */
//    private void collapseReorder() {
//        final boolean visibile = isVisibile;
//        final List<SETreeNode<D>> nodes = getCurrentNodeSelfAndAllSubOrderedNodes(true);
//        final List<SETreeNode<D>> orderedNodes = getOrderedNodes();
//        final int orderedIndex = orderedNodes.indexOf(this);
//        nodes.remove(0);
//        orderedNodes.removeAll(nodes);
//
//        /*if (!orderSelf)
//
//        if (visibile) orderedNodes.addAll(orderedIndex + (orderSelf ? 0 : 1), nodes);*/
//
//
//    }
//
//
//    public SETreeNode<D> getRootNode() {
//        SETreeNode<D> node = this;
//        while (!node.isRoot()) {
//            node = node.parentNode;
//        }
//        return node;
//    }
//
//    public void setTailLeaf(SETreeNode<D> tailLeaf) {
//
//        if (tailLeaf == null) {
//            throw new NullPointerException();
//        }
//        if (!tailLeaf.isRoot() || !tailLeaf.isLeaf()) {
//            throw new IllegalArgumentException();
//        }
//
//        int index;
//        if (this.tailLeaf != null) {
//            index = getOrderedNodes().indexOf(this.tailLeaf);
//            getOrderedNodes().remove(this.tailLeaf);
//        } else {
//            index = fastFindOrderEndPosition() + 1;
//        }
//        this.tailLeaf = tailLeaf;
//        this.tailLeaf.parentNode = this;
//        getOrderedNodes().add(Math.min(getOrderedNodes().size(), index), this.tailLeaf);
//
//    }
//
//    public void setHeadLeaf(SETreeNode<D> head) {
//
//        if (head == null) {
//            throw new NullPointerException();
//        }
//        if (!head.isRoot() || !head.isLeaf()) {
//            throw new IllegalArgumentException();
//        }
//
//        int index;
//        if (this.headLeaf != null) {
//            index = getOrderedNodes().indexOf(this.headLeaf);
//            getOrderedNodes().remove(this.headLeaf);
//        } else {
//            index = getOrderStartPosition() + 1;
//        }
//        this.headLeaf = head;
//        this.headLeaf.parentNode = this;
//        getOrderedNodes().add(Math.min(getOrderedNodes().size(), index), this.headLeaf);
//
//    }
//
//
//    public List<SETreeNode<D>> getOrderedNodes() {
//        return getOrderedNodesInner(false);
//    }
//
//    private List<SETreeNode<D>> getOrderedNodesInner(boolean copyed) {
//        if (!isRoot()) {
//            return getRootNode().getOrderedNodesInner(copyed);
//        }
//        if (orderedNodes.size() == 0) {
//            orderNodes(orderedNodes, 0);
//        }
//        return copyed ? new ArrayList<>(orderedNodes) : orderedNodes;
//    }
//
//    /*void orderNodes(List<SETreeNode<D>> nodes, int pos) {
//        nodes.add(Math.min(pos, nodes.size()), this);
//        if (this.headLeaf != null) nodes.add(this.headLeaf);
//        if (!isLeaf()) {
//            for (final SETreeNode<D> node : childrenNodes) {
//                if (node == null) continue;
//                node.orderNodes(nodes, nodes.size());
//            }
//        }
//        if (this.tailLeaf != null) nodes.add(this.tailLeaf);
//    }*/
//
//    void orderNodes(List<SETreeNode<D>> nodes, int pos) {
//        orderNodes(nodes, pos, null);
//    }
//
////    void orderNodes(List<SETreeNode<D>> nodes, int pos, Filter<SETreeNode<D>> noneSelffilter) {
////        nodes.add(Math.min(pos, nodes.size()), this);
////        addOrderedNodesWithFilter(nodes, headLeaf, noneSelffilter);
////        if (!isLeaf()) {
////            Useless.foreach(childrenNodes, n -> n.orderNodes(nodes, nodes.size()));
////        }
////        addOrderedNodesWithFilter(nodes, tailLeaf, noneSelffilter);
////    }
////
////    private void addOrderedNodesWithFilter(List<SETreeNode<D>> nodes, SETreeNode<D> node, Filter<SETreeNode<D>> noneSelffilter) {
////        addOrderedNodesWithFilter(nodes, node, -1, noneSelffilter);
////    }
////
////    private void addOrderedNodesWithFilter(List<SETreeNode<D>> nodes, SETreeNode<D> node, int pos, Filter<SETreeNode<D>> noneSelffilter) {
////        if (Useless.nulls(nodes, node)) return;
////        if (noneSelffilter == null || noneSelffilter.filter(node)) {
////            nodes.add(pos < 0 || pos > nodes.size() ? nodes.size() : pos, node);
////        }
////    }
//
//    public void resetData(D d) {
//        D old = this.data;
//        this.data = d;
//        if (getParentNode() != null)
//            getParentNode().notifyDataChanged(d, old, INodeDataChangedListener.ACTION_MODIFIED);
//    }
//
//    public SETreeNode<D> addBranchNode(SETreeNode<D> node) {
//        return addBranchNode(node, true);
//    }
//
//    public SETreeNode<D> addBranchNode(SETreeNode<D> node, boolean notify) {
//        if (!isRoot()) getParentNode().addChildrenNode(node, notify);
//        return this;
//    }
//
//    public SETreeNode<D> addChildrenNode(SETreeNode<D> node) {
//        return addChildrenNode(node, true);
//    }
//
//    public int getChildrenCount() {
//        return childrenNodes == null ? 0 : childrenNodes.size();
//    }
//
//    public SETreeNode<D> addChildrenNode(SETreeNode<D> node, boolean notify) {
//        return addChildrenNode(getChildrenCount() - 1, node, notify);
//    }
//
//    public SETreeNode<D> addChildrenNode(SETreeNode<D> afterChild, SETreeNode<D> node, boolean notify) {
//        if(childrenNodes != null && childrenNodes.contains(afterChild)) {
//            return addChildrenNode(childrenNodes.indexOf(afterChild), node, notify);
//        }
//        return null;
//    }
//
//    public SETreeNode<D> addChildrenNode(int index, SETreeNode<D> node, boolean notify) {
//
//        // null check
//        if (node == null) {
//            throw new NullPointerException("Null Node is not supportted to be added in to nodes!");
//        }
//
//        // root check
//        if (!node.isRoot()) {
//            throw new IllegalStateException("Parameter Node already has a preant Node!");
//        }
//
//        // recursely add
//        if (node == this) {
//            throw new IllegalStateException("Please do not add node recursely!");
//        }
//
//        if (isTailLeaf()) {
//            throw new IllegalStateException("Tail Leaf can not add child or branch!");
//        }
//
//        // opengl_render_init default array-list for branch nodes
//        if (childrenNodes == null) {
//            childrenNodes = new ArrayList<>();
//        }
//
//        // check if need re-order target nodes
//        final List<SETreeNode<D>> orderedNodes = getOrderedNodesInner(false);
//        // re-order target nodes
//        final List<SETreeNode<D>> targetOrderedNodes = new ArrayList<>();
//        final SETreeNode<D> indexedNode = childrenNodes.size() == 0 ? null : childrenNodes.get(index);
//        final int endPosition = (indexedNode == null ? getOrderedNodes().indexOf(this) : Math.max(indexedNode.getOrderedEndPosition(), 0)) + 1;
//        final int targetOrderedPosition = Math.max(0, endPosition);
//        node.orderNodes(targetOrderedNodes, targetOrderedPosition);
//
//        // add re-ordered target nodes into current ordered nodes
//        orderedNodes.addAll(Math.min(targetOrderedPosition, orderedNodes.size()), targetOrderedNodes);
//
//        // add node into branch nodes
//        node.parentNode = this;
//        childrenNodes.add(Useless.limitInRange(index + 1, 0, childrenNodes.size()), node);
//        if (notify) notifyDataChanged(node.data, null, INodeDataChangedListener.ACTION_ADD);
//        return this;
//    }
//
//    private int getOrderStartPosition() {
//        return getOrderedNodes().indexOf(this);
//    }
//
//    private int getOrderedEndPosition() {
//        int position = -1;
//        List<SETreeNode<D>> allNodes = getOrderedNodes();
//        List<SETreeNode<D>> nodes = getCurrentNodeSelfAndAllSubOrderedNodes();
//        if (!Useless.isEmpty(nodes)) {
//            for (int i = nodes.size() - 1; i >= 0; i--) {
//                SETreeNode<D> n = nodes.get(i);
//                if (n == null /*|| n == this.tailLeaf*/) continue;
//                position = allNodes.indexOf(nodes.get(i));
//                if (position >= 0) break;
//            }
//        }
//        return position;
//    }
//
//    @Deprecated
//    private int fastFindOrderEndPosition() {
//        List<SETreeNode<D>> nodes = getOrderedNodes();
//        if (this.tailLeaf != null) {
//            return nodes.indexOf(this.tailLeaf) - 1;
//        }
//        SETreeNode<D> n = this;
//        while (!n.isLeaf()) {
//            n = n.childrenNodes.get(n.childrenNodes.size() - 1);
//            if (n.tailLeaf != null) {
//                n = n.tailLeaf;
//                break;
//            }
//        }
//        return nodes.indexOf(n);
//    }
//
//    public boolean isLastChildOfParent() {
//        return getParentNode() != null && getParentNode().getChildrenNodes().indexOf(this) == getParentNode().getChildrenNodes().size() - 1;
//    }
//
//    public boolean isFirstChildOfParent() {
//        return getParentNode() != null && getParentNode().getChildrenNodes().indexOf(this) == 0;
//    }
//
//    public SETreeNode<D> getTailLeaf() {
//        return tailLeaf;
//    }
//
//    public SETreeNode<D> getHeadLeaf() {
//        return headLeaf;
//    }
//
//    public SETreeNode<D> getSuperTailLeaf() {
//        return isRoot() ? null : getParentNode().tailLeaf;
//    }
//
//    public int size() {
//        return orderedNodes.size();
//    }
//
//    public int getLayer() {
//        SETreeNode<D> n = this;
//        int count = 0;
//        while (!n.isRoot()) {
//            n = n.getParentNode();
//            count++;
//        }
//        return count;
//    }
//
//    public void swapToLeft() {
//        if (isRoot()) return;
//        if (!isFirstChildOfParent()) {
//            SETreeNode<D> preNode = getParentNode().getChildrenNodes().get(getParentNode().getChildrenNodes().indexOf(this) - 1);
//            getParentNode().swap(preNode, this);
//            getParentNode().notifyDataChanged(this.data, preNode.data, INodeDataChangedListener.ACTION_SWAP);
//        }
//    }
//
//    public void swapToRight() {
//        if (isRoot()) return;
//        if (!isLastChildOfParent()) {
//            SETreeNode<D> nextNode = getParentNode().getChildrenNodes().get(getParentNode().getChildrenNodes().indexOf(this) + 1);
//            getParentNode().swap(nextNode, this);
//            getParentNode().notifyDataChanged(this.data, nextNode.data, INodeDataChangedListener.ACTION_SWAP);
//        }
//    }
//
//
//    private void swap(SETreeNode<D> c1, SETreeNode<D> c2) {
//        final List<SETreeNode<D>> orderedNodes = getOrderedNodes();
//        if (orderedNodes.contains(c1) && orderedNodes.contains(c2)) {
//            final int index1 = orderedNodes.indexOf(c1);
//            final int index2 = orderedNodes.indexOf(c2);
//            final int lastIndex = Math.max(index1, index2);
//            final List<SETreeNode<D>> nodes1 = c1.getCurrentNodeSelfAndAllSubOrderedNodes(false);
//            final List<SETreeNode<D>> nodes2 = c2.getCurrentNodeSelfAndAllSubOrderedNodes(false);
//            if (index1 < index2) {
//                swapOrderedNodes(orderedNodes, nodes1, index1, nodes2, index2);
//            } else if (index1 > index2) {
//                swapOrderedNodes(orderedNodes, nodes2, index2, nodes1, index1);
//            }
//        }
//        Useless.swap(childrenNodes, c1, c2);
//    }
//
//    /**
//     * 垃圾代码
//     *
//     * @param orderedNodes
//     * @param smallNodes
//     * @param smallIndex
//     * @param biggerNodes
//     * @param biggerIndex
//     */
//    private void swapOrderedNodes(List<SETreeNode<D>> orderedNodes, List<SETreeNode<D>> smallNodes, int smallIndex, List<SETreeNode<D>> biggerNodes, int biggerIndex) {
//        orderedNodes.removeAll(biggerNodes);
//        orderedNodes.addAll(Math.min(biggerIndex, orderedNodes.size()), smallNodes);
//        //TODO: trash code here
//        for (SETreeNode<D> n : smallNodes) {
//            final int index = orderedNodes.indexOf(n);
//            if (index >= 0) orderedNodes.remove(index);
//        }
//        //orderedNodes.removeAll(smallNodes);
//        orderedNodes.addAll(Math.min(smallIndex, orderedNodes.size()), biggerNodes);
//    }
//
//    private List<SETreeNode<D>> getCurrentAndNextSubsOrdedNodes() {
//        List<SETreeNode<D>> nodes = new ArrayList<>();
//        nodes.add(this);
//        if (this.headLeaf != null) nodes.add(this.headLeaf);
//        if (!Useless.isEmpty(childrenNodes)) nodes.addAll(this.childrenNodes);
//        if (this.tailLeaf != null) nodes.add(this.tailLeaf);
//        return nodes;
//    }
//
//    private List<SETreeNode<D>> getCurrentNodeSelfAndAllSubOrderedNodes() {
//        return getCurrentNodeSelfAndAllSubOrderedNodes(true);
//    }
//
//    private List<SETreeNode<D>> getCurrentNodeSelfAndAllSubOrderedNodes(boolean includeInvisible) {
//        List<SETreeNode<D>> nodes = new ArrayList<>();
//        orderNodes(nodes, 0);
//        if (!includeInvisible) {
//            List<SETreeNode<D>> nodesResult = new ArrayList<>();
//            Useless.foreach(nodes, t -> {
//                if (t == this || t.isSelfVisibile()) {
//                    nodesResult.add(t);
//                }
//            });
//            return nodesResult;
//        }
//        return nodes;
//    }
//
//    private void reorder() {
//        SETreeNode<D> root = getRootNode();
//        if (root.orderedNodes != null) {
//            root.orderedNodes.clear();
//            root.orderNodes(orderedNodes, 0);
//        }
//    }
//
//    public void delete() {
//        delete(true);
//    }
//
//    public void delete(boolean notify) {
//        if (!isRemoveable || isRoot()) return;
//        if (isTailLeaf()) return;
//        /*// get target nodes from exist ordered nodes list
//        final List<SETreeNode<D>> targetOrderedNodes = new ArrayList<>();
//        for(int i = this.orderedNodePosition; i <= this.orderedEndNodePosition; i ++) {
//            targetOrderedNodes.add(orderedNodes.get(i));
//        }
//        // reset old node's tree-position those stay behind of target-nodes
//        final List<SETreeNode<D>> orderedNodes = getOrderedNodesInner(false);
//        resetThesePositions(targetOrderedNodes, orderedNodes, -targetOrderedNodes.size(), this.getOrderedEndNodePosition() + 1);
//        // doDelete target-nodes from ordered-nodes-list
//        orderedNodes.doDelete(targetOrderedNodes);
//        // doDelete this from parent node
//        getParentNode().orderedEndNodePosition -= targetOrderedNodes.size();*/
//        boolean succes = getParentNode().childrenNodes.remove(this);
//        if (notify)
//            getParentNode().notifyDataChanged(this.data, null, INodeDataChangedListener.ACTION_REMOVE);
//        // doDelete from ordered-list
//        doDelete();
//        //reorder();
//
//    }
//
//    private void doDelete() {
//        getOrderedNodes().remove(this);
//        if (headLeaf != null) getOrderedNodes().remove(headLeaf);
//        if (tailLeaf != null) getOrderedNodes().remove(tailLeaf);
//        System.out.println("delete is " + this.data + " -> ");
//        if (!isLeaf()) {
//            for (SETreeNode<D> n : childrenNodes) {
//                n.doDelete();
//            }
//        }
//    }
//
//    public int getNodePosition() {
//        return isRoot() ? 0 : getParentNode().positionOf(this);
//    }
//
//    int positionOf(SETreeNode<D> child) {
//        return isLeaf() ? 0 : childrenNodes.indexOf(child);
//    }
//
//    public int getOrderedPosition() {
//        return getOrderedNodes().indexOf(this);
//    }
//
//    public D getData() {
//        return data;
//    }
//
//    private INodeDataChangedListener<D> mOnDataChanged;
//
//    public void notifyDataChanged(D d, D d2, int action) {
//        if (mOnDataChanged != null) mOnDataChanged.onChanged(d, d2, action);
//    }
//
//    public void setOnDataChangedListener(INodeDataChangedListener<D> l) {
//        this.mOnDataChanged = l;
//    }
//
//    public boolean isTailLeaf() {
//        boolean isleaf = isLeaf();
//        boolean isRoot = isRoot();
//        boolean isTaileaf = !isRoot && getParentNode().tailLeaf == this;
//        return isLeaf() && !isRoot() && getParentNode().tailLeaf == this;
//    }
//
//    public boolean isHeadLeaf() {
//        return isLeaf() && !isRoot() && getParentNode().headLeaf == this;
//    }
//
//    public void toggleVisible() {
//        final boolean visible = !isSelfVisibile();
//        if (isExpanded()) {
//            collapseAll();
//        } else {
//            expand();
//        }
//    }
//
//    public void expand() {
//        if(Useless.isEmpty(childrenNodes)) return;
//        setNodeSelfAndNextSubsVisible(true);
//        reorderSelfAndNextSubs();
//    }
//
//    public boolean isExpanded() {
////        List<SETreeNode<D>> nodes = getCurrentAndNextSubsOrdedNodes();
////        return Useless.isEmpty(nodes) || Useless.checkAll(nodes, t -> t.isVisibile);
//        return false;
//    }
//
//    public boolean hasNoChildren(){
//        return Useless.isEmpty(getChildrenNodes());
//    }
//
//    private void reorderSelfAndNextSubs() {
//        final int index = getOrderedPosition();
//        if (index >= 0) {
//            List<SETreeNode<D>> nodes = getCurrentAndNextSubsOrdedNodes();
//            if (!Useless.isEmpty(nodes)) {
//                nodes.remove(0);
//                getOrderedNodes().addAll(index + 1, nodes);
//            }
//        }
//
//    }
//
//    public void collapseAll() {
//        if(Useless.isEmpty(childrenNodes)) return;
//        collapseSetVisible();
//        collapseReorder();
//    }
//
//    public void rootClear() {
//        getRootNode().clear();
//    }
//
//    public void clear() {
//        this.tailLeaf = null;
//        this.headLeaf = null;
//        if(this.childrenNodes != null) {
//            this.childrenNodes.clear();
//            this.childrenNodes = null;
//            this.orderedNodes.clear();
//        }
//    }
//
//    public interface INodeDataChangedListener<D> {
//        int ACTION_ADD = 0;
//        int ACTION_REMOVE = 1;
//        int ACTION_MODIFIED = 2;
//        int ACTION_SWAP = 3;
//
//        void onChanged(D d, D old, int action);
//    }
//
//    @Override
//    protected SETreeNode<D> clone() throws CloneNotSupportedException {
//        return (SETreeNode<D>) super.clone();
//    }
}
