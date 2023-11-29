package models;

import dao.BankAccountDAO;
import dao.TransactionDAO;

import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Модель транзакции. На данном классе построена вся логика работы приложения.
 * После парсинга входных файлов создается список транзакций, каждая из которых выполняется.
 * Транзакция управляет переводом средств между счетами.
 */
public class Transaction {
    private LocalDateTime dateOfTransaction;
    private final String fileName;
    /*
    Транзакция может содержать счета, которых не существует, поэтому использую
    номера счетов в строковом виде, а не сами объекты счетов
     */
    private final String accountNumberFrom;
    private final String accountNumberTo;
    private final double amount;
    private boolean transactionSuccess;
    private String errorMassages;


    /**
     * Информация, необходимая для выполнения транзакции.
     */
    public Transaction(String fileName, String accountNumberFrom, String accountNumberTo, double amount) {
        this.fileName = fileName;
        this.accountNumberFrom = accountNumberFrom;
        this.accountNumberTo = accountNumberTo;
        this.amount = amount;
        errorMassages = "";
    }

    public Transaction(LocalDateTime dateOfTransaction, String fileName, String accountNumberFrom, String accountNumberTo, double amount, boolean transactionSuccess, String errorMassages) {
        this.dateOfTransaction = dateOfTransaction;
        this.fileName = fileName;
        this.accountNumberFrom = accountNumberFrom;
        this.accountNumberTo = accountNumberTo;
        this.amount = amount;
        this.transactionSuccess = transactionSuccess;
        this.errorMassages = errorMassages;
    }

    /**
     * Метод, управляющий переводами между счетами.
     * <p>
     * Если выполнить транзакцию возможно. Происходит перевод средств между счетами,
     * обновление банковских аккаунтов в БД, успех выполнения транзакции = true.
     * <p>
     * Если выполнить транзакцию невозможно. Банковские счета остаются неизменными,
     * указывается причина ошибки, успех выполнения транзакции = false.
     * <p>
     * Во всех случаях происходит запись транзакции в БД и файле отчете.
     */
    public boolean execute() {
        this.dateOfTransaction = LocalDateTime.now(); //Время запуска транзакции

        BankAccountDAO bankAccountDAO = new BankAccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();

        BankAccount bankAccountFrom = bankAccountDAO.get(this.accountNumberFrom);
        BankAccount bankAccountTo = bankAccountDAO.get(this.accountNumberTo);

        /*
        Блок проверки условий для выполнения транзакции.
        1. Банковские счета в транзакции должны существовать.
        2. Сумма для перевода должна быть больше нуля
        3. На списываемом счете должны быть средства для списания.
         */
        if (bankAccountFrom == null) {
            this.transactionSuccess = false;
            this.errorMassages = "Не существует банковского счета, с которого необходимо перевести сумму";
        } else if (bankAccountTo == null) {
            this.transactionSuccess = false;
            this.errorMassages = "Не существует банковского счета, на который необходимо перевести сумму";
        } else if (amount <= 0) {
            this.transactionSuccess = false;
            this.errorMassages = "Некорректная сумма для перевода";
        } else if (bankAccountFrom.withdrawMoney(this.amount)) {
            bankAccountTo.addMoney(this.amount);
            this.transactionSuccess = true;
            bankAccountDAO.update(bankAccountFrom);
            bankAccountDAO.update(bankAccountTo);
        } else {
            this.transactionSuccess = false;
            this.errorMassages = "Недостаточно средств для перевода";
        }

        transactionDAO.save(this);//сохранение транзакции в БД
        printToReportFile();//вызов сохранения транзакции в файл

        return this.transactionSuccess;
    }

    /**
     * Метод для добавления транзакции в файл-отчет
     */
    private void printToReportFile() {
        try (FileWriter fileWriter = new FileWriter("src/main/resources/ReportTransactions.txt", true)) {
            fileWriter.write(this.toString() + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDateTime getDateOfTransaction() {
        return dateOfTransaction;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAccountNumberFrom() {
        return accountNumberFrom;
    }

    public String getAccountNumberTo() {
        return accountNumberTo;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isTransactionSuccess() {
        return transactionSuccess;
    }

    public String getErrorMassages() {
        return errorMassages;
    }

    @Override
    public java.lang.String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateOfTransaction.format(dtf) + " | " + fileName + " | " + "transaction from "
                + accountNumberFrom + " to " + accountNumberTo + " " + amount + " | " + transactionSuccess + " " + errorMassages;
    }
}
