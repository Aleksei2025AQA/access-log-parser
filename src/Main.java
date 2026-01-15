import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int fileCounter = 0;

        while (true) {
            System.out.print("Введите путь к файлу: ");
            String filePath = scanner.nextLine();
            File file = new File(filePath);

            boolean fileExists = file.exists();
            boolean isFile = file.isFile();

            if (!fileExists) {
                System.out.println("Файл не существует: " + filePath);
                continue;
            }

            if (!isFile) {
                System.out.println("Указанный путь ведет к папке, а не к файлу: " + filePath);
                continue;
            }

            fileCounter++;
            System.out.println("Путь указан верно");
            System.out.println("Это файл номер " + fileCounter);

            processFile(filePath);

            break;
        }

        scanner.close();
    }

    private static void processFile(String filePath) {
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader reader = new BufferedReader(fileReader);

            int lineCount = 0;
            int googlebotCount = 0;
            int yandexbotCount = 0;
            Statistics statistics = new Statistics();
            String line;

            while ((line = reader.readLine()) != null) {
                lineCount++;

                if (line.length() > 1024) {
                    throw new LineTooLongException("Строка #" + lineCount + " превышает 1024 символа. Длина: " + line.length());
                }

                try {
                    LogEntry logEntry = new LogEntry(line);

                    statistics.addEntry(logEntry);

                    UserAgent userAgent = logEntry.getUserAgent();
                    String agentString = userAgent.getBrowser();

                    if ("Googlebot".equals(agentString)) {
                        googlebotCount++;
                    } else if ("YandexBot".equals(agentString)) {
                        yandexbotCount++;
                    }

                } catch (IllegalArgumentException e) {
                    System.err.println("Ошибка парсинга строки #" + lineCount + ": " + e.getMessage());
                }
            }

            reader.close();

            System.out.println("\nСтатистика файла:");
            System.out.println("Общее количество строк: " + lineCount);

            if (lineCount > 0) {
                double googlebotShare = (double) googlebotCount / lineCount * 100;
                double yandexbotShare = (double) yandexbotCount / lineCount * 100;

                System.out.printf("Запросов от Googlebot: %.2f%%\n", googlebotShare);
                System.out.printf("Запросов от YandexBot: %.2f%%\n", yandexbotShare);

                System.out.println("\nСтатистика трафика:");
                System.out.println("Общий трафик: " + statistics.getTotalTraffic() + " байт");
                System.out.printf("Средний трафик в час: %.2f байт/час\n", statistics.getTrafficRate());

                if (statistics.getMinTime() != null && statistics.getMaxTime() != null) {
                    System.out.println("Период логов: с " + statistics.getMinTime() + " по " + statistics.getMaxTime());
                }
            } else {
                System.out.println("Файл пуст");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}