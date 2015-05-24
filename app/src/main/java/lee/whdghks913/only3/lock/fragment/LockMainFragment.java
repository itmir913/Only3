package lee.whdghks913.only3.lock.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.tools.LockTools;
import lee.whdghks913.only3.tools.ServiceTools;

public class LockMainFragment extends Fragment {

    TextView mFormat;
    ButtonFlat mIf;
    SimpleDateFormat mDateFormat;

    public static LockMainFragment newInstance() {
        return new LockMainFragment();
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
        View mView = inflater.inflate(R.layout.fragment_lock_activity_main, container, false);

        mDateFormat = new SimpleDateFormat(LockTools.TimeFormat);

        mFormat = (TextView) mView.findViewById(R.id.mFormat);

        long finishTime = LockTools.getFinishTime(getActivity());
        if (finishTime == -1L) {
            ServiceTools.stopLockSubService(getActivity());
            ServiceTools.stopLockService(getActivity());
            getActivity().finish();
            return mView;
        }

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(finishTime);

        mFormat.setText(String.format(getString(R.string.LockActivity_main_2), mDateFormat.format(mCalendar.getTime())));

        mIf = (ButtonFlat) mView.findViewById(R.id.mIf);
        mIf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinish()) {
                    ServiceTools.stopLockSubService(getActivity());
                    ServiceTools.stopLockService(getActivity());

                    LockTools.removeFinishTime(getActivity());

                    getActivity().finish();
                }
            }
        });

        return mView;
    }

    private boolean isFinish() {
        long finishTime = LockTools.getFinishTime(getActivity());
        long currentTime = System.currentTimeMillis();

        return (currentTime >= finishTime);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFinish()) {
            ServiceTools.stopLockSubService(getActivity());
            ServiceTools.stopLockService(getActivity());

            LockTools.removeFinishTime(getActivity());

            getActivity().finish();
        }
    }

}
