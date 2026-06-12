module com.example.mvvm {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.mvvm to javafx.fxml;
    exports com.example.mvvm;
}
