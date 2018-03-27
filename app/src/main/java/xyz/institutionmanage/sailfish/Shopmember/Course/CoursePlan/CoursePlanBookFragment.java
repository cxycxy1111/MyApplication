package xyz.institutionmanage.sailfish.Shopmember.Course.CoursePlan;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class CoursePlanBookFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "CoursePlanBookF:";
    private String [] keys = new String[] {"id","name"};
    private String[] strs_supported_cards= new String[]{"mc_id","c_name","balance","type"};
    private OnFragmentInteractionListener mListener;
    private int selected_mc_id_positon,selected_m_id_position;

    private String mParam1;
    private View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AlertDialog dialog;

    private RVSimpleAdapter adapter;

    private List<String> list_name = new ArrayList<>();
    private List<Map<String,String>> mapList_member = new ArrayList<>();
    private List<String> list_member_card = new ArrayList<>();
    private ArrayList<Map<String,String>> mapList_member_card = new ArrayList<>();

    public CoursePlanBookFragment() {
    }

    public static CoursePlanBookFragment newInstance(String param1) {
        CoursePlanBookFragment fragment = new CoursePlanBookFragment();
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
        view = inflater.inflate(R.layout.fragment_course_plan_book, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_course_plan_book);
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
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

    private void initDataFromWeb() {
        String url = "/coursePlanBookListQueryByCoursePlanId?cp_id=" + mParam1;
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
                selected_m_id_position = position;
                Log.d(TAG, "onItemClick: " + selected_m_id_position);
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
        getActivity().getMenuInflater().inflate(R.menu.menu_context_courseplan_book_list,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            switch (item.getItemId()) {
                case R.id.attend_context_courseplan_book_list:
                    initDialogData();
                    break;
                case R.id.unbook_context_courseplan_book_list:
                    unbook();
                    break;
                default:break;
            }
            return true;
        }
        return false;

    }

    private void initDialogData() {
        list_member_card.clear();
        selected_mc_id_positon = 0;
        String m_id = String.valueOf(mapList_member.get(selected_m_id_position).get("id"));
        String url = "/memberCardAttendList?cp_id=" + mParam1 + "&m_id=" +m_id ;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.d(TAG, "onResponse: " + resp);
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        mapList_member_card = JsonHandler.strToListMap(resp,strs_supported_cards);
                        for (int i = 0;i < mapList_member_card.size();i++) {
                            if (Integer.parseInt(String.valueOf(mapList_member_card.get(i).get("type"))) == 1) {
                                list_member_card.add(String.valueOf(mapList_member_card.get(i).get("c_name")) + "(" + String.valueOf(mapList_member_card.get(i).get("balance")) + "元)");
                            }else if (Integer.parseInt(String.valueOf(mapList_member_card.get(i).get("type"))) == 2) {
                                list_member_card.add(String.valueOf(mapList_member_card.get(i).get("c_name")) + "(" + String.valueOf(mapList_member_card.get(i).get("balance")) + "次)");
                            }else if (Integer.parseInt(String.valueOf(mapList_member_card.get(i).get("type"))) == 3) {
                                list_member_card.add(String.valueOf(mapList_member_card.get(i).get("c_name")));
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initDialog(view,list_member_card);
                                }
                            });
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(getActivity(),"你暂时无法签到");
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

    private void initDialog(View view,List<String> list){
        int size = list.size();
        String[] strs = new String[size];
        final String[] items = list.toArray(strs);
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle("请选择待签到的卡");
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
                selected_mc_id_positon = index;
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                MethodTool.hideProgressBar(getActivity(),getProgressBar(getActivity()));
                attend();;
                dialog.dismiss();
            }
        });
        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                MethodTool.hideProgressBar(getActivity(),getProgressBar(getActivity()));
                dialog.dismiss();
            }
        });
        dialog = alertBuilder.create();
        dialog.show();
    }

    private void attend() {
        String mc_id = String.valueOf(mapList_member_card.get(selected_mc_id_positon).get("mc_id"));
        String m_id = String.valueOf(mapList_member.get(selected_m_id_position).get("id"));
        String url = "/attend?cp_id="+ mParam1 + "&mc_id="+ mc_id + "&m_id=" + m_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(),Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                MethodTool.hideProgressBar(getActivity(),getProgressBar(getActivity()));
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_ERROR:
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case AUTHORIZE_FAIL:
                                MethodTool.showToast(getActivity(),"你未预订该课程");
                                break;
                            case DUPLICATE:
                                MethodTool.showToast(getActivity(),"你已签到");
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(getActivity(),"签到失败");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getActivity());
                                break;
                            case MEMBERCARD_EXPIRED:
                                MethodTool.showToast(getActivity(),Ref.OP_MEMBER_CARD_EXPIRED);
                                break;
                            case BALANCE_NOT_ENOUGH:
                                MethodTool.showToast(getActivity(),Ref.OP_BALANCE_NOT_ENOUGH);
                                break;
                            case EXE_SUC:
                                MethodTool.showToast(getActivity(),"签到成功");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mapList_member.remove(selected_m_id_position);
                                        list_name.remove(selected_m_id_position);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
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

    private void unbook(){
        String m_id = String.valueOf(mapList_member.get(selected_m_id_position).get("id"));
        String url = "/unbook?cp_id="+ mParam1 + "&m_id=" + m_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(),Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MethodTool.hideProgressBar(getActivity(),getProgressBar(getActivity()));
                    }
                });
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_ERROR:
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case NSR:
                                MethodTool.showToast(getActivity(),"你未预订该课程");
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(getActivity(),"退订失败");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getActivity());
                                break;
                            case EXE_SUC:
                                MethodTool.showToast(getActivity(),"退订成功");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mapList_member.remove(selected_m_id_position);
                                        list_name.remove(selected_m_id_position);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
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
