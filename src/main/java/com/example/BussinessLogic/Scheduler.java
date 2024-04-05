package com.example.BussinessLogic;

import com.example.Model.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scheduler {

    private List<Server> servers;
    private int maxNoServers;

    private Strategy strategy;
    private FileWriter logger;
    private List<Task> displayGeneratedTasks; // Add reference to the list of generated tasks

    public Scheduler(int maxNoServers, FileWriter logger, List<Task> displayGeneratedTasks) { // Modified constructor to accept displayGeneratedTasks
        this.maxNoServers = maxNoServers;
        this.logger = logger;
        this.servers = Collections.synchronizedList(new ArrayList<>());
        this.displayGeneratedTasks = displayGeneratedTasks; // Assign displayGeneratedTasks

        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server(i + 1, logger, displayGeneratedTasks); // Pass displayGeneratedTasks to the Server constructor
            Thread serverThread = new Thread(server);
            serverThread.start();
            servers.add(server);
        }
    }

    public void changeStrategy(SelectionPolicy policy){

        if(policy==SelectionPolicy.SHORTEST_QUEUE){
            strategy=new ShortestQueueStrategy();
        }
        if(policy==SelectionPolicy.SHORTEST_TIME){
            strategy=new ShortestTimeStrategy();
        }
    }

    public void dispatchTask(Task t) throws InterruptedException {
        if(strategy!=null){
            strategy.addTask(this.servers,t);
        }else{
            System.out.println("Strategy not set.");
        }
    }

    public synchronized void interruptServers(){
        for(Server server: servers){
            server.interruptServer();
        }
    }

    public List<Server> getServers(){
        return this.servers;
    }
}
