package com.example.BussinessLogic;

import com.example.Model.Server;
import com.example.Model.Task;

import java.util.List;

public class ShortestTimeStrategy implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t) throws InterruptedException {
        Server shortestTime = servers.get(0);
        for (Server server : servers) {
            // System.out.println("Server " + server.id + " has time " + server.getEstimatedRemainingTime());
            if (shortestTime.getEstimatedRemainingTime().get() > server.getEstimatedRemainingTime().get()) {
                shortestTime = server;
            }
        }
        shortestTime.addTask(t);
    }
}
