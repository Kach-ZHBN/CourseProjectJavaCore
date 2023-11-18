import models.Transaction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для поиска текстовых файлов в каталоге input.
 * Логика класса: на входе каталог input, на выходе список транзакций (либо пустой список,
 * если в каталоге нет необходимой информации).
 *
 * Текстовые файлы после выполнения перемещаются в архив
 */
public class InputParser {
    private final File INPUT_DIRECTORY;
    private final String ARCHIVE_DIRECTORY;

    public InputParser(String directoryPath, String archiveDirectory) {
        this.INPUT_DIRECTORY = new File(directoryPath);
        ARCHIVE_DIRECTORY = archiveDirectory;
    }

    /**
     * Метод, который возвращает список транзакций полученный после исследования текстовых файлов в папке input.
     */
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        List<File> files = List.of(INPUT_DIRECTORY.listFiles((dir, name) -> name.matches(".+\\.txt")));

        try {
            for (File file : files) {
                transactions.add(getTransactionFromFile(file));
                Files.move(Paths.get(file.getAbsolutePath()),
                        Paths.get(ARCHIVE_DIRECTORY + file.getName()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }

    /**
     * Метод, который создает транзакцию на основании файла.
     */
    private Transaction getTransactionFromFile(File file) throws IOException {
        String textFromFile = Files.readString(Path.of(file.toString()));

        String accountFrom = null;
        String accountTo = null;
        String accountAmount = "0";

        //Номер счета с: XXXXX-XXXXX
        Pattern patternAccountFrom = Pattern.compile("[Н|н]омер счета с: (\\d{5}-\\d{5})");
        Matcher matcherAccountFrom = patternAccountFrom.matcher(textFromFile);
        if (matcherAccountFrom.find()) {
            accountFrom = matcherAccountFrom.group(1);
        }

        //Номер счета на: XXXXX-XXXXX
        Pattern patternAccountTo = Pattern.compile("[Н|н]омер счета на: (\\d{5}-\\d{5})");
        Matcher matcherAccountTo = patternAccountTo.matcher(textFromFile);
        if (matcherAccountTo.find()) {
            accountTo = matcherAccountTo.group(1);
        }

        //Сумма для перевода: double
        Pattern patternAmount = Pattern.compile("[С|с]умма для перевода: (\\d+([\\.|,]\\d+)*)");
        Matcher matcherAmount = patternAmount.matcher(textFromFile);
        if (matcherAmount.find()) {
            //Разделителем целой и дробно части может быть как ',', так и ','.
            accountAmount = matcherAmount.group(1).replaceAll(",", ".");
        }

        return new Transaction(file.getName(), accountFrom, accountTo, Double.parseDouble(accountAmount));
    }
}
