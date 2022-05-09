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

    ATMachine ATM;

    @Mock
    Bank bank;

    @BeforeEach
    void setUp() {
        bank = Mockito.mock(Bank.class);
        ATM = new ATMachine(bank, Money.DEFAULT_CURRENCY);
        List<BanknotesPack> list = new ArrayList<>();
        list.add(BanknotesPack.create(10, Banknote.PL_10));
        MoneyDeposit moneyDeposit = MoneyDeposit.create(Money.DEFAULT_CURRENCY, list);
        ATM.setDeposit(moneyDeposit);
    }

    @Test
    void withdrawSomeCashTest() throws ATMOperationException, AuthorizationException {
        PinCode pinCode = PinCode.createPIN(1, 2, 3, 4);
        Card card = Card.create("123");
        Mockito.when(bank.authorize(pinCode.getPIN(), card.getNumber())).thenReturn(AuthorizationToken.create("12345"));
        ATM.withdraw(pinCode, card, new Money(new BigDecimal(10), Money.DEFAULT_CURRENCY));
    }

}
