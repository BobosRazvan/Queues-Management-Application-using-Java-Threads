module com.example.pt2024_30425_bobos_razvanandrei_assigment_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example to javafx.fxml;
    exports com.example;


}