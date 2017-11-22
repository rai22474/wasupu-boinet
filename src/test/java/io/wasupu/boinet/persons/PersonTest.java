package io.wasupu.boinet.persons;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import io.wasupu.boinet.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonTest {

    @Test
    public void testEquals() {
        new EqualsTester().
            addEqualityGroup(new Person("person1",
                    "fullName1",
                    CELL_PHONE,
                    world),
                new Person("person1",
                    "fullName1",
                    CELL_PHONE,
                    world)).

            addEqualityGroup(new Person("person2",
                    "fullName1",
                    CELL_PHONE,
                    world),
                new Person("person2",
                    "fullName1",
                    CELL_PHONE,
                    world)).
            testEquals();
    }

    @Test
    public void shouldNotBeUnemployedAfterYouAreHired() {
        person.youAreHired();

        assertNotNull("Must be not null", person.isUnemployed());
        assertFalse("The person must be hired", person.isUnemployed());
    }

    @Test
    public void shouldDepositASocialSalaryInTheFirstTick() {
        person.tick();

        verify(bank).deposit(IBAN, Person.INITIAL_CAPITAL);
    }

    @Test
    public void shouldNotDepositAnySalaryInOtherTick() {
        person.tick();
        person.tick();

        verify(bank).deposit(IBAN, Person.INITIAL_CAPITAL);
    }

    @Test
    public void shouldPayElectricityOnDay25th() {
        when(bank.contractDebitCard(IBAN)).thenReturn(PAN);
        when(world.findCompany()).thenReturn(company);
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(25));

        person.tick();

        verify(company, atLeastOnce()).buyProduct(eq(PAN), eq(ProductType.ELECTRICITY), pricesCaptor.capture());
        assertTrue("The 25 tick must pay electricity",
            priceBetween(getLastRecordedPrice(pricesCaptor.getAllValues()), new BigDecimal(60), new BigDecimal(120)));
    }

    @Test
    public void shouldNotPayElectricityOnDifferentDayThan25th() {
        when(bank.contractDebitCard(IBAN)).thenReturn(PAN);

        when(world.findCompany()).thenReturn(company);

        IntStream.range(1, 28)
            .filter(day -> day != 25)
            .forEach(dayOfMonth -> {
                when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(dayOfMonth));
                person.tick();
            });

        verify(company, never()).buyProduct(eq(PAN), eq(ProductType.ELECTRICITY), any());
    }

    @Test
    public void shouldPublishPersonStatusOnFirstTick() {
        person.tick();

        verify(eventPublisher).publish("personEventStream", PERSON_INFO);
    }

    @Test
    public void shouldPublishPersonStatusAt30Ticks() {
        when(world.getBank()).thenReturn(bank);
        when(bank.contractAccount()).thenReturn(IBAN);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
        when(world.findCompany()).thenReturn(company);

        IntStream.range(0, 31).forEach(i -> person.tick());

        verify(eventPublisher, times(2)).publish("personEventStream", PERSON_INFO);
    }


    @Before
    public void setupEventPublisher() {
        when(world.getEventPublisher()).thenReturn(eventPublisher);
    }

    @Before
    public void setupPerson() {
        person = new Person(IDENTIFIER, FULL_NAME, CELL_PHONE, world);
    }

    @Before
    public void setupAccount() {
        when(world.getBank()).thenReturn(bank);

        when(world.getCurrentDateTime()).thenReturn(new DateTime(CURRENT_DATE));
        when(bank.contractAccount()).thenReturn(IBAN);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
    }

    private boolean priceBetween(BigDecimal bigDecimal, BigDecimal begin, BigDecimal end) {
        return bigDecimal.compareTo(begin) >= 0 && bigDecimal.compareTo(end) <= 0;
    }

    private BigDecimal getLastRecordedPrice(List<BigDecimal> prices) {
        return prices.get(pricesCaptor.getAllValues().size() - 1);
    }


    @Mock
    private World world;

    @Mock
    private Bank bank;

    @Mock
    private Company company;

    @Mock
    private EventPublisher eventPublisher;

    private static final String IDENTIFIER = "personId";

    private Person person;

    private static final String IBAN = "2";

    private static final String PAN = "12312312312";

    private static final Date CURRENT_DATE = new Date();

    private static final String FULL_NAME = "fullName";
    private static final String CELL_PHONE = "686338292";

    private static final Map<String, Object> PERSON_INFO = ImmutableMap
        .<String, Object>builder()
        .put("person", "personId")
        .put("name", FULL_NAME)
        .put("cellPhone", CELL_PHONE)
        .put("balance", new BigDecimal("12"))
        .put("currency", "EUR")
        .put("date", CURRENT_DATE)
        .build();

    @Captor
    private ArgumentCaptor<BigDecimal> pricesCaptor;

}