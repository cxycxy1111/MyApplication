package com.example.dengweixiong.Activity.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private EditText et_loginname_a_regist_admin,et_password_a_regist_admin;
    private String loginname,password;
    private Button btn_submit_a_regist_admin;
    private Context context;
    private Activity activity;

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
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
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
        this.context = context;
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
                    requestShopmember();
                    jumpTo();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 校验
     */
    private void requestShopmember() {
        String add = "/ShopMemberLogin?user_name=" + loginname + "&password=" + password;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(),Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Map<String,String> map = new HashMap<>();
                map = JsonHandler.strToMap(response.body().toString());
                ArrayList<String> keys = MethodTool.getKeys(map);
                ArrayList<String> values = MethodTool.getValues(map,keys);
                if (keys.get(0).equals(Reference.STATUS)) {
                    if (values.get(0).equals("not_match")) {
                        MethodTool.showToast(getActivity(),"登录名与密码不匹配");
                    } else if (values.get(0).equals("no_such_record")){
                        MethodTool.showToast(getActivity(),"登录名与密码不匹配");
                    } else {
                        MethodTool.showToast(getActivity(),Reference.UNKNOWN_ERROR);
                    }
                }else if (keys.get(0).equals("data")) {
                    sm_id = Long.parseLong(values.get(0));
                    storeLoginMessage(loginname,password);
                    requestShop();
                }else {
                    MethodTool.showToast(getActivity(),"内部错误，无法完成请求");
                }
            }
        };
        NetUtil.sendHttpRequest(getContext(),add,callback);
    }

    /**
     * 查询机构ID
     */
    private void requestShop() {
        String url = "/ShopIdQueryByShopMemberId?sm_id=" + sm_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Map<String,String> hashMap = JsonHandler.strToMap(response.body().toString());
                ArrayList<String> keys = MethodTool.getKeys(hashMap);
                ArrayList<String> values = MethodTool.getValues(hashMap,keys);
                //储存舞馆ID
                storeShopAndShopMemberInfo("sasm","s_id", Long.parseLong(values.get(0)));
                //储存教师ID
                storeShopAndShopMemberInfo("sasm","sm_id",sm_id);
                getActivity().finish();
            }
        };
        NetUtil.sendHttpRequest(getContext(),url,callback);
    }

    /**
     * 跳至主界面
     */
    private void jumpTo() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * 储存登录信息
     * @param l_name 登录名
     * @param pwd 密码
     */
    private void storeLoginMessage(String l_name,String pwd) {
        FragmentActivity activity = getActivity();
        SharedPreferences.Editor editor = activity.getSharedPreferences("login_data",Context.MODE_PRIVATE).edit();
        editor.putString("login_name",l_name);
        editor.putString("password",pwd);
        editor.apply();
    }

    /**
     * 储存舞馆信息或教师信息
     * @param file_name 文件名
     * @param key 键
     * @param value 值
     */
    private void storeShopAndShopMemberInfo(String file_name,String key,long value) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(file_name,Context.MODE_PRIVATE).edit();
        editor.putLong(key,value);
        editor.apply();
    }


    /**
     *
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
