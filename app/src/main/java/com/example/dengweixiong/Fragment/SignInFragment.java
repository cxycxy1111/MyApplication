package com.example.dengweixiong.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
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


    private void initView(View view) {
        et_loginname_a_regist_admin = (EditText)view.findViewById(R.id.et_loginname_a_signin_admin);
        et_password_a_regist_admin = (EditText)view.findViewById(R.id.et_password_a_signin_admin);
        btn_submit_a_regist_admin = (Button)view.findViewById(R.id.btn_submit_a_signin_admin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_a_signin_admin :
                loginname = et_loginname_a_regist_admin.getText().toString();
                password = et_password_a_regist_admin.getText().toString();
                if (loginname.equals("") | loginname.equals(null) | password.equals("") | password.equals(null)) {
                    Toast.makeText(getContext(),"登录名或密码不能为空",Toast.LENGTH_LONG).show();
                }else {

                }

        }
    }

    private void request() {
        String add = "/ShopMemberLogin?user_name=" + loginname + "&password=" + password;
        Call call = NetUtil.sendHttpRequest(getContext(),add);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), Reference.CANT_CONNECT_INTERNET,Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Map<String,String> map = new HashMap<>();
                map = JsonHandler.strToMap(response);
                String value = map.get("stat");
                if ()
            }
        });

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
