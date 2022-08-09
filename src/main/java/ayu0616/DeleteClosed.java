package ayu0616;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DeleteClosed {
    public static void main(String[] args) throws Exception {
        String kujiListPath =
                "/Users/OgawaAyumu/Library/CloudStorage/OneDrive-KyotoUniversity/趣味/プログラミング練習/Java/auto-rakuten-luckey-kuji/kuji_list.txt";
        List<String> kujiUrlList = App.readKujiList(kujiListPath);

        WebDriver driver = new ChromeDriver();

        List<String> newList = new ArrayList<String>();

        for (String kujiUrl : kujiUrlList) {
            driver.get(kujiUrl);
            Thread.sleep(1000);
            String pageUrl = driver.getCurrentUrl();
            boolean isClose = pageUrl.substring(pageUrl.length() - 5).equals("close");
            if (isClose) {
                continue;
            } else {
                newList.add(kujiUrl);
            }
        }

        driver.quit();

        String newListString = String.join("\n", newList);
        FileWriter fileWriter = new FileWriter(kujiListPath);
        PrintWriter pw = new PrintWriter(new BufferedWriter(fileWriter));
        pw.println(newListString);
        pw.flush();
        pw.close();
    }
}
