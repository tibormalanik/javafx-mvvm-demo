package com.example.mvvm.model;

/**
 * Stand-in for a repository / backend.
 * The talk's point: UI patterns sit ON TOP of this; the service knows
 * nothing about JavaFX or the View.
 */
public class PersonService {

    private Person stored = new Person("Ada", "Lovelace", 36);

    public Person load() {
        return stored;
    }

    public void save(Person person) {
        this.stored = person;
        // In real life: persist to DB / call REST endpoint, etc.
        System.out.println("[PersonService] saved: " + person);
    }
}
