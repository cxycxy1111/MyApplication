package xyz.instmgr.sailfish.Login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Member.Main.MemberMainActivity;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;
import xyz.instmgr.sailfish.Util.SharePreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberSignInFragment extends Fragment implements HttpResultListener {

    private static final String ARG_PARAM1 = "param1";
    private CheckBox cb_is_remember_password;
    private EditText et_login_name;
    private EditText et_password;

    private String mParam1;
    private Context context;
    private Button login_button;

    private OnFragmentInteractionListener mListener;

    public MemberSignInFragment() {
    }

    public static MemberSignInFragment newInstance(String param1) {
        MemberSignInFragment fragment = new MemberSignInFragment();
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
        View view = inflater.inflate(R.layout.activity_member_sign_in_f, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        login_button = (Button)view.findViewById(R.id.btn_submit_a_signin_member);
        et_login_name = (EditText)view.findViewById(R.id.et_loginname_a_signin_member);
        et_password = (EditText)view.findViewById(R.id.et_password_a_signin_member);
        cb_is_remember_password = (CheckBox)view.findViewById(R.id.cb_is_remember_password_a_signin_member);
        if (SharePreferenceManager.getSharePreferenceValue(getActivity(),"member_login_data","is_remember_password",1).equals("1")) {
            cb_is_remember_password.setChecked(true);
            et_login_name.setText(SharePreferenceManager.getSharePreferenceValue(getActivity(),"member_login_data","login_name",3));
            et_password.setText(SharePreferenceManager.getSharePreferenceValue(getActivity(),"member_login_data","password",3));
        }else {
            cb_is_remember_password.setChecked(false);
        }
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginInfoCheck();
            }
        });
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

    private void loginInfoCheck() {
        String user_name = et_login_name.getText().toString();
        String password = et_password.getText().toString();
        String deviceBrand = Build.BRAND;
        String systemVersion = Build.VERSION.RELEASE;
        String systemModel = Build.MODEL;
        HashMap<String,Object> map_1 = new HashMap<>();
        map_1.put("login_name",user_name);
        map_1.put("password",password);
        map_1.put("system_version",systemVersion);
        map_1.put("system_model",systemModel);
        map_1.put("device_brand",deviceBrand);
        try {
            map_1.put("app_version",getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!user_name.equals(null) && !password.equals(null)) {
            HttpCallback callback = new HttpCallback(this,getActivity(),1);
            //NetUtil.reqSendGet(getActivity(),"/memberLogin",callback);
            NetUtil.reqSendPost(getActivity(),"/memberLogin",map_1,callback);
        }else {
            Toast.makeText(getActivity(), Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case STATUS_NOT_MATCH:
                ViewHandler.snackbarShowLow(getActivity(),getView(),"登录名与密码不匹配");
                break;
            default:break;
        }
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.snackbarShowLow(getActivity(),getView(),Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.snackbarShowLow(getActivity(),getView(),Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        ArrayList<Map<String,String>> list = new ArrayList<>();
        String [] keys = new String[]{"id","shop_id"};
        try {
            list = JsonUtil.strToListMap(body,keys);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharePreferenceManager.storeSharePreferenceLong(getActivity(),"sam","m_id",Long.parseLong(String.valueOf(list.get(0).get("id"))));
        SharePreferenceManager.storeSharePreferenceLong(getActivity(),"sam","s_id",Long.parseLong(String.valueOf(list.get(0).get("shop_id"))));
        if (cb_is_remember_password.isChecked()) {
            SharePreferenceManager.storeSharePreferenceInt(getActivity(),"member_login_data","is_remember_password",1);
            SharePreferenceManager.storeSharePreferenceString(getActivity(),"member_login_data","login_name",et_login_name.getText().toString());
            SharePreferenceManager.storeSharePreferenceString(getActivity(),"member_login_data","password",et_password.getText().toString());
        }else {
        }
        Intent intent = new Intent(getActivity(), MemberMainActivity.class);
        startActivity(intent);
    }
}
