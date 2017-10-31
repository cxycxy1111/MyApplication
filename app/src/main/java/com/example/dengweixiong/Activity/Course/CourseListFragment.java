package com.example.dengweixiong.Activity.Course;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dengweixiong.Activity.MainActivity;
import com.example.dengweixiong.Adapter.CourseListRVAdapter;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Reference;
import com.example.dengweixiong.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

    private OnFragmentInteractionListener mListener;
    private long s_id,sm_id;
    private String [] keys = {"id","name","last_time","supportedcard"};
    private View view;
    private MainActivity mainActivity = (MainActivity)getActivity();
    private RecyclerView recyclerView;
    private CourseListRVAdapter adapter;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_list,container,false);
        initShopData();
        initRecyclerView();
        return view;
    }

    /**
     * 初始化舞馆及当前登陆者的ID
     */
    private void initShopData() {
        SharedPreferences preferences = getActivity().getSharedPreferences("sasm", Activity.MODE_PRIVATE);
        s_id = preferences.getLong("s_id",0);
        sm_id = preferences.getLong("sm_id",0);
    }


    /**
     * 获取recyclerview的数据模型
     * @return
     */
    private void initRecyclerView() {
        recyclerviewdata = new ArrayList<>();
        String url = "/CourseListQuery?s_id=" + s_id;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(), Reference.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                regional_list = JsonHandler.strToListMap(resp,keys);
                for (int i = 0;i < regional_list.size();i++) {
                    Map<String,String> temp_map = regional_list.get(i);
                    Map<String,String> new_map = new HashMap<>();
                    String supportedcard = temp_map.get("supportedcard");
                    supportedcard = supportedcard.substring(0,supportedcard.length()-1);
                    new_map.put("id",temp_map.get("id"));
                    new_map.put("name",temp_map.get("name"));
                    new_map.put("last_time",temp_map.get("last_time"));
                    new_map.put("supportedcard","支持" + supportedcard);
                    recyclerviewdata.add(new_map);
                }
                adapter = new CourseListRVAdapter(recyclerviewdata,getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    initRecyclerView(view);
                    }
                });
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
    }

    /**
     * 初始化recyclerview
     * @param view
     */
    private void initRecyclerView (View view) {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_course_list);
        recyclerView.setLayoutManager(layoutManager);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
