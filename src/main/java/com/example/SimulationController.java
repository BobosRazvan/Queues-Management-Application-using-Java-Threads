package com.example;
import com.example.BussinessLogic.*;
import com.example.Model.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;


public class SimulationController {
    @FXML
    private Button buttonShortestQueue;

    @FXML
    private Button buttonShortestTime;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField textFieldClients;

    @FXML
    private TextField textFieldMaxArrival;

    @FXML
    private TextField textFieldMaxProcesingTime;

    @FXML
    private TextField textFieldMinArrival;

    @FXML
    private TextField textFieldMinProcesingTime;

    @FXML
    private TextField textFieldServers;

    @FXML
    private TextField textFieldTimeLimit;
    @FXML
    private TextArea textAreaMetrics;
    private SelectionPolicy lastSelectionPolicy = SelectionPolicy.SHORTEST_TIME; // Keep track of the last selection policy
    @FXML
    public void startSimulation() {
        // Ensure text fields have valid input before parsing
        int numberOfClients = !textFieldClients.getText().isEmpty() ? Integer.parseInt(textFieldClients.getText()) : 0;
        int numberOfServers = !textFieldServers.getText().isEmpty() ? Integer.parseInt(textFieldServers.getText()) : 0;
        int minArrivalTime = !textFieldMinArrival.getText().isEmpty() ? Integer.parseInt(textFieldMinArrival.getText()) : 0;
        int maxArrivalTime = !textFieldMaxArrival.getText().isEmpty() ? Integer.parseInt(textFieldMaxArrival.getText()) : 0;
        int minProcessingTime = !textFieldMinProcesingTime.getText().isEmpty() ? Integer.parseInt(textFieldMinProcesingTime.getText()) : 0;
        int maxProcessingTime = !textFieldMaxProcesingTime.getText().isEmpty() ? Integer.parseInt(textFieldMaxProcesingTime.getText()) : 0;
        int timeLimit = !textFieldTimeLimit.getText().isEmpty() ? Integer.parseInt(textFieldTimeLimit.getText()) : 0;

        System.out.println("The policy is: "+ lastSelectionPolicy);
        // Create a new SimulationManager instance using the last selected policy
        SimulationManager simulationManager = new SimulationManager(
                lastSelectionPolicy,
                numberOfClients,
                numberOfServers,
                timeLimit,
                minArrivalTime,
                maxArrivalTime,
                minProcessingTime,
                maxProcessingTime
        );

        //SimulationManager simulationManager=new SimulationManager(SelectionPolicy.SHORTEST_TIME,6,2,15,10,10,1,5);
        Thread simulationThread=new Thread(simulationManager);
        simulationThread.start();


        new Thread(() -> {
            while (simulationThread.isAlive()) {
                textArea.appendText(simulationManager.getCurrentOutput());
                try {
                    Thread.sleep(1000); // Update every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String metrics = simulationManager.getMetrics();
            textAreaMetrics.setText(metrics);
        }).start();
    }

    @FXML
    private void handleButtonQueue() {
        lastSelectionPolicy = SelectionPolicy.SHORTEST_QUEUE; // Update the last selected policy
    }

    @FXML
    private void handleButtonTime() {
        lastSelectionPolicy = SelectionPolicy.SHORTEST_TIME; // Update the last selected policy
    }



}