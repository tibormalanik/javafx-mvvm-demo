package com.example.mvvm.person;

import com.example.mvvm.service.Person;
import com.example.mvvm.service.PersonService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
    private final Executor uiExecutor;

    // --- editable state, exposed as Properties to view ---
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");

    // --- derived state (read-only for the View) ---
    private final StringProperty validationMessage = new SimpleStringProperty("");
    private final BooleanProperty saveDisabled = new SimpleBooleanProperty(true);

    // true while the service call runs on a background thread; the View binds
    // a ProgressBar's visibility to this and the Save button stays disabled.
    private final BooleanProperty busy = new SimpleBooleanProperty(false);

    private final Person personToEdit;

    private final Runnable onLeave;

    public PersonViewModel(PersonService service, Person person, Runnable onLeave, Executor uiExecutor) {
        this.service = service;
        this.personToEdit = person;
        this.onLeave = onLeave;
        this.uiExecutor = uiExecutor;

        // save is disabled unless the form is valid, OR while a save is running.
        validationMessage.bind(Bindings.createStringBinding(this::validate, firstName, lastName, email));
        saveDisabled.bind(validationMessage.isNotEmpty().or(busy));

        fill();
    }

    // --- commands the view invokes
    public CompletableFuture<Void> save() {
        if (validate() != null) {
            return CompletableFuture.completedFuture(null);
        }
        busy.set(true);
        return CompletableFuture.runAsync(() -> {
            service.save(new Person(
                    personToEdit != null ? personToEdit.uid() : null,
                    firstName.get().trim(),
                    lastName.get().trim(),
                    email.get() != null ? email.get().trim() : null)
            );
        }).whenCompleteAsync((ignored, error) -> {
            busy.set(false);
            if (error == null) {
                onLeave.run();
            }
        }, uiExecutor);
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

    public BooleanProperty busyProperty() {
        return busy;
    }

    private String validate() {
        if (firstName.get() == null || firstName.get().trim().isEmpty()){
            return "First name is required";
        }
        if (firstName.get() == null || lastName.get().trim().isEmpty()) {
            return "Last name is required";
        }
        if (email.get() != null && !email.get().trim().isEmpty() && !EMAIL_PATTERN.matcher(email.get().trim()).matches()) {
            return "E-mail is invalid";
        }
        return null;
    }

    private void fill() {
        if (personToEdit != null) {
            firstName.set(personToEdit.firstName());
            lastName.set(personToEdit.lastName());
            email.set(personToEdit.email());
        } else {
            firstName.set("");
            lastName.set("");
            email.set("");
        }
    }

}
