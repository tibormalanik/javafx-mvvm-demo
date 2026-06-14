package com.example.mvvm.person;

import com.example.mvvm.service.Person;
import com.example.mvvm.service.PersonService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;
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

    // Pragmatic email check: one @ with non-empty, dotted domain. The bounded
    // {2,} TLD and lack of nested quantifiers keep it ReDoS-safe, while no
    // longer rejecting valid long TLDs (.museum, .software, ...).
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final PersonService service;

    // --- editable state, exposed as Properties to view ---
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");

    // --- derived state (read-only for the View) ---
    private final StringProperty validationMessage = new SimpleStringProperty("");
    private final BooleanProperty saveDisabled = new SimpleBooleanProperty(true);

    private final Person personToEdit;

    private final Runnable onLeave;

    public PersonViewModel(PersonService service, Person person, Runnable onLeave) {
        this.service = Objects.requireNonNull(service, "service");
        this.personToEdit = person; // null means "create new"
        this.onLeave = Objects.requireNonNull(onLeave, "onLeave");

        // save is disabled unless the form is valid.
        validationMessage.bind(Bindings.createStringBinding(this::validate, firstName, lastName, email));
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
                safeTrim(firstName.get()),
                safeTrim(lastName.get()),
                emptyToNull(safeTrim(email.get())))
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

    public StringProperty validationMessageProperty() {
        return validationMessage;
    }

    public BooleanProperty saveDisabledProperty() {
        return saveDisabled;
    }

    private String validate() {
        if (safeTrim(firstName.get()).isEmpty()) {
            return "First name is required";
        }
        if (safeTrim(lastName.get()).isEmpty()) {
            return "Last name is required";
        }
        String trimmedEmail = safeTrim(email.get());
        if (!trimmedEmail.isEmpty() && !EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return "E-mail is invalid";
        }
        return null;
    }

    /** Null-safe trim: a null property value behaves like an empty string. */
    private static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private static String emptyToNull(String value) {
        return value.isEmpty() ? null : value;
    }

    private void fill() {
        if (personToEdit != null) {
            // Service fields may be null; normalise to empty so the bound
            // TextFields and validation never see a null String.
            firstName.set(safeTrim(personToEdit.getFirstName()));
            lastName.set(safeTrim(personToEdit.getLastName()));
            email.set(safeTrim(personToEdit.getEmail()));
        } else {
            firstName.set("");
            lastName.set("");
            email.set("");
        }
    }

}
