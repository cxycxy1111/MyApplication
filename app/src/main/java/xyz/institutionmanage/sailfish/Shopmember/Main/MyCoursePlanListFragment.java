package xyz.institutionmanage.sailfish.Shopmember.Main;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.Adapter.RVCoursePlanAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Shopmember.Course.CoursePlan.CoursePlanDetailActivity;
import xyz.institutionmanage.sailfish.Shopmember.Course.CoursePlan.CoursePlanDetailPrivateActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MyCoursePlanListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "MyCoursePlanList";
    private String[] keys = new String[]{"courseplan_id","course_name","classroom_name","start_time","end_time"};
    private String mParam1;
    private View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
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
        Log.d(TAG, "onCreate: ");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_my_course_plan_list, container, false);
        initDataFromPreviousActivity();
        initComponent();
        initDataFromWeb();
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
        Log.d(TAG, "onAttach: ");
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
        Log.d(TAG, "onDetach: ");
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void initDataFromPreviousActivity() {

    }

    private void initComponent() {
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_my_course_plan);
    }

    private void initDataFromWeb() {
        String url = "/coursePlanMyQuery";
        okhttp3.Callback callback = new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getParentFragment().getActivity(), Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        list = JsonHandler.strToListMap(resp,keys);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateComponent();
                            }
                        });
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(getParentFragment().getActivity(),"你暂没有排课");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getParentFragment().getActivity());
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(getParentFragment().getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(getParentFragment().getActivity(),url,callback);
    }

    private void updateComponent() {
        adapter = new RVCoursePlanAdapter(list,getParentFragment().getActivity());
        linearLayoutManager = new LinearLayoutManager(getParentFragment().getActivity(),LinearLayoutManager.VERTICAL,false);
        adapter.setOnItemClickListener(new RVCoursePlanAdapter.OnItemClickListener() {
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
        });
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }
}
