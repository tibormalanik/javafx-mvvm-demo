package com.example.mvvm.person;

import com.example.mvvm.service.Person;
import com.example.mvvm.service.PersonService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.regex.Pattern;

/**
 * The ViewModel: the heart of MVVM.
 * <p>
 * Responsibilities:
 * - Expose UI state as observable JavaFX Properties (no Nodes here!).
 * - Expose derived/computed state via bindings (fullName, validation).
 * - Expose commands (save, reset) the View can call without knowing how.
 * <p>
 * What it must NOT do:
 * - import javafx.scene.* (no TextField, no Button, no Stage).
 * - This is exactly what makes the ViewModel unit-testable WITHOUT a UI.
 * <p>
 * The View observes these properties; it never pushes values into the model
 * by hand. Binding does the wiring once, declaratively.
 */
public class PersonViewModel {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private final PersonService service;

    // --- Editable state, exposed as Properties to view ---
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");

    // --- Derived state (read-only for the View) ---
    private final StringProperty status = new SimpleStringProperty("");
    private final BooleanProperty saveDisabled = new SimpleBooleanProperty(true);

    private final Person personToEdit;

    private final Runnable onLeave;

    public PersonViewModel(PersonService service, Person person, Runnable onLeave) {
        this.service = service;
        this.personToEdit = person;
        this.onLeave = onLeave;

        // save is disabled unless the form is valid.
        saveDisabled.bind(Bindings.createBooleanBinding(() -> validationMessage() != null, firstName, lastName, email));

        status.bind(Bindings.createStringBinding(this::validationMessage, firstName, lastName, email));

        fill();
    }

    private String validationMessage() {
        if (firstName.get().trim().isEmpty()){
            return "First name is required";
        }
        if (lastName.get().trim().isEmpty()) {
            return "Last name is required";
        }
        if (email.get() != null && !email.get().trim().isEmpty() && !EMAIL_PATTERN.matcher(email.get().trim()).matches()) {
            return "E-mail is invalid";
        }
        return null;
    }

    private void fill() {
        if (personToEdit != null) {
            firstName.set(personToEdit.getFirstName());
            lastName.set(personToEdit.getLastName());
            email.set(personToEdit.getEmail());
        } else {
            firstName.set("");
            lastName.set("");
            email.set("");
        }
    }

    public void save() {
        if (validationMessage() != null) {
            return;
        }
        service.save(new Person(
                personToEdit != null ? personToEdit.getUid() : null,
                firstName.get().trim(),
                lastName.get().trim(),
                email.get() != null ? email.get().trim() : null)
        );
        onLeave.run();
    }

    public void cancel() {
        onLeave.run();
    }

    // --- Property accessors the View binds to ---
    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public BooleanProperty saveDisabledProperty() {
        return saveDisabled;
    }

}
