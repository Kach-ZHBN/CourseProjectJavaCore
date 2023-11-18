package models;

/**
 * Модель банковского аккаунта.
 */
public class BankAccount {
    private final String number;
    private double amount;

    public BankAccount(String accountNumber, double accountAmount) {
        this.number = accountNumber;
        this.amount = accountAmount;
    }

    public String getNumber() {
        return number;
    }

    public double getAmount() {
        return amount;
    }

    public boolean addMoney(double amount) {
        if (amount >= 0) {
            this.amount += amount;
            return true;
        } else
            return false;
    }

    /**
     * Для снятия денег со счета требует их наличие. Если деньги удалось списать, возвращает true.
     * Если деньги списать не удалось, возвращает false без каких-либо изменений в аккаунте.
     */
    public boolean withdrawMoney(double amount) {
        if (amount >= 0 && this.amount >= amount) {
            this.amount -= amount;
            return true;
        } else
            return false;
    }

    @Override
    public java.lang.String toString() {
        return "BankAccount{" +
                "accountNumber='" + number + '\'' +
                ", accountAmount=" + amount +
                '}';
    }
}
