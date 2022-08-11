package ayu0616;

import java.util.Date;

public class Wait extends Date {
    public int getSecondsLeft() {
        long now = (long) this.getTime();
        int oneDayMilliSec = 1000 * 60 * 60 * 24; // 1日のミリ秒
        long milliSecLeft = oneDayMilliSec - (now % oneDayMilliSec); // 日付が変わるまでの時間
        return (int) (milliSecLeft / 1000 - 60 * 60 * 9);
    }

    public boolean isShouldWait(int secondsLeft) {
        final int beforeSec = 60 * 15; // 日付が変わる15分前から日付が変わるまでは実行を待機する
        return secondsLeft <= beforeSec;
    }

    public void doWait() throws InterruptedException {
        int secondsLeft = getSecondsLeft();
        if (isShouldWait(secondsLeft)) {
            System.out.println(secondsLeft + "秒待機します");
            Thread.sleep(secondsLeft * 1000);
        } else {
            System.out.println("待機せず実行します");
        }
    }
}
