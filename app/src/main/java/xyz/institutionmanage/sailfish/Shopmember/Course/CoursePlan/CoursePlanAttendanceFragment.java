package xyz.institutionmanage.sailfish.Shopmember.Course.CoursePlan;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.Adapter.RVSimpleAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.BaseFragment;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class CoursePlanAttendanceFragment extends BaseFragment {

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
                            case EMPTY_RESULT:
                                MethodTool.showToast(getActivity(),"暂无会员预订排课");
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_MAPLIST:
                        mapList_member = JsonHandler.strToListMap(resp,keys);
                        for (int i = 0;i<mapList_member.size();i++) {
                            list_name.add(mapList_member.get(i).get("name"));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateViewComponent();
                            }
                        });
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
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
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(),Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_ERROR:
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getActivity());
                                break;
                            case NSR:
                                MethodTool.showToast(getActivity(),"你暂未签到该课程");
                                break;
                            case EXE_SUC:
                                MethodTool.showToast(getActivity(),Ref.OP_SUCCESS);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mapList_member.remove(int_selected_m_position);
                                        list_name.remove(int_selected_m_position);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(getActivity(),Ref.OP_FAIL);
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
    }
}
