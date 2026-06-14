package com.example.mvvm.personlist;

import com.example.mvvm.service.Person;
import com.example.mvvm.service.PersonService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class PersonListViewModel {

    private final PersonService service;
    private final Executor uiExecutor;

    private Consumer<Person> openPerson;

    private final ListProperty<Person> persons = new SimpleListProperty<>(FXCollections.observableArrayList());

    public PersonListViewModel(PersonService service, Executor uiExecutor) {
        this.service = service;
        this.uiExecutor = uiExecutor;
        fill();
    }

    public void setOpenPerson(Consumer<Person> openPerson) {
        this.openPerson = openPerson;
    }

    public void refresh() {
        fill();
    }

    private void fill() {
        CompletableFuture.supplyAsync(service::loadAll).thenAcceptAsync(personList -> {
            persons.set(FXCollections.observableArrayList(personList));
        }, uiExecutor);
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
        CompletableFuture.runAsync(() -> {
            service.delete(person);
            fill();
        });
    }

}
