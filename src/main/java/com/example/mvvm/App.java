package com.example.mvvm;

import com.example.mvvm.person.PersonView;
import com.example.mvvm.person.PersonViewModel;
import com.example.mvvm.personlist.PersonListView;
import com.example.mvvm.personlist.PersonListViewModel;
import com.example.mvvm.service.PersonService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    private final PersonService personService = new PersonService();

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(header());

        PersonListViewModel listViewModel = new PersonListViewModel(personService, Platform::runLater);
        PersonListView listView = new PersonListView(listViewModel);
        listViewModel.setOpenPerson((person -> {
            PersonViewModel viewModel = new PersonViewModel(personService, person, () -> {
                listViewModel.refresh();
                root.setCenter(listView);
            }, Platform::runLater);
            PersonView view = new PersonView(viewModel);
            root.setCenter(view);
        }));

        root.setCenter(listView);

        Scene scene = new Scene(root, 560, 460);
        stage.setTitle("A practical guide to MVVM in JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    private Label header() {
        Label l = new Label("Customer management");
        l.setStyle("-fx-font-size: 20px;");
        l.setPadding(new Insets(10, 12, 6, 12));
        return l;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
