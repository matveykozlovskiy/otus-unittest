package otus.study.cashmachine.bank.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    AccountDao accountDao = mock(AccountDao.class);

    AccountServiceImpl accountServiceImpl = new AccountServiceImpl(accountDao);

    @Test
    void createAccountMock() {
        Account expectedAccount = new Account(1234, new BigDecimal(1000));
        when(accountDao.saveAccount(any())).thenReturn(expectedAccount);

        Account actualAccount = accountServiceImpl.createAccount(new BigDecimal(1000));

        Assertions.assertEquals(expectedAccount, actualAccount);
    }

    @Test
    void createAccountCaptor() {
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        BigDecimal expectedAccountAmount =  new BigDecimal(1500);

        accountServiceImpl.createAccount(expectedAccountAmount);

        verify(accountDao, times(1)).saveAccount(accountCaptor.capture());

        Assertions.assertEquals(expectedAccountAmount, accountCaptor.getValue().getAmount());
    }

    @Test
    void addSum() {
        Account account = new Account(20L, new BigDecimal("1700"));
        ArgumentCaptor<Long> accountIdCaptor = ArgumentCaptor.forClass(Long.class);

        when(accountDao.getAccount(anyLong())).thenReturn(account);

        accountServiceImpl.putMoney(20L, new BigDecimal("3000"));

        verify(accountDao, times(1)).getAccount(accountIdCaptor.capture());

        Assertions.assertEquals(account.getId(), accountIdCaptor.getValue());
        Assertions.assertEquals(new BigDecimal("4700"), account.getAmount());
    }

    @Test
    void getSum() {
        ArgumentCaptor<Long> accountIdCaptor = ArgumentCaptor.forClass(Long.class);
        Account account = new Account(25L, new BigDecimal("2500"));

        when(accountDao.getAccount(anyLong())).thenReturn(account);

        BigDecimal accountAmountAfterGetMoney = accountServiceImpl.getMoney(25L, new BigDecimal("500"));

        verify(accountDao, times(1)).getAccount(accountIdCaptor.capture());

        Assertions.assertEquals(account.getId(), accountIdCaptor.getValue());
        Assertions.assertEquals(new BigDecimal("2000"), accountAmountAfterGetMoney);
    }

    @Test
    void getAccount() {
        Account account = new Account(10L, new BigDecimal("12500"));
        when(accountDao.getAccount(anyLong())).thenReturn(account);

        Account actualAccount = accountServiceImpl.getAccount(10L);
        Assertions.assertEquals(account, actualAccount);
    }

    @Test
    void checkBalance() {
        Account account = new Account(30L, new BigDecimal("2500"));

        when(accountDao.getAccount(anyLong())).thenReturn(account);

        Assertions.assertEquals(account.getAmount(), accountServiceImpl.checkBalance(account.getId()));
    }
}
