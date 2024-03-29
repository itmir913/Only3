package lee.whdghks913.only3.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.tools.AlarmTools;
import lee.whdghks913.only3.tools.LockTools;
import lee.whdghks913.only3.tools.ToastTools;

public class LockFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DATE_PICKER_TAG = "DatePicker";
    public static final String TIME_PICKER_TAG = "TimePicker";

    public final int startLockType = 1;
    public final int finishLockType = 2;

    ButtonFlat startLockTime, finishLockTime, nowTime;
    ButtonRectangle mLockStartButton;

    Calendar mCalendar, mStart, mFinish;

    SimpleDateFormat mDateFormat;

    LayoutInflater mInflater;

    public static LockFragment newInstance() {
        return new LockFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_lock, container, false);

        mStart = Calendar.getInstance();
        mFinish = Calendar.getInstance();

        mDateFormat = new SimpleDateFormat(LockTools.TimeFormat);
        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mLockStartButton = (ButtonRectangle) mView.findViewById(R.id.mLockStartButton);
        mLockStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LockTools.isSetAlarm(getActivity())) {
                    showUnLockDialog();
                    return;
                }

                if (isPastCalendar(mStart) || isPastCalendar(mFinish) || (mFinish.getTimeInMillis() < mStart.getTimeInMillis())) {
                    ToastTools.createToast(getActivity(), R.string.do_not_start_full_lock, false);
                    return;
                }

                showLockDialog();
            }
        });

        startLockTime = (ButtonFlat) mView.findViewById(R.id.startLockTime);
        finishLockTime = (ButtonFlat) mView.findViewById(R.id.finishLockTime);
        nowTime = (ButtonFlat) mView.findViewById(R.id.nowTime);

        startLockTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(LockFragment.this, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), false, startLockType);
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(2015, 2020);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getActivity().getSupportFragmentManager(), DATE_PICKER_TAG);
            }
        });

        finishLockTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(LockFragment.this, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), false, finishLockType);
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(2015, 2020);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getActivity().getSupportFragmentManager(), DATE_PICKER_TAG);
            }
        });

        nowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStart = Calendar.getInstance();

                startLockTime.setText(mDateFormat.format(mStart.getTime()));
            }
        });

        return mView;
    }

    private void showLockDialog() {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());

        View mView = mInflater.inflate(R.layout.dialog_warn, null);

        ((TextView) mView.findViewById(R.id.mMainText1)).setText(Html.fromHtml(getString(R.string.LockActivity_warn_1)));
        ((TextView) mView.findViewById(R.id.mMainText2)).setText(Html.fromHtml(getString(R.string.LockActivity_warn_2)));

        mAlertDialog.setView(mView);

        final Dialog mDialog = mAlertDialog.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((ButtonFlat) mView.findViewById(R.id.mCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        ((ButtonFlat) mView.findViewById(R.id.mOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockTools.putFinishTime(getActivity(), mFinish.getTimeInMillis());

                AlarmTools.setLockService(getActivity(), mStart);
                getActivity().finish();

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void showUnLockDialog() {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());

        View mView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_info, null);

        ((TextView) mView.findViewById(R.id.mMainText)).setText(Html.fromHtml(getString(R.string.LockActivity_info)));

        mAlertDialog.setView(mView);

        final Dialog mDialog = mAlertDialog.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((ButtonFlat) mView.findViewById(R.id.mCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        ((ButtonFlat) mView.findViewById(R.id.mOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmTools.cancelLockService(getActivity());
                LockTools.removeFinishTime(getActivity());
                LockTools.removeLockStarted(getActivity());

                alarmCheck();

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day, int type) {
        if (type == startLockType) {
            mStart.set(year, month, day);
        } else if (type == finishLockType) {
            mFinish.set(year, month, day);
        }

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false, false, type);
        timePickerDialog.setVibrate(true);
        timePickerDialog.setCloseOnSingleTapMinute(false);
        timePickerDialog.show(getActivity().getSupportFragmentManager(), TIME_PICKER_TAG);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int type) {
        mCalendar = Calendar.getInstance();

        if (type == startLockType) {
            mStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mStart.set(Calendar.MINUTE, minute);

            if (isPastCalendar(mStart)) {
                ToastTools.createToast(getActivity(), R.string.not_allow_past_time_set, false);
                mStart = Calendar.getInstance();
            }

            startLockTime.setText(mDateFormat.format(mStart.getTime()));

        } else if (type == finishLockType) {
            mFinish.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mFinish.set(Calendar.MINUTE, minute);

            if (isPastCalendar(mFinish)) {
                ToastTools.createToast(getActivity(), R.string.not_allow_past_time_set, false);
                mFinish = Calendar.getInstance();
            } else if (mFinish.getTimeInMillis() < mStart.getTimeInMillis()) {
                ToastTools.createToast(getActivity(), R.string.not_allow_time_set_than_start_time, false);
                mFinish = Calendar.getInstance();
            }

            finishLockTime.setText(mDateFormat.format(mFinish.getTime()));
        }
    }

    /**
     * 과거이면 true, 미래이면 false
     *
     * @param mCalendar
     * @return
     */
    private boolean isPastCalendar(Calendar mCalendar) {
        Calendar myTime = Calendar.getInstance();

        myTime.set(Calendar.SECOND, 0);
        myTime.add(Calendar.MINUTE, -1);
        mCalendar.set(Calendar.SECOND, 0);

        long nowTime = myTime.getTimeInMillis();
        long touchTime = mCalendar.getTimeInMillis();

        long diff = (touchTime - nowTime);

        return (diff < 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        alarmCheck();
    }

    private void alarmCheck() {
        boolean isSet = LockTools.isSetAlarm(getActivity());

        startLockTime.setEnabled(!isSet);
        finishLockTime.setEnabled(!isSet);
        nowTime.setEnabled(!isSet);
    }

}
