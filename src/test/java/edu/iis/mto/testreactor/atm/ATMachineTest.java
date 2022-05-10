package edu.iis.mto.testreactor.atm;

import edu.iis.mto.testreactor.atm.bank.AccountException;
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
import java.util.Currency;
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

    @Test
    void withdraw330withDifferentBanknotesTest() throws ATMOperationException, AuthorizationException {
        Mockito.when(bank.authorize(pinCode.getPIN(), card.getNumber())).thenReturn(authorizationToken);
        List<Banknote> list = Banknote.getDescFor(Money.DEFAULT_CURRENCY);
        for(Banknote b : list){
            banknotes.add(BanknotesPack.create(1, b));
        }
        MoneyDeposit moneyDeposit = MoneyDeposit.create(Money.DEFAULT_CURRENCY, banknotes);
        ATM.setDeposit(moneyDeposit);

        List<BanknotesPack> expectedWithdrawalBanknotes = new ArrayList<>();
        expectedWithdrawalBanknotes.add(BanknotesPack.create(1, Banknote.PL_200));
        expectedWithdrawalBanknotes.add(BanknotesPack.create(1, Banknote.PL_100));
        expectedWithdrawalBanknotes.add(BanknotesPack.create(1, Banknote.PL_20));
        expectedWithdrawalBanknotes.add(BanknotesPack.create(1, Banknote.PL_10));

        Withdrawal expectedWithdrawal = Withdrawal.create(expectedWithdrawalBanknotes);
        Withdrawal actualWithdrawal = ATM.withdraw(pinCode, card, plnGenerate("330"));
        Assertions.assertTrue(compareBanknoteLists(expectedWithdrawal.getBanknotes(), actualWithdrawal.getBanknotes()));
    }

    @Test
    void throwAuthorizationExceptionTest() throws AuthorizationException {
        Mockito.when(bank.authorize(pinCode.getPIN(), card.getNumber())).thenReturn(authorizationToken);

        List<Banknote> list = Banknote.getDescFor(Money.DEFAULT_CURRENCY);
        for(Banknote b : list){
            banknotes.add(BanknotesPack.create(1, b));
        }
        MoneyDeposit moneyDeposit = MoneyDeposit.create(Money.DEFAULT_CURRENCY, banknotes);
        ATM.setDeposit(moneyDeposit);

        ATMOperationException exception = Assertions.assertThrows(ATMOperationException.class, () -> ATM.withdraw(pinCode, card, plnGenerate("3300")));
        Assertions.assertEquals(ErrorCode.WRONG_AMOUNT, exception.getErrorCode());
    }

    @Test
    void throwATMExceptionTest() {
        Money money = plnGenerate("100");

        List<Banknote> list = Banknote.getDescFor(Money.DEFAULT_CURRENCY);
        for(Banknote b : list){
            banknotes.add(BanknotesPack.create(1, b));
        }
        MoneyDeposit moneyDeposit = MoneyDeposit.create(Currency.getInstance("USD"), banknotes);
        ATM.setDeposit(moneyDeposit);

        ATMOperationException exception = Assertions.assertThrows(ATMOperationException.class, () -> ATM.withdraw(pinCode, card, money));
        Assertions.assertEquals(ErrorCode.WRONG_CURRENCY, exception.getErrorCode());
    }

    @Test
    void throwAccountExceptionTest() throws AuthorizationException, AccountException {
        Money money = plnGenerate("100");

        Mockito.when(bank.authorize(pinCode.getPIN(), card.getNumber())).thenReturn(authorizationToken);
        Mockito.doThrow(AccountException.class).when(bank).charge(authorizationToken, money);

        List<Banknote> list = Banknote.getDescFor(Money.DEFAULT_CURRENCY);
        for(Banknote b : list){
            banknotes.add(BanknotesPack.create(1, b));
        }
        MoneyDeposit moneyDeposit = MoneyDeposit.create(Money.DEFAULT_CURRENCY, banknotes);
        ATM.setDeposit(moneyDeposit);

        ATMOperationException exception = Assertions.assertThrows(ATMOperationException.class, () -> ATM.withdraw(pinCode, card, money));
        Assertions.assertEquals(ErrorCode.NO_FUNDS_ON_ACCOUNT, exception.getErrorCode());
    }

    @Test
    void authorizationTest() throws AuthorizationException {
        Mockito.doThrow(AuthorizationException.class).when(bank).authorize(pinCode.getPIN(), card.getNumber());

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

        ATMOperationException exception = Assertions.assertThrows(ATMOperationException.class, () -> ATM.withdraw(pinCode, card, plnGenerate("100")));
        Assertions.assertEquals(ErrorCode.AHTHORIZATION, exception.getErrorCode());
    }

}
