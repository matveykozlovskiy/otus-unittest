package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    @Spy
    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardsDao cardsDao;

    @Mock
    private AccountService accountService;

    @Mock
    private MoneyBoxService moneyBoxService;

    private CashMachineServiceImpl cashMachineService;

    private CashMachine cashMachine = new CashMachine(new MoneyBox());

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
// @TODO create get money test using spy as mock
        Mockito.doReturn(new BigDecimal(1500)).when(cardService).getMoney(any(), any(), any());
        Mockito.when(moneyBoxService.getMoney(any(), anyInt())).thenReturn(List.of(0,1,0,0));

        List<Integer> actualList = cashMachineService.getMoney(cashMachine, "123456", "1234", new BigDecimal(500));

        Assertions.assertEquals(List.of(0,1,0,0), actualList);
    }

    @Test
    void putMoney() {
        List<Integer> notes = List.of(0,0,0,1);

        when(cardsDao.getCardByNumber(any()))
                .thenReturn(new Card(1L, "1234", 1L, TestUtil.getHash("1234")));
        when(accountService.putMoney(1L, new BigDecimal("100"))).thenReturn(BigDecimal.TEN);
        when(cardService.getBalance("1234", "1234")).thenReturn(BigDecimal.ZERO);

        BigDecimal actualAmount = cashMachineService.putMoney(cashMachine, "1234", "1234", notes);

        Assertions.assertEquals(BigDecimal.TEN, actualAmount);
    }

    @Test
    void checkBalance() {
        ArgumentCaptor<String> cardNumberCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> cardPinCaptor = ArgumentCaptor.forClass(String.class);
        String cardNumber = "12345678";
        String pin = "1234";
        Card card = new Card(1L, cardNumber, 1L, TestUtil.getHash(pin));

        when(cardsDao.getCardByNumber(anyString())).thenReturn(card);
        when(accountService.checkBalance(anyLong())).thenReturn(BigDecimal.TEN);

        BigDecimal actualBalance = cashMachineService.checkBalance(cashMachine, "12345678", "1234");

        verify(cardService, only()).getBalance(cardNumberCaptor.capture(), cardPinCaptor.capture());

        Assertions.assertEquals(BigDecimal.TEN, actualBalance);
        Assertions.assertEquals(cardNumber, cardNumberCaptor.getValue());
        Assertions.assertEquals(pin, cardPinCaptor.getValue());
    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn

        Card cardBeforeChanges = new Card(1L, "123456", 1L, TestUtil.getHash("123"));
        Card cardAfterChanges = new Card(1L, "123456", 1L, TestUtil.getHash("321"));

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);

        when(cardsDao.getCardByNumber(anyString())).thenReturn(cardBeforeChanges);

        Assertions.assertTrue(cashMachineService.changePin("123456", "123", "321"));
        verify(cardsDao, times(1)).saveCard(cardCaptor.capture());
        verify(cardService, times(1)).cnangePin("123456", "123", "321");

        Assertions.assertEquals(cardAfterChanges.getPinCode(), cardCaptor.getValue().getPinCode());
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer

        Card card = new Card(1L, "123456", 1L, TestUtil.getHash("123"));

        when(cardsDao.getCardByNumber(anyString())).thenAnswer(invocation -> card);
        when(cardsDao.saveCard(any(Card.class))).thenAnswer(invocation -> card);

        Assertions.assertTrue(cashMachineService.changePin("123456", "123", "321"));

        Assertions.assertEquals(card.getPinCode(),TestUtil.getHash("321"));

    }
}