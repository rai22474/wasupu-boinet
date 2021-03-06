package io.wasupu.boinet.companies;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.economicalSubjects.EconomicalSubjectType;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.GenerateRandomPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.wasupu.boinet.economicalSubjects.EconomicalSubjectType.COMPANY;

public class Company extends EconomicalSubject {

    public Company(String identifier, World world) {
        super(identifier, world);
        this.name = faker.company().name();
    }

    @Override
    public String getName() {
        return name;
    }

    public void buyProduct(String pan, ProductType productType, BigDecimal price) {
        getWorld().getBank().payWithCard(price,
            pan,
            getIban(),
            getIdentifier(),
            productType.toString().toLowerCase(),
            getWorld().getGPS()
                .coordinatesAround(
                    getCoordinates().getLeft(),
                    getCoordinates().getRight()));
    }

    public void buyHouse(String buyerIban, BigDecimal amount) {
        getWorld().getBank().transfer(buyerIban, getIban(), amount);
    }

    public void requestSalaryRevision(Person person) {
        if (getWorld().getBank().getBalance(getIban()).compareTo(new BigDecimal(6000)) < 0) return;

        var salary = employees.get(person);
        var newSalary = salary.add(salary.multiply(new BigDecimal(0.2))).setScale(2, RoundingMode.CEILING);

        employees.put(person, newSalary);
    }

    public BigDecimal getEmployeeSalary(Person person) {
        return employees.get(person);
    }

    public void hire(Person person) {
        employees.put(person, generateSalary());
        person.youAreHired(this);
    }

    public void payEmployee(Person employee, BigDecimal amount) {
        if (getWorld().getBank().getBalance(getIban()).compareTo(amount) < 0) {
            fire(employee);
            return;
        }

        getWorld().getBank().paySalary(getIban(), employee.getIban(), amount);
    }

    public int getNumberOfEmployees() {
        return employees.size();
    }

    private void fire(Person person) {
        employees.remove(person);
        person.youAreFired();
    }

    @Override
    public EconomicalSubjectType getType() {
        return COMPANY;
    }

    public Map<Person, BigDecimal> getEmployees() {
        return Map.copyOf(employees);
    }

    private BigDecimal generateSalary() {
        return randomDistribution.apply(1800, 2800);
    }

    private Map<Person, BigDecimal> employees = new ConcurrentHashMap<>();

    private String name;

    private GenerateRandomPrice randomDistribution = new GenerateRandomPrice();

    static final BigDecimal MINIMUM_SALARY = new BigDecimal("600");
}
