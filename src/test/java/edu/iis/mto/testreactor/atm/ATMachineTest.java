package edu.iis.mto.testreactor.atm;

import edu.iis.mto.testreactor.atm.bank.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class ATMachineTest {

    ATMachine ATM;

    @Mock
    Bank bank;

    @BeforeEach
    void setUp() {
        bank = Mockito.mock(Bank.class);
        ATM = new ATMachine(bank, Money.DEFAULT_CURRENCY);
    }

    @Test
    void itCompiles() {

    }

}
