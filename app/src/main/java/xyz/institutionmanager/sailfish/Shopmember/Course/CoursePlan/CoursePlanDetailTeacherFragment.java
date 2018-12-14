package xyz.institutionmanager.sailfish.Shopmember.Course.CoursePlan;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVPureCheckBoxAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoursePlanDetailTeacherFragment
        extends Fragment
        implements View.OnClickListener,HttpResultListener {

    private static final int REQUEST_CoursePlanTeacherQuery = 1;
    private static final int REQUEST_QueryShopmemberList = 2;
    private static final int REQUEST_CoursePlanTeacherModify = 3;
    private static final String ARG_PARAM1 = "param1";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String sm_id,s_id,cp_id,course_name;
    private String[] strs_keys_teacher = new String[] {"id","name","type"};
    private String[] strs_keys_supported_teacher = new String[] {"teacher_id","name"};
    private List<Map<String,String>> mapList_recyclerview_data = new ArrayList<>();
    private List<Map<String,String>> mapList_teacher_origin = new ArrayList<>();
    private List<Map<String,String>> mapList_supported_teacher_origin = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private Button btn_save;
    private RVPureCheckBoxAdapter adapter;
    public CoursePlanDetailTeacherFragment() {
    }

    public static CoursePlanDetailTeacherFragment newInstance(Map<String,String> map) {
        CoursePlanDetailTeacherFragment fragment = new CoursePlanDetailTeacherFragment();
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
            course_name = getArguments().getString("course_name");
            cp_id = getArguments().getString("cp_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_course_plan_detail_teacher_f, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_course_plan_detail_teacher);
        btn_save = (Button)view.findViewById(R.id.btn_save_f_course_plan_detail_teacher);
        btn_save.setOnClickListener(this);
        initCoursePlanTeacher();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_f_course_plan_detail_teacher:
                saveCoursePlanTeacherModify();
                break;
            default:break;
        }
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_CoursePlanTeacherQuery) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(getActivity(),Ref.STAT_NSR);
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(getActivity());
                    break;
                default:
                    ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
                    break;
            }
        }else if (source == REQUEST_QueryShopmemberList) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(getActivity(),Ref.STAT_NSR);
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(getActivity());
                    break;
                default:break;
            }
        }else if (source == REQUEST_CoursePlanTeacherModify) {
            NetRespStatType type = NetRespStatType.dealWithRespStat(body);
            switch (type) {
                case SUCCESS:
                    ViewHandler.toastShow(getActivity(),Ref.OP_MODIFY_SUCCESS);
                    break;
                case FAIL:
                    ViewHandler.toastShow(getActivity(),Ref.OP_MODIFY_FAIL);
                    break;
                case PARTYLY_FAIL:
                    ViewHandler.toastShow(getActivity(),Ref.OP_PARTLY_FAIL);
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(getActivity());
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_CoursePlanTeacherQuery) {
            mapList_supported_teacher_origin = JsonUtil.strToListMap(body,strs_keys_supported_teacher);
            initShopmember();
        }else if (source == REQUEST_QueryShopmemberList) {
            mapList_teacher_origin = JsonUtil.strToListMap(body,strs_keys_teacher);
            for (int i = 0;i < mapList_teacher_origin.size();i++) {
                Map<String,String> map_origin = mapList_teacher_origin.get(i);
                Map<String,String> map_temp = new HashMap<>();
                map_temp.put("teacherId",map_origin.get("id"));
                map_temp.put("teacherName",map_origin.get("name"));
                String c = "0";
                map_temp.put("isChecked",c);
                for (int j = 0;j < mapList_supported_teacher_origin.size();j++) {
                    Map<String,String> map_supported_origin = mapList_supported_teacher_origin.get(j);
                    int left = Integer.parseInt(String.valueOf(map_origin.get("id")));
                    int right = Integer.valueOf(String.valueOf(map_supported_origin.get("teacher_id")));
                    if ( left == right) {
                        c = "1";
                    }
                }
                map_temp.put("isChecked",c);
                mapList_recyclerview_data.add(map_temp);
            }
            initRecyclerView(mapList_recyclerview_data);
        }else if (source == REQUEST_CoursePlanTeacherModify) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(getActivity(), Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //初始化排课计划中的教师
    private void initCoursePlanTeacher() {
        String url = "/CoursePlanTeacherQuery?cp_id=" + cp_id;
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_CoursePlanTeacherQuery);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    //初始化舞馆中存量的所有教师
    private void initShopmember() {
        String url = "/QueryShopmemberList?type=0";
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_QueryShopmemberList);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    private void initRecyclerView(List<Map<String,String>> list_map) {
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        adapter = new RVPureCheckBoxAdapter(list_map,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void saveCoursePlanTeacherModify() {
        List<Map<String,String>> mapList_after = new ArrayList<>();
        mapList_after = adapter.getListmap_after();
        StringBuilder builder = new StringBuilder();
        for (int i = 0;i < mapList_recyclerview_data.size();i++) {
            Map<String,String> map_temp_before = new HashMap<>();
            map_temp_before = mapList_recyclerview_data.get(i);
            for (int j = 0;j < mapList_after.size();j++) {
                Map<String,String> map_temp_after = new HashMap<>();
                map_temp_after = mapList_after.get(i);
                if (map_temp_before.get("isChecked").equals("0") && map_temp_after.get("isChecked").equals("0")) {
                }else if (map_temp_before.get("isChecked").equals("0") && map_temp_after.get("isChecked").equals("1")) {
                    builder.append("1").append("_").append(cp_id).append("_").append(String.valueOf(map_temp_after.get("teacherId"))).append("-");
                }else if (map_temp_before.get("isChecked").equals("1") && map_temp_after.get("isChecked").equals("0")) {
                    builder.append("2").append("_").append(cp_id).append("_").append(String.valueOf(map_temp_after.get("teacherId"))).append("-");
                }else if (map_temp_before.get("isChecked").equals("1") && map_temp_after.get("isChecked").equals("1")) {

                }
            }
        }
        String req = builder.toString();
        if (req.length() == 0) {
            ViewHandler.toastShow(getActivity(),"您暂未作任何更改，无需保存");
        }else {
            req = req.substring(0,req.length()-1);
            String url = "/CoursePlanTeacherModify?m=" + req;
            HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_CoursePlanTeacherModify);
            NetUtil.reqSendGet(getActivity(),url,callback);
        }

    }
}
