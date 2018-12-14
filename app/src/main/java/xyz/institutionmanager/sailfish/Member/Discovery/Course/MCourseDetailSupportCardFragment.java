package xyz.institutionmanager.sailfish.Member.Discovery.Course;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.Adapter.RVMemberCardListAdapter;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCourseDetailSupportCardFragment extends Fragment implements HttpResultListener {

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
        String url = "/mCourseSupportCardQuery?c_id=" + c_id;
        HttpCallback callback = new HttpCallback(this,getActivity(),0);
        NetUtil.reqSendGet(getActivity(),url,callback);
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

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        mapArrayList = JsonUtil.strToListMap(body,keys);
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
