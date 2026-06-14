package com.example.mvvm.service;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Dummy repository / backend.
 * The talk's point: UI patterns sit ON TOP of this; the service knows nothing about JavaFX or the View.
 */
public class PersonService {

    private final List<Person> storage = new ArrayList<>();

    public PersonService() {
        storage.add(new Person(UUID.randomUUID(), "Clark", "Smith", null));
        storage.add(new Person(UUID.randomUUID(), "Max", "Mustermann", "mm@example.de"));
    }

    public List<Person> loadAll() {
        checkThread();
        return storage;
    }

    public void save(Person person) {
        checkThread();
        if (person.uid() == null) {
            var toSave = new Person(UUID.randomUUID(), person.firstName(), person.lastName(), person.email());
            System.out.println("[PersonService] insert: " + toSave);
            storage.add(toSave);
        } else {
            System.out.println("[PersonService] update: " + person);
            storage.replaceAll(p ->
                    Objects.equals(p.uid(), person.uid())
                            ? person
                            : p
            );
        }
    }

    public void delete(Person person) {
        checkThread();
        System.out.println("[PersonService] delete: " + person);
        storage.removeIf(p -> Objects.equals(p.uid(), person.uid()));
    }

    private void checkThread() {
        if (Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Should be invoked on the FX thread");
        }
    }
}
