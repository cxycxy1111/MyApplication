package xyz.instmgr.sailfish.Member.Book;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.alfred.alfredtools.*;
import xyz.instmgr.sailfish.R;
import xyz.instmgr.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBookDetailInfoFragment extends BaseFragment implements View.OnClickListener,HttpResultListener {

    private static final String TAG = "MCoursePlanDetailF";
    private static final String key = "cp_id";
    private int selected_mc_id_positon = 0;
    private String value;
    private String[] strs_detail_keys = new String[] {"course_name","classroom_name","start_time","last_time","remark","status"};
    private String[] strs_supported_cards= new String[]{"mc_id","c_name","balance","type"};
    private static final int QUEST_TYPE_LIST_LOAD = 1;
    private static final int QUEST_TYPE_DIALOG_LOAD = 2;
    private static final int QUEST_TYPE_BOOK = 3;
    private static final int QUEST_TYPE_UNBOOK = 4;
    private static final int QUEST_TYPE_ATTEND = 5;
    private AlertDialog alertDialog2;
    private TextView tv_state,tv_course_name,tv_classroom,tv_start_time,tv_last_time,tv_note;
    private Button btn_book,btn_unbook,btn_attend;
    private View view;
    private ArrayList<Map<String,String>> mapArrayList = new ArrayList<>();
    private List<String> dialog_card_list = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public MBookDetailInfoFragment() {
    }

    public static MBookDetailInfoFragment newInstance(String aValue) {
        MBookDetailInfoFragment fragment = new MBookDetailInfoFragment();
        Bundle args = new Bundle();
        args.putString(key, aValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            value = getArguments().getString(key);
        }
    }


    @Override
    public void createProgressBar(FragmentActivity activity) {
        super.createProgressBar(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mcourse_plan_detail_info, container, false);
        initViews();
        initDialogData();
        return view;
    }

    private void initViews() {
        tv_state = (TextView)view.findViewById(R.id.tv_hint_bookstate_f_m_course_plan_detail_basic);
        tv_course_name = (TextView)view.findViewById(R.id.tv_hint_name_f_m_course_plan_detail_basic);
        tv_classroom = (TextView)view.findViewById(R.id.tv_hint_classroom_f_m_course_plan_detail_basic);
        tv_start_time = (TextView)view.findViewById(R.id.tv_hint_start_time_f_m_course_plan_detail_basic);
        tv_last_time = (TextView)view.findViewById(R.id.tv_hint_last_time_f_m_course_plan_detail_basic);
        tv_note = (TextView)view.findViewById(R.id.tv_hint_remark_f_m_course_plan_detail_basic);
        btn_book = (Button)view.findViewById(R.id.btn_book_f_m_course_plan_detail_basic);
        btn_unbook = (Button)view.findViewById(R.id.btn_unbook_f_m_course_plan_detail_basic);
        btn_attend = (Button)view.findViewById(R.id.btn_attend_f_m_course_plan_detail_basic);
        btn_book.setOnClickListener(this);
        btn_unbook.setOnClickListener(this);
        btn_attend.setOnClickListener(this);
    }

    private void initDialogData() {
        String url = "/mMemberCardAttendList?cp_id=" + value;
        HttpCallback callback = new HttpCallback(this,getActivity(),QUEST_TYPE_DIALOG_LOAD);
        NetUtil.reqSendGet(getActivity(),url,callback);
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
                ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
                attend();;
                alertDialog2.dismiss();
            }
        });
        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
                alertDialog2.dismiss();
            }
        });
        alertDialog2 = alertBuilder.create();
    }

    private void initDatas() {
        String url = "/mCoursePlanDetail?cp_id=" + value;
        HttpCallback callback = new HttpCallback(this,getActivity(),QUEST_TYPE_LIST_LOAD);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    private void updateViews(final Map<String,String> map) {
        tv_course_name.setText(map.get("course_name"));
        tv_classroom.setText(map.get("classroom_name"));
        String start_time = map.get("start_time");
        tv_start_time.setText(start_time.substring(0,start_time.length()-5));
        tv_last_time.setText(String.valueOf(map.get("last_time")) + "分钟");
        if (String.valueOf(map.get("status")).equals("0")) {
            tv_state.setText("未预订");
            btn_attend.setVisibility(View.GONE);
            btn_unbook.setVisibility(View.GONE);
            btn_book.setVisibility(View.VISIBLE);
        }else if (String.valueOf(map.get("status")).equals("1")) {
            tv_state.setText("未签到");
            btn_book.setVisibility(View.GONE);
            btn_unbook.setVisibility(View.VISIBLE);
            btn_attend.setVisibility(View.VISIBLE);
        }else if (String.valueOf(map.get("status")).equals("2")) {
            tv_state.setText("已签到");
            btn_book.setVisibility(View.GONE);
            btn_unbook.setVisibility(View.GONE);
            btn_attend.setVisibility(View.GONE);
        }
        if (String.valueOf(map.get("remark")).equals("")) {
            tv_note.setText("暂无备注");
        }else {
            tv_note.setText(map.get("remark"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_book_f_m_course_plan_detail_basic:
                if (dialog_card_list.size() != 0) {
                    getProgressBar(getActivity()).setVisibility(View.VISIBLE);
                    book();
                    break;
                }
            case R.id.btn_unbook_f_m_course_plan_detail_basic:
                getProgressBar(getActivity()).setVisibility(View.VISIBLE);
                unbook();
                break;
            case R.id.btn_attend_f_m_course_plan_detail_basic:
                getProgressBar(getActivity()).setVisibility(View.VISIBLE);
                alertDialog2.show();
                break;
            default:break;
        }
    }

    private void book() {
        String url = "/mBook?cp_id="+ value;
        HttpCallback callback = new HttpCallback(this,getActivity(),QUEST_TYPE_BOOK);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    private void unbook() {
        String url = "/mUnbook?cp_id="+ value;
        HttpCallback callback = new HttpCallback(this,getActivity(),QUEST_TYPE_UNBOOK);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    private void attend() {
        String url = "/mAttend?cp_id="+ value + "&mc_id="+String.valueOf(mapArrayList.get(selected_mc_id_positon).get("mc_id"));
        HttpCallback callback = new HttpCallback(this,getActivity(),QUEST_TYPE_ATTEND);
        NetUtil.reqSendGet(getActivity(),url,callback);
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
    public void onRespStatus(String body, int source) {
        ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
        if (source == QUEST_TYPE_LIST_LOAD) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(getActivity(),Ref.OP_NSR);
                    getActivity().finish();
                    break;
                default:break;
            }
        }else if (source == QUEST_TYPE_DIALOG_LOAD) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case EMPTY:
                    ViewHandler.toastShow(getActivity(),"你暂时无法签到");
                    initDatas();
                    break;
                default:break;
            }
        }else if (source == QUEST_TYPE_BOOK) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.toastShow(getActivity(),"你尚未办理所该课程需要的会员卡，暂无法预订");
                    break;
                case COURSEPLAN_EXPIRED:
                    ViewHandler.toastShow(getActivity(),"排课已过期，无法预订");
                    break;
                case DUPLICATE:
                    ViewHandler.toastShow(getActivity(),"你已预订该排课");
                    break;
                case FAIL:
                    ViewHandler.toastShow(getActivity(),"预订失败");
                    break;
                case SUCCESS:
                    ViewHandler.toastShow(getActivity(),"预订成功");
                    btn_attend.setVisibility(View.VISIBLE);
                    btn_unbook.setVisibility(View.VISIBLE);
                    btn_book.setVisibility(View.GONE);
                    break;
                default:break;
            }
        }else if (source == QUEST_TYPE_UNBOOK) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    ViewHandler.toastShow(getActivity(),"你未预订该课程");
                    break;
                case FAIL:
                    ViewHandler.toastShow(getActivity(),"退订失败");
                    break;
                case SUCCESS:
                    ViewHandler.toastShow(getActivity(),"退订成功");
                    btn_attend.setVisibility(View.GONE);
                    btn_unbook.setVisibility(View.GONE);
                    btn_book.setVisibility(View.VISIBLE);
                    break;
                default:break;
            }
        }else if (source == QUEST_TYPE_ATTEND) {
            ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
            switch (NetRespStatType.dealWithRespStat(body)) {
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.toastShow(getActivity(),"你未预订该课程");
                    tv_state.setText("未预订");
                    btn_unbook.setVisibility(View.GONE);
                    btn_attend.setVisibility(View.GONE);
                    break;
                case DUPLICATE:
                    ViewHandler.toastShow(getActivity(),"你已签到");
                    tv_state.setText("已签到");
                    btn_attend.setVisibility(View.GONE);
                    btn_unbook.setVisibility(View.GONE);
                    btn_book.setVisibility(View.GONE);
                    break;
                case FAIL:
                    ViewHandler.toastShow(getActivity(),"签到失败");
                    break;
                case MEMBERCARD_EXPIRED:
                    ViewHandler.toastShow(getActivity(),Ref.OP_MEMBER_CARD_EXPIRED);
                    break;
                case BALANCE_NOT_ENOUGH:
                    ViewHandler.toastShow(getActivity(),Ref.OP_BALANCE_NOT_ENOUGH);
                    break;
                case SUCCESS:
                    ViewHandler.toastShow(getActivity(),"签到成功");
                    btn_attend.setVisibility(View.GONE);
                    btn_unbook.setVisibility(View.GONE);
                    btn_book.setVisibility(View.GONE);
                    tv_state.setText("已签到");
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == QUEST_TYPE_LIST_LOAD) {
            ArrayList<Map<String,String>> mapArrayList = new ArrayList<>();
            mapArrayList = JsonUtil.strToListMap(body,strs_detail_keys);
            Map<String,String> map = new HashMap<>();
            map = mapArrayList.get(0);
            updateViews(map);
        }else if (source == QUEST_TYPE_DIALOG_LOAD) {
            initDatas();
            mapArrayList = JsonUtil.strToListMap(body,strs_supported_cards);
            for (int i = 0;i < mapArrayList.size();i++) {
                if (Integer.parseInt(String.valueOf(mapArrayList.get(i).get("type"))) == 1) {
                    dialog_card_list.add(String.valueOf(mapArrayList.get(i).get("c_name")) + "(" + String.valueOf(mapArrayList.get(i).get("balance")) + "元)");
                }else if (Integer.parseInt(String.valueOf(mapArrayList.get(i).get("type"))) == 2) {
                    dialog_card_list.add(String.valueOf(mapArrayList.get(i).get("c_name")) + "(" + String.valueOf(mapArrayList.get(i).get("balance")) + "次)");
                }else if (Integer.parseInt(String.valueOf(mapArrayList.get(i).get("type"))) == 3) {
                    dialog_card_list.add(String.valueOf(mapArrayList.get(i).get("c_name")));
                }
                initDialog(view,dialog_card_list);
            }
        }else if (source == QUEST_TYPE_BOOK) {

        }else if (source == QUEST_TYPE_UNBOOK) {

        }else if (source == QUEST_TYPE_ATTEND) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        if (source == QUEST_TYPE_LIST_LOAD) {

        }else if (source == QUEST_TYPE_DIALOG_LOAD) {

        }else if (source == QUEST_TYPE_BOOK) {

        }else if (source == QUEST_TYPE_UNBOOK) {
            ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
        }else if (source == QUEST_TYPE_ATTEND) {
            ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
        }else {

        }
    }

    @Override
    public void onReqFailure(Object object, int source) {
        if (source == QUEST_TYPE_LIST_LOAD) {

        }else if (source == QUEST_TYPE_DIALOG_LOAD) {

        }else if (source == QUEST_TYPE_BOOK) {
            ViewHandler.toastShow(getActivity(),Ref.CANT_CONNECT_INTERNET);
            ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
        }else if (source == QUEST_TYPE_UNBOOK) {

        }else if (source == QUEST_TYPE_ATTEND) {

        }else {

        }
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
        if (source == QUEST_TYPE_LIST_LOAD) {

        }else if (source == QUEST_TYPE_DIALOG_LOAD) {

        }else if (source == QUEST_TYPE_BOOK) {

        }else if (source == QUEST_TYPE_UNBOOK) {

        }else if (source == QUEST_TYPE_ATTEND) {

        }else {

        }
    }
}
