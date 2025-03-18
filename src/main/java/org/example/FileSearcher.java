package org.example;

import java.io.BufferedReader;
import java.io.File; //File sınıfı, java.io paketine ait bir sınıftır.
// Hem dosyaları hem de klasörleri temsil eder.
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ExecutorService;

public class FileSearcher {

    private final ExecutorService executorService;

    public FileSearcher(int threadCount) {
        this.executorService = Executors.newFixedThreadPool(threadCount); // Belirtilen sayıda iş parçacığı oluşturacak.
    }

    // Belirtilen dizindeki tüm dosyaları ve alt dizinlerdeki dosyaları listeleyen metot
    public List<File> ListFilesRecurcively(File directory) {
        List<File> fileList = new ArrayList<>();
        File[] files = directory.listFiles(); // Dizindeki dosyalar listelenir.  Sadece dizinler (directories) üzerinde çalışır.
        //listFiles() metodu, bir dizinin içindeki dosya ve klasörleri listelemek için kullanılır.
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) { //file bir dizin (klasör) mü?
                    fileList.addAll(ListFilesRecurcively(file));
                } //bir dizinse, bu metot çağrılarak içindeki tüm dosya ve klasörler bulunur.
                    else{
                        fileList.add(file);
                    }
                }
            }
        return fileList;
    }

    public void searchFiles(List<File> files, String keyword) {
        List<Future<List<String>>> results = new ArrayList<>();

//Bu döngü, her dosyada arama işlemini farklı iş parçacıklarında paralel olarak çalıştırır
        for (File file : files) { // files bir dosya listesi - her adımda file değişkeni bir dosyayı temsil edecek. Bu dosya üzerinde anahtar kelime araması yapılacak.
            FileSearcherTask task = new FileSearcherTask(file, keyword); //FileSearchTask, bir Callable<List<String>> sınıfıdır. - yani bu sınıf bir iş parçacığında çalıştırılabilir
            // sonucunda bir List<String> dönecektir. Jer dosyada anahtar kelimeyi arayacak bir görev (task) oluşturdum.
            Future<List<String>> future = executorService.submit(task); //executor.submit(task) ile FileSearchTask iş parçacığına gönderecek. Task'ı bir iş parçacığında çalıştırmaya başlar
            results.add(future);
        }
        for (int i = 0; i < files.size(); i++) {
            try {
                List<String> matches = results.get(i).get();
                if (!matches.isEmpty()) {
                    System.out.println("Dosya: " + files.get(i).getAbsolutePath());
                    for (String line : matches) {
                        System.out.println(" " + line);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Thread Hatası : " + e.getMessage());
            }

        }


    }

    public void shutdown() {
        executorService.shutdown();
    }

    // Bu metod, dosyayı okuyup her satırı kontrol eder ve eşleşen satırları bir listeye ekler.
    public List<String> searchInFile(File file, String keyword) {
        List<String> matchingLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null){
                lineNumber++;
                if (line.contains(keyword)) {
                    matchingLines.add("Line" + lineNumber + ": " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Hata! dosya okunurken : " + e.getMessage());
        }
        return matchingLines;
    }
}

