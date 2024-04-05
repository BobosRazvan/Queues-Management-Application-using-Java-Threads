package com.example.BussinessLogic;

import com.example.Model.*;
import java.util.*;

public interface Strategy {

    public void addTask(List<Server> servers, Task t) throws InterruptedException;
}
