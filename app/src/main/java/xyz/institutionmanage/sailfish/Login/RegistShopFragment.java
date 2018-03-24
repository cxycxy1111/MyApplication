package xyz.institutionmanage.sailfish.Login;

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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

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
        View view = inflater.inflate(R.layout.activity_login_regist_f, container, false);
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
                checkBeforeRequest();
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
                request(name,intro);
            }
        }
    }

    private void request(String name,String intro) {

        String url_add_shop = "/RegisterShop?name=" + name + "&intro=" + intro;
        Log.d(TAG, url_add_shop);
        if (name.equals("") | name.equals(null)) {
            Toast.makeText(getContext(), Ref.OP_EMPTY_ESSENTIAL_INFO,Toast.LENGTH_LONG).show();
        }else {
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
                                case SESSION_EXPIRED:
                                    MethodTool.showExitAppAlert(getActivity());
                                    break;
                                case AUTHORIZE_FAIL:
                                    MethodTool.exitAcitivityDueToAuthorizeFail(getActivity());
                                    break;
                                case EXE_FAIL:
                                    MethodTool.showToast(getActivity(),Ref.OP_ADD_FAIL);
                                    break;
                                case DUPLICATE:
                                    MethodTool.showToast(getActivity(),"机构名重复");
                                    break;
                                default:break;
                            }
                            break;
                        case RESP_DATA:
                            long id = Long.parseLong(String.valueOf(JsonHandler.strToMap(resp).get("data")));
                            Intent intent = new Intent(getActivity(), RegistAdministraotrActivity.class);
                            intent.putExtra("s_id",id);
                            getActivity().startActivity(intent);
                            break;
                        case RESP_ERROR:
                            MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                            break;
                        default:break;
                    }
                }
            };
            NetUtil.sendHttpRequest(getContext(),url_add_shop,callback);
        }

    }
}
