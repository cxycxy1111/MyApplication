package xyz.institutionmanager.sailfish.Member.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVMemberMyBookAdapter;
import xyz.institutionmanager.sailfish.Member.Book.MBookDetailActivity;
import xyz.institutionmanager.sailfish.Member.Book.MBookDetailPrivateActivity;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MBookFragment extends BaseFragment implements HttpResultListener {

    private static final int REQUEST_BookListQuery = 1;
    private static final int REQUEST_MemberCardAttendList = 2;
    private static final int REQUEST_Attend = 3;
    private int card_selected_position,book_selected_position;
    private int status = 0;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private String[] keys = new String[]{"id","courseplan_id","c_name","start_time","end_time","type","name","cr_name","teacher"};
    private String[] keys_cards= new String[]{"mc_id","c_name","balance","type"};
    private View view;
    private RecyclerView recyclerView;
    private RelativeLayout rl_empty;
    private AlertDialog dialog_card_list;
    private AlertDialog dialog_confirm;
    private RVMemberMyBookAdapter rvMemberMyBookAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Map<String,String>> list_book = new ArrayList<>();
    private List<String> list_card = new ArrayList<>();
    private ArrayList<Map<String,String>> mapList_card = new ArrayList<>();
    private OnFragmentInteractionListener mListener;

    public MBookFragment() {

    }

    public static MBookFragment newInstance(String param1) {
        MBookFragment fragment = new MBookFragment();
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

        if (view == null) {
            view = inflater.inflate(R.layout.activity_m_book_f, container, false);
            recyclerView = (RecyclerView)view.findViewById(R.id.rv_a_m_book_f);
            linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
            rl_empty = (RelativeLayout)view.findViewById(R.id.rl_t_text_empty);
            initAlertDialog("签到","课程签到","确定","取消");
            initBookList();
        }

        return view;
    }

    private void initBookList() {
        String url = "/mBookListQuery";
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_BookListQuery);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    private void initBookView() {
        RVMemberMyBookAdapter.OnItemClickListener onItemClickListener = new RVMemberMyBookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!String.valueOf(list_book.get(position).get("type")).equals("4")) {
                    Intent intent = new Intent(getActivity(), MBookDetailActivity.class);
                    intent.putExtra("cp_id",String.valueOf(list_book.get(position).get("courseplan_id")));
                    intent.putExtra("c_name",String.valueOf(list_book.get(position).get("c_name")));
                    intent.putExtra("start_time",String.valueOf(list_book.get(position).get("start_time")));
                    getActivity().startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), MBookDetailPrivateActivity.class);
                    intent.putExtra("cp_id",String.valueOf(list_book.get(position).get("courseplan_id")));
                    intent.putExtra("c_name",String.valueOf(list_book.get(position).get("c_name")));
                    intent.putExtra("start_time",String.valueOf(list_book.get(position).get("start_time")));
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onButtonClick(View view, int position) {
                book_selected_position = position;
                if (!String.valueOf(list_book.get(position).get("type")).equals("4")) {
                    mapList_card.clear();
                    list_card.clear();
                    initCardListData(position);
                }else {
                    dialog_confirm.show();
                }

            }
        };
        rvMemberMyBookAdapter = new RVMemberMyBookAdapter(getActivity(),list_book);
        rvMemberMyBookAdapter.setOnItemClickListener(onItemClickListener);
        rvMemberMyBookAdapter.notifyDataSetChanged();
        if (recyclerView == null) {
            recyclerView = (RecyclerView)view.findViewById(R.id.rv_a_m_book_f);
        }
        recyclerView.setAdapter(rvMemberMyBookAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initCardListData(int position) {
        String url = "/mMemberCardAttendList?cp_id=" + String.valueOf(list_book.get(position).get("courseplan_id"));
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_MemberCardAttendList);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    private void initCardListDialog(View view,List<String> list){
        int size = list.size();
        String[] strs = new String[size];
        final String[] items = list.toArray(strs);
        final AlertDialog.Builder cardListBuilder = new AlertDialog.Builder(getActivity());
        cardListBuilder.setTitle("请选择待签到的卡");
        cardListBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
                card_selected_position = index;
            }
        });
        cardListBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
                dialog_confirm.show();
            }
        });
        cardListBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
                dialog_confirm.dismiss();
            }
        });
        dialog_card_list = cardListBuilder.create();
        dialog_card_list.show();
    }

    private void initAlertDialog(String title,String content,String confirm,String cancle) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setCancelable(false);
        dialog.setPositiveButton(confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                attend();
                dialog.dismiss();

            }
        });
        dialog.setNegativeButton(cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog_confirm = dialog.create();
    }

    private void attend() {
        String url = "";
        if (String.valueOf(list_book.get(book_selected_position).get("type")).equals("4")) {
            url = "/mAttend?cp_id="+ String.valueOf(list_book.get(book_selected_position).get("courseplan_id")) + "&mc_id=0";
        }else {
            url = "/mAttend?cp_id="+ String.valueOf(list_book.get(book_selected_position).get("courseplan_id")) + "&mc_id="+String.valueOf(mapList_card.get(card_selected_position).get("mc_id"));
        }
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_Attend);
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

    @Override
    public void onResume() {
        super.onResume();
        if (view != null) {
            list_book.clear();
            mapList_card.clear();
            list_card.clear();
            if (rvMemberMyBookAdapter != null) {
                rvMemberMyBookAdapter.notifyDataSetChanged();
            }
            initBookList();
        }
    }

    @Override
    public void onRespStatus(String body, int source) {
        ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
        if (source == REQUEST_BookListQuery) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case EMPTY:
                    ViewHandler.toastShow(getActivity(),"你暂未预订任何排课");
                    break;
                default:break;
            }
        }else if (source == REQUEST_MemberCardAttendList) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case EMPTY:
                    rl_empty.setVisibility(View.VISIBLE);
                    ViewHandler.toastShow(getActivity(),"你暂时无法签到");
                    break;
                default:break;
            }
        }else if (source == REQUEST_Attend) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.toastShow(getActivity(),"你未预订该课程");
                    break;
                case DUPLICATE:
                    ViewHandler.toastShow(getActivity(),"你已签到");
                    getProgressBar(getActivity()).setVisibility(View.GONE);
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
                    list_book.remove(book_selected_position);
                    book_selected_position= 0;
                    rvMemberMyBookAdapter.notifyDataSetChanged();
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_BookListQuery) {
            list_book = JsonUtil.strToListMap(body,keys);
            Tool.sortListMapAsc(list_book,"start_time");
            initBookView();
        }else if (source == REQUEST_MemberCardAttendList) {
            mapList_card = JsonUtil.strToListMap(body,keys_cards);
            for (int i = 0;i < mapList_card.size();i++) {
                if (Integer.parseInt(String.valueOf(mapList_card.get(i).get("type"))) == 1) {
                    list_card.add(String.valueOf(mapList_card.get(i).get("c_name")) + "(" + String.valueOf(mapList_card.get(i).get("balance")) + "元)");
                }else if (Integer.parseInt(String.valueOf(mapList_card.get(i).get("type"))) == 2) {
                    list_card.add(String.valueOf(mapList_card.get(i).get("c_name")) + "(" + String.valueOf(mapList_card.get(i).get("balance")) + "次)");
                }else if (Integer.parseInt(String.valueOf(mapList_card.get(i).get("type"))) == 3) {
                    list_card.add(String.valueOf(mapList_card.get(i).get("c_name")));
                }
            }
            initCardListDialog(view,list_card);
        }else if (source == REQUEST_Attend) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
        if (source == REQUEST_BookListQuery) {
        }else if(source == REQUEST_MemberCardAttendList) {
            rl_empty.setVisibility(View.VISIBLE);
        }else if (source == REQUEST_Attend) {

        }else {

        }
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.progressBarHide(getActivity(),getProgressBar(getActivity()));
        ViewHandler.toastShow(getActivity(), Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
