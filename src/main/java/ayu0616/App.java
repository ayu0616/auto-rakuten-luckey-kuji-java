package ayu0616;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
// import org.openqa.selenium.JavascriptExecutor;

public class App {
    public static void main(String[] args) throws Exception {
        if (!internetIsAvailable()) {
            System.out.println("インターネットに接続されていません");
            return;
        }

        String urlListPath = "/Users/OgawaAyumu/Desktop/auto-rakuten-lucky-kuji/kuji_list.txt";
        List<String> kujiUrlList = readKujiList(urlListPath);

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200",
                "--ignore-certificate-errors", "--silent");

        WebDriver driver = new ChromeDriver(chromeOptions);
        // JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        // ログインする
        System.out.println("ログイン開始");
        int errorNum = 0;
        while (true) {
            try {
                login(driver);
                break;
            } catch (Exception e) {
                errorNum++;
                if (errorNum >= 2) {
                    break;
                }
            }
        }
        System.out.println("ログイン終了");

        // 日付変更が近いときは日付が変わるまで待機する
        Wait wait = new Wait();
        wait.doWait();

        // 実際にくじを引く
        List<String> erroredUrl = new ArrayList<String>(); // 失敗したくじのURLを格納
        NotClosedUrlList notClosedUrlList = new NotClosedUrlList(); // くじのうち閉鎖されていないものを格納するリスト

        System.out.println("1回目");
        int len = kujiUrlList.size();
        for (int i = 0; i < len; i++) {
            String kujiUrl = kujiUrlList.get(i);
            try {
                drawKuji(driver, kujiUrl, notClosedUrlList);
                System.out.printf("%d/%d : succeeded %s\n", i + 1, len, kujiUrl);
            } catch (Exception e) {
                System.out.printf("%d/%d : failed %s\n", i + 1, len, kujiUrl);
                // e.printStackTrace();
                erroredUrl.add(kujiUrl);
            }
        }

        // エラーしたくじをやり直す
        System.out.println("2回目");
        len = erroredUrl.size();
        for (int i = 0; i < len; i++) {
            String kujiUrl = kujiUrlList.get(i);
            try {
                drawKuji(driver, kujiUrl, notClosedUrlList);
                System.out.printf("%d/%d : succeeded %s\n", i + 1, len, kujiUrl);
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.printf("%d/%d : failed %s\n", i + 1, len, kujiUrl);
            }
        }

        notClosedUrlList.updateList(urlListPath);

        // ドライバーを閉じる
        Thread.sleep(60 * 1000);
        driver.quit();
    }

    public static List<String> readKujiList(String pathString) throws IOException {
        Path path = Paths.get(pathString);
        List<String> lines = Files.readAllLines(path); // くじのURLのリスト
        return lines;
    }

    public static void login(WebDriver driver) {
        Env env = new Env();
        driver.get("https://grp01.id.rakuten.co.jp/rms/nid/vc?__event=login&service_id=top");
        driver.findElement(By.cssSelector("#loginInner_u")).sendKeys(env.GMAIL_ADDRESS);
        driver.findElement(By.cssSelector("#loginInner_p")).sendKeys(env.RAKUTEN_PASSWORD);
        driver.findElement(By.cssSelector("#loginInner > p:nth-child(3) > input")).click();
    }

    public static void drawKuji(WebDriver driver, String kujiUrl, NotClosedUrlList notClosedUrlList)
            throws Exception {
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(kujiUrl);
        Thread.sleep(1000 * 2); // リンクが開くまで待機
        String pageUrl = driver.getCurrentUrl();
        notClosedUrlList.push(kujiUrl, pageUrl); // 閉鎖されていないURLかどうかを判定してリストに格納
        driver.findElement(By.cssSelector("#entry")).click(); // くじを引く
    }

    private static boolean internetIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection connection = url.openConnection();
            connection.connect();
            connection.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}
