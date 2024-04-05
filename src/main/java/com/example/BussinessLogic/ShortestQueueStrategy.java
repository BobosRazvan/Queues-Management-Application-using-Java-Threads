package com.example.BussinessLogic;

import com.example.Model.Server;
import com.example.Model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy{

    @Override
    public void addTask(List<Server> servers, Task t) throws InterruptedException {
        Server shortestQueue=servers.get(0);
        for(Server server: servers){
            System.out.println("Server "+server.id+ "has queue "+server.getQueueSize());
            if(server.getQueueSize()<shortestQueue.getQueueSize()){
                shortestQueue=server;
            }
        }
        shortestQueue.addTask(t);

    }
}
