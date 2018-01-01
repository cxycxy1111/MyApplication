package com.example.dengweixiong.Shopmember.Course.CoursePlan;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ScrollingTabContainerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

public class CoursePlanDetailBasicFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private String seleted_classroom,sm_id,s_id,cp_id,course_name;
    private String[] strs_detail_keys = new String[] {"course_name","classroom_name","start_time","last_time","remark"};
    private String[] str_classroom_keys = new String[] {"id","name"};
    private List<Map<String,String>> mapList_classroom = new ArrayList<>();
    private List<String> list_classroom = new ArrayList<>();
    private List<Map<String,String>> mapList_detail = new ArrayList<>();
    private EditText et_course_name,et_start_date,et_start_time,et_last_time,et_remark;
    private Spinner sp_classroom;
    private Button btn_save;
    private View view;
    private OnFragmentInteractionListener mListener;

    public CoursePlanDetailBasicFragment() {

    }

    public static CoursePlanDetailBasicFragment newInstance(Map<String,String> map) {
        CoursePlanDetailBasicFragment fragment = new CoursePlanDetailBasicFragment();
        Bundle bundle = new Bundle();
        for (Map.Entry<String,String> entry:map.entrySet()) {
            bundle.putString(entry.getKey(),entry.getValue());
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            s_id = getArguments().getString("s_id");
            sm_id = getArguments().getString("sm_id");
            cp_id = getArguments().getString("cp_id");
            course_name = getArguments().getString("course_name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_course_plan_detail_basic_f, container, false);
        btn_save = (Button)view.findViewById(R.id.btn_save_f_course_plan_detail_basic);
        initViews(view);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_f_course_plan_detail_basic:
                saveCoursePlanModify();
                break;
            default:break;
        }
    }

    private void initViews(View view) {
        et_course_name = (EditText)view.findViewById(R.id.et_name_f_course_plan_detail_basic);
        et_start_date = (EditText)view.findViewById(R.id.et_start_date_f_course_plan_detail_basic);
        et_start_time = (EditText)view.findViewById(R.id.et_start_time_f_course_plan_detail_basic);
        et_last_time = (EditText)view.findViewById(R.id.et_last_time_f_course_plan_detail_basic);
        sp_classroom = (Spinner) view.findViewById(R.id.sp_classroom_f_course_plan_detail_basic);
        initCoursePlanDetail();
    }

    //初始化课室
    private void initClassroomSpinner() {
        String url = "/ClassroomListQuery?s_id=" + s_id;
        okhttp3.Callback callback = new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(),Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                int int_type_resp = MethodTool.dealWithResponse(resp);
                if (int_type_resp == Ref.RESP_TYPE_MAPLIST) {
                    mapList_classroom = JsonHandler.strToListMap(resp,str_classroom_keys);
                    for (int i = 0;i < mapList_classroom.size();i++) {
                        list_classroom.add(mapList_classroom.get(i).get("name"));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter adapter = new ArrayAdapter(getActivity(),R.layout.tile_spinner,R.id.tile_spinner_text,list_classroom);
                            adapter.setDropDownViewResource(R.layout.tile_spinner_dropdown);
                            sp_classroom.setAdapter(adapter);
                            sp_classroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    seleted_classroom = mapList_classroom.get(position).get("id");
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    for (int i = 0;i < mapList_classroom.size();i++) {
                                        Map<String,String> map = mapList_classroom.get(i);
                                        if (map.get("name").equals(mapList_detail.get(0).get("classroom_name")));
                                        seleted_classroom = map.get("id");
                                    }
                                }
                            });
                        }
                    });
                }else if (int_type_resp == Ref.RESP_TYPE_STAT) {
                    Map<String,String> map = new HashMap<>();
                    map = JsonHandler.strToMap(resp);
                    if (map.get(Ref.STATUS).equals(Ref.STAT_NSR)) {
                        MethodTool.showToast(getActivity(),Ref.STAT_NSR);
                    }
                }
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
    }

    //初始化排课信息
    private void initCoursePlanDetail() {
        String url = "/CoursePlanDetailQuery?cp_id=" + cp_id;
        okhttp3.Callback callback = new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(), Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                if (MethodTool.dealWithResponse(resp) == Ref.RESP_TYPE_STAT) {
                    Map<String,String> map = new HashMap<>();
                    map = JsonHandler.strToMap(resp);
                    if (map.get(Ref.STATUS).equals(Ref.STAT_NSR)) {
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                    }
                }else if (MethodTool.dealWithResponse(resp) == Ref.RESP_TYPE_MAPLIST) {
                    mapList_detail = JsonHandler.strToListMap(resp,strs_detail_keys);
                    final List<Map<String, String>> final_list_map_detail = mapList_detail;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String,String> map = final_list_map_detail.get(0);
                            et_course_name.setText(map.get("course_name"));
                            et_start_date.setText(map.get("start_time").split(" ")[0]);
                            et_start_time.setText(map.get("start_time").split(" ")[1]);
                            et_last_time.setText(map.get("last_time"));
                            et_remark.setText(map.get("remark"));
                            initClassroomSpinner();
                        }
                    });
                }
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
    }

    private void saveCoursePlanModify() {

    }
}
