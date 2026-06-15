package com.example.mvvm.person;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The View in MVVM.
 * <p/>
 * Notice what is NOT here:
 *   - No business logic.
 *   - No "if first name empty then disable button" code.
 *   - No manual reading of TextField text to build a Person.
 * <p/>
 * The View only:
 *   1. Creates Nodes.
 *   2. Binds them to the ViewModel's Properties (two-way for inputs).
 *   3. Routes user gestures to ViewModel commands.
 * <p/>
 * Swap the ViewModel and this View still works. Test the ViewModel and you
 * have tested the logic without ever starting a JavaFX stage.
 */
public class PersonView extends VBox {

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;

    public PersonView(PersonViewModel viewModel) {
        createView(viewModel);
        createBindings(viewModel);
    }

    private void createView(PersonViewModel viewModel) {
        firstNameField = new TextField();
        firstNameField.setPromptText("First name");
        lastNameField = new TextField();
        lastNameField.setPromptText("Last name");
        emailField = new TextField();
        emailField.setPromptText("E-mail");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("First name:"), firstNameField);
        form.addRow(1, new Label("Last name:"),  lastNameField);
        form.addRow(2, new Label("E-mail:"), emailField);

        // live validation text
        Label status = new Label();
        status.textProperty().bind(viewModel.validationMessageProperty());
        status.setStyle("-fx-text-fill: red");

        Button save = new Button("Save");
        save.disableProperty().bind(viewModel.saveDisabledProperty()); // no handler re-checks!
        save.setOnAction(e -> viewModel.save());                        // just a command
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> viewModel.cancel());
        HBox buttons = new HBox(10, save, cancel);
        buttons.setAlignment(Pos.CENTER_LEFT);

        // Indeterminate progress bar shown only while the ViewModel is busy
        // (i.e. the service runs on a background thread). Pure binding, no logic.
        ProgressBar progress = new ProgressBar();
        progress.setMaxWidth(Double.MAX_VALUE);
        progress.visibleProperty().bind(viewModel.busyProperty());
        progress.managedProperty().bind(viewModel.busyProperty());

        setSpacing(10);
        getChildren().addAll(form, status, buttons, progress);
        setPadding(new Insets(20));
    }

    private void createBindings(PersonViewModel viewModel) {
        firstNameField.textProperty().bindBidirectional(viewModel.firstNameProperty());
        lastNameField.textProperty().bindBidirectional(viewModel.lastNameProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
    }

}
