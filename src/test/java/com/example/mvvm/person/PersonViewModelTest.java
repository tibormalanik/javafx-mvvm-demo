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
        underTest = new PersonViewModel(personService, null, onLeave);

        // then
        assertThat(underTest.saveDisabledProperty().get()).isTrue();

        // and when
        underTest.firstNameProperty().setValue("firstName");
        underTest.lastNameProperty().setValue("lastName");
        underTest.emailProperty().setValue("f@l.de");
        underTest.phoneProperty().setValue("+4915123456789");
        underTest.save();

        // then
        Mockito.verify(personService).save(captorPerson.capture());
        assertThat(captorPerson.getValue().getFirstName()).isEqualTo("firstName");
        assertThat(captorPerson.getValue().getLastName()).isEqualTo("lastName");
        assertThat(captorPerson.getValue().getEmail()).isEqualTo("f@l.de");
        assertThat(captorPerson.getValue().getPhone()).isEqualTo("+4915123456789");
        Mockito.verify(onLeave).run();
    }

    @Test
    public void testUpdate() {
        // given
        Runnable onLeave = Mockito.mock(Runnable.class);
        var person = new Person(UUID.randomUUID(), "John", "Doe", null, null);

        // when
        underTest = new PersonViewModel(personService, person, onLeave);

        // then
        assertThat(underTest.saveDisabledProperty().get()).isFalse();

        // and when
        underTest.save();

        // then
        Mockito.verify(personService).save(captorPerson.capture());
        assertThat(captorPerson.getValue().getFirstName()).isEqualTo("John");
        assertThat(captorPerson.getValue().getLastName()).isEqualTo("Doe");
        assertThat(captorPerson.getValue().getEmail()).isNullOrEmpty();
        Mockito.verify(onLeave).run();
    }

    @Test
    public void testCancel() {
        // given
        Runnable onLeave = Mockito.mock(Runnable.class);
        underTest = new PersonViewModel(personService, null, onLeave);

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
        underTest = new PersonViewModel(personService, null, onLeave);

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

    @Test
    public void testPhoneValidation() {
        // given
        Runnable onLeave = Mockito.mock(Runnable.class);

        // when
        underTest = new PersonViewModel(personService, null, onLeave);
        underTest.firstNameProperty().setValue("firstName");
        underTest.lastNameProperty().setValue("lastName");

        // then valid without phone
        assertThat(underTest.saveDisabledProperty().get()).isFalse();

        // and when phone without leading +
        underTest.phoneProperty().setValue("4915123456789");

        // then save is disabled
        assertThat(underTest.saveDisabledProperty().get()).isTrue();
        assertThat(underTest.validationMessageProperty().get()).isEqualTo("Phone must start with +");

        // and when phone starts with +
        underTest.phoneProperty().setValue("+4915123456789");

        // then save is enabled again
        assertThat(underTest.saveDisabledProperty().get()).isFalse();
    }

}
