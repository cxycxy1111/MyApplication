package com.example.dengweixiong.Shopmember.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.dengweixiong.Shopmember.Profile.Card.CardTypeListActivity;
import com.example.dengweixiong.Shopmember.Profile.Classroom.ClassroomListActivity;
import com.example.dengweixiong.Shopmember.Profile.Shopmember.ShopmemberListActivity;
import com.example.dengweixiong.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonFragment
        extends Fragment
        implements ListView.OnItemClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "Nothing to Tell";
    private String mParam1;
    private ListView listView;
    private int [] icons = {R.mipmap.icon,R.mipmap.icon,R.mipmap.icon};
    private String [] values = {"会员卡类型管理","教师管理","课室管理"};
    private String [] map = {"icon","name"};
    private int [] id = {R.id.simple_list_view_img,R.id.simple_list_view_text};
    private OnFragmentInteractionListener mListener;
    private static ShopmemberMainActivity context;

    public PersonFragment() {
    }

    public static PersonFragment newInstance(String param1,ShopmemberMainActivity shopmemberMainActivity) {
        context = shopmemberMainActivity;
        PersonFragment fragment = new PersonFragment();
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
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_person, container, false);
        initToolbar();
        initListView(view);
        return view;
    }

    private void initToolbar() {
        context.getSupportActionBar().setTitle("个人中心");
    }

    private void initListView(View view) {
        listView = (ListView)view.findViewById(R.id.lv_f_person);
        SimpleAdapter adapter = new SimpleAdapter(view.getContext(),initData(),R.layout.tile_simple_list_view_with_icon,map,id);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private List<Map<String,Object>> initData() {
        List<Map<String,Object>> list = new ArrayList<>();
        for (int i = 0;i < icons.length;i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("icon",icons[i]);
            map.put("name",values[i]);
            list.add(map);
        }
        return list;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(getActivity(),CardTypeListActivity.class);
                break;
            case 1:
                intent = new Intent(getActivity(), ShopmemberListActivity.class);
                break;
            case 2:
                intent = new Intent(getActivity(),ClassroomListActivity.class);
                break;
            default:
                intent = new Intent(getActivity(),getActivity().getClass());
                break;
        }
        startActivity(intent);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_person,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
