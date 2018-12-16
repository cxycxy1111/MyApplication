package xyz.instmgr.sailfish.Shopmember.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Shopmember.Profile.Card.CardTypeListActivity;
import xyz.instmgr.sailfish.Shopmember.Profile.Classroom.ClassroomListActivity;
import xyz.instmgr.sailfish.Shopmember.Profile.HelpActivity;
import xyz.instmgr.sailfish.Shopmember.Profile.ShopConfigActivity;
import xyz.instmgr.sailfish.Shopmember.Profile.ShopInfoActivity;
import xyz.instmgr.sailfish.Shopmember.Profile.Shopmember.ShopmemberListActivity;
import xyz.instmgr.sailfish.Util.Ref;
import xyz.instmgr.sailfish.Util.SharePreferenceManager;

import java.io.IOException;

public class PersonFragment
        extends BaseFragment
        implements View.OnClickListener,HttpResultListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "PersonFragment";
    private String sm_type;
    private String mParam1;
    private RelativeLayout rl_shop_info,rl_card_type,rl_teacher,rl_classroom,rl_general_settings,rl_logout,rl_help;
    private ScrollView scrollView;
    private Dialog dialog_logout;
    private OnFragmentInteractionListener mListener;
    private static ShopmemberMainActivity context;

    public PersonFragment() {
    }

    public static PersonFragment newInstance(String param1,ShopmemberMainActivity shopmemberMainActivity) {
        context = shopmemberMainActivity;
        PersonFragment fragment = new PersonFragment();
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
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_person, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_shop_info_f_person:
                intent = new Intent(getActivity(), ShopInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_card_type_f_person:
                intent = new Intent(getActivity(),CardTypeListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_teacher_f_person:
                intent = new Intent(getActivity(),ShopmemberListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_classroom_f_person:
                intent = new Intent(getActivity(),ClassroomListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_shop_config_f_person:
                intent = new Intent(getActivity(),ShopConfigActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_logout_f_person:
                initAlert();
                break;
            case R.id.rl_help_f_person:
                intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            default:break;
        }
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case SUCCESS:
                String is_remember_password = SharePreferenceManager.getSharePreferenceValue(getActivity(),"login_data","is_remember_password",1);
                if (is_remember_password.equals("0")) {
                    clearLoginInfo();
                }
                clearSession();
                ViewHandler.toastShow(getActivity(),"退出成功");
                ActivityMgr.removeAllActivity();
                break;
            case FAIL:
                ViewHandler.toastShow(getActivity(),"退出失败，请重试");
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
        ViewHandler.toastShow(getActivity(), Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }

    private void initAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("退出登录");
        builder.setMessage("退出登录后，程序将自动退出，你需要重新打开程序。");
        builder.setPositiveButton("退出登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
                dialog_logout.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog_logout.dismiss();
            }
        });
        dialog_logout = builder.show();
    }

    private void initViews(View view) {
        sm_type = SharePreferenceManager.getSharePreferenceValue(getActivity(),"sasm","sm_type",1);
        rl_shop_info = (RelativeLayout)view.findViewById(R.id.rl_shop_info_f_person);
        rl_card_type = (RelativeLayout)view.findViewById(R.id.rl_card_type_f_person);
        rl_teacher = (RelativeLayout)view.findViewById(R.id.rl_teacher_f_person);
        rl_classroom = (RelativeLayout)view.findViewById(R.id.rl_classroom_f_person);
        rl_general_settings = (RelativeLayout)view.findViewById(R.id.rl_shop_config_f_person);
        rl_logout = (RelativeLayout)view.findViewById(R.id.rl_logout_f_person);
        rl_help = (RelativeLayout)view.findViewById(R.id.rl_help_f_person);
        scrollView = (ScrollView)view.findViewById(R.id.sv_f_person);
        scrollView.setVerticalScrollBarEnabled(false);

        rl_shop_info.setOnClickListener(this);
        rl_card_type.setOnClickListener(this);
        rl_teacher.setOnClickListener(this);
        rl_classroom.setOnClickListener(this);
        rl_general_settings.setOnClickListener(this);
        rl_logout.setOnClickListener(this);
        rl_help.setOnClickListener(this);

        if (!sm_type.equals("1")) {
            rl_general_settings.setVisibility(View.GONE);
        }
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

    private void logout() {
        String url = "/ShopmemberLogout";
        HttpCallback callback = new HttpCallback(this,getActivity(),0);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    private void clearSession() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Cookies_Prefs",Context.MODE_PRIVATE).edit();
        editor.clear().apply();
    }

    private void clearLoginInfo() {
        SharedPreferences preferences_sasm = getActivity().getSharedPreferences("sasm",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor_sasm = preferences_sasm.edit();
        editor_sasm.clear().apply();
        SharedPreferences preferences_login = getActivity().getSharedPreferences("login_data",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor_login = preferences_login.edit();
        editor_login.clear().apply();
    }
}

