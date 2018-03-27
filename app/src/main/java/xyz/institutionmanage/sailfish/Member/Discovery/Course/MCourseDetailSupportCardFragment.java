package xyz.institutionmanage.sailfish.Member.Discovery.Course;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.Adapter.RVMemberCardListAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MCourseDetailSupportCardFragment extends Fragment {

    private static final String ARG_PARAM1 = "c_id";
    private RecyclerView recyclerView;
    private long c_id;
    private String[] keys = new String[]{"c_id","card_name","cs_price","course_id","type"};
    private ArrayList<Map<String,String>> mapArrayList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public MCourseDetailSupportCardFragment() {
    }

    public static MCourseDetailSupportCardFragment newInstance(long param1) {
        MCourseDetailSupportCardFragment fragment = new MCourseDetailSupportCardFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            c_id = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mcourse_detail_support_card, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_m_course_detail_support_card);
        initData();
        return view;
    }

    private void initData() {
        String url = "/mCourseSupportCardQuery?c_id=" + c_id;
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
                        mapArrayList = JsonHandler.strToListMap(resp,keys);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initRecyclerView();
                            }
                        });
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getActivity());
                                break;
                            default:break;
                        }
                        break;
                    case RESP_ERROR:
                        MethodTool.showToast(getActivity(),Ref.UNKNOWN_ERROR);
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
    }

    private void initRecyclerView() {
        List<Map<String,String>> arrayList = new ArrayList<>();
        for (int i = 0;i < mapArrayList.size();i++) {
            Map<String,String> map_temp = mapArrayList.get(i);
            Map<String,String> hashMap = new HashMap<>();
            hashMap.put("name",map_temp.get("card_name"));
            hashMap.put("type",map_temp.get("type"));
            if (String.valueOf(map_temp.get("type")).equals("1")) {
                hashMap.put("balance",String.valueOf(map_temp.get("cs_price")));
            }else if (String.valueOf(map_temp.get("type")).equals("2")){
                hashMap.put("balance",String.valueOf(map_temp.get("cs_price")));
            }else if (String.valueOf(map_temp.get("type")).equals("3")) {
                hashMap.put("balance","");
            }
            arrayList.add(hashMap);
        }
        RVMemberCardListAdapter adapter = new RVMemberCardListAdapter(getActivity(),arrayList);
        adapter.setOnItemClickListener(new RVMemberCardListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
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
}
