package io.wasupu.boinet.persons;

import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.behaviours.GoToCountrysideBehaviour;
import io.wasupu.boinet.persons.behaviours.MonthlyRecurrentPaymentBehaviour;

import java.math.BigDecimal;
import java.util.Random;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Person {


    public Person(String identifier,
                  String name,
                  String cellPhone,
                  World world) {
        this.identifier = identifier;
        this.name = name;
        this.world = world;
        this.cellPhone = cellPhone;

        this.goToCountryside = new GoToCountrysideBehaviour(world,this);
        this.payElectricity = new MonthlyRecurrentPaymentBehaviour(world,
            this,
            25,
            ProductType.ELECTRICITY,
            60,
            120);

        world.listenTicks(this::tick);
    }

    public void tick() {
        contractAccount();
        initialCapital();
        contractDebitCard();
        eatEveryDay();
        this.payElectricity.tick();
        this.goToCountryside.tick();
        publishPersonBalance();

        age++;
    }

    public Boolean isUnemployed() {
        return !employed;
    }

    public void youAreHired() {
        employed = TRUE;
    }

    public String getIban() {
        return iban;
    }

    public String getPan() {
        return pan;
    }

    private void contractDebitCard() {
        if (age != 0) return;

        pan = world.getBank().contractDebitCard(iban);
    }

    private void contractAccount() {
        if (age != 0) return;

        iban = world.getBank().contractAccount();
    }

    private void initialCapital() {
        if (age != 0) return;

        world.getBank().deposit(iban, INITIAL_CAPITAL);
    }

    private void eatEveryDay() {
        if (age < 2) return;

        world.findCompany().buyProduct(pan, ProductType.MEAL, generateRandomPrice(10, 20));
    }


    private BigDecimal generateRandomPrice(Integer startPrice, Integer endPrice) {
        Random random = new Random();
        double randomValue = startPrice + (endPrice - startPrice) * random.nextDouble();
        return new BigDecimal(randomValue)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private void publishPersonBalance() {
        if (age % 30 != 0) return;

        world.getEventPublisher().publish(STREAM_ID, ImmutableMap
            .<String, Object>builder()
            .put("person", identifier)
            .put("name", name)
            .put("cellPhone", cellPhone)
            .put("balance", world.getBank().getBalance(iban))
            .put("currency", "EUR")
            .put("date", world.getCurrentDateTime().toDate())
            .build());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return identifier.equals(person.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    private String iban;

    private String identifier;

    private String pan;

    private World world;

    private Long age = 0L;

    private Boolean employed = FALSE;

    static final BigDecimal INITIAL_CAPITAL = new BigDecimal(1000);

    private String name;

    private final String cellPhone;

    private static final String STREAM_ID = "personEventStream";

    private final MonthlyRecurrentPaymentBehaviour payElectricity;

    private final GoToCountrysideBehaviour goToCountryside;
}
