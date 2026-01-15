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
            String line;

            while ((line = reader.readLine()) != null) {
                lineCount++;
                int length = line.length();

                if (length > 1024) {
                    throw new LineTooLongException("Строка #" + lineCount + " превышает 1024 символа. Длина: " + length);
                }

                String userAgent = extractUserAgent(line);
                if (userAgent != null) {
                    if ("Googlebot".equals(userAgent)) {
                        googlebotCount++;
                    } else if ("YandexBot".equals(userAgent)) {
                        yandexbotCount++;
                    }
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
            } else {
                System.out.println("Файл пуст");
            }

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        } catch (LineTooLongException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String extractUserAgent(String logLine) {
        try {
            int startBracket = logLine.indexOf('(');
            int endBracket = logLine.indexOf(')', startBracket);

            if (startBracket == -1 || endBracket == -1) {
                return null;
            }

            String firstBrackets = logLine.substring(startBracket + 1, endBracket);

            String[] parts = firstBrackets.split(";");

            if (parts.length >= 2) {
                String fragment = parts[1].trim();

                int slashIndex = fragment.indexOf('/');
                if (slashIndex != -1) {
                    return fragment.substring(0, slashIndex).trim();
                }
                return fragment;
            }
        } catch (Exception e) {
        }
        return null;
    }
}