package xyz.institutionmanager.sailfish.Shopmember.Course.CoursePlan;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVSimpleAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoursePlanAttendanceFragment extends BaseFragment implements HttpResultListener {

    private static final int REQUEST_coursePlanAttendanceListQueryByCoursePlanId = 1;
    private static final int REQUEST_unattend = 2;
    private static final String ARG_PARAM1 = "cp_id";
    private static final String TAG = "CoursePlanAttenF:";
    private String [] keys = new String[] {"id","name"};
    private List<Map<String,String>> mapList_member = new ArrayList<>();
    private List<String> list_name = new ArrayList<>();
    private int int_selected_m_position;
    private OnUnattendListener onUnattendListener;

    private String mParam1;
    private View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RVSimpleAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public CoursePlanAttendanceFragment() {
    }

    public static CoursePlanAttendanceFragment newInstance(String param1) {
        CoursePlanAttendanceFragment fragment = new CoursePlanAttendanceFragment();
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
        view = inflater.inflate(R.layout.fragment_course_plan_attendance, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_course_plan_attendance);
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

    public interface OnUnattendListener {
        void onAttend();
    }

    public OnUnattendListener getOnUnttendListener() {
        return onUnattendListener;
    }

    public void setOnunattendListener(OnUnattendListener onUnattendListener) {
        this.onUnattendListener = onUnattendListener;
    }

    private void initDataFromWeb() {
        String url = "/coursePlanAttendanceListQueryByCoursePlanId?cp_id=" + mParam1;
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_coursePlanAttendanceListQueryByCoursePlanId);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    private void updateViewComponent() {
        adapter = new RVSimpleAdapter(list_name);
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        }
        adapter.setOnItemClickListener(new RVSimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int_selected_m_position = position;
                unregisterForContextMenu(view);
                registerForContextMenu(view);
                view.showContextMenu();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_context_courseplan_attend_list,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            switch (item.getItemId()) {
                case R.id.unattend_context_courseplan_attend_list:
                    unattend();
                    break;
                default:break;
            }
            return true;
        }
        return false;
    }

    public void unattend() {
        String m_id = String.valueOf(mapList_member.get(int_selected_m_position).get("id"));
        String url = "/unattend?cp_id=" + mParam1 + "&m_id=" + m_id;
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_unattend);
        NetUtil.reqSendGet(getContext(),url,callback);
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_coursePlanAttendanceListQueryByCoursePlanId) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case EMPTY:
                    ViewHandler.toastShow(getActivity(),"暂无会员预订排课");
                    break;
                default:break;
            }
        }else if (source == REQUEST_unattend) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(getActivity(),"你暂未签到该课程");
                    break;
                case SUCCESS:
                    ViewHandler.toastShow(getActivity(),Ref.OP_SUCCESS);
                    mapList_member.remove(int_selected_m_position);
                    list_name.remove(int_selected_m_position);
                    adapter.notifyDataSetChanged();
                    break;
                case FAIL:
                    ViewHandler.toastShow(getActivity(),Ref.OP_FAIL);
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_coursePlanAttendanceListQueryByCoursePlanId) {
            mapList_member = JsonUtil.strToListMap(body,keys);
            for (int i = 0;i<mapList_member.size();i++) {
                list_name.add(mapList_member.get(i).get("name"));
            }
            updateViewComponent();
        }else if (source == REQUEST_unattend) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }
}
