package xyz.institutionmanager.sailfish.Login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;

public class RegistShopFragment
        extends Fragment
        implements View.OnClickListener,HttpResultListener {
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = "RegisF:";
    private static final String ARG_PARAM1 = "param1";
    private Button btn_register;
    private TextView tv_user_agreement,tv_what_is_sailfish;
    private EditText et_name,et_intro;

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public RegistShopFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment RegistShopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistShopFragment newInstance(String param1) {
        RegistShopFragment fragment = new RegistShopFragment();
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
        View view = inflater.inflate(R.layout.activity_login_regist_f, container, false);
        btn_register = (Button)view.findViewById(R.id.btn_submit_f_regist);
        tv_user_agreement = (TextView)view.findViewById(R.id.tv_user_agreement_f_regist);
        tv_user_agreement.setOnClickListener(this);
        tv_what_is_sailfish = (TextView)view.findViewById(R.id.tv_what_is_sailfish_f_regist);
        tv_what_is_sailfish.setOnClickListener(this);
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
                checkBeforeRequest();
                break;
            case R.id.tv_user_agreement_f_regist:
                Intent intent = new Intent(getActivity(),UserAgreementActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_what_is_sailfish_f_regist:
                Intent intent1 = new Intent(getActivity(),WhatIsSailfishActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    private void checkBeforeRequest() {
        String name = et_name.getText().toString();
        String intro = et_intro.getText().toString();
        if (name.length() == 0) {
            Toast.makeText(getActivity(),"机构名不能为空",Toast.LENGTH_SHORT).show();
        }else {
            if (name.length()>20 || name.length()==0 || intro.length()>200) {
                Toast.makeText(getActivity(),"机构名或简介太长",Toast.LENGTH_SHORT).show();
            }else {
                HttpCallback callback = new HttpCallback(this,getActivity(),1);
                NetUtil.reqSendGet(getActivity(),"/RegisterShop?name=" + name + "&intro=" + intro,callback);
            }
        }
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
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(getActivity());
                break;
            case FAIL:
                ViewHandler.toastShow(getActivity(), Ref.OP_ADD_FAIL);
                break;
            case DUPLICATE:
                ViewHandler.toastShow(getActivity(), "机构名重复");
                break;
            default:
                break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) {
        long id = 0;
        try {
            id = Long.parseLong(String.valueOf(JsonUtil.strToMap(body).get("data")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getActivity(), RegistAdministraotrActivity.class);
        intent.putExtra("s_id",id);
        getActivity().startActivity(intent);
    }
}
