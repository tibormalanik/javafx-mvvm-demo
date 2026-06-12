package com.example.mvvm.mvvm;

import com.example.mvvm.model.Person;
import com.example.mvvm.model.PersonService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The ViewModel: the heart of MVVM.
 *
 * Responsibilities:
 *   - Expose UI state as observable JavaFX Properties (no Nodes here!).
 *   - Expose derived/computed state via bindings (fullName, validation).
 *   - Expose commands (save, reset) the View can call without knowing how.
 *
 * What it must NOT do:
 *   - import javafx.scene.* (no TextField, no Button, no Stage).
 *   - This is exactly what makes the ViewModel unit-testable WITHOUT a UI.
 *
 * The View observes these properties; it never pushes values into the model
 * by hand. Binding does the wiring once, declaratively.
 */
public class PersonViewModel {

    private final PersonService service;

    // --- Editable state, exposed as Properties ---
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName  = new SimpleStringProperty("");
    private final StringProperty ageText    = new SimpleStringProperty("");

    // --- Derived state (read-only for the View) ---
    private final StringProperty fullName   = new SimpleStringProperty("");
    private final StringProperty status      = new SimpleStringProperty("");
    private final BooleanProperty saveDisabled = new SimpleBooleanProperty(true);

    public PersonViewModel(PersonService service) {
        this.service = service;

        // Computed full name: recomputes automatically when inputs change.
        fullName.bind(Bindings.createStringBinding(
                () -> (firstName.get().trim() + " " + lastName.get().trim()).trim(),
                firstName, lastName));

        // Validation: Save is disabled unless the form is valid.
        // Again declarative - no event handlers re-checking by hand.
        saveDisabled.bind(Bindings.createBooleanBinding(
                () -> !isValid(),
                firstName, lastName, ageText));

        status.bind(Bindings.createStringBinding(
                this::validationMessage,
                firstName, lastName, ageText));

        load();
    }

    private boolean isValid() {
        return !firstName.get().trim().isEmpty()
                && !lastName.get().trim().isEmpty()
                && parsedAge() >= 0;
    }

    private String validationMessage() {
        if (firstName.get().trim().isEmpty()) return "First name is required";
        if (lastName.get().trim().isEmpty())  return "Last name is required";
        if (parsedAge() < 0)                   return "Age must be a non-negative number";
        return "Ready to save";
    }

    /** Returns parsed age, or -1 if the text is not a valid non-negative int. */
    private int parsedAge() {
        try {
            int v = Integer.parseInt(ageText.get().trim());
            return v >= 0 ? v : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // --- Commands ---

    public void save() {
        if (!isValid()) return;
        service.save(new Person(
                firstName.get().trim(),
                lastName.get().trim(),
                parsedAge()));
    }

    public void load() {
        Person p = service.load();
        firstName.set(p.firstName());
        lastName.set(p.lastName());
        ageText.set(Integer.toString(p.age()));
    }

    // --- Property accessors the View binds to ---
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty()  { return lastName; }
    public StringProperty ageTextProperty()    { return ageText; }
    public StringProperty fullNameProperty()   { return fullName; }
    public StringProperty statusProperty()     { return status; }
    public BooleanProperty saveDisabledProperty() { return saveDisabled; }
}
