package com.example.mvvm.personlist;

import com.example.mvvm.service.Person;
import com.example.mvvm.service.PersonService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.Objects;
import java.util.function.Consumer;

public class PersonListViewModel {

    private final PersonService service;

    private Consumer<Person> openPerson;

    private final ListProperty<Person> persons = new SimpleListProperty<>(FXCollections.observableArrayList());

    public PersonListViewModel(PersonService service) {
        this.service = Objects.requireNonNull(service, "service");
        fill();
    }

    public void setOpenPerson(Consumer<Person> openPerson) {
        this.openPerson = Objects.requireNonNull(openPerson, "openPerson");
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
        requireOpenPerson().accept(null);
    }

    public void open(Person person) {
        // Guard against "Open" with no selection (person == null).
        if (person == null) {
            return;
        }
        requireOpenPerson().accept(person);
    }

    public void delete(Person person) {
        // Guard against "Delete" with no selection.
        if (person == null) {
            return;
        }
        service.delete(person);
        fill();
    }

    private Consumer<Person> requireOpenPerson() {
        return Objects.requireNonNull(openPerson,
                "openPerson callback not set; call setOpenPerson(...) first");
    }

}
