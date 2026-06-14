package com.example.mvvm.personlist;

import com.example.mvvm.service.Person;
import com.example.mvvm.service.PersonService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.function.Consumer;

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
public class PersonListViewModel {

    private final PersonService service;

    private Consumer<Person> openPerson;

    private final ListProperty<Person> persons = new SimpleListProperty<>(FXCollections.observableArrayList());

    public PersonListViewModel(PersonService service) {
        this.service = service;
        fill();
    }

    public void setOpenPerson(Consumer<Person> openPerson) {
        this.openPerson = openPerson;
    }

    public void refresh() {
        fill();
    }

    private void fill() {
        persons.set(FXCollections.observableArrayList(service.loadAll()));
    }

    public ListProperty<Person> persons() {
        return persons;
    }

    public void create() {
        openPerson.accept(null);
    }

    public void open(Person person) {
        openPerson.accept(person);
    }

    public void delete(Person person) {
        service.delete(person);
        fill();
    }

}
