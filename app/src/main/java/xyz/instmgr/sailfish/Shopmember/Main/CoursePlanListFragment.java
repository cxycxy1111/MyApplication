package xyz.instmgr.sailfish.Shopmember.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.Adapter.RVCoursePlanAdapter;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Shopmember.Course.CoursePlan.CoursePlanDetailActivity;
import xyz.instmgr.sailfish.Shopmember.Course.CoursePlan.CoursePlanDetailPrivateActivity;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoursePlanListFragment
        extends BaseFragment
        implements HttpResultListener,RVCoursePlanAdapter.OnItemClickListener {

    private static final String TAG = "CoursePlanListFragment:";
    private static final int REQUEST_FIRST_TIME = 1;
    private static final int REQUEST_REFRESH = 2;
    private OnDataLoadedListener onDataLoadedListener;
    private String s_id,sm_id,mParam1;
    private static final String ARG_PARAM1 = "param1";
    private String[] strs_keys = new String[] {"courseplan_id","course_name","course_type","classroom_name","end_time","start_time"};
    private RecyclerView rv_course_plan;
    private View view;
    private List<Map<String,String>> mapList_data = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private RVCoursePlanAdapter adapter;

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
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_main_course_plan_list, container, false);
        }
        ViewGroup viewGroup = (ViewGroup)view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
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
            onDataLoadedListener = (OnDataLoadedListener)context;
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

    public interface OnDataLoadedListener {
        void onDataFirstTimeLoad(int source);
        void onDataRefreshLoad(int source);
    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case EMPTY:
                Snackbar.make(getView(),"暂无排课",Snackbar.LENGTH_SHORT).show();
                ViewHandler.snackbarShowTall(getActivity(),getView(),"暂时没有排课");
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(getParentFragment().getActivity());
                break;
            default:break;
        }
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getParentFragment().getActivity());
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.snackbarShowTall(getActivity(),getView(),Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.snackbarShowTall(getActivity(),getView(),Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_REFRESH) {
            List<Map<String,String>> list_temp = new ArrayList<>();
            list_temp = JsonUtil.strToListMap(body,strs_keys);
            mapList_data.clear();
            mapList_data.addAll(list_temp);
        }else {
            mapList_data = JsonUtil.strToListMap(body,strs_keys);
        }
        if (adapter == null) {
            adapter = new RVCoursePlanAdapter(mapList_data,getParentFragment().getActivity());
        }
        onDataLoadedListener.onDataFirstTimeLoad(2);
        rv_course_plan = (RecyclerView)view.findViewById(R.id.rv_f_course_plan);
        LinearLayoutManager llm_rv = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        adapter.setOnItemClickListener(this);
        if (rv_course_plan.getAdapter() == null) {
            rv_course_plan.setAdapter(adapter);
        }
        if (rv_course_plan.getLayoutManager() == null) {
            rv_course_plan.setLayoutManager(llm_rv);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        Map<String,String> map = mapList_data.get(position);
        String course_type = String.valueOf(map.get("course_type"));
        Intent intent;
        switch (course_type) {
            case "4":
                intent = new Intent(getParentFragment().getActivity(), CoursePlanDetailPrivateActivity.class);
                break;
            default:
                intent = new Intent(getParentFragment().getActivity(), CoursePlanDetailActivity.class);
                break;
        }
        intent.putExtra("cp_id",String.valueOf(map.get("courseplan_id")));
        intent.putExtra("course_name",map.get("course_name"));
        intent.putExtra("time",map.get("start_time"));
        getParentFragment().getActivity().startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapList_data.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_REFRESH);
        NetUtil.reqSendGet(getActivity(),"/CoursePlanListQurey",callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}