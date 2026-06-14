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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        underTest.save();

        // then
        Mockito.verify(personService).save(captorPerson.capture());
        assertThat(captorPerson.getValue().getFirstName()).isEqualTo("firstName");
        assertThat(captorPerson.getValue().getLastName()).isEqualTo("lastName");
        assertThat(captorPerson.getValue().getEmail()).isEqualTo("f@l.de");
        Mockito.verify(onLeave).run();
    }

    @Test
    public void testUpdate() {
        // given
        Runnable onLeave = Mockito.mock(Runnable.class);
        var person = new Person(UUID.randomUUID(), "John", "Doe", null);

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
    public void testEditPersonWithNullFieldsDoesNotThrow() {
        // given a Person whose fields are null (the service allows it)
        Runnable onLeave = Mockito.mock(Runnable.class);
        var person = new Person(UUID.randomUUID(), null, null, null);

        // when - constructing must not throw and must not crash validation
        underTest = new PersonViewModel(personService, person, onLeave);

        // then - null fields normalise to empty, so save stays disabled
        assertThat(underTest.firstNameProperty().get()).isEmpty();
        assertThat(underTest.lastNameProperty().get()).isEmpty();
        assertThat(underTest.emailProperty().get()).isEmpty();
        assertThat(underTest.saveDisabledProperty().get()).isTrue();
    }

    @Test
    public void testBlankEmailIsSavedAsNull() {
        // given
        Runnable onLeave = Mockito.mock(Runnable.class);
        underTest = new PersonViewModel(personService, null, onLeave);
        underTest.firstNameProperty().setValue("firstName");
        underTest.lastNameProperty().setValue("lastName");
        underTest.emailProperty().setValue("   "); // whitespace only

        // when
        underTest.save();

        // then - blank email is normalised to null, not "   "
        Mockito.verify(personService).save(captorPerson.capture());
        assertThat(captorPerson.getValue().getEmail()).isNull();
    }

    @Test
    public void testNullCallbackRejectedAtConstruction() {
        assertThatThrownBy(() -> new PersonViewModel(personService, null, null))
                .isInstanceOf(NullPointerException.class);
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

}
