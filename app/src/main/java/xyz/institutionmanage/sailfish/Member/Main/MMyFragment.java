package xyz.institutionmanage.sailfish.Member.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.Member.MemberCard.MMemberCardListActivity;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.ActivityManager;
import xyz.institutionmanage.sailfish.Util.BaseFragment;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;


public class MMyFragment extends BaseFragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private RelativeLayout rl_logout,rl_my_membercard;
    private View view;

    private OnFragmentInteractionListener mListener;

    public MMyFragment() {
    }

    public static MMyFragment newInstance(String param1) {
        MMyFragment fragment = new MMyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_m_my_f, container, false);
        rl_logout = (RelativeLayout)view.findViewById(R.id.rl_logout_f_m_my);
        rl_my_membercard = (RelativeLayout)view.findViewById(R.id.rl_my_membercard_f_m_my);
        rl_logout.setOnClickListener(this);
        rl_my_membercard.setOnClickListener(this);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_logout_f_m_my:
                getProgressBar(getActivity()).setVisibility(View.VISIBLE);
                logout();
                break;
            case R.id.rl_my_membercard_f_m_my:
                Intent intent = new Intent(getActivity(), MMemberCardListActivity.class);
                startActivity(intent);
                break;
            default:break;
        }
    }

    private void logout() {
        String url = "/mLogout";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.hideProgressBar(getActivity(),getProgressBar(getActivity()));
                MethodTool.showToast(getActivity(), Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MethodTool.hideProgressBar(getActivity(),getProgressBar(getActivity()));
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showToast(getActivity(),Ref.ALERT_SESSION_EXPIRED);
                                ActivityManager.removeAllActivity();
                                break;
                            case EXE_SUC:
                                MethodTool.showToast(getActivity(),"退出成功");
                                ActivityManager.removeAllActivity();
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                }
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
    }
}
