package com.example.mvvm.mvc;

import com.example.mvvm.model.Person;
import com.example.mvvm.model.PersonService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The SAME feature, done MVC-style (controller-centric, imperative).
 *
 * This is deliberately written the "traditional" way to contrast with MVVM:
 *   - The controller holds references to concrete widgets.
 *   - Every change handler manually recomputes derived state.
 *   - Building/reading the model is hand-wired in multiple places.
 *
 * It works! But notice:
 *   - Logic (validation, full name) is entangled with widget access.
 *   - You cannot unit-test this without spinning up JavaFX nodes.
 *   - Each new field means touching several listeners.
 *
 * MVVM moves all of that into a UI-free ViewModel and replaces the manual
 * recomputation with declarative bindings.
 */
public class PersonControllerView {

    private final VBox root;
    private final PersonService service;

    private final TextField firstNameField = new TextField();
    private final TextField lastNameField  = new TextField();
    private final TextField ageField        = new TextField();
    private final Label fullName            = new Label();
    private final Label status              = new Label();
    private final Button save               = new Button("Save");
    private final Button reset              = new Button("Reset");

    public PersonControllerView(PersonService service) {
        this.service = service;

        firstNameField.setPromptText("First name");
        lastNameField.setPromptText("Last name");
        ageField.setPromptText("Age");
        fullName.setStyle("-fx-font-weight: bold;");
        status.setStyle("-fx-text-fill: #2a6;");

        // Imperative wiring: every field needs a listener that re-runs
        // the SAME recompute logic by hand.
        firstNameField.textProperty().addListener((o, a, b) -> recompute());
        lastNameField.textProperty().addListener((o, a, b) -> recompute());
        ageField.textProperty().addListener((o, a, b) -> recompute());

        save.setOnAction(e -> onSave());
        reset.setOnAction(e -> loadFromModel());

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

        loadFromModel();
    }

    private void loadFromModel() {
        Person p = service.load();
        firstNameField.setText(p.firstName());
        lastNameField.setText(p.lastName());
        ageField.setText(Integer.toString(p.age()));
        recompute();
    }

    /** Manually recompute every piece of derived state on each change. */
    private void recompute() {
        fullName.setText((firstNameField.getText().trim()
                + " " + lastNameField.getText().trim()).trim());

        if (firstNameField.getText().trim().isEmpty()) {
            status.setText("First name is required");
            save.setDisable(true);
        } else if (lastNameField.getText().trim().isEmpty()) {
            status.setText("Last name is required");
            save.setDisable(true);
        } else if (parsedAge() < 0) {
            status.setText("Age must be a non-negative number");
            save.setDisable(true);
        } else {
            status.setText("Ready to save");
            save.setDisable(false);
        }
    }

    private int parsedAge() {
        try {
            int v = Integer.parseInt(ageField.getText().trim());
            return v >= 0 ? v : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void onSave() {
        if (parsedAge() < 0) return;
        service.save(new Person(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                parsedAge()));
    }

    public VBox getRoot() {
        return root;
    }
}
