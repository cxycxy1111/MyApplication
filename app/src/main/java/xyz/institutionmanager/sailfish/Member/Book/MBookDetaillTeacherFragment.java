package xyz.institutionmanager.sailfish.Member.Book;

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
import xyz.institutionmanager.sailfish.Adapter.RVSimpleAdapter;
import xyz.institutionmanager.sailfish.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MBookDetaillTeacherFragment extends Fragment implements HttpResultListener {

    private static final String key = "cp_id";

    private String cp_id;
    private View view;
    private RecyclerView recyclerView;
    private RVSimpleAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private OnFragmentInteractionListener mListener;

    public MBookDetaillTeacherFragment() {
    }

    public static MBookDetaillTeacherFragment newInstance(String value) {
        MBookDetaillTeacherFragment fragment = new MBookDetaillTeacherFragment();
        Bundle args = new Bundle();
        args.putString(key, value);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cp_id = getArguments().getString(key);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mcourse_plan_detail_teacher, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_mcourse_plan_detail_teacher);
        initData();
        return view;
    }

    private void initData() {
        String url = "/mCoursePlanTeacherListQuery?cp_id=" + cp_id;
        HttpCallback callback = new HttpCallback(this,getActivity(),0);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    private void initRecyclerView(List<Map<String,String>> maps) {
        List <String> list = new ArrayList<>();
        for (int i = 0;i < maps.size();i++) {
            list.add(maps.get(i).get("name"));
        }
        adapter = new RVSimpleAdapter(list);
        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
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
            case NSR:
                ViewHandler.toastShow(getActivity(),"暂未安排教师授课");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        String[] keys = new String[] {"teacher_id","name"};
        List<Map<String,String>> maps = new ArrayList<>();
        maps = JsonUtil.strToListMap(body,keys);
        final List<Map<String, String>> finalMaps = maps;
        initRecyclerView(finalMaps);
    }

    @Override
    public void onRespError(int source) {

    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }
}
