package com.example.dengweixiong.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Shopmember.Main.ShopmemberMainActivity;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.Util.SharePreferenceManager;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
        implements View.OnClickListener{
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
                    logIn();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 登录入口
     */
    private void logIn() {
        String add = "/ShopMemberLogin?user_name=" + loginname + "&password=" + password;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(), Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case NOT_MATCH:
                                MethodTool.showToast(getActivity(),"登录名与密码不匹配");
                                break;
                            case NSR:
                                MethodTool.showToast(getActivity(),"登录名与密码不匹配");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getActivity());
                                break;
                            default:
                                MethodTool.showToast(getActivity(), Ref.UNKNOWN_ERROR);
                                break;
                        }
                        break;
                    case RESP_MAPLIST:
                        String[] keys = new String[] {"id","shop_id","type","user_name"};
                        List<Map<String,String>> mapList = JsonHandler.strToListMap(resp,keys);
                        map = mapList.get(0);
                        storeLoginMessage();
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(getContext(),add,callback);
    }


    /**
     * 跳至主界面
     */
    private void jumpTo() {
        Intent intent = new Intent(getActivity(), ShopmemberMainActivity.class);
        startActivity(intent);
    }

    /**
     * 储存登录信息
     */
    private void storeLoginMessage() {
        SharedPreferences.Editor editor_sasm = getActivity().getSharedPreferences("sasm",Context.MODE_PRIVATE).edit();
        editor_sasm.clear().apply();
        SharedPreferences.Editor editor_login_data = getActivity().getSharedPreferences("login_data",Context.MODE_PRIVATE).edit();
        editor_login_data.clear().apply();

        SharePreferenceManager.storeSharePreferenceInt(getActivity(),"sasm","sm_type",Integer.valueOf(String.valueOf(map.get("type"))));
        SharePreferenceManager.storeSharePreferenceLong(getActivity(),"sasm","sm_id",Integer.valueOf(String.valueOf(map.get("id"))));
        SharePreferenceManager.storeSharePreferenceLong(getActivity(),"sasm","s_id",Integer.valueOf(String.valueOf(map.get("shop_id"))));
        SharePreferenceManager.storeSharePreferenceString(getActivity(),"login_data","login_name",loginname);
        SharePreferenceManager.storeSharePreferenceString(getActivity(),"login_data","password",password);

        jumpTo();
    }

    /**
     *
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
