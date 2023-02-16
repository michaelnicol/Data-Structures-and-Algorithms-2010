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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class HW1 {
   /**
    * A order holds the requested books and electronics for a given customer.
    * This does not store the assigned worker.
    */
   private static class Order {
      public String customer;
      public int books;
      public int electronics;

      public Order(String customer, int books, int electronics) {
         this.customer = customer;
         this.books = books;
         this.electronics = electronics;
      }

      /**
       * A debug toString() method used to show the amount of books, electronics and
       * customer.
       */
      public String toString() {
         return "[Order: " + books + "B, " + electronics + "E" + " @ " + customer + "]";
      }
   }

   /**
    * A worker instance stores information about a given warehouse worker.
    * This data is the name, and a boolean that decides if it is busy or not.
    */
   private static class Worker {
      /*
       * The name of the worker.
       * A name of WorkerContainer.NO_WORKERS_SIGNIFIER represents a worker that does
       * not exist.
       */
      private String name;
      /**
       * If this worker is busy with a order.
       */
      private boolean isInUse;

      public Worker(String name, boolean isInUse) {
         this.name = name;
         this.isInUse = isInUse;
      }

      public void setUse(boolean x) {
         this.isInUse = x;
      }

      public boolean getUse() {
         return isInUse;
      }

      public String getName() {
         return name;
      }

      /**
       * A debug toString() method to show the name in {name, boolean} format.
       */
      public String toString() {
         return "{" + name + ", " + isInUse + "}";
      }

      /**
       * Deep clones the current Worker, returns a mutation free copy.
       */
      @Override
      protected Worker clone() throws CloneNotSupportedException {
         return new Worker(name, isInUse);
      }
   }

   /**
    * Manages all workers within the program within a singly linked FIFO list.
    */
   private static class WorkerContainer {
      /**
       * If a new worker is requested and none can be found,
       * a new worker with the following name will be returned.
       */
      public static final String NO_WORKERS_SIGNIFIER = "NO_WORKERS";
      /**
       * Stores all of the warehouse workers in a FIFO queue.
       */
      private SinglyLinkedList<Worker> allWorkers = new SinglyLinkedList<Worker>();

      /**
       * Constructs a new worker queue with five default workers.
       */
      public WorkerContainer() {
         final String[] workerNames = new String[] { "Alice", "Bob", "Carol", "David",
               "Emily" };
         for (int i = 0; i < workerNames.length; i++) {
            allWorkers.addLast(new Worker(workerNames[i], false));
            // allWorkers.addItem(allWorkers.size()-1, new Worker(workerNames[i], false));
         }
      }

      /**
       * Loops through the FIFO queue until it finds a worker that is not in use.
       * If no worker is found, it will return a new worker named "NO_WORKERS".
       * This "NO_WORKERS" Worker instance is not stored within the "allWorkers" FIFO.
       * 
       * @return The next worker available
       */
      public Worker grabNewWorker() {
         for (int i = 0; i < allWorkers.size(); i++) {
            Worker currentWorker = allWorkers.getIndex(i);
            if (!currentWorker.getUse()) {
               currentWorker.setUse(true);
               return currentWorker;
            }
         }
         return new Worker(WorkerContainer.NO_WORKERS_SIGNIFIER, false);
      }

      /**
       * If a worker is done completing an order, it will be shifted to the end of the
       * FIFO,
       * and its use will be set to false.
       * 
       * @param w The worker to free from an order.
       * @throws Error if this worker does not exist within the FIFO queue.
       */
      public void freeWorker(Worker w) {
         for (int i = 0; i < allWorkers.size(); i++) {
            Worker currentWorker = allWorkers.getIndex(i);
            if (currentWorker == w) {
               currentWorker.setUse(false);
               allWorkers.removeItem(i);
               allWorkers.addLast(currentWorker);
               return;
            }
         }
         // throw new Error("The worker: " + w + ", is not listed as a valid warehouse worker");
      }

      /*
       * Prints the offically formatted 'AvailableWorkerList' requirement of unused
       * workers.
       */
      public void printAvailableWorkerList(TimeManager currentTime) {
         String str = "AvailableWorkerList " + currentTime.toString();
         for (int i = 0; i < allWorkers.size(); i++) {
            Worker currentWorker = allWorkers.getIndex(i);
            if (!currentWorker.getUse()) {
               str += " " + currentWorker.getName();
            }
         }
         System.out.println(str);
         return;
      }

      /**
       * A debugging toString() for all FIFO Workers assigned or not.
       */
      public String toString() {
         String str = "[<Worker Container: ";
         for (int i = 0; i < allWorkers.size(); i++) {
            str += allWorkers.getIndex(i);
            if (i + 1 != allWorkers.size()) {
               str += ", ";
            }
         }
         str += ">]";
         return str;
      }
   }

   /**
    * Manages and stores the information relating to a single unit of time in
    * minutes, hours and days.
    */
   private static class TimeManager {
      private int minutes = 0;
      private int hours = 0;
      private int days = 0;

      /**
       * Constructs a new TimeManager starting at 00:00 on day 0.
       */
      public TimeManager() {
      }

      /**
       * Constructs a new TimeManager using a formatted time string, days are set to
       * zero.
       * 
       * @param rawTime HHMM or HMM format
       */
      public TimeManager(String rawTime) {
         int[] output = TimeManager.convertRawTime(rawTime);
         this.hours = output[0];
         this.minutes = output[1];
      }

      /**
       * Converts a string time string to an array of [int hours, int
       * minutes]
       * 
       * @param rawTime HHMM, HMM, or HM format.
       * @return [int hours, int minutes]
       */
      public static int[] convertRawTime(String rawTime) {
         int[] timeData = new int[2];
         if (rawTime.length() == 2) {
            // Casts to a String from Char for each charAt.
            timeData[0] = (Integer.parseInt("" + rawTime.charAt(0)));
            timeData[1] = (Integer.parseInt("" + rawTime.charAt(1)));
         } else if (rawTime.length() == 3) {
            timeData[0] = (Integer.parseInt("" + rawTime.charAt(0)));
            timeData[1] = (Integer.parseInt("" + rawTime.charAt(1) + "" + rawTime.charAt(2)));
         } else {
            timeData[0] = (Integer.parseInt("" + rawTime.charAt(0) + "" + rawTime.charAt(1)));
            timeData[1] = (Integer.parseInt("" + rawTime.charAt(2) + "" + rawTime.charAt(3)));
         }
         return timeData;
      }

      /**
       * @return The amount of time, in minutes, since this TimeManager was created
       *         including days.
       */
      public int deltaT() {
         return (this.getDays() * 1440) + (this.getHours() * 60) + (this.getMinutes());
      }

      /**
       * Returns the minute difference between two times, A and B.
       * 
       * Internal calculations preform B minutes - A minutes.
       * 
       * @param a Time Data A
       * @param b Time Data B
       * @return The amount of minutes from A to B, this will be negative if A > B.
       */
      public static int differenceBetweenTimes(TimeManager a, TimeManager b) {
         int minutesA = (a.getDays() * 1440) + (a.getHours() * 60) + (a.getMinutes());
         int minutesB = (b.getDays() * 1440) + (b.getHours() * 60) + (b.getMinutes());
         return minutesB - minutesA;
      }

      public int getHours() {
         return hours;
      }

      public int getMinutes() {
         return minutes;
      }

      public int getDays() {
         return days;
      }

      public void setDays(int d) {
         this.days = d;
      }

      /**
       * Adds a given amount of minutes to the current time, deals with hour and day
       * overflow.
       * 
       * @param minutes
       */
      public void addMinutes(int minutes) {
         this.minutes += minutes;
         while (this.minutes >= 60) {
            this.minutes -= 60;
            this.hours++;
         }
         while (this.hours >= 24) {
            this.hours -= 24;
            this.days++;
         }
      }

      /**
       * @return The time in HHMM format.
       */
      public String toString() {
         return "" + (hours < 10 ? "0" + hours : hours) + "" + (minutes < 10 ? "0" + minutes : minutes);
      }

      /**
       * Deep clones the current Time, returns a mutation free copy.
       */
      @Override
      protected TimeManager clone() throws CloneNotSupportedException {
         TimeManager clonedTime = new TimeManager();
         clonedTime.addMinutes(this.deltaT());
         return clonedTime;
      }
   }

   /**
    * A assignment holds a worker, start and end times, orders for a given worker,
    * and the items across all orders.
    */
   private static class Assignment {
      /**
       * Worker assigned to this assignment. This can be a "NO_WORKERS" Worker if none
       * can be found.
       */
      private Worker assignedWorker;
      private TimeManager startTime;
      private TimeManager endTime;
      private TimeManager initTime;
      private ArrayList<Order> storedOrders = new ArrayList<>();
      /**
       * When printing WorkerAssignment, this will track the print status of each
       * order.
       */
      private ArrayList<Boolean> hasAssignmentPrinted = new ArrayList<>();
      /**
       * Total books across all orders.
       */
      private int books = 0;
      /**
       * Total electronics across all orders.
       */
      private int electronics = 0;

      /**
       * Constructs a new assignment
       * 
       * @param assignedWorker The worker, can be a "NO_WORKERS" Worker.
       * @param currentTime
       */
      public Assignment(Worker assignedWorker, TimeManager currentTime) {
         this.assignedWorker = assignedWorker;
         this.startTime = new TimeManager(currentTime.toString());
         this.endTime = new TimeManager(currentTime.toString());
         this.initTime = new TimeManager(currentTime.toString());
      }

      /**
       * Checks if a given order is within this current assignment.
       * 
       * @param other Another Order instance.
       * @return True if the other order is within this assignment, false otherwise.
       */
      public boolean containsOrder(Order other) {
         for (int i = 0; i < storedOrders.size(); i++) {
            if (storedOrders.get(i) == other) {
               return true;
            }
         }
         return false;
      }

      /**
       * @return Time in minutes between the creation of the first order and the end
       *         of the order.
       */
      public int getFufillmentTime() {
         return endTime.deltaT() - initTime.deltaT();
      }

      /**
       * Given a simulation time, returns if the order can be batched due to time
       * difference and items stored.
       * 
       * @param currentTime
       * @return True if it can be batched, false if it can not.
       */
      public boolean isBatchable(TimeManager currentTime) {
         int timeDifference = TimeManager.differenceBetweenTimes(this.getStartTime(), currentTime);
         return (timeDifference <= 5 &&
               (books < 10 && electronics < 10) &&
               ((books > 0 && electronics == 0)
                     || (electronics > 0 && books == 0)));
      }

      /**
       * The offical 'WorkerAssignment' print statement. This will loop through all
       * orders, only printing those that have not been printed before. This is kept
       * track of in hasAssignmentPrinted array list.
       * <p>
       * For each order: "WorkerAssignment currentTime workerName customer"
       * 
       * @param currentTime
       */
      public void printWorkerAssignment(TimeManager currentTime) {
         if (assignedWorker.getName().equals(WorkerContainer.NO_WORKERS_SIGNIFIER)) {
            throw new Error();
         }
         for (int i = 0; i < storedOrders.size(); i++) {
            if (!hasAssignmentPrinted.get(i)) {
               System.out.println(
                     "WorkerAssignment " + currentTime.toString() + " " + assignedWorker.getName() + " "
                           + storedOrders.get(i).customer);
               hasAssignmentPrinted.set(i, true);
            }
         }
      }

      /**
       * For the WorkerAssignmentList command, returns customers for this given
       * worker.
       * <p>
       * 
       * @return "worker:customer1,customer2, ... ,customerN"
       */
      public String getWorkerAssignmentList() {
         String str = assignedWorker.getName() + ":";
         for (int i = 0; i < storedOrders.size(); i++) {
            str += storedOrders.get(i).customer;
            if (i + 1 != storedOrders.size()) {
               str += ",";
            }
         }
         return str;
      }

      /**
       * For the offical "OrderCompletion" print statement.
       * <p>
       * When the assignment is complete, it will print all the orders within this
       * assignment.
       * <p>
       * Prints "OrderCompletion endTime customer" for each order.
       */
      public void printOrderCompletion() {
         for (int i = 0; i < storedOrders.size(); i++) {
            System.out.println("OrderCompletion " + endTime + " " + storedOrders.get(i).customer);
         }
      }

      /**
       * @return The assigned Worker
       */
      public Worker getWorker() {
         return this.assignedWorker;
      }

      /**
       * Changes the current worker, useful for when the current worker is a
       * "NO_WORKERS" placeholder.
       * 
       * @param worker New Worker
       */
      public void setWorker(Worker worker) {
         this.assignedWorker = worker;
      }

      /**
       * Starts this assignment at the currentTime. Sets start time and then
       * calculates sets the end time.
       * 
       * @param curentTime Time to start this assignment at.
       */
      public void startOrder(TimeManager curentTime) {
         this.setStartTime(curentTime);
         this.calculateEndTime();
      }

      /**
       * Used internally to set the number of books
       */
      private void calculateBooks() {
         books = 0;
         for (int i = 0; i < storedOrders.size(); i++) {
            books += storedOrders.get(i).books;
         }
      }

      /**
       * Used internally to set the number of electronics
       */
      private void calculateElectronics() {
         electronics = 0;
         for (int i = 0; i < storedOrders.size(); i++) {
            electronics += storedOrders.get(i).electronics;
         }
      }

      /**
       * Adds a new order to this assignment for bundling. Re-calculates endTime,
       * books, electronics, sets print status.
       * 
       * @param order Added Order
       */
      public void addOrder(Order order) {
         storedOrders.add(order);
         hasAssignmentPrinted.add(false);
         this.calculateElectronics();
         this.calculateBooks();
         this.calculateEndTime();
      }

      /**
       * @return Total books.
       */
      public int getBooks() {
         this.calculateBooks();
         return this.books;
      }

      /**
       * @return Total electornics.
       */
      public int getElectronics() {
         this.calculateElectronics();
         return this.electronics;
      }

      /**
       * Sets the startTime of this assignment, mutation free.
       * 
       * @param o The new time.
       */
      private void setStartTime(TimeManager o) {
         this.startTime = new TimeManager(o.toString());
      }

      /**
       * @return Copy of the current startTime.
       */
      public TimeManager getStartTime() {
         return new TimeManager(this.startTime.toString());
      }

      /**
       * @return Copy of the current endTime.
       */
      public TimeManager getEndTime() {
         return new TimeManager(this.endTime.toString());
      }

      /**
       * Used the startTime, travel time, total books and electronics to set the
       * endTime.
       */
      public void calculateEndTime() {
         endTime = new TimeManager();
         endTime.addMinutes(books + electronics + startTime.deltaT());
         if (books >= 1) {
            endTime.addMinutes(5);
         }
         if (electronics >= 1) {
            endTime.addMinutes(5);
         }
         endTime.addMinutes(5);
      }

      /**
       * Debug toString() method to print data belonging to this assignment.
       * 
       * @return [worker, endTime, \[order1, order2, ... orderN]\]
       */
      public String toString() {
         String str = "[Assigned Worker: " + assignedWorker.toString() + ", endTime: " + endTime + ", \\";
         for (int i = 0; i < storedOrders.size(); i++) {
            str += storedOrders.get(i).toString();
            if (i + 1 != storedOrders.size()) {
               str += ", ";
            }
         }
         return str + "\\]";
      }

      /**
       * Deep clones the current Assignment including all workers, orders and time
       * objects. Returns a mutation free copy.
       */
      @Override
      protected Assignment clone() throws CloneNotSupportedException {
         Assignment copyAssignment = new Assignment(assignedWorker.clone(), startTime.clone());
         for (int i = 0; i < storedOrders.size(); i++) {
            copyAssignment.addOrder(storedOrders.get(i));
         }
         return copyAssignment;
      }
   }

   /**
    * A class that managed the Assignments into singly linked lists.
    * Manages the bundling, printing and simulation of the program between times.
    */
   private static class AssignmentContainer {
      /**
       * The time at which the container is simulated to. If the simulation time is
       * 16:20, all sent and unsent assignments are properally simulated up until that
       * time.
       */
      public TimeManager simulationTime = new TimeManager();
      /**
       * Holds all of the setAssignments that are currently in the warehouse. Stored
       * by endTime of each Assignment.
       */
      public final SinglyLinkedList<Assignment> sentAssignments;
      /**
       * Holds all of the non-sent assignments. These are assignments that are waiting
       * for a worker or to be bundled in FIFO order.
       */
      public final SinglyLinkedList<Assignment> notSentAssignments;
      /**
       * Stores the longest order for MaxFufillmentTime command.
       */
      public static int longestFullmentTime = 0;

      /**
       * Constructs empty sent and not send assignment singly linked lists.
       */
      public AssignmentContainer() {
         this.sentAssignments = new SinglyLinkedList<Assignment>();
         this.notSentAssignments = new SinglyLinkedList<Assignment>();
      }

      /**
       * Prints all of the Assigned workers sorted by endTime, sent or not.
       * <p>
       * Prints "WorkerAssignmentList worker1:customer1,customer2, ... ,customerN".
       * 
       * @param currentTime
       */
      public void printWorkerAssignmentList(TimeManager currentTime) throws CloneNotSupportedException {
         String str = "WorkerAssignmentList " + currentTime.toString();
         // First, clone all of the sent SinglyLinkedList sentAssignments into a new one.
         SinglyLinkedList<Assignment> completeSorted = sentAssignments.clone();
         // From here, loop through and add the notSentAssignments to the clone by
         // hypothical endTime order.
         for (int i = 0; i < notSentAssignments.size(); i++) {
            boolean hasBeenAdded = false;
            Assignment assignmentToAdd = notSentAssignments.getIndex(i).clone();
            // Calculate the endTime if this was actually sent, only if a worker exists.
            if (assignmentToAdd.getWorker().getName().equals(WorkerContainer.NO_WORKERS_SIGNIFIER)) {
               continue;
            }
            assignmentToAdd.calculateEndTime();
            // Loop through all sortedAssignments.
            for (int j = 0; j < completeSorted.size(); j++) {
               // Calculaute when the current assignment is greater end time then the sorted
               // assignment.
               if (TimeManager.differenceBetweenTimes(assignmentToAdd.getEndTime(),completeSorted.getIndex(j).getEndTime()) < 0) {
                  hasBeenAdded = true;
                  // add this assignment to this position.
                  completeSorted.addItem(j, assignmentToAdd);
                  break;
               }
            }
            if (!hasBeenAdded) {
               completeSorted.addLast(assignmentToAdd);
            }
         }
         // loop through and print out these sorted orders.
         for (int i = 0; i < completeSorted.size(); i++) {
            Assignment printable = completeSorted.getIndex(i);
            str += " " + printable.getWorkerAssignmentList();
         }
         System.out.println(str);
      }

      /**
       * Takes in an assignment, and adds it to sentAssignments sorted by endTime.
       * 
       * @param a New Assignment
       */
      public void addSentAssignment(Assignment a) {
         boolean hasBeenAdded = false;
         for (int i = 0; i < sentAssignments.size(); i++) {
            Assignment compareItem = sentAssignments.getIndex(i);
            // If the time elapsed between compareItem to A is less than zero minutes (A is
            // less thean Compare)
            if (TimeManager.differenceBetweenTimes(compareItem.getEndTime(), a.getEndTime()) < 0) {
               hasBeenAdded = true;
               sentAssignments.addItem(i, a);
               return;
            }
         }
         if (!hasBeenAdded) {
            sentAssignments.addLast(a);
         }
      }

      /**
       * Loops from the simulationTime to the newTime, simulating all of the
       * assignments.
       * 
       * This not deal with bundling orders, only simulates the warehouse for the
       * assignments already created.
       * 
       * @param currentTime Time to simulate to.
       * @param allWorkers  Workers within the warehouse.
       */
      public void simulateToTime(TimeManager newTime, WorkerContainer allWorkers) {
         // First, get the start and the end minutes as a loop range.
         int startMinutes = simulationTime.deltaT();
         int endMinutes = newTime.deltaT();
         if (endMinutes < startMinutes) {
            throw new Error("Incorrect Simulation Input, Unable to travel back in time");
         }
         // Keeps track of the currentTime throughout the loop.
         TimeManager currentTime = new TimeManager();
         currentTime.addMinutes(startMinutes);
         for (int i = startMinutes; i <= endMinutes; i++) {
            // First, free up any completed orders
            this.removeCompletedOrders(currentTime, allWorkers);
            int currentNotSendSize = notSentAssignments.size();
            /**
             * if the simulation has no more items to simulate between the current time and
             * the end of simulation, then the simulation can jump ahead to the end time.
             */
            if (notSentAssignments.size() == 0 && sentAssignments.size() == 0) {
               this.simulationTime = new TimeManager(newTime.toString());
               return;
            }
            /**
             * From here, loop through all of the notSent orders to see if
             * any can be sent off now that workers have been freed up.
             */
            notSentCheckLoop: for (int j = 0; j < currentNotSendSize; j++) {
               Assignment currentAssignment = notSentAssignments.getIndex(j);
               // Does this assignment have a worker?
               if (currentAssignment.getWorker().getName().equals(WorkerContainer.NO_WORKERS_SIGNIFIER)) {
                  Worker newWorker = allWorkers.grabNewWorker();
                  // If not, can a new worker be found
                  if (newWorker.getName().equals(WorkerContainer.NO_WORKERS_SIGNIFIER)) {
                     // If no worker can be found, we need to move to the next minute in the
                     // simulation.
                     // There will never be a situation where a assignment with no worker is infront
                     // of
                     // another assignment in the FIFO queue with that has a valid worker.
                     break notSentCheckLoop;
                  } else {
                     // Otherwise, we have found a worker and we can set the startTime.
                     // This does not start the worker, because their is a chance it could be
                     // bundled.
                     currentAssignment.setWorker(newWorker);
                     currentAssignment.printWorkerAssignment(currentTime);
                     currentAssignment.setStartTime(currentTime);
                  }
               }
               /**
                * This checks a varity of condiations if this assignment should be started.
                *
                * 1) If this is not the last item in the FIFO unsent queue.
                * 2) Or, this order is not batchable.
                * 3) Or, the difference between when the worker was assigned verse the current
                * time is >= 5.
                *
                * If any of these condiations are true, we can add it to the sentAssignments
                * and start the order.
                *
                * We can not bundled with a assignment unless it is the only unsent assingment,
                * since bundling is consecutive.
                *
                * Previous condiations gurantee that this unsentAssignment will have a valid
                * worker.
                */
               if (j + 1 != notSentAssignments.size() || !currentAssignment.isBatchable(currentTime)
                     || TimeManager.differenceBetweenTimes(currentAssignment.getStartTime(), currentTime) >= 5) {
                  notSentAssignments.removeItem(j);
                  currentNotSendSize--;
                  // Re-calculautes startTime to account for bundling or worker delay
                  currentAssignment.setStartTime(currentTime);
                  currentAssignment.startOrder(currentTime);
                  this.addSentAssignment(currentAssignment);
               }
            }
            // Tick the simulation by one.
            currentTime.addMinutes(1);
         }
         /**
          * Since orders may be completed on the last minute after the loop ends,
          * re-check sent assignments.
          */
         this.removeCompletedOrders(currentTime, allWorkers);
         // Now unsent and sent are simulated up to this newTime.
         this.simulationTime = new TimeManager(newTime.toString());
      }

      /**
       * Loops through all sentAssignments and prints and removes any completed
       * assignments at this current time; Frees up any workers.
       * <p>
       * Private because this method should be managed by the simulation method.
       * 
       * @param currentTime Time to check if any assignments are finished by.
       * @param allWorkers  Worker manager to add works back to.
       */
      private void removeCompletedOrders(TimeManager currentTime, WorkerContainer allWorkers) {
         // Loop through all of the assignments
         int currentSize = sentAssignments.size();
         for (int i = 0; i < currentSize; i++) {
            Assignment checkAssignment = sentAssignments.getIndex(i);
            TimeManager endTime = checkAssignment.getEndTime();
            /**
             * If >= 0 minutes have passed from the end of the assignment to the
             * currentTime.
             */
            if (TimeManager.differenceBetweenTimes(endTime, currentTime) >= 0) {
               // Calculate how long it took to complete the assignment for "MaxFufillmentTime"
               int fufillmentTime = checkAssignment.getFufillmentTime();
               if (fufillmentTime > AssignmentContainer.longestFullmentTime) {
                  AssignmentContainer.longestFullmentTime = fufillmentTime;
               }
               // Print out all order compleitions for this assignment
               checkAssignment.printOrderCompletion();
               // Remove this assignment from sentAssignments
               sentAssignments.removeItem(i);
               // Adds this worker back the FIFIO worker queue.
               allWorkers.freeWorker(checkAssignment.getWorker());
               // call again for any more complete assignments, account for index and size
               // difference.
               currentSize--;
               i--;
            }
         }
      }

      /**
       * A debug toString() method to print all assignments within the container.
       * 
       * @return [<sentAssignments: [assignment1], ... , [assignmentN]>,
       *         <notSentAssignments: [assignment1], ... , [assignmentN]>]
       */
      public String toString() {
         String str = "[<sentAssignments: ";
         for (int i = 0; i < sentAssignments.size(); i++) {
            str += sentAssignments.getIndex(i).toString();
            if (i + 1 != sentAssignments.size()) {
               str += ", ";
            }
         }
         str += ">, <notSentAssignments: ";
         for (int i = 0; i < notSentAssignments.size(); i++) {
            str += notSentAssignments.getIndex(i).toString();
            if (i + 1 != notSentAssignments.size()) {
               str += ", ";
            }
         }
         str += ">]";
         return str;
      }
   }

   /**
    * This variable decides if the timer for a bundle will reset or not.
    * 
    * Dr.Chan has agreeded that the timer will reset.
    */
   public static boolean shouldTimerResetUponBundle = true;

   public static void main(final String[] args) throws FileNotFoundException, CloneNotSupportedException {
      /*
       * Create the AssignmentContainer, references to the singly linked lists,
       * WorkerContainer, Orders.
       * Much of the work for this program was placed into simulation class methods,
       * making the main method shorter.
       */
      final AssignmentContainer assignmentsContainer = new AssignmentContainer();
      final SinglyLinkedList<Assignment> sentAssignments = assignmentsContainer.sentAssignments;
      final SinglyLinkedList<Assignment> notSentAssignments = assignmentsContainer.notSentAssignments;
      final WorkerContainer allWorkers = new WorkerContainer();
      // Stores all orders within the program regardless of if they have been
      // completed or not.
      final SinglyLinkedList<Order> currentOrders = new SinglyLinkedList<Order>();
      // Start time for the program of 00:00.
      TimeManager currentTime = new TimeManager();
      // Read in the file via scanner.
      File file = new File(args[0]);
      final Scanner scanner = new Scanner(file);
      orderInputLoop: while (scanner.hasNext()) {
         // Take in the incoming order data
         String[] incomingOrderData = scanner.nextLine().split(" ");

         // Take the current time out of it in the form of a string
         TimeManager incomingOrderTime = new TimeManager(incomingOrderData[1]);
         // Create a new time for this incoming order.
         incomingOrderTime.setDays(currentTime.getDays());
         /**
          * If the incoming order's time:
          *
          * 1) The orders are less than the current hours of currentTime
          * 2) The orders hours are equal, but the minutes are less from currentTime.
          *
          * This will result in a signal that the next day has come. 
          * This shows the modularity of the program.
          */
         if (incomingOrderTime.getHours() < currentTime.getHours() ||
               ((incomingOrderTime.getHours() == currentTime.getHours())
                     && (incomingOrderTime.getMinutes() < currentTime.getMinutes()))) {
            // Increase the days by one.
            incomingOrderTime.setDays(currentTime.getDays() + 1);
         }

         // Set the currentTime to be the incomingTime by adding the difference.
         currentTime.addMinutes(incomingOrderTime.deltaT() - currentTime.deltaT());
         // For the very first order, the assignments will be simulated to 00:00.
         // If this is the case, no simulating needs to be done to catch up to the
         // current time since no previous orders have been created.
         if (assignmentsContainer.simulationTime.deltaT() == 0) {
            assignmentsContainer.simulationTime = new TimeManager(currentTime.toString());
         } else {
            // Otherwise, catch up the simulation.
            assignmentsContainer.simulateToTime(currentTime, allWorkers);
         }
         // Deals with special commands, where each container manager has special print
         // functions.
         if (incomingOrderData[0].equals("PrintAvailableWorkerList")) {
            allWorkers.printAvailableWorkerList(currentTime);
            continue orderInputLoop;
         } else if (incomingOrderData[0].equals("PrintWorkerAssignmentList")) {
            assignmentsContainer.printWorkerAssignmentList(currentTime);
            continue orderInputLoop;
         } else if (incomingOrderData[0].equals("PrintMaxFulfillmentTime")) {
            /**
                * Prints zero if no order has been completed yet.
                * This allows another program anylizing the output of this program to still
                * parse
                * the time into a integer, then check if it is zero.
                */
                System.out.println(
                  "MaxFullfillmentTime " + currentTime.toString() + " " + AssignmentContainer.longestFullmentTime);
            continue orderInputLoop;
         }

         // Create a new order for this data.
         Order currentOrder = new Order(incomingOrderData[2], Integer.parseInt(incomingOrderData[3]),
               Integer.parseInt(incomingOrderData[4]));
         System.out.println("CustomerOrder " + currentTime.toString() + " " + incomingOrderData[2] + ' '
               + Integer.parseInt(incomingOrderData[3]) + " " + Integer.parseInt(incomingOrderData[4]));
         currentOrders.addLast(currentOrder);
         /**
          * Within the main loop, after catching up the simulation, we can deal with
          * bundling.
          * First, check if it can be budling to a previous order if their is any.
          */
         if (notSentAssignments.size() == 0) {
            // We can gurantee no bundling is possible, so simply grab a new worker and new
            // assignment.
            Worker newWorker = allWorkers.grabNewWorker();
            Assignment newAssignment = new Assignment(newWorker, currentTime);
            newAssignment.addOrder(currentOrder);
            // Check if it can be bundled in the future or it has no worker.
            // Print assignment if one exists.
            if (!newWorker.getName().equals(WorkerContainer.NO_WORKERS_SIGNIFIER)) {
               newAssignment.printWorkerAssignment(currentTime);
            }
            if (newAssignment.isBatchable(currentTime)) {
               notSentAssignments.addFirst(newAssignment);
            } else {
               // If not, send it. The assignment was just created, so no startTime needs to be
               // updated.
               assignmentsContainer.addSentAssignment(newAssignment);
            }
            continue orderInputLoop;
         } else {
            // Check the previous assignment to se eif it can be bundled.
            Assignment previousAssignment = notSentAssignments.first();
            // Total the new hypothical number of books and electronics
            int newTotalBooks = previousAssignment.getBooks() + currentOrder.books;
            int newTotalElectronics = previousAssignment.getElectronics() + currentOrder.electronics;
            int timeDifference = TimeManager.differenceBetweenTimes(previousAssignment.getStartTime(), currentTime);
            // We can only bundle with consective orders, so make sure the previous order is
            // within this previous assignment.
            // Make sure the books and electornics are <= 10 and only one catagory.
            if (previousAssignment.containsOrder(currentOrders.getIndex(currentOrders.size() - 2))
                  && timeDifference <= 5 &&
                  (newTotalBooks <= 10 && newTotalElectronics <= 10) &&
                  ((newTotalBooks == 0 && newTotalElectronics > 0)
                        || (newTotalBooks > 0 && newTotalElectronics == 0))) {
               // We can batch this order to the previous assignment
               previousAssignment.addOrder(currentOrder);
               if (!previousAssignment.getWorker().getName().equals(WorkerContainer.NO_WORKERS_SIGNIFIER)) {
                  previousAssignment.printWorkerAssignment(currentTime);
               }
               // In the scenerio where we don't want to reset the timer, this is now an
               // option.
               if (HW1.shouldTimerResetUponBundle) {
                  previousAssignment.setStartTime(currentTime);
               }
               // Because the simulate method will pick it up on the next order input file
               // loop, the order does not need to be sent now.
               // This also deals with the scenerio where two orders were bundled with no
               // worker.
            } else {
               // Otherwise, just add this to unsent FIFO and let simulation method manage it.
               Assignment newAssignment = new Assignment(allWorkers.grabNewWorker(), currentTime);
               newAssignment.addOrder(currentOrder);
               notSentAssignments.addLast(newAssignment);
            }
         }
      }
      // At the end of all incoming orders, deal with unsent assignments.
      while (true) {
         assignmentsContainer.simulateToTime(currentTime, allWorkers);
         // Keep adding more minutes and simulating until no assignments are left.
         if (notSentAssignments.size() == 0 && sentAssignments.size() == 0) {
            return;
         }
         currentTime.addMinutes(1);
      }
   }
}
