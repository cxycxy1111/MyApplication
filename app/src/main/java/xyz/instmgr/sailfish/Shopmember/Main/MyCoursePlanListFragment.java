package xyz.instmgr.sailfish.Shopmember.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class MyCoursePlanListFragment extends BaseFragment implements HttpResultListener,RVCoursePlanAdapter.OnItemClickListener {

    private static final int REQUEST_FIRST_TIME = 1;
    private static final int REQUEST_REFRESH = 2;
    private OnDataLoadedListener onDataLoadedListener;
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "MyCoursePlanList";
    private String[] keys = new String[]{"courseplan_id","course_name","classroom_name","start_time","end_time"};
    private String mParam1;
    private static final int FIRST_TIME=1;
    private static final int SECOND_TIME=2;
    private View view;
    private List<Map<String,String>> list = new ArrayList<>();
    RVCoursePlanAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public MyCoursePlanListFragment() {
    }

    public static MyCoursePlanListFragment newInstance(String param1) {
        MyCoursePlanListFragment fragment = new MyCoursePlanListFragment();
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_course_plan_list, container, false);
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
                ViewHandler.snackbarShowTall(getActivity(),getView(),"你暂没有排课");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_REFRESH) {
            List<Map<String,String>> list_temp = new ArrayList<>();
            list_temp = JsonUtil.strToListMap(body,keys);
            list.addAll(list_temp);
        }else {
            list = JsonUtil.strToListMap(body,keys);
        }
        if (adapter == null) {
            adapter = new RVCoursePlanAdapter(list,getParentFragment().getActivity());
        }
        onDataLoadedListener.onDataFirstTimeLoad(3);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_my_course_plan);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getParentFragment().getActivity(),LinearLayoutManager.VERTICAL,false);
        adapter.setOnItemClickListener(this);
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        adapter.notifyDataSetChanged();
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
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }

    @Override
    public void onItemClick(View view, int position) {
        Map<String,String> map = list.get(position);
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (list != null) {
            list.clear();
        }
        HttpCallback callback = new HttpCallback(this,getParentFragment().getActivity(),REQUEST_REFRESH);
        NetUtil.reqSendGet(getParentFragment().getActivity(),"/coursePlanMyQuery",callback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
