package xyz.institutionmanage.sailfish.Shopmember.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import xyz.institutionmanage.sailfish.R;
import xyz.institutionmanage.sailfish.Shopmember.Adapter.RVCourseListAdapter;
import xyz.institutionmanage.sailfish.Shopmember.Course.Course.CourseDetailActivity;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespStatType;
import xyz.institutionmanage.sailfish.Util.Enum.EnumRespType;
import xyz.institutionmanage.sailfish.Util.JsonHandler;
import xyz.institutionmanage.sailfish.Util.MethodTool;
import xyz.institutionmanage.sailfish.Util.NetUtil;
import xyz.institutionmanage.sailfish.Util.Ref;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private static final String TAG = "CourseListFragment:";
    private OnFragmentInteractionListener mListener;
    private long s_id,sm_id;
    private String [] keys = {"id","name","last_time","supportedcard"};
    private View view;
    private RecyclerView recyclerView;
    private RVCourseListAdapter adapter;
    private List<Map<String,String>> recyclerviewdata = new ArrayList<>();
    private List<Map<String,String>> regional_list = new ArrayList<>();

    public CourseListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CourseDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseListFragment newInstance(String param1) {
        CourseListFragment fragment = new CourseListFragment();
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
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main_course_list,container,false);
        initShopData();
        initRecyclerView();
        return view;
    }

    /**
     * 初始化舞馆及当前登陆者的ID
     */
    private void initShopData() {
        SharedPreferences preferences = getParentFragment().getActivity().getSharedPreferences("sasm", Activity.MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0);
        sm_id = preferences.getLong("sm_id",0);
    }


    /**
     * 获取recyclerview的数据模型
     * @return
     */
    private void initRecyclerView() {
        recyclerviewdata = new ArrayList<>();
        String url = "/CourseListQuery";
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getParentFragment().getActivity(), Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.d(TAG, "onResponse: " + resp);
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_MAPLIST:
                        regional_list = JsonHandler.strToListMap(resp,keys);
                        for (int i = 0;i < regional_list.size();i++) {
                            Map<String,String> temp_map = regional_list.get(i);
                            Map<String,String> new_map = new HashMap<>();
                            String supportedcard = temp_map.get("supportedcard");
                            new_map.put("id",temp_map.get("id"));
                            new_map.put("name",temp_map.get("name"));
                            new_map.put("last_time",String.valueOf(temp_map.get("last_time")));
                            new_map.put("type",temp_map.get("type"));
                            new_map.put("supportedcard",temp_map.get("supportedcard"));
                            recyclerviewdata.add(new_map);
                        }
                        getParentFragment().getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initRecyclerView(view);
                            }
                        });
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case NSR:
                                MethodTool.showToast(getParentFragment().getActivity(), Ref.STAT_NSR);
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getParentFragment().getActivity());
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(getParentFragment().getActivity());
                                break;
                            case EMPTY_RESULT:
                                MethodTool.showToast(getParentFragment().getActivity(),"暂无课程");
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
        NetUtil.sendHttpRequest(getParentFragment().getActivity(),url,callback);
    }

    /**
     * 初始化recyclerview
     * @param view
     */
    private void initRecyclerView (View view) {
        adapter = new RVCourseListAdapter(recyclerviewdata,getParentFragment().getActivity());
        adapter.setOnItemClickListener(new RVCourseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Map<String,String> map = recyclerviewdata.get(position);
                Intent intent = new Intent(getParentFragment().getActivity(), CourseDetailActivity.class);
                intent.putExtra("name",map.get("name"));
                intent.putExtra("id",String.valueOf(map.get("id")));
                intent.putExtra("type",String.valueOf(map.get("type")));
                getParentFragment().getActivity().startActivityForResult(intent,1);
            }
        });
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_course_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case 1:
                        break;
                    default:break;
                }
            default:break;
        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerviewdata.clear();
        regional_list.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        initRecyclerView();
    }
}
