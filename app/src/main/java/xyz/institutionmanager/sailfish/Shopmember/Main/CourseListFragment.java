package xyz.institutionmanager.sailfish.Shopmember.Main;

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
import xyz.institutionmanager.sailfish.Adapter.RVCourseListAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Shopmember.Course.Course.CourseDetailActivity;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseListFragment
        extends BaseFragment
        implements HttpResultListener,RVCourseListAdapter.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private static final String TAG = "CourseListFragment:";
    private OnFragmentInteractionListener mListener;
    private String [] keys = {"id","name","last_time","supportedcard"};
    private View view;
    private RecyclerView recyclerView;
    private RVCourseListAdapter adapter;
    private List<Map<String,String>> recyclerviewdata = new ArrayList<>();
    private List<Map<String,String>> regional_list = new ArrayList<>();

    public CourseListFragment() {
    }

    public static CourseListFragment newInstance(String param1) {
        CourseListFragment fragment = new CourseListFragment();
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
        view = inflater.inflate(R.layout.activity_main_course_list,container,false);
        getProgressBar(getActivity()).setVisibility(View.VISIBLE);
        initShopData();
        //initRecyclerView();
        return view;
    }

    /**
     * 初始化舞馆及当前登陆者的ID
     */
    private void initShopData() {
    }

    @Override
    public void onRespStatus(String body, int source) {
        ViewHandler.progressBarHide(getParentFragment().getActivity(),getProgressBar(getParentFragment().getActivity()));
        switch (NetRespStatType.dealWithRespStat(body)) {
            case NSR:
                ViewHandler.toastShow(getParentFragment().getActivity(), Ref.STAT_NSR);
                break;
            case STATUS_AUTHORIZE_FAIL:
                ViewHandler.exitAcitivityDueToAuthorizeFail(getParentFragment().getActivity());
                break;
            case EMPTY:
                ViewHandler.toastShow(getParentFragment().getActivity(),"暂无课程");
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        ViewHandler.progressBarHide(getParentFragment().getActivity(),getProgressBar(getParentFragment().getActivity()));
        regional_list = JsonUtil.strToListMap(body,keys);
        for (int i = 0;i < regional_list.size();i++) {
            Map<String,String> temp_map = regional_list.get(i);
            Map<String,String> new_map = new HashMap<>();
            String supportedcard = temp_map.get("supportedcard");
            new_map.put("id",temp_map.get("id"));
            new_map.put("name",temp_map.get("name"));
            new_map.put("last_time",String.valueOf(temp_map.get("last_time")));
            new_map.put("type",temp_map.get("type"));
            new_map.put("supportedcard",temp_map.get("supportedcard"));
            recyclerviewdata.add(new_map);
        }
        initRecyclerView(view);
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.progressBarHide(getParentFragment().getActivity(),getProgressBar(getParentFragment().getActivity()));
        ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.progressBarHide(getParentFragment().getActivity(),getProgressBar(getParentFragment().getActivity()));
        ViewHandler.toastShow(getParentFragment().getActivity(), Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.progressBarHide(getParentFragment().getActivity(),getProgressBar(getParentFragment().getActivity()));
        ViewHandler.alertShowAndExitApp(getParentFragment().getActivity());
    }

    @Override
    public void onItemClick(View view, int position) {
        Map<String,String> map = recyclerviewdata.get(position);
        Intent intent = new Intent(getParentFragment().getActivity(), CourseDetailActivity.class);
        intent.putExtra("name",map.get("name"));
        intent.putExtra("id",String.valueOf(map.get("id")));
        intent.putExtra("type",String.valueOf(map.get("type")));
        getParentFragment().getActivity().startActivityForResult(intent,1);
    }

    /**
     * 获取recyclerview的数据模型
     * @return
     */
    private void initRecyclerView() {
        recyclerviewdata = new ArrayList<>();
        recyclerviewdata.clear();
        regional_list.clear();
        String url = "/CourseListQuery";
        HttpCallback callback = new HttpCallback(this,getParentFragment().getActivity(),1);
        com.alfred.alfredtools.NetUtil.reqSendGet(getParentFragment().getActivity(),url,callback);
    }

    /**
     * 初始化recyclerview
     * @param view
     */
    private void initRecyclerView (View view) {
        adapter = new RVCourseListAdapter(recyclerviewdata,getParentFragment().getActivity());
        adapter.setOnItemClickListener(this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_course_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case 1:
                        break;
                    default:break;
                }
            default:break;
        }
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
    public void onResume() {
        super.onResume();
        recyclerviewdata.clear();
        regional_list.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        initRecyclerView();
    }
}
