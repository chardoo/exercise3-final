package exercise3.impl;

import exercise3.lib.Converter;
import exercise3.lib.FixedSizeConverter;

import java.util.*;

public class BPlusTree<K extends Comparable<? super K>, V> {
    static final int ENTRY_COUNT = 100;

    final class Record implements Comparable<Map.Entry<K, V>>, Map.Entry<K, V> {
        final K key;
        V value;

        Record(K key) {
            this.key = key;
        }

        @Override
        public int compareTo(Map.Entry<K, V> o) {
            return this.key.compareTo(o.getKey());
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            return this.value = value;
        }
    }

    abstract static class Node {
        abstract Node split();

        abstract boolean isLeaf();
    }

    final class IndexNode extends Node {
        final List<K> keys = new ArrayList<>(ENTRY_COUNT);
        final List<Node> children = new ArrayList<>(ENTRY_COUNT + 1);

        boolean isLeaf() {
            return false;
        }

        @Override
        Node split() {
            // TODO: Impl.
            IndexNode sibling = new IndexNode();
            int midIndex = keys.size() / 2;

            sibling.keys.addAll(keys.subList(midIndex + 1, keys.size())); // Right half of keys
            sibling.children.addAll(children.subList(midIndex + 1, children.size())); // Right half of children
            keys.subList(midIndex, keys.size()).clear();
            children.subList(midIndex + 1, children.size()).clear();
            return sibling;
        }
    }

    final class LeafNode extends Node {
        final List<Record> records = new ArrayList<>(ENTRY_COUNT);
        LeafNode next;

        @Override
        Node split() {
            // TODO: Impl.
            LeafNode sibling = new LeafNode();
            int midIndex = records.size() / 2;
            sibling.records.addAll(records.subList(midIndex, records.size()));
            records.subList(midIndex, records.size()).clear();
            sibling.next = this.next;
            this.next = sibling;
            return sibling;

        }

        @Override
        boolean isLeaf() {
            return true;
        }
    }

    private Node root;

    public BPlusTree() {
        // TODO: Impl.
        this.root = new LeafNode();
    }

    public V insert(K key, V value) {
        // TODO: Impl.

        LeafNode leaf = findLeafNode(root, key);
        Record newRecord = new Record(key);
        int pos = Collections.binarySearch(leaf.records, newRecord);

        if (pos >= 0) {
            return leaf.records.get(pos).setValue(value);
        }

        pos = -pos - 1;
        leaf.records.add(pos, newRecord);
        newRecord.setValue(value);

        // Check for overflow
        if (leaf.records.size() > ENTRY_COUNT) {
            Node sibling = leaf.split();

            promote(leaf, sibling, leaf.records.getFirst().key); // Promote the first key in the sibling
        }

        return null;
    }

    private LeafNode findLeafNode(Node node, K key) {
        if (node.isLeaf()) {
            return (LeafNode) node;
        }

        IndexNode index = (IndexNode) node;
        int pos = Collections.binarySearch(index.keys, key);
        pos = pos >= 0 ? pos + 1 : -pos - 1;
        return findLeafNode(index.children.get(pos), key);
    }


    private void promote(Node left, Node right, K key) {
        if (left == root) {
            IndexNode newRoot = new IndexNode();
            newRoot.keys.add(key);
            newRoot.children.add(left);
            newRoot.children.add(right);
            root = newRoot;
            return;
        }
        IndexNode parent = (IndexNode) findParent(root, left);
        int pos = Collections.binarySearch(parent.keys, key);
        pos = -pos - 1; // Convert binary search result to insertion point
        parent.keys.add(pos, key);
        parent.children.add(pos + 1, right);
        if (parent.keys.size() > ENTRY_COUNT) {
            Node sibling = parent.split();
            promote(parent, sibling, parent.keys.getFirst());
        }
    }


    private Node findParent(Node current, Node child) {
        if (current.isLeaf()) {
            return null; // No parent for leaf nodes
        }
        IndexNode index = (IndexNode) current;
        for (Node c : index.children) {
            if (c == child) {
                return current;
            }
            Node result = findParent(c, child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public V pointQuery(K key) {
        // TODO: Impl.
        //return null;
        LeafNode leaf = findLeafNode(root, key); // Find the appropriate leaf node
        Record searchRecord = new Record(key);
        int pos = Collections.binarySearch(leaf.records, searchRecord);

        if (pos >= 0) {
            return leaf.records.get(pos).getValue();
        }
        return null;
    }

    public List<? extends Map.Entry<K, V>> rangeQuery(K minKey, K maxKey) {
        // TODO: Impl.
       // return List.of();
        List<Map.Entry<K, V>> result = new ArrayList<>();
        LeafNode leaf = findLeafNode(root, minKey); // Find the starting leaf node

        while (leaf != null) {
            for (Record record : leaf.records) {
                if (record.key.compareTo(minKey) >= 0 && record.key.compareTo(maxKey) <= 0) {
                    result.add(record);
                } else if (record.key.compareTo(maxKey) > 0) {
                    return result; // Stop if the key exceeds maxKey
                }
            }
            leaf = leaf.next; // Move to the next leaf node
        }

        return result;
    }

    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>();
        for (int i = 0; i < 100; i++) {
            tree.insert(i, "Node" + i);

            for (int j = 0; j <= i; j++) {
                if (!tree.pointQuery(i).equals("Node" + i)) {

                    throw new RuntimeException("Key not found: " + j);
                }
            }

            if (tree.rangeQuery(0, i).size() != i + 1) {

                throw new RuntimeException("Range query failed at key " + i);
            }
        }
     
        System.out.println(tree.root);
        System.out.println(tree.root.isLeaf());
        System.out.println(tree.pointQuery(60));
        System.out.println(tree.rangeQuery(1, 2));



    }
}
