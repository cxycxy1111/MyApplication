package xyz.instmgr.sailfish.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Shopmember.Main.ShopmemberMainActivity;
import xyz.instmgr.sailfish.Util.Ref;
import xyz.instmgr.sailfish.Util.SharePreferenceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignInFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment
        extends Fragment
        implements View.OnClickListener,HttpResultListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "SIGNINFRAGMENT :";

    // TODO: Rename and change types of parameters

    private String mParam1,mParam2;
    private static final int S_ID = 1;
    private static final int SM_ID = 2;
    private long sm_id,s_id;
    private Map<String,String> map = new HashMap<>();
    private EditText et_loginname_a_regist_admin,et_password_a_regist_admin;
    private String loginname,password;
    private Button btn_submit_a_regist_admin;
    private CheckBox cb_is_remember_password;

    private OnFragmentInteractionListener mListener;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1) {
        SignInFragment fragment = new SignInFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login_sign_in_f, container, false);
        initView(view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * 初始化界面
     * @param view
     */
    private void initView(View view) {
        et_loginname_a_regist_admin = (EditText)view.findViewById(R.id.et_loginname_a_signin_admin);
        et_password_a_regist_admin = (EditText)view.findViewById(R.id.et_password_a_signin_admin);
        btn_submit_a_regist_admin = (Button)view.findViewById(R.id.btn_submit_a_signin_admin);
        cb_is_remember_password = (CheckBox)view.findViewById(R.id.cb_is_remember_password_a_signin_admin);
        String is_remember = SharePreferenceManager.getSharePreferenceValue(getActivity(),"login_data","is_remember_password",1);
        if (is_remember.equals("1")) {
            cb_is_remember_password.setChecked(true);
            String login_name = SharePreferenceManager.getSharePreferenceValue(getActivity(),"login_data","login_name",3);
            String password = SharePreferenceManager.getSharePreferenceValue(getActivity(),"login_data","password",3);
            et_loginname_a_regist_admin.setText(login_name);
            et_password_a_regist_admin.setText(password);
        }else {
            cb_is_remember_password.setChecked(false);
        }
        btn_submit_a_regist_admin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_a_signin_admin :
                loginname = et_loginname_a_regist_admin.getText().toString();
                password = et_password_a_regist_admin.getText().toString();
                if (loginname.equals("") || loginname.equals(null) || password.equals("") || password.equals(null)) {
                    Toast.makeText(getContext(),"登录名或密码不能为空",Toast.LENGTH_LONG).show();
                }else {
                    dealWithLoginParamater();
                }
                break;
            default:
                break;
        }
    }

    private void dealWithLoginParamater() {
        String deviceBrand = Build.BRAND;
        String systemVersion = Build.VERSION.RELEASE;
        String systemModel = Build.MODEL;
        HashMap<String,Object> map_1 = new HashMap<>();
        map_1.put("user_name",loginname);
        map_1.put("password",password);
        map_1.put("system_version",systemVersion);
        map_1.put("system_model",systemModel);
        map_1.put("device_brand",deviceBrand);
        try {
            map_1.put("app_version",getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        HttpCallback callback = new HttpCallback(this,getActivity(),1);
        NetUtil.reqSendPost(getActivity(),"/ShopMemberLogin",map_1,callback);
    }

    /**
     *
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(getActivity(), Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        ViewHandler.snackbarShowTall(getActivity(),getView(),"登录成功");
        String[] keys = new String[] {"id","shop_id","type","user_name"};
        List<Map<String,String>> mapList = null;
        try {
            mapList = JsonUtil.strToListMap(body,keys);
        } catch (IOException e) {
            e.printStackTrace();
        }
        map = mapList.get(0);

        SharedPreferences.Editor editor_sasm = getActivity().getSharedPreferences("sasm",Context.MODE_PRIVATE).edit();
        editor_sasm.clear().apply();
        SharedPreferences.Editor editor_login_data = getActivity().getSharedPreferences("login_data",Context.MODE_PRIVATE).edit();
        editor_login_data.clear().apply();

        SharePreferenceManager.storeSharePreferenceInt(getActivity(),"sasm","sm_type",Integer.valueOf(String.valueOf(map.get("type"))));
        SharePreferenceManager.storeSharePreferenceLong(getActivity(),"sasm","sm_id",Integer.valueOf(String.valueOf(map.get("id"))));
        SharePreferenceManager.storeSharePreferenceLong(getActivity(),"sasm","s_id",Integer.valueOf(String.valueOf(map.get("shop_id"))));
        if (cb_is_remember_password.isChecked()) {
            SharePreferenceManager.storeSharePreferenceInt(getActivity(),"login_data","is_remember_password",1);
            SharePreferenceManager.storeSharePreferenceString(getActivity(),"login_data","login_name",loginname);
            SharePreferenceManager.storeSharePreferenceString(getActivity(),"login_data","password",password);
        }else {
            //SharePreferenceManager.storeSharePreferenceInt(getActivity(),"login_data","is_remember_password",0);
        }

        Intent intent = new Intent(getActivity(), ShopmemberMainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case STATUS_NOT_MATCH:
                ViewHandler.snackbarShowTall(getActivity(),getView(),"登录名与密码不匹配");
                break;
            case NSR:
                ViewHandler.snackbarShowTall(getActivity(),getView(),"登录名与密码不匹配");
                break;
            default:
                ViewHandler.snackbarShowTall(getActivity(),getView(),Ref.UNKNOWN_ERROR);
                break;
        }
    }
}
