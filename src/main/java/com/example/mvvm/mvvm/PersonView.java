package com.example.mvvm.mvvm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
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
public class PersonView {

    private final VBox root;

    public PersonView(PersonViewModel vm) {
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last name");
        TextField ageField = new TextField();
        ageField.setPromptText("Age");

        // THE KEY LINES: two-way bindings. This is the entire wiring.
        firstNameField.textProperty().bindBidirectional(vm.firstNameProperty());
        lastNameField.textProperty().bindBidirectional(vm.lastNameProperty());
        ageField.textProperty().bindBidirectional(vm.ageTextProperty());

        Label fullName = new Label();
        fullName.textProperty().bind(vm.fullNameProperty());  // derived, read-only
        fullName.setStyle("-fx-font-weight: bold;");

        Label status = new Label();
        status.textProperty().bind(vm.statusProperty());      // live validation text
        status.setStyle("-fx-text-fill: #2a6;");

        Button save = new Button("Save");
        save.disableProperty().bind(vm.saveDisabledProperty()); // no handler re-checks!
        save.setOnAction(e -> vm.save());                        // just a command

        Button reset = new Button("Reset");
        reset.setOnAction(e -> vm.load());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("First name:"), firstNameField);
        form.addRow(1, new Label("Last name:"),  lastNameField);
        form.addRow(2, new Label("Age:"),         ageField);
        form.addRow(3, new Label("Full name:"),   fullName);

        HBox buttons = new HBox(10, save, reset);
        buttons.setAlignment(Pos.CENTER_LEFT);

        root = new VBox(14, form, buttons, status);
        root.setPadding(new Insets(18));
    }

    public VBox getRoot() {
        return root;
    }
}
