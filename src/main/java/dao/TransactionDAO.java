package dao;


import models.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object. Клас-посредник для доступа к БД, который инкапсулирует логику взаимодействия с БД.
 * TransactionDAO работает с таблицей, соответствующей классу Transaction:
 * create table Transaction(
 * id int GENERATED BY DEFAULT AS IDENTITY,
 * date timestamp without time zone,
 * file varchar,
 * accountNumberFrom varchar,
 * accountNumberTo varchar,
 * amount numeric,
 * transactionSuccess boolean,
 * errorMassages varchar
 * )
 */
public class TransactionDAO {
    private final Connection connection = DBConnection.getInstance().connection;

    /**
     * Метод для получения всех транзакций из БД.
     */
    public List<Transaction> getAll() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Transaction");

            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getTimestamp("date").toLocalDateTime(),
                        resultSet.getString("file"),
                        resultSet.getString("accountNumberFrom"),
                        resultSet.getString("accountNumberTo"),
                        resultSet.getDouble("amount"),
                        resultSet.getBoolean("transactionSuccess"),
                        resultSet.getString("errorMassages")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }

    /**
     * Метод для получения транзакци в промежутке дат.
     */
    public List<Transaction> getBetweenDates(LocalDateTime dateFrom, LocalDateTime dateTo) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Transaction WHERE date >= ? AND date <= ?");
            preparedStatement.setTimestamp(1, Timestamp.valueOf(dateFrom));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(dateTo));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getTimestamp("date").toLocalDateTime(),
                        resultSet.getString("file"),
                        resultSet.getString("accountNumberFrom"),
                        resultSet.getString("accountNumberTo"),
                        resultSet.getDouble("amount"),
                        resultSet.getBoolean("transactionSuccess"),
                        resultSet.getString("errorMassages")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }

    /**
     * Метод для добавления транзакции в БД
     */
    public void save(Transaction transaction) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO Transaction(date, file, accountNumberFrom, " +
                            "accountNumberTo, amount, transactionSuccess, errorMassages) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setTimestamp(1, Timestamp.valueOf(transaction.getDateOfTransaction()));
            preparedStatement.setString(2, transaction.getFileName());
            preparedStatement.setString(3, transaction.getAccountNumberFrom());
            preparedStatement.setString(4, transaction.getAccountNumberTo());
            preparedStatement.setDouble(5, transaction.getAmount());
            preparedStatement.setBoolean(6, transaction.isTransactionSuccess());
            preparedStatement.setString(7, transaction.getErrorMassages());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
