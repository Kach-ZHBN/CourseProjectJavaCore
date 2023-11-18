package models;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BankAccountTest {
    private BankAccount bankAccount;

    @Test
    public void testAddMoney() {
        this.bankAccount = new BankAccount("12345-54321", 2500);
        assertEquals(bankAccount.getAmount(), 2500);
        assertTrue(bankAccount.addMoney(0));
        assertEquals(bankAccount.getAmount(), 2500);
        assertTrue(bankAccount.addMoney(500.63));
        assertEquals(bankAccount.getAmount(), 3000.63);
        assertFalse(bankAccount.addMoney(-562));
    }

    @Test
    public void testWithdrawMoney() {
        this.bankAccount = new BankAccount("12345-54321", 2500);
        assertEquals(bankAccount.getAmount(), 2500);
        assertTrue(bankAccount.withdrawMoney(0));
        assertEquals(bankAccount.getAmount(), 2500);
        assertTrue(bankAccount.withdrawMoney(254.84));
        assertEquals(bankAccount.getAmount(), 2245.16);
        assertFalse(bankAccount.withdrawMoney(-154.3));
    }
}