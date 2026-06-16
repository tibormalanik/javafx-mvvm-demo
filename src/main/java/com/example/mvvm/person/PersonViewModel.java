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
 * - Expose UI state as observable JavaFX Properties (no Nodes here).
 * - Expose derived/computed state via bindings (saveDisabled, status).
 * - Expose commands (save, reset) the View can call without knowing how.
 * <p>
 * What it must NOT do:
 * - import javafx.scene.* (no TextField, no Button, no Stage).
 * - This is exactly what makes the ViewModel unit-testable WITHOUT a UI.
 * <p>
 * The View observes bound properties; it never pushes values into the model
 * by hand. Binding does the wiring once, declaratively.
 */
public class PersonViewModel {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private final PersonService service;

    // --- editable state, exposed as Properties to view ---
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty phone = new SimpleStringProperty("");

    // --- derived state (read-only for the View) ---
    private final StringProperty validationMessage = new SimpleStringProperty("");
    private final BooleanProperty saveDisabled = new SimpleBooleanProperty(true);

    private final Person personToEdit;

    private final Runnable onLeave;

    public PersonViewModel(PersonService service, Person person, Runnable onLeave) {
        this.service = service;
        this.personToEdit = person;
        this.onLeave = onLeave;

        // save is disabled unless the form is valid.
        validationMessage.bind(Bindings.createStringBinding(this::validate, firstName, lastName, email, phone));
        saveDisabled.bind(validationMessage.isNotEmpty());

        fill();
    }

    // --- commands the view invokes
    public void save() {
        if (validate() != null) {
            return;
        }
        service.save(new Person(
                personToEdit != null ? personToEdit.getUid() : null,
                firstName.get().trim(),
                lastName.get().trim(),
                email.get() != null ? email.get().trim() : null,
                phone.get() != null ? phone.get().trim() : null)
        );
        onLeave.run();
    }

    public void cancel() {
        onLeave.run();
    }

    // --- property accessors the View binds to ---
    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public StringProperty validationMessageProperty() {
        return validationMessage;
    }

    public BooleanProperty saveDisabledProperty() {
        return saveDisabled;
    }

    private String validate() {
        if (firstName.get().trim().isEmpty()){
            return "First name is required";
        }
        if (lastName.get().trim().isEmpty()) {
            return "Last name is required";
        }
        if (email.get() != null && !email.get().trim().isEmpty() && !EMAIL_PATTERN.matcher(email.get().trim()).matches()) {
            return "E-mail is invalid";
        }
        if (phone.get() != null && !phone.get().trim().isEmpty() && !phone.get().trim().startsWith("+")) {
            return "Phone must start with +";
        }
        return null;
    }

    private void fill() {
        if (personToEdit != null) {
            firstName.set(personToEdit.getFirstName());
            lastName.set(personToEdit.getLastName());
            email.set(personToEdit.getEmail());
            phone.set(personToEdit.getPhone());
        } else {
            firstName.set("");
            lastName.set("");
            email.set("");
            phone.set("");
        }
    }

}
