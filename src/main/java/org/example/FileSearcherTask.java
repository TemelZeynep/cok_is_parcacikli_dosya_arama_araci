package org.example;

import java.util.concurrent.Callable;import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileSearcherTask implements Callable <List<String>>{
    private final File file; // Aranacak dosya
    private final String keyword; // Dosyada aranacak kelime

    public FileSearcherTask(File file, String keyword) {
        this.file = file;
        this.keyword = keyword;
    }

    @Override
    public List<String> call() throws Exception {
        List<String> matchingLines = new ArrayList<>(); //Aranacak kelime içeren satırları tutacak liste
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){ // Burda ilgili dosyam satır satır okunacak
            String line;
            int linenumber= 0;
            while((line = reader.readLine()) != null){ //dosyanın her satırı kontrol edilecek
                linenumber ++;
                if (line.contains(keyword)){
                    matchingLines.add("Line" + linenumber + " : " + line);
                }
            }
        }catch (IOException e){
            System.out.println("Dosya okunurken hata oluştu!");
        }
        return List.of();
    }
}
