import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class NotClosedUrlList extends ArrayList<String> {
    static boolean isClosed(String url) {
        return url.substring(url.length() - 5).equals("close");
    }

    public void push(String listUrl, String checkUrl) {
        if (!isClosed(checkUrl)) {
            this.add(listUrl);
        }
    }

    public void updateList(String listPath) throws IOException {
        String newListString = String.join("\n", this);
        FileWriter fileWriter = new FileWriter(listPath);
        PrintWriter pw = new PrintWriter(new BufferedWriter(fileWriter));
        pw.println(newListString);
        pw.flush();
        pw.close();
    }
}
