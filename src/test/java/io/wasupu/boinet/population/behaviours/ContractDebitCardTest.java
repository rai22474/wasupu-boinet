package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContractDebitCardTest {

    @Test
    public void it_should_contract_a_debit_card_in_first_tick() {
        when(person.getAge()).thenReturn(0L);
        when(world.getBank()).thenReturn(bank);
        when(person.getIban()).thenReturn(IBAN);
        when(person.getIdentifier()).thenReturn(IDENTIFIER);
        when(bank.contractDebitCard(IDENTIFIER,IBAN)).thenReturn(PAN);

        contractDebitCard.tick();

        verify(person).setPan(PAN);
    }

    @Test
    public void it_should_not_contract_again_debit_card_if_has_one() {
        when(person.getAge()).thenReturn(1L);

        contractDebitCard.tick();
        verify(person, never()).setPan(any());
    }

    @Before
    public void setupContractDebitCard() {
        contractDebitCard = new ContractDebitCard(world, person);
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    private static final String PAN = "12312312312";

    private static final String IBAN = "2";

    private static final String IDENTIFIER = "234214234";

    private ContractDebitCard contractDebitCard;

}
