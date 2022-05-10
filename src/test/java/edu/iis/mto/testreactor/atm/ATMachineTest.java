package edu.iis.mto.testreactor.atm;

import edu.iis.mto.testreactor.atm.bank.AuthorizationException;
import edu.iis.mto.testreactor.atm.bank.AuthorizationToken;
import edu.iis.mto.testreactor.atm.bank.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class ATMachineTest {

    @Mock
    Bank bank;

    ATMachine ATM;
    List<BanknotesPack> banknotes;

    final PinCode pinCode = PinCode.createPIN(1, 2, 3, 4);
    final Card card = Card.create("123");

    @BeforeEach
    void setUp() {
        bank = Mockito.mock(Bank.class);
        ATM = new ATMachine(bank, Money.DEFAULT_CURRENCY);
        banknotes = new ArrayList<>();
    }

    @Test
    void withdrawSomeCashTest() throws ATMOperationException, AuthorizationException {
        banknotes.add(BanknotesPack.create(10, Banknote.PL_10));
        MoneyDeposit moneyDeposit = MoneyDeposit.create(Money.DEFAULT_CURRENCY, banknotes);
        ATM.setDeposit(moneyDeposit);

        Mockito.when(bank.authorize(pinCode.getPIN(), card.getNumber())).thenReturn(AuthorizationToken.create("12345"));
        ATM.withdraw(pinCode, card, new Money(new BigDecimal(10), Money.DEFAULT_CURRENCY));
    }

}
