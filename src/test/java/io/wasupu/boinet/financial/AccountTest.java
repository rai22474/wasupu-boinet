package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;
import io.wasupu.boinet.eventPublisher.EventPublisher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountTest {

    @Test
    public void shouldDepositMoneyIntoAccount() {
        account.deposit(new BigDecimal(10));

        assertEquals("The balance of the account is not the expected",
            new BigDecimal(10),
            account.getBalance());
    }

    @Test
    public void shouldWithdrawMoneyFromAccount() {
        account.deposit(new BigDecimal(10));
        account.withdraw(new BigDecimal(3));
        assertEquals("The balance of the account is not the expected",
            new BigDecimal(7),
            account.getBalance());
    }

    @Test
    public void it_should_publish_an_event_when_deposit_money() {
        account.deposit(new BigDecimal(10));

        verify(eventPublisher, atLeastOnce()).publish(Map.of(
            "eventType", "deposit",
            "iban", IBAN,
            "amount", new BigDecimal(10),
            "amount.currency", "EUR",
            "balance",
            new BigDecimal(10),
            "balance.currency",
            "EUR"));
    }

    @Before
    public void setupAccount() {
        account = new Account(IBAN, world);
    }

    @Before
    public void setupEventPublisher() {
        when(world.getEvenPublisher()).thenReturn(eventPublisher);
    }


    @Mock
    private EventPublisher eventPublisher;

    private static final String IBAN = "12";

    private Account account;

    @Mock
    private World world;
}


