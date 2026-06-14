package com.example.mvvm.person;

import com.example.mvvm.service.Person;
import com.example.mvvm.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PersonViewModelTest {

    private PersonViewModel underTest;

    @Mock
    private PersonService personService;

    @Captor
    private ArgumentCaptor<Person> captorPerson;

    @Test
    public void testCreate() {
        // given
        Runnable onLeave = Mockito.mock(Runnable.class);

        // when
        underTest = new PersonViewModel(personService, null, onLeave, Runnable::run);

        // then
        assertThat(underTest.saveDisabledProperty().get()).isTrue();

        // and when
        underTest.firstNameProperty().setValue("firstName");
        underTest.lastNameProperty().setValue("lastName");
        underTest.emailProperty().setValue("f@l.de");
        underTest.save().join();

        // then
        Mockito.verify(personService).save(captorPerson.capture());
        assertThat(captorPerson.getValue().firstName()).isEqualTo("firstName");
        assertThat(captorPerson.getValue().lastName()).isEqualTo("lastName");
        assertThat(captorPerson.getValue().email()).isEqualTo("f@l.de");
        Mockito.verify(onLeave).run();
    }

    @Test
    public void testUpdate() {
        // given
        Runnable onLeave = Mockito.mock(Runnable.class);
        var person = new Person(UUID.randomUUID(), "John", "Doe", null);

        // when
        underTest = new PersonViewModel(personService, person, onLeave, Runnable::run);

        // then
        assertThat(underTest.saveDisabledProperty().get()).isFalse();

        // and when
        underTest.save();

        // then
        Mockito.verify(personService).save(captorPerson.capture());
        assertThat(captorPerson.getValue().firstName()).isEqualTo("John");
        assertThat(captorPerson.getValue().lastName()).isEqualTo("Doe");
        assertThat(captorPerson.getValue().email()).isNullOrEmpty();
        Mockito.verify(onLeave).run();
    }

    @Test
    public void testCancel() {
        // given
        Runnable onLeave = Mockito.mock(Runnable.class);
        underTest = new PersonViewModel(personService, null, onLeave, Runnable::run);

        // when
        underTest.cancel();

        // then
        Mockito.verify(personService, Mockito.never()).save(captorPerson.capture());
        Mockito.verify(onLeave).run();
    }


    @Test
    public void testValidation() {
        // given
        Runnable onLeave = Mockito.mock(Runnable.class);

        // when
        underTest = new PersonViewModel(personService, null, onLeave, Runnable::run);

        // then
        assertThat(underTest.saveDisabledProperty().get()).isTrue();

        // and when
        underTest.firstNameProperty().setValue("firstName");
        underTest.lastNameProperty().setValue("lastName");
        underTest.emailProperty().setValue("incorrect");

        // then
        assertThat(underTest.saveDisabledProperty().get()).isTrue();

        // and when
        underTest.emailProperty().setValue("f@l.de");

        // then
        assertThat(underTest.saveDisabledProperty().get()).isFalse();
    }

}
