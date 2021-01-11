package app.provider.bestpricedelivery.Bookings;

import android.os.CountDownTimer;

import java.util.Locale;

public class CounterTimer {
    NewBooking mActivity;
    CountDownTimer countDownTimer;
    private String mDefaultFormat = "%02d:%02d:%02d";
    long currentTimerValue = 0;

    public CounterTimer(NewBooking mActivity) {
        this.mActivity = mActivity;
    }

    void startTimer(long startTime) {
//        startTime = startTime + currentTimerValue;
        System.out.println("startTime==" + startTime);

        countDownTimer = new CountDownTimer(startTime, 1000) {
            @Override
            public void onTick(long l) {
                System.out.println("CountDownTimer==" + l);

                currentTimerValue = l;
                updatedText();
            }

            @Override
            public void onFinish() {
                mActivity.acceptRejectOrder("rejected","request_expired");
            }
        }.start();
    }

    void updateTimer(long newValue) {
        System.out.println("currentTimerValue 0=" + newValue);

        newValue = this.currentTimerValue + newValue;
        System.out.println(currentTimerValue + "currentTimerValue 1=" + newValue / 1000);

        countDownTimer.cancel();
        System.out.println("currentTimerValue 2=" + newValue);
        startTimer(newValue);
    }

    void stopTimer() {
        countDownTimer.cancel();
    }

    void updatedText() {
        long seconds = currentTimerValue / 1000;
        int hh = (int) (seconds / 3600);
        int mm = (int) ((seconds % 3600) / 60);
        int ss = (int) (seconds % 60);
        System.out.println("==TIMER" + hh + "     " + mm + "     " + ss);
//        if (mm == 5 && ss == 10) {
////            mActivity.showDialog();
//            mActivity.checkSlotAvailabilityService();
//        }if (mm == 0 && ss ==1) {
////            mActivity.showDialog();
//            mActivity.disconnectCall();
//        }
        String time=String.format(Locale.US, mDefaultFormat, hh, mm, ss);
        String value = "Booking will cancel in XX seconds.";
        value = value.replace("XX",time);
        mActivity.timerText.setText(value);
    }

}
