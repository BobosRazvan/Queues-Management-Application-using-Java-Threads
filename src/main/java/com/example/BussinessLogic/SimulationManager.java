package com.example.BussinessLogic;

import com.example.Model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable{

    public SelectionPolicy selectionPolicy=SelectionPolicy.SHORTEST_TIME;
    public int numberOfClients;
    public int numberOfServers;
    public int minProcessingTime;
    public int maxProcessingTime;
    public int timeLimit;


    private List<Task> generatedTasks;
    private List<Task> displayGeneratedTasks;
    private Scheduler scheduler;
    int minArrivalTime;
    int maxArrivalTime;
    private FileWriter logger;
    public int totalWaitingTime;
    public int totalServiceTime;
    public int totalTasksArrived;
    public int peakHourTasks;
    public int peakHour;
    private int[] taskArrivalCount;
    private AtomicInteger currentTime;

    public SimulationManager(SelectionPolicy selectionPolicy, int numberOfClients, int numberOfServers, int timeLimit, int minArrivalTime,int maxArrivalTime,int minProcessingTime, int maxProcessingTime) {
        this.currentTime = new AtomicInteger(0);
        this.selectionPolicy = selectionPolicy;
        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.minProcessingTime = minProcessingTime;
        this.maxProcessingTime = maxProcessingTime;
        this.minArrivalTime=minArrivalTime;
        this.maxArrivalTime=maxArrivalTime;
        this.timeLimit = timeLimit;
        this.totalWaitingTime = 0;
        this.totalServiceTime = 0;
        this.totalTasksArrived= 0;
        this.taskArrivalCount=new int[timeLimit+1];
        this.peakHourTasks=0;
        this.peakHour=0;
        this.generatedTasks=new ArrayList<>();
        this.displayGeneratedTasks=new ArrayList<>();
        try {
            logger = new FileWriter("src/simulation.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduler=new Scheduler(numberOfServers,logger,displayGeneratedTasks);///aici schimba maxTasks PEr Servers
        scheduler.changeStrategy(SelectionPolicy.SHORTEST_TIME);


    }





    // Inside SimulationManager class
    @Override
    public void run() {
        try {
            generateNRandomTasks();
            //generateHardcodedTasks();

            while (currentTime.get() <= timeLimit && !Thread.currentThread().isInterrupted()) {


                System.out.println("Time: " + currentTime);

                for (Task task : generatedTasks) {
                    if (currentTime.get() >= task.getArrivalTime() && !task.isDispatched()) {
                        scheduler.dispatchTask(task);
                        task.setDispatched(true);
                        displayGeneratedTasks.remove(task);
                        updateMetrics(task);
                    }
                }

                updateTxt();
                Thread.sleep(1000);
                currentTime.incrementAndGet();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            scheduler.interruptServers();
            try {
                //logMetrics(); removed this only displaying results in GUI
                logger.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void generateNRandomTasks() {
        Random random = new Random();

        for (int i = 0; i < this.getNumberOfClients(); i++) {
            int processingTime = random.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            Task task = new Task(i + 1, arrivalTime, processingTime);
            generatedTasks.add(task);

        }

        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
        displayGeneratedTasks.addAll(generatedTasks);
        for (Task task : this.generatedTasks) {
            System.out.println(task.getID() + " " + task.getArrivalTime() + " " + task.getServiceTime());
        }
    }

    public synchronized void generateHardcodedTasks(){
        // Hardcode tasks
        Task task1 = new Task(1, 1, 4);  // Task ID: 1, Arrival Time: 0, Service Time: 3
        Task task2 = new Task(2, 1, 4);  // Task ID: 2, Arrival Time: 0, Service Time: 4
        Task task3 = new Task(3, 1, 3);  // Task ID: 3, Arrival Time: 0, Service Time: 2
        Task task4 = new Task(4, 2, 6);  // Task ID: 4, Arrival Time: 1, Service Time: 5
        Task task5 = new Task(5, 2, 3);  // Task ID: 5, Arrival Time: 2, Service Time: 3
        Task task6 = new Task(6, 4, 4);  // Task ID: 6, Arrival Time: 3, Service Time: 4

        // Add tasks to the list of generated tasks
        generatedTasks.add(task1);
        generatedTasks.add(task2);
        generatedTasks.add(task3);
        generatedTasks.add(task4);
        generatedTasks.add(task5);
        generatedTasks.add(task6);

        // Sort the list of generated tasks by arrival time
        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
        displayGeneratedTasks.addAll(generatedTasks);
    }

    public synchronized void updateTxt() throws IOException {
        StringBuilder output = new StringBuilder();

        // Append current time
        output.append("Time: ").append(currentTime).append("\n");

        // Append waiting clients
        output.append("Waiting clients:").append(displayGeneratedTasks).append("\n");

        // Append queues for each server
        for (Server server : scheduler.getServers()) {
            BlockingQueue<Task> tasks = server.getTasks();
            output.append("Queue ").append(server.getId()).append(": ");
            if (tasks.isEmpty()) {
                output.append("closed\n");
            } else {
                for (Task task : tasks) {
                    output.append("(").append(task.getID()).append(",").append(task.getArrivalTime()).append(",").append(task.getServiceTime()).append(") ");
                }
                output.append("\n");
            }
        }

        // Write the output to the logger
        logger.write(output.toString());
    }

    public synchronized String getCurrentOutput() {
        StringBuilder output = new StringBuilder();

        // Append current time
        output.append("Time: ").append(currentTime).append("\n");

        // Append waiting clients
        output.append("Waiting clients:").append(displayGeneratedTasks).append("\n");

        // Append queues for each server
        for (Server server : scheduler.getServers()) {
            BlockingQueue<Task> tasks = server.getTasks();
            output.append("Queue ").append(server.getId()).append(": ");
            if (tasks.isEmpty()) {
                output.append("closed\n");
            } else {
                for (Task task : tasks) {
                    output.append("(").append(task.getID()).append(",").append(task.getArrivalTime()).append(",").append(task.getServiceTime()).append(") ");
                }
                output.append("\n");
            }
        }

        return output.toString();
    }
    private synchronized void updateMetrics(Task task) {
        totalWaitingTime += task.getServiceTime()+2; // not correct but for now it is like this
        totalServiceTime += task.getServiceTime();
        totalTasksArrived++;
        updatePeakHour(task);
    }

    // Method to update peak hour
    private synchronized void updatePeakHour(Task task) {
        // Increment the count of tasks arrived in the hour of the current task
        taskArrivalCount[task.getArrivalTime()]++;

        // Update peak hour if the current hour has more tasks than the previous peak hour
        if (taskArrivalCount[task.getArrivalTime()] > peakHourTasks) {
            peakHourTasks = taskArrivalCount[task.getArrivalTime()];
            peakHour = task.getArrivalTime();
        }
    }
    public synchronized void logMetrics() {
        try {
            StringBuilder metricsLog = new StringBuilder();

            double avgWaitingTime = totalTasksArrived == 0 ? 0 : totalWaitingTime / (double) totalTasksArrived;
            double avgServiceTime = totalTasksArrived == 0 ? 0 : totalServiceTime / (double) totalTasksArrived;

            // Append average waiting time, average service time, and peak hour to the log
            metricsLog.append("Average Waiting Time: ").append(avgWaitingTime).append("\n");
            metricsLog.append("Average Service Time: ").append(avgServiceTime).append("\n");
            metricsLog.append("Peak hour: Time ").append(peakHour).append("\n");

            // Write the log to the file
            try (FileWriter logger = new FileWriter("src/simulation.txt", true)) {
                logger.write(metricsLog.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized String getMetrics() {
        StringBuilder metrics = new StringBuilder();

        double avgWaitingTime = totalTasksArrived == 0 ? 0 : totalWaitingTime / (double) totalTasksArrived;
        double avgServiceTime = totalTasksArrived == 0 ? 0 : totalServiceTime / (double) totalTasksArrived;

        metrics.append("Average Waiting Time: ").append(avgWaitingTime).append("\n");
        metrics.append("Average Service Time: ").append(avgServiceTime).append("\n");
        metrics.append("Peak hour: Time ").append(peakHour).append("\n");

        return metrics.toString();
    }



    public int getNumberOfClients() {
        return numberOfClients;
    }

    public int getNumberOfServers() {
        return numberOfServers;
    }

    public int getMinProcessingTime() {
        return minProcessingTime;
    }

    public int getMaxProcessingTime() {
        return maxProcessingTime;
    }

    public int getTimeLimit() {
        return timeLimit;
    }




}