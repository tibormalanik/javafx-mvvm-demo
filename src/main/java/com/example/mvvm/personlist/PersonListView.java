package com.example.mvvm.personlist;

import com.example.mvvm.service.Person;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The View in MVVM.
 *
 * Notice what is NOT here:
 *   - No business logic.
 *   - No "if first name empty then disable button" code.
 *   - No manual reading of TextField text to build a Person.
 *
 * The View only:
 *   1. Creates Nodes.
 *   2. Binds them to the ViewModel's Properties (two-way for inputs).
 *   3. Routes user gestures to ViewModel commands.
 *
 * Swap the ViewModel and this View still works. Test the ViewModel and you
 * have tested the logic without ever starting a JavaFX stage.
 */
public class PersonListView extends VBox {

    private TableView<Person> tableView;

    Button create;
    Button open;
    Button delete;

    public PersonListView(PersonListViewModel viewModel) {
        createView(viewModel);
        createBindings(viewModel);
    }

    private void createView(PersonListViewModel viewModel) {
        tableView = new TableView<>();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn<Person, String> firstName = new TableColumn<>("First name");
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstName.setPrefWidth(150);
        TableColumn<Person, String> lastName = new TableColumn<>("Last name");
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastName.setPrefWidth(150);
        TableColumn<Person, String> email = new TableColumn<>("E-mail");
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        email.setPrefWidth(218);
        tableView.getColumns().addAll(firstName, lastName, email);

        create = new Button("Create");
        create.setOnAction(e -> viewModel.create());
        open = new Button("Open");
        open.setOnAction(e -> viewModel.open(tableView.getSelectionModel().getSelectedItem()));
        delete = new Button("Delete");
        delete.setOnAction(e -> viewModel.delete(tableView.getSelectionModel().getSelectedItem()));
        HBox buttons = new HBox(10, create, open, delete);
        buttons.setAlignment(Pos.CENTER_LEFT);

        setSpacing(10);
        getChildren().addAll(tableView, buttons);
        setPadding(new Insets(20));
    }

    private void createBindings(PersonListViewModel viewModel) {
        open.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        delete.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        tableView.itemsProperty().bind(viewModel.persons());
    }

}
