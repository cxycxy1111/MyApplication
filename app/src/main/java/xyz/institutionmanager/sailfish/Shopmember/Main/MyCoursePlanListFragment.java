package xyz.institutionmanager.sailfish.Shopmember.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVCoursePlanAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Shopmember.Course.CoursePlan.CoursePlanDetailActivity;
import xyz.institutionmanager.sailfish.Shopmember.Course.CoursePlan.CoursePlanDetailPrivateActivity;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyCoursePlanListFragment extends Fragment implements HttpResultListener,RVCoursePlanAdapter.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "MyCoursePlanList";
    private String[] keys = new String[]{"courseplan_id","course_name","classroom_name","start_time","end_time"};
    private String mParam1;
    private static final int FIRST_TIME=1;
    private static final int SECOND_TIME=2;
    private View view;
    private RVCoursePlanAdapter adapter;
    private List<Map<String,String>> list = new ArrayList<>();

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
        Log.i(TAG, "onCreate: ");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_my_course_plan_list, container, false);
        initData();
        initComponent();
        HttpCallback callback = new HttpCallback(this,getParentFragment().getActivity(),1);
        NetUtil.reqSendGet(getParentFragment().getActivity(),"/coursePlanMyQuery",callback);
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
        Log.i(TAG, "onAttach: ");
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
        Log.i(TAG, "onDetach: ");
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void initData() {
    }

    private void initComponent() {

    }

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case EMPTY:
                ViewHandler.toastShow(getParentFragment().getActivity(),"你暂没有排课");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        list = JsonUtil.strToListMap(body,keys);
        updateComponent();
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(getParentFragment().getActivity(),Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(getParentFragment().getActivity(), Ref.CANT_CONNECT_INTERNET);
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

    private void updateComponent() {
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_my_course_plan);
        adapter = new RVCoursePlanAdapter(list,getParentFragment().getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getParentFragment().getActivity(),LinearLayoutManager.VERTICAL,false);
        adapter.setOnItemClickListener(this);
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: list.size=" + list.size());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: list.size=" + list.size());
        if (list != null) {
            list.clear();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        HttpCallback callback = new HttpCallback(this,getParentFragment().getActivity(),1);
        NetUtil.reqSendGet(getParentFragment().getActivity(),"/coursePlanMyQuery",callback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");
    }
}
