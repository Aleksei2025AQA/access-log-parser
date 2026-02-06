import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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

            System.out.println("\n=== Статистика файла ===");
            System.out.println("Общее количество строк: " + lineCount);

            if (lineCount > 0) {
                double googlebotShare = (double) googlebotCount / lineCount * 100;
                double yandexbotShare = (double) yandexbotCount / lineCount * 100;

                System.out.printf("Запросов от Googlebot: %.2f%%\n", googlebotShare);
                System.out.printf("Запросов от YandexBot: %.2f%%\n", yandexbotShare);

                System.out.println("\n=== Статистика трафика ===");
                System.out.println("Общий трафик: " + statistics.getTotalTraffic() + " байт");
                System.out.printf("Средний трафик в час: %.2f байт/час\n", statistics.getTrafficRate());

                if (statistics.getMinTime() != null && statistics.getMaxTime() != null) {
                    System.out.println("Период логов: с " + statistics.getMinTime() + " по " + statistics.getMaxTime());
                }

                System.out.println("\n=== Новая статистика (Stream API) ===");
                System.out.printf("Среднее количество посещений в час (только пользователи): %.2f\n",
                        statistics.getAverageVisitsPerHour());
                System.out.printf("Среднее количество ошибочных запросов в час: %.2f\n",
                        statistics.getAverageErrorRequestsPerHour());
                System.out.printf("Средняя посещаемость одним пользователем: %.2f\n",
                        statistics.getAverageVisitsPerUser());

                System.out.println("\n=== Результаты задания (Collections) ===");
                Set<String> existingPages = statistics.getAllExistingPages();
                System.out.println("\n1. Существующие страницы (код 200):");
                System.out.println("   Всего: " + existingPages.size() + " уникальных страниц");
                if (!existingPages.isEmpty()) {
                    System.out.println("   Список:");
                    int counter = 1;
                    for (String page : existingPages) {
                        System.out.println("   " + counter + ". " + page);
                        counter++;
                        if (counter > 10) {
                            System.out.println("   ... и еще " + (existingPages.size() - 10) + " страниц");
                            break;
                        }
                    }
                }
                Map<String, Double> osStats = statistics.getOperatingSystemStatistics();
                System.out.println("\n2. Статистика операционных систем (доля):");
                if (!osStats.isEmpty()) {
                    System.out.println("   ОС           | Доля");
                    System.out.println("   -------------|--------");
                    for (Map.Entry<String, Double> entry : osStats.entrySet()) {
                        System.out.printf("   %-12s | %.3f (%.1f%%)\n",
                                entry.getKey(),
                                entry.getValue(),
                                entry.getValue() * 100);
                    }
                    double sum = 0;
                    for (Double value : osStats.values()) {
                        sum += value;
                    }
                    System.out.printf("   Сумма долей: %.4f (должно быть ~1.0000)\n", sum);
                } else {
                    System.out.println("   Нет данных об операционных системах");
                }
                System.out.println("\n=== Несуществующие страницы (код 404) ===");
                Set<String> notFoundPages = statistics.getNotFoundPages();
                System.out.println("Всего несуществующих страниц: " + notFoundPages.size());
                if (!notFoundPages.isEmpty()) {
                    System.out.println("Первые 10 несуществующих страниц:");
                    int counter = 1;
                    for (String page : notFoundPages) {
                        System.out.println("   " + counter + ". " + page);
                        counter++;
                        if (counter > 10) {
                            System.out.println("   ... и еще " + (notFoundPages.size() - 10) + " страниц");
                            break;
                        }
                    }
                }

                System.out.println("\n=== Статистика браузеров ===");
                Map<String, Double> browserStats = statistics.getBrowserStatistics();
                if (!browserStats.isEmpty()) {
                    System.out.println("   Браузер       | Доля");
                    System.out.println("   --------------|--------");
                    for (Map.Entry<String, Double> entry : browserStats.entrySet()) {
                        System.out.printf("   %-13s | %.3f (%.1f%%)\n",
                                entry.getKey(),
                                entry.getValue(),
                                entry.getValue() * 100);
                    }

                    double sum = 0;
                    for (Double value : browserStats.values()) {
                        sum += value;
                    }
                    System.out.printf("   Сумма долей: %.4f\n", sum);
                } else {
                    System.out.println("   Нет данных о браузерах");
                }
            } else {
                System.out.println("Файл пуст");
            }
        } catch (LineTooLongException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
