package com.example.mvvm.service;

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
        storage.add(new Person(UUID.randomUUID(), "Clark", "Smith", null, null));
        storage.add(new Person(UUID.randomUUID(), "Max", "Mustermann", "mm@example.de", "+4915123456789"));
    }

    public List<Person> loadAll() {
        return storage;
    }

    public void save(Person person) {
        if (person.getUid() == null) {
            person.setUid(UUID.randomUUID());
            System.out.println("[PersonService] insert: " + person);
            storage.add(person);
        } else {
            System.out.println("[PersonService] update: " + person);
            storage.replaceAll(p ->
                    Objects.equals(p.getUid(), person.getUid())
                            ? person
                            : p
            );
        }
    }

    public void delete(Person person) {
        System.out.println("[PersonService] delete: " + person);
        storage.removeIf(p -> Objects.equals(p.getUid(), person.getUid()));
    }
}
