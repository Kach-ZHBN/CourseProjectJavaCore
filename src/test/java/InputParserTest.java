import models.Transaction;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.testng.Assert.*;

public class InputParserTest {

    @Test
    public void testGetTransactions() {
        InputParser inputParser = new InputParser("src/test/resources/input", "src/test/resources/archive/");
        try {
            List<Transaction> transactions = inputParser.getTransactions();
            List<Transaction> transactionsForComparison = List.of(
                    new Transaction("test1.txt", "12345-54321", "98765-56789", 984.35),
                    new Transaction("test3.txt", "21569-98541", "46312-74669", 1000.99),
                    new Transaction("test4.txt", "19856-74368", null, 10.0),
                    new Transaction("test5.txt", null, "96310-99856", 5896.3),
                    new Transaction("test6.txt", "87456-84065", "85036-12035", 0),
                    new Transaction("test7.txt", null, null, 0));
            assertEquals(transactions.size(), transactionsForComparison.size());
            for (int i = 0; i < transactions.size(); i++) {
                assertEquals(transactions.get(i).getFileName(), transactionsForComparison.get(i).getFileName());
                assertEquals(transactions.get(i).getAccountNumberFrom(), transactionsForComparison.get(i).getAccountNumberFrom());
                assertEquals(transactions.get(i).getAccountNumberTo(), transactionsForComparison.get(i).getAccountNumberTo());
                assertEquals(transactions.get(i).getAmount(), transactionsForComparison.get(i).getAmount());
            }
        } finally {
            try {
                Files.move(Paths.get("src/test/resources/archive/test1.txt"), Paths.get("src/test/resources/input/test1.txt"));
                Files.move(Paths.get("src/test/resources/archive/test3.txt"), Paths.get("src/test/resources/input/test3.txt"));
                Files.move(Paths.get("src/test/resources/archive/test4.txt"), Paths.get("src/test/resources/input/test4.txt"));
                Files.move(Paths.get("src/test/resources/archive/test5.txt"), Paths.get("src/test/resources/input/test5.txt"));
                Files.move(Paths.get("src/test/resources/archive/test6.txt"), Paths.get("src/test/resources/input/test6.txt"));
                Files.move(Paths.get("src/test/resources/archive/test7.txt"), Paths.get("src/test/resources/input/test7.txt"));
            } catch (IOException e) {
                fail();
            }
        }
    }
}