package xyz.institutionmanage.sailfish.Member.Main;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.Adapter.RVMemberMyBookAdapter;
import xyz.institutionmanage.sailfish.Member.Book.MBookDetailActivity;
import xyz.institutionmanage.sailfish.Member.Book.MBookDetailPrivateActivity;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.ActivityManager;
import xyz.institutionmanage.sailfish.Util.BaseFragment;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MBookFragment extends BaseFragment {

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
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(), Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        list_book = JsonHandler.strToListMap(resp,keys);
                        MethodTool.sortListMap(list_book,"start_time");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initBookView();
                            }
                        });
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EMPTY_RESULT:
                                MethodTool.showToast(getActivity(),"你暂未预订任何排课");
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
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        mapList_card = JsonHandler.strToListMap(resp,keys_cards);
                        for (int i = 0;i < mapList_card.size();i++) {
                            if (Integer.parseInt(String.valueOf(mapList_card.get(i).get("type"))) == 1) {
                                list_card.add(String.valueOf(mapList_card.get(i).get("c_name")) + "(" + String.valueOf(mapList_card.get(i).get("balance")) + "元)");
                            }else if (Integer.parseInt(String.valueOf(mapList_card.get(i).get("type"))) == 2) {
                                list_card.add(String.valueOf(mapList_card.get(i).get("c_name")) + "(" + String.valueOf(mapList_card.get(i).get("balance")) + "次)");
                            }else if (Integer.parseInt(String.valueOf(mapList_card.get(i).get("type"))) == 3) {
                                list_card.add(String.valueOf(mapList_card.get(i).get("c_name")));
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initCardListDialog(view,list_card);
                            }
                        });
                        break;
                    case RESP_ERROR:
                        rl_empty.setVisibility(View.VISIBLE);
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case EMPTY_RESULT:
                                rl_empty.setVisibility(View.VISIBLE);
                                MethodTool.showToast(getActivity(),"你暂时无法签到");
                                break;
                            case SESSION_EXPIRED:
                                rl_empty.setVisibility(View.VISIBLE);
                                MethodTool.showToast(getActivity(),Ref.ALERT_SESSION_EXPIRED);
                                ActivityManager.removeAllActivity();
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
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
                MethodTool.hideProgressBar(getActivity(),getProgressBar(getActivity()));
                dialog_confirm.show();
            }
        });
        cardListBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                MethodTool.hideProgressBar(getActivity(),getProgressBar(getActivity()));
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
                            case AUTHORIZE_FAIL:
                                MethodTool.showToast(getActivity(),"你未预订该课程");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                                break;
                            case DUPLICATE:
                                MethodTool.showToast(getActivity(),"你已签到");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getProgressBar(getActivity()).setVisibility(View.GONE);
                                    }
                                });
                                break;
                            case EXE_FAIL:
                                MethodTool.showToast(getActivity(),"签到失败");
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showToast(getActivity(),Ref.ALERT_SESSION_EXPIRED);
                                ActivityManager.removeAllActivity();
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
                                        list_book.remove(book_selected_position);
                                        book_selected_position= 0;
                                        rvMemberMyBookAdapter.notifyDataSetChanged();
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
