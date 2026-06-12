package com.example.mvvm.model;

/**
 * Plain domain model. No JavaFX, no UI concerns.
 * This is intentionally a record: it is immutable data the rest of the
 * application moves around. The ViewModel adapts it to observable Properties.
 */
public record Person(String firstName, String lastName, int age) {

    public static Person empty() {
        return new Person("", "", 0);
    }
}
