import dao.TransactionDAO;
import models.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - вызов операции парсинга файлов перевода из input");
        System.out.println("2 - вызов операции вывода списка всех переводов из файла-отчета");
        System.out.println("3 - вызов операции вывода списка переводов в диапазоне дат");
        int inputString = scanner.nextInt();
        if(inputString == 1){
            parse();
        }else if(inputString == 2){
            getReportFile();
        }else if(inputString == 3){
            System.out.println("Введите диапазон дат для отчета транзакций");
            System.out.print("Введите дату с (YYYY-MM-DD): ");
            LocalDateTime dateFrom = LocalDateTime.parse(scanner.next());
            System.out.print("Введите дату по (YYYY-MM-DD): ");
            LocalDateTime dateTo = LocalDateTime.parse(scanner.next());
            getNotesBetweenDatesFromReportFile(dateFrom, dateTo);
        }else{
            System.out.println("Некорректный ввод");
        }
    }

    private static void parse(){
        InputParser inputParser = new InputParser("src/main/resources/input", "src/main/resources/archive/");
        List<Transaction> transactions = inputParser.getTransactions();
        if(transactions.isEmpty()){
            System.out.println("Каталог input не содержит txt-файлов");
            return;
        }
        for (Transaction transaction : transactions) {
            transaction.execute();
        }
    }

    private static void getReportFile(){
        TransactionDAO transactionDAO = new TransactionDAO();
        List<Transaction> transactions = transactionDAO.getAll();
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private static void getNotesBetweenDatesFromReportFile(LocalDateTime dateFrom, LocalDateTime dateTo){
        TransactionDAO transactionDAO = new TransactionDAO();
        List<Transaction> transactions = transactionDAO.getBetweenDates(dateFrom, dateTo);
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
}
