package com.example.dengweixiong.Fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Activity.Login.LoginActivity;
import com.example.dengweixiong.Activity.Login.RegistAdministraotrActivity;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class RegistFragment
        extends Fragment
        implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private Button btn_register;
    private EditText et_name,et_intro;

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public RegistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment RegistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistFragment newInstance(String param1) {
        RegistFragment fragment = new RegistFragment();
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
        View view = inflater.inflate(R.layout.fragment_regist, container, false);
        btn_register = (Button)view.findViewById(R.id.btn_submit_f_regist);
        et_name = (EditText) view.findViewById(R.id.et_name_f_regist);
        et_intro = (EditText) view.findViewById(R.id.et_intro_f_regist);
        btn_register.setOnClickListener(this);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_f_regist :
                String name = et_name.getText().toString();
                String intro = et_intro.getText().toString();
                Intent intent = new Intent(getActivity(), RegistAdministraotrActivity.class);
                intent.putExtra("shop_name",name);
                intent.putExtra("intro",intro);
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }
}
