package xyz.institutionmanage.sailfish.Member.Discovery.CoursePlan;

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
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.institutionmanage.sailfish.Adapter.RVSimpleAdapter;
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

public class MCoursePlanDetailTeacherFragment extends Fragment {

    private static final String key = "cp_id";

    private String cp_id;
    private View view;
    private RecyclerView recyclerView;
    private RVSimpleAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private OnFragmentInteractionListener mListener;

    public MCoursePlanDetailTeacherFragment() {
    }

    public static MCoursePlanDetailTeacherFragment newInstance(String value) {
        MCoursePlanDetailTeacherFragment fragment = new MCoursePlanDetailTeacherFragment();
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
                        String[] keys = new String[] {"teacher_id","name"};
                        List<Map<String,String>> maps = new ArrayList<>();
                        maps = JsonHandler.strToListMap(resp,keys);
                        final List<Map<String, String>> finalMaps = maps;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initRecyclerView(finalMaps);
                            }
                        });
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getActivity());
                                break;
                            case NSR:
                                MethodTool.showToast(getActivity(),"暂未安排教师授课");
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

    private void initRecyclerView(List<Map<String,String>> maps) {
        List <String> list = new ArrayList<>();
        for (int i = 0;i < maps.size();i++) {
            list.add(maps.get(i).get("name"));
        }
        adapter = new RVSimpleAdapter(list);
        adapter.setOnItemClickListener(new RVSimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
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
}
