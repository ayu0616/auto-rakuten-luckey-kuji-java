package ayu0616;

import java.util.Date;

public class Wait extends Date {
    public int getSecondsLeft() {
        long now = (long) this.getTime() + 9 * 60 * 60 * 1000;
        int oneDayMilliSec = 1000 * 60 * 60 * 24; // 1日のミリ秒
        long milliSecLeft = oneDayMilliSec - (now % oneDayMilliSec); // 日付が変わるまでの時間
        return (int) (milliSecLeft / 1000);
    }

    public boolean isShouldWait(int secondsLeft) {
        final int beforeSec = 60 * 15; // 日付が変わる15分前から日付が変わるまでは実行を待機する
        return secondsLeft <= beforeSec;
    }

    public void doWait() throws InterruptedException {
        int secondsLeft = getSecondsLeft();
        if (isShouldWait(secondsLeft)) {
            System.out.println(secondsLeft + "秒待機します");
            logWaitSeconds(secondsLeft);
            // Thread.sleep(secondsLeft * 1000);
        } else {
            System.out.println("待機せず実行します");
        }
    }

    public void logWaitSeconds(int secondsLeft) throws InterruptedException {
        for (int i = 0; i < secondsLeft; i++) {
            System.out.printf("\rあと%d秒", secondsLeft - i);
            Thread.sleep(1000);
        }
        System.out.printf("\rあと%d秒\n", 0);
    }
}
