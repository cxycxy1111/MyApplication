package com.example.dengweixiong.Shopmember.Main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dengweixiong.Shopmember.Adapter.RVCoursePlanAdapter;
import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.Util.SharePreferenceManager;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CoursePlanListFragment extends Fragment {

    private String s_id,sm_id,mParam1;
    private static final String ARG_PARAM1 = "param1";
    private String[] strs_keys = new String[] {"courseplan_id","course_name","course_type","classroom_name","end_time","start_time"};
    private RecyclerView rv_course_plan;
    private View view;
    private List<Map<String,String>> mapList_data = new ArrayList<>();
    private OnFragmentInteractionListener mListener;

    public CoursePlanListFragment() {

    }

    public static CoursePlanListFragment newInstance(String param1) {
        CoursePlanListFragment fragment = new CoursePlanListFragment();
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
        initData();
        view = inflater.inflate(R.layout.activity_main_course_plan_list, container, false);
        initCoursePlanData();
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
        void onFragmentInteraction(Uri uri);
    }

    private void initData() {
        s_id = SharePreferenceManager.getSharePreferenceValue(getParentFragment().getActivity(),"sasm","s_id",2);
        sm_id = SharePreferenceManager.getSharePreferenceValue(getParentFragment().getActivity(),"sasm","sm_id",2);
    }

    private void initCoursePlanData() {
        String url = "/CoursePlanListQurey";
        okhttp3.Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getParentFragment().getActivity(),Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        mapList_data = JsonHandler.strToListMap(resp,strs_keys);
                        initCoursePlanViews();
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(getParentFragment().getActivity(),"暂无排课");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getParentFragment().getActivity());
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(getParentFragment().getActivity(),url,callback);
    }

    private void initCoursePlanViews() {
        getParentFragment().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rv_course_plan = (RecyclerView)view.findViewById(R.id.rv_f_course_plan);
                LinearLayoutManager llm_rv = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                RVCoursePlanAdapter adapter = new RVCoursePlanAdapter(mapList_data,getParentFragment().getActivity());
                rv_course_plan.setAdapter(adapter);
                rv_course_plan.setLayoutManager(llm_rv);
            }
        });
    }
}