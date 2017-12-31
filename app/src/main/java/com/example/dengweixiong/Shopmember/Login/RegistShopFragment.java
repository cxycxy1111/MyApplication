package com.example.dengweixiong.Shopmember.Login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegistShopFragment
        extends Fragment
        implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = "RegisF:";
    private static final String ARG_PARAM1 = "param1";
    private Button btn_register;
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
        View view = inflater.inflate(R.layout.activity_login_regist, container, false);
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
                request();
                break;
            default:
                break;
        }
    }

    private void request() {
        String name = et_name.getText().toString();
        String intro = et_intro.getText().toString();

        String url_add_shop = "/RegisterShop?name=" + name + "&intro=" + intro;
        Log.d(TAG, url_add_shop);
        if (name.equals("") | name.equals(null)) {
            Toast.makeText(getContext(),Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_LONG).show();
        }else {
            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    MethodTool.showToast(getActivity(), Ref.CANT_CONNECT_INTERNET);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    HashMap<String,String> map = JsonHandler.strToMap(resp);
                    //打印转化出来的map
                    ArrayList<String> keys = MethodTool.getKeys(map);
                    ArrayList<String> values = MethodTool.getValues(map,keys);
                    Set<String> set = map.keySet();
                    if (keys.get(0).equals(Ref.STATUS)) {
                        if (values.get(0).equals("exe_fail")) {
                            MethodTool.showToast(getActivity(),Ref.OP_ADD_FAIL);
                        }else if (values.get(0).equals("duplicate")) {
                            MethodTool.showToast(getActivity(),"机构名重复");
                        } else {
                            MethodTool.showToast(getActivity(), Ref.UNKNOWN_ERROR);
                        }

                    }else if (keys.get(0).equals("data")) {
                        long id = Long.parseLong(values.get(0));
                        Intent intent = new Intent(getActivity(), RegistAdministraotrActivity.class);
                        intent.putExtra("s_id",id);
                        getActivity().startActivity(intent);
                    }else if (keys.get(0).equals(null)){
                        MethodTool.showToast(getActivity(), Ref.NULL_POINTER_ERROR);
                    } else {
                        MethodTool.showToast(getActivity(),resp);
                    }
                }
            };
            NetUtil.sendHttpRequest(getContext(),url_add_shop,callback);
        }

    }
}
