package org.example;
import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Arama yapılacak klasör yolunu giriniz: ");
        String directoryPath = scanner.nextLine();
        System.out.print("Aranacak kelimeyi giriniz: ");
        String keyword = scanner.nextLine();
        System.out.println("Klasör: " + directoryPath + " | Aranacak Kelime: " + keyword);

//belirtilen dizindeki tüm dosyaları tarar, her dosya için anahtar kelimeyi arar ve eşleşen satırları ekrana yazdırır.
        File directory = new File(directoryPath);
        if(!directory.exists() || !directory.isDirectory()){
            System.out.println("Geçerli bir dizin girmediniz!");
            return;
        }
        int threadCount = 4;
        FileSearcher fileSearcher = new FileSearcher(threadCount);

        List<File> allFiles = fileSearcher.ListFilesRecurcively(directory);
        //Tüm dosyaları listele
        System.out.println("Toplam" + allFiles.size() + "dosya bulundu.");

        fileSearcher.searchFiles(allFiles, keyword);
        fileSearcher.shutdown();

    }
}