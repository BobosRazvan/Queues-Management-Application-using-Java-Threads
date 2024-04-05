package com.example.Model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    BlockingQueue<Task> tasks;
    public int id;
    AtomicInteger waitingPeriod;
    private Task currentTask;
    int maxNoOfTasks;
    AtomicBoolean active;
    private FileWriter logger; // Added logger
    private List<Task> displayGeneratedTasks; // Added reference to the list of generated tasks

    public Server(int id, FileWriter logger, List<Task> displayGeneratedTasks) { // Modified constructor to accept displayGeneratedTasks
        this.id = id;
        this.active = new AtomicBoolean(true);
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.logger = logger; // Assign logger
        this.displayGeneratedTasks = displayGeneratedTasks; // Assign displayGeneratedTasks

        try {
            logger.write("Server " + id + " initialized\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void addTask(Task newTask) throws InterruptedException {
        try {
            this.tasks.put(newTask);
            System.out.println("added task" + newTask.ID + "to server " + this.id);
            waitingPeriod.addAndGet(newTask.serviceTime);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }

    @Override
    public void run() {
        while (active.get() && !Thread.currentThread().isInterrupted()) {
            try {
                Task task = tasks.peek();//nu scoate din array , faci sa iasa doar cand are service time 0
                if (task != null) {

                    System.out.println("Server " + id + "  task: (" + task.getID() + "," + task.getArrivalTime() + "," + task.getServiceTime() + ")");

                    Thread.sleep(1000);
                    waitingPeriod.decrementAndGet();

                    task.setServiceTime(task.getServiceTime() - 1);

                    if (task.getServiceTime() == 0) {
                        tasks.poll();
                        System.out.println("Server " + id + " finished processing task: (" + task.getID() + "," + task.getArrivalTime() + "," + task.getServiceTime() + ")");

                    }

                }
            } catch (InterruptedException  e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized BlockingQueue<Task> getTasks() {
        return tasks;
    }



    public void interruptServer(){
        Thread.currentThread().interrupt();
        active.set(false);
    }

    public void setTasks(BlockingQueue<Task> tasks) {
        this.tasks = tasks;
    }

    public int getQueueSize() {
        return tasks.size();
    }

    public AtomicInteger getEstimatedRemainingTime() {
        return waitingPeriod;
    }

    public void setWaitingPeriod(AtomicInteger waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

    public int getId() {
        return id;
    }

    public Task getCurrentTask() {
        return currentTask;
    }
}
