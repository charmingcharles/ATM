package edu.iis.mto.testreactor.atm;

import edu.iis.mto.testreactor.atm.bank.AuthorizationException;
import edu.iis.mto.testreactor.atm.bank.AuthorizationToken;
import edu.iis.mto.testreactor.atm.bank.Bank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ATMachineTest {

    @Mock
    Bank bank;

    ATMachine ATM;
    List<BanknotesPack> banknotes;

    final PinCode pinCode = PinCode.createPIN(1, 2, 3, 4);
    final Card card = Card.create("123");
    final AuthorizationToken authorizationToken = AuthorizationToken.create("12345");

    Money plnGenerate(String denomination){
        return new Money(new BigDecimal(denomination), Money.DEFAULT_CURRENCY);
    }

    boolean compareBanknoteLists(List<Banknote> a, List<Banknote> b){
        Collections.sort(a);
        Collections.sort(b);
        return a.equals(b);
    }

    @BeforeEach
    void setUp() {
        bank = Mockito.mock(Bank.class);
        ATM = new ATMachine(bank, Money.DEFAULT_CURRENCY);
        banknotes = new ArrayList<>();
    }

    @Test
    void withdraw10PLNTest() throws ATMOperationException, AuthorizationException {
        Mockito.when(bank.authorize(pinCode.getPIN(), card.getNumber())).thenReturn(authorizationToken);

        banknotes.add(BanknotesPack.create(10, Banknote.PL_10));
        MoneyDeposit moneyDeposit = MoneyDeposit.create(Money.DEFAULT_CURRENCY, banknotes);
        ATM.setDeposit(moneyDeposit);

        List<BanknotesPack> expectedWithdrawalBanknotes = new ArrayList<>();
        expectedWithdrawalBanknotes.add(BanknotesPack.create(1, Banknote.PL_10));

        Withdrawal expectedWithdrawal = Withdrawal.create(expectedWithdrawalBanknotes);
        Withdrawal actualWithdrawal = ATM.withdraw(pinCode, card, plnGenerate("10"));
        Assertions.assertEquals(expectedWithdrawal, actualWithdrawal);
    }

    @Test
    void withdraw100with10x10Test() throws ATMOperationException, AuthorizationException {
        Mockito.when(bank.authorize(pinCode.getPIN(), card.getNumber())).thenReturn(authorizationToken);

        banknotes.add(BanknotesPack.create(10, Banknote.PL_10));
        MoneyDeposit moneyDeposit = MoneyDeposit.create(Money.DEFAULT_CURRENCY, banknotes);
        ATM.setDeposit(moneyDeposit);

        List<BanknotesPack> expectedWithdrawalBanknotes = new ArrayList<>();
        expectedWithdrawalBanknotes.add(BanknotesPack.create(10, Banknote.PL_10));

        Withdrawal expectedWithdrawal = Withdrawal.create(expectedWithdrawalBanknotes);
        Withdrawal actualWithdrawal = ATM.withdraw(pinCode, card, plnGenerate("100"));
        Assertions.assertEquals(expectedWithdrawal, actualWithdrawal);
    }

    @Test
    void withdraw1000withDifferentBanknotesTest() throws ATMOperationException, AuthorizationException {
        Mockito.when(bank.authorize(pinCode.getPIN(), card.getNumber())).thenReturn(authorizationToken);

        banknotes.add(BanknotesPack.create(10, Banknote.PL_10));
        List<Banknote> list = Banknote.getDescFor(Money.DEFAULT_CURRENCY);
        for(Banknote b : list){
            banknotes.add(BanknotesPack.create(2, b));
        }
        MoneyDeposit moneyDeposit = MoneyDeposit.create(Money.DEFAULT_CURRENCY, banknotes);
        ATM.setDeposit(moneyDeposit);

        List<BanknotesPack> expectedWithdrawalBanknotes = new ArrayList<>();
        for(Banknote b : list){
            expectedWithdrawalBanknotes.add(BanknotesPack.create(1, b));
        }

        Withdrawal expectedWithdrawal = Withdrawal.create(expectedWithdrawalBanknotes);
        Withdrawal actualWithdrawal = ATM.withdraw(pinCode, card, plnGenerate("880"));
        Assertions.assertTrue(compareBanknoteLists(expectedWithdrawal.getBanknotes(), actualWithdrawal.getBanknotes()));
    }

}
