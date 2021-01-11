package app.provider.bestpricedelivery.Bookings;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import app.provider.bestpricedelivery.R;

/**
 * Created by Admin on 1/10/2018.
 */

public class CancelOrderDialog extends Dialog implements View.OnClickListener {


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkLL1:
                check2.setChecked(false);
                check3.setChecked(false);
                check4.setChecked(false);
                if (check1.isChecked()) {
                    check1.setChecked(false);
                } else {
                    check1.setChecked(true);

                }
                break;
            case R.id.checkLL2:
                check1.setChecked(false);
                check3.setChecked(false);
                check4.setChecked(false);
                if (check2.isChecked()) {
                    check2.setChecked(false);
                } else {
                    check2.setChecked(true);

                }
                break;
            case R.id.checkLL3:
                check2.setChecked(false);
                check1.setChecked(false);
                check4.setChecked(false);
                if (check3.isChecked()) {
                    check3.setChecked(false);
                } else {
                    check3.setChecked(true);

                }
                break;
            case R.id.checkLL4:
                check2.setChecked(false);
                check3.setChecked(false);
                check1.setChecked(false);
                if (check4.isChecked()) {
                    check4.setChecked(false);
                } else {
                    check4.setChecked(true);

                }
                break;
        }
    }

    public enum confirmTask {
        NONE
    }

    ;
    CheckBox check1, check2, check3, check4;
    ConfirmationDialogListener confirmationDialogListener;
    confirmTask task;
    RadioGroup cancelOption;
    TextView noBtn, yesBtn;
    boolean isCancel = true, isConfirm = true;
    LinearLayout checkLL1, checkLL2, checkLL3, checkLL4;

    public CancelOrderDialog(@NonNull Context context,
                             ConfirmationDialogListener confirmationDialogListener,
                             confirmTask task) {
        super(context);
        this.confirmationDialogListener = confirmationDialogListener;
        this.task = task;
        init(context);
    }

    public CancelOrderDialog(@NonNull Context context,
                             ConfirmationDialogListener confirmationDialogListener,
                             confirmTask task, String message, String title, String noStr,
                             String yesStr, boolean isCancel, boolean isConfirm) {
        super(context);
        this.confirmationDialogListener = confirmationDialogListener;
        this.task = task;
        this.isCancel = isCancel;
        this.isConfirm = isConfirm;
        init(context);
    }


    public CancelOrderDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CancelOrderDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected CancelOrderDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(final Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.cancel_order_dialog_layout);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cancelOption = (RadioGroup) findViewById(R.id.cancelOption);
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check4 = findViewById(R.id.check4);
        checkLL1 = findViewById(R.id.checkLL1);
        checkLL2 = findViewById(R.id.checkLL2);
        checkLL3 = findViewById(R.id.checkLL3);
        checkLL4 = findViewById(R.id.checkLL4);
        noBtn = (TextView) findViewById(R.id.cancelTxt);
        yesBtn = (TextView) findViewById(R.id.confirmTxt);
        checkLL1.setOnClickListener(this);
        checkLL2.setOnClickListener(this);
        checkLL3.setOnClickListener(this);
        checkLL4.setOnClickListener(this);

        if (isCancel)
            noBtn.setVisibility(View.VISIBLE);
        else
            noBtn.setVisibility(View.GONE);


        if (isConfirm)
            yesBtn.setVisibility(View.VISIBLE);
        else
            yesBtn.setVisibility(View.GONE);


        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                System.out.println("yesBtn ==" + task);
                if (check1.isChecked()) {
                    confirmationDialogListener.onYesButtonClicked(task, "I have another order.");
                } else if (check2.isChecked()) {
                    confirmationDialogListener.onYesButtonClicked(task, "Out of stock.");
                } else if (check3.isChecked()) {
                    confirmationDialogListener.onYesButtonClicked(task, "Struck in the traffic.");
                } else if (check4.isChecked()) {
                    confirmationDialogListener.onYesButtonClicked(task, "Wont be able to make it on time.");
                } else {
                    Toast.makeText(context, "Please select an option.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                System.out.println("noBtn ==" + task);
                confirmationDialogListener.onNoButtonClicked(task);
            }
        });
        show();
    }

    public interface ConfirmationDialogListener {
        public void onYesButtonClicked(confirmTask task, String msg);

        public void onNoButtonClicked(confirmTask task);
    }
}
