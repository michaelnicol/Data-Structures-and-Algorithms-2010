/*
 * Author:  Michael Nicol, mnicol2022@my.fit.edu
 * Course:  CSE 2010, Section 14, Spring 2023
 * Project:  HW1, Warehouse Management System
 */

/**
 * This program relies upon a sourced SinglyLinked List:
 *
 * Copyright 2014, Michael T. Goodrich, Roberto Tamassia, Michael H. Goldwasser
 *
 * Then modified by Michael Nicol 1/23/22
 */

import javax.lang.model.element.Element;

/*
 * Copyright 2014, Michael T. Goodrich, Roberto Tamassia, Michael H. Goldwasser
 *
 * Developed for use with the book:
 *
 *    Data Structures and Algorithms in Java, Sixth Edition
 *    Michael T. Goodrich, Roberto Tamassia, and Michael H. Goldwasser
 *    John Wiley & Sons, 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
//package net.datastructures;

/**
 * A basic singly linked list implementation.
 *
 * @author Michael T. Goodrich
 * @author Roberto Tamassia
 * @author Michael H. Goldwasser
 * 
 *         MODIFICATIONS HAVE BEEN MADE BY MICHAEL NICOL.
 * 
 *         MODIFIED OR CREATED METHODS ARE SPECIFIED.
 */
public class SinglyLinkedList<E> implements Cloneable {
   // ---------------- nested Node class ----------------
   /**
    * Node of a singly linked list, which stores a reference to its
    * element and to the subsequent node in the list (or null if this
    * is the last node).
    */
   public static class Node<E> {

      /** The element stored at this node */
      private E element; // reference to the element stored at this node

      /** A reference to the subsequent node in the list */
      private Node<E> next; // reference to the subsequent node in the list

      /**
       * Creates a node with the given element and next node.
       *
       * @param e the element to be stored
       * @param n reference to a node that should follow the new node
       */
      public Node(E e, Node<E> n) {
         element = e;
         next = n;
      }

      // Accessor methods
      /**
       * Returns the element stored at the node.
       * 
       * @return the element stored at the node
       */
      public E getElement() {
         return element;
      }

      /**
       * Returns the node that follows this one (or null if no such node).
       * 
       * @return the following node
       */
      public Node<E> getNext() {
         return next;
      }

      // Modifier methods
      /**
       * Sets the node's next reference to point to Node n.
       * 
       * @param n the node that should follow this one
       */
      public void setNext(Node<E> n) {
         next = n;
      }

      /**
       * @author Michael Nicol 1/16/22
       */
      public String toString() {
         return "Node Value: " + element.toString();
      }

   } // ----------- end of nested Node class -----------

   // instance variables of the SinglyLinkedList
   /** The head node of the list */
   private Node<E> head = null; // head node of the list (or null if empty)

   /** The last node of the list */
   private Node<E> tail = null; // last node of the list (or null if empty)

   /** Number of nodes in the list */
   private int size = 0; // number of nodes in the list

   /** Constructs an initially empty list. */
   public SinglyLinkedList() {
   } // constructs an initially empty list

   /**
    * @author Michael Nicol
    *
    *         Adds E value to index.
    *
    * @param index
    * @param value
    */
   void addItem(int index, E value) {
      Node<E> addedNode = new Node<E>(value, null);
      // Add as head and reconigure head to currentNode.
      if (index == 0) {
         addedNode.setNext(head);
         head = addedNode;
         this.size();
         if (size == 1) {
            tail = head;
         }
         return;
      }
      // If the list is 1 in length, and you want to add at index 1,
      if (index == this.size()) {
         tail.setNext(addedNode);
         tail = addedNode;
         this.size();
         return;
      }
      // We want to add onto the end
      Node<E> previousNode = this.head;
      Node<E> currentNode = this.head;
      for (int i = 0; i < this.size(); i++) {
         // Loop through until the index is found
         if (i == index) {
            addedNode.setNext(currentNode);
            previousNode.setNext(addedNode);
            if (i == this.size() - 1) {
               tail = currentNode;
            }
            break;
         }
         if (i == 0) {
            currentNode = currentNode.getNext();
         } else {
            // Only increment previous if not at head of list.
            currentNode = currentNode.getNext();
            previousNode = previousNode.getNext();
         }
      }
   }

   void insert(E element, int index) {
      Node newNode = new Node(element, null);
      // if (head == null) {
      //    head = newNode;
      //    tail = newNode;
      // }
       if (index >= size) {
         throw new Error("Invalid Index");
       }
   }

   /**
    * Removes an item at a given index.
    *
    * @author Michael Nicol
    */
   void removeItem(int index) {
      if (index >= size) {
         throw new Error("[ Incorrect removeItem index ]");
      }
      // Keep track of the previous and current node.
      Node<E> previousNode = this.head;
      Node<E> currentNode = this.head;
      for (int i = 0; i < size; i++) {
         if (i == index) {
            // If removing the only item in the list.
            if (size == 1 && index == 0) {
               this.head = null;
               this.tail = null;
               previousNode = null;
               currentNode = null;
               // Otherwise, just reconfigure the head.
            } else if (i == 0) {
               this.head = currentNode.getNext();
               // Otherwise, reconfigure the tail.
            } else if (i == size - 1) {
               this.tail = previousNode;
               this.tail.setNext(null);
            } else {
               // Otherwise, jump the previous to the next
               previousNode.setNext(currentNode.getNext());
               currentNode.setNext(null);
            }
            break;
         }
         // Only increment the previous if not on the first node.
         if (i == 0) {
            currentNode = currentNode.getNext();
         } else {
            currentNode = currentNode.getNext();
            previousNode = previousNode.getNext();
         }
      }
      // Re-check size.

      this.size();
   }

