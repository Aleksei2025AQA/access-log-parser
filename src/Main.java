import java.io.File;
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
        }
    }
}