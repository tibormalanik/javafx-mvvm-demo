package com.example.mvvm;

import com.example.mvvm.mvc.PersonControllerView;
import com.example.mvvm.mvvm.PersonView;
import com.example.mvvm.mvvm.PersonViewModel;
import com.example.mvvm.model.PersonService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Entry point. Two tabs let us compare the SAME feature implemented two ways:
 *   - MVVM  : View binds to a ViewModel that exposes JavaFX Properties.
 *   - MVC   : Controller wires raw widgets to a model imperatively.
 *
 * The point of the talk: in JavaFX, Property + binding makes MVVM the
 * path of least resistance, not the heavyweight option.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        // A shared "backend" service. Notice neither pattern's UI code
        // talks to persistence directly.
        PersonService service = new PersonService();

        TabPane tabs = new TabPane();
        tabs.getTabs().add(buildMvvmTab(service));
        tabs.getTabs().add(buildMvcTab(service));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane root = new BorderPane();
        root.setTop(header());
        root.setCenter(tabs);

        Scene scene = new Scene(root, 560, 460);
        stage.setTitle("A Practical Guide to MVVM in JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    private Label header() {
        Label l = new Label("Same feature, two architectures \u2014 compare the tabs");
        l.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        l.setPadding(new Insets(10, 12, 6, 12));
        return l;
    }

    private Tab buildMvvmTab(PersonService service) {
        PersonViewModel viewModel = new PersonViewModel(service);
        PersonView view = new PersonView(viewModel);
        Tab tab = new Tab("MVVM (binding)", view.getRoot());
        return tab;
    }

    private Tab buildMvcTab(PersonService service) {
        PersonControllerView mvc = new PersonControllerView(service);
        Tab tab = new Tab("MVC (imperative)", mvc.getRoot());
        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
