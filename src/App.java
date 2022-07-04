import java.io.IOException;
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
        List<String> kujiUrlList = readKujiList(
                "/Users/OgawaAyumu/Library/CloudStorage/OneDrive-KyotoUniversity/趣味/プログラミング練習/Java/auto-rakuten-luckey-kuji/kuji_list.txt");

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
        // int urlNum = kujiUrlList.size();
        List<String> erroredUrl = new ArrayList<String>();

        System.out.println("1回目");
        for (String kujiUrl : kujiUrlList) {
            try {
                drawKuji(driver, kujiUrl);
                System.out.println("succeeded " + kujiUrl);
            } catch (Exception e) {
                System.out.println("failed " + kujiUrl);
                // e.printStackTrace();
                erroredUrl.add(kujiUrl);
            }
        }

        // エラーしたくじをやり直す
        System.out.println("2回目");
        for (String kujiUrl : erroredUrl) {
            try {
                drawKuji(driver, kujiUrl);
                System.out.println("succeeded " + kujiUrl);
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("failed " + kujiUrl);
            }
        }

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

    public static void drawKuji(WebDriver driver, String kujiUrl) throws Exception {
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(kujiUrl);
        Thread.sleep(1000 * 2); // リンクが開くまで待機
        driver.findElement(By.cssSelector("#entry")).click(); // くじを引く
    }
}
