package xyz.institutionmanager.sailfish.Member.Main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Member.Profile.MemberCard.MMemberCardListActivity;
import xyz.institutionmanager.sailfish.Member.Profile.MyProfile.MMemberDetailActivity;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;


public class MMyFragment extends BaseFragment implements View.OnClickListener,HttpResultListener {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private RelativeLayout rl_logout,rl_my_membercard,rl_my_profile;
    private View view;
    private AlertDialog dialog_logout;

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
        rl_my_profile = (RelativeLayout)view.findViewById(R.id.rl_my_profile_f_m_my);
        initAlert();
        rl_my_profile.setOnClickListener(this);
        rl_logout.setOnClickListener(this);
        rl_my_membercard.setOnClickListener(this);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void initAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("退出登录");
        builder.setMessage("退出登录后，程序将自动退出，你需要重新打开程序。");
        builder.setPositiveButton("退出登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getProgressBar(getActivity()).setVisibility(View.VISIBLE);
                logout();
                dialog_logout.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getProgressBar(getActivity()).setVisibility(View.GONE);
                dialog_logout.dismiss();
            }
        });
        dialog_logout = builder.create();
        dialog_logout.hide();
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
            case R.id.rl_my_profile_f_m_my:
                Intent intent_1 = new Intent(getActivity(), MMemberDetailActivity.class);
                startActivity(intent_1);
                break;
            case R.id.rl_logout_f_m_my:
                dialog_logout.show();
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
        HttpCallback callback = new HttpCallback(this,getActivity(),0);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    @Override
    public void onRespStatus(String body, int source) {
        ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
        switch (NetRespStatType.dealWithRespStat(body)) {
            case SUCCESS:
                ViewHandler.toastShow(getActivity(),"退出成功");
                ActivityMgr.removeAllActivity();
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {

    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
        ViewHandler.toastShow(getActivity(), Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }
}
