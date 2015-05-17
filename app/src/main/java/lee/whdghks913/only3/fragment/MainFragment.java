package lee.whdghks913.only3.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFloat;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.service.Only3Service;
import lee.whdghks913.only3.tools.Tools;

public class MainFragment extends Fragment {
    ButtonFloat mButton;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
        // Required empty public constructor
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
        View mView = inflater.inflate(R.layout.fragment_main, container, false);

        mButton = (ButtonFloat) mView.findViewById(R.id.buttonFloat);
        if (Tools.getServiceRunning(getActivity())) {
            // 서비스 실행중
            mButton.setDrawableIcon(getResources().getDrawable(android.R.drawable.ic_media_pause));
        }else{
            mButton.setDrawableIcon(getResources().getDrawable(android.R.drawable.ic_media_play));
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tools.getServiceRunning(getActivity())) {
                    // 서비스 실행중이므로 정지해야 함
                    getActivity().stopService(new Intent(getActivity(), Only3Service.class));
                    mButton.setDrawableIcon(getResources().getDrawable(android.R.drawable.ic_media_play));
                } else {
                    // 서비스 실행이 안되어 있으므로 실행해야 함
                    getActivity().startService(new Intent(getActivity(), Only3Service.class));
                    mButton.setDrawableIcon(getResources().getDrawable(android.R.drawable.ic_media_pause));
                }
            }
        });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