   /**
    * @author Michael Nicol
    *
    * @param index Index to retrieve element at
    * @return Element value of node at index specified
    * @throws Error if incorrect.
    */
   public E getIndex(int index) {
      if (index >= this.size()) {
         throw new Error("[ Out of range getIndex specified for " + index + " ]");
      }
      Node<E> currentNode = this.head;
      for (int i = 0; i < size; i++) {
         // If these two blocks of memory are the same
         if (i == index) {
            return currentNode.getElement();
         }
         currentNode = currentNode.getNext();
      }
      return null;
   }

   /**
    * @author Michael Nicol
    *
    *         Returns the number of elements in the linked list.
    * 
    * @return number of elements in the linked list
    */
   public int size() {
      size = 0;
      Node<E> currentNode = head;
      if (head == null) {
         return 0;
      }
      size = 1;
      while (currentNode.getNext() != null) {
         size++;
         currentNode = currentNode.getNext();
      }
      return size;
   }

   /**
    * Tests whether the linked list is empty.
    * 
    * @return true if the linked list is empty, false otherwise
    */
   public boolean isEmpty() {
      return this.size() == 0;
   }

   /**
    * Returns (but does not remove) the first element of the list
    * 
    * @return element at the front of the list (or null if empty)
    */
   public E first() { // returns (but does not remove) the first element
      if (isEmpty())
         return null;
      return head.getElement();
   }

   /**
    * Returns (but does not remove) the last element of the list.
    * 
    * @return element at the end of the list (or null if empty)
    */
   public E last() { // returns (but does not remove) the last element
      if (isEmpty())
         return null;
      return tail.getElement();
   }

   // update methods
   /**
    * Adds an element to the front of the list.
    * 
    * @param e the new element to add
    */
   public void addFirst(E e) { // adds element e to the front of the list
      Node<E> newNode = new Node<>(e, head); // create and link a new node
      newNode.setNext(head);
      head = newNode;
      if (tail == null) {
         tail = head;
      }
      this.size(); // added by michael nicol
   }

   /**
    * Adds an element to the end of the list.
    * 
    * @param e the new element to add
    */
   public void addLast(E e) { // adds element e to the end of the list
      Node<E> newest = new Node<>(e, null); // node will eventually be the tail
      if (isEmpty()) {
         head = newest; // special case: previously empty list
      } else {
         tail.setNext(newest);
      } // new node after existing tail
      tail = newest; // new node becomes the tail
      this.size(); // added by michael nicol
   }

   /**
    * Removes and returns the first element of the list.
    * 
    * @return the removed element (or null if empty)
    */
   public E removeFirst() { // removes and returns the first element
      if (isEmpty())
         return null; // nothing to remove
      E answer = head.getElement();
      head = head.getNext(); // will become null if list had only one node
      size--;
      if (size == 0)
         tail = null; // special case as list is now empty
      this.size(); // added by michael nicol
      return answer;
   }

   @SuppressWarnings({ "unchecked" })
   public boolean equals(Object o) {
      if (o == null)
         return false;
      if (getClass() != o.getClass())
         return false;
      SinglyLinkedList other = (SinglyLinkedList) o; // use nonparameterized type
      if (size != other.size)
         return false;
      Node walkA = head; // traverse the primary list
      Node walkB = other.head; // traverse the secondary list
      while (walkA != null) {
         if (!walkA.getElement().equals(walkB.getElement()))
            return false; // mismatch
         walkA = walkA.getNext();
         walkB = walkB.getNext();
      }
      return true; // if we reach this, everything matched successfully
   }

   @SuppressWarnings({ "unchecked" })
   public SinglyLinkedList<E> clone() throws CloneNotSupportedException {
      // always use inherited Object.clone() to create the initial copy
      SinglyLinkedList<E> other = (SinglyLinkedList<E>) super.clone(); // safe cast
      if (size > 0) { // we need independent chain of nodes
         other.head = new Node<>(head.getElement(), null);
         Node<E> walk = head.getNext(); // walk through remainder of original list
         Node<E> otherTail = other.head; // remember most recently created node
         while (walk != null) { // make a new node storing same element
            Node<E> newest = new Node<>(walk.getElement(), null);
            otherTail.setNext(newest); // link previous node to this one
            otherTail = newest;
            walk = walk.getNext();
         }
      }
      return other;
   }

   public int hashCode() {
      int h = 0;
      for (Node walk = head; walk != null; walk = walk.getNext()) {
         h ^= walk.getElement().hashCode(); // bitwise exclusive-or with element's code
         h = (h << 5) | (h >>> 27); // 5-bit cyclic shift of composite code
      }
      return h;
   }

   /**
    * Produces a string representation of the contents of the list.
    * This exists for debugging purposes only.
    */
   public String toString() {
      StringBuilder sb = new StringBuilder("[(");
      Node<E> walk = head;
      while (walk != null) {
         sb.append("<");
         sb.append(walk.getElement());
         if (walk != tail)
            sb.append(">, ");
         walk = walk.getNext();
      }
      sb.append(")]");
      return sb.toString();
   }
}