package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public class TriggeredByBalanceThreshold extends PersonBehaviour {

    public TriggeredByBalanceThreshold(World world,
                                       Person person,
                                       BigDecimal lowerThreshold,
                                       BigDecimal upperThreshold,
                                       RecurrentPayment recurrentPayment) {
        super(world, person);

        this.recurrentPayment = recurrentPayment;
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
    }

    public void tick() {
        if (iHaveLessThan(lowerThreshold)) {
            iWasGoingToCountryside.set(false);
            return;
        }

        if (!iWasGoingToCountryside.get() && !iHaveMoreThan(upperThreshold)) return;

        iWasGoingToCountryside.set(true);
        recurrentPayment.tick();
    }

    private boolean iHaveLessThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(getBalance()) >= 0;
    }

    private BigDecimal getBalance() {
        return getWorld().getBank().getBalance(getPerson().getIban());
    }

    private boolean iHaveMoreThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(getBalance()) < 0;
    }

    private AtomicBoolean iWasGoingToCountryside = new AtomicBoolean(false);


    private RecurrentPayment recurrentPayment;

    private BigDecimal lowerThreshold;
    private BigDecimal upperThreshold;

}
