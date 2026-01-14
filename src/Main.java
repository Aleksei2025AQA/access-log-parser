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
            int maxLength = 0;
            int minLength = Integer.MAX_VALUE;
            String line;

            while ((line = reader.readLine()) != null) {
                lineCount++;
                int length = line.length();

                if (length > 1024) {
                    throw new LineTooLongException("Строка #" + lineCount + " превышает 1024 символа. Длина: " + length);
                }

                if (length > maxLength) {
                    maxLength = length;
                }

                if (length < minLength) {
                    minLength = length;
                }
            }

            reader.close();

            System.out.println("\nСтатистика файла:");
            System.out.println("Общее количество строк: " + lineCount);
            System.out.println("Длина самой длинной строки: " + maxLength);

            if (minLength == Integer.MAX_VALUE) {
                System.out.println("Длина самой короткой строки: 0 (файл пуст)");
            } else {
                System.out.println("Длина самой короткой строки: " + minLength);
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
}