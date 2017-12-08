package io.wasupu.boinet.population.behaviours.balance;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.EconomicalSubjectBehaviour;

import java.math.BigDecimal;

public class WhenBalanceIsBelowThreshold extends EconomicalSubjectBehaviour {
    public WhenBalanceIsBelowThreshold(World world,
                                       Person person,
                                       BigDecimal threshold,
                                       EconomicalSubjectBehaviour personBehaviour) {
        super(world, person);
        this.threshold = threshold;
        this.personBehaviour = personBehaviour;
    }

    @Override
    public void tick() {
        if (threshold.compareTo(getWorld().getBank().getBalance(getEconomicalSubject().getIban())) > 0) return;

        personBehaviour.tick();
    }

    private BigDecimal threshold;

    private EconomicalSubjectBehaviour personBehaviour;

}
