package com.example.dengweixiong.Shopmember.Main;

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

import com.example.dengweixiong.Shopmember.Member.AddNewMemberActivity;
import com.example.dengweixiong.Shopmember.MemberCard.AddNewMemberCardActivity;
import com.example.dengweixiong.Shopmember.MemberCard.ChargeActivity;
import com.example.dengweixiong.Shopmember.MemberCard.DeductionActivity;
import com.example.dengweixiong.Shopmember.MemberCard.MemberCardListActivity;
import com.example.dengweixiong.Shopmember.Member.MemberListActivity;
import com.example.dengweixiong.myapplication.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberFragment
        extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();
    private List<Map<String,Object>> mapList_2 = new ArrayList<Map<String, Object>>();
    private String [] values = {"新增会员","会员管理"};
    private String [] values_2 = {"新增会员卡","会员卡管理","充值","扣费"};
    private int [] icons = {R.mipmap.icon,R.mipmap.icon};
    private int [] icons_2 = {R.mipmap.icon,R.mipmap.icon,R.mipmap.icon,R.mipmap.icon};
    private String [] map = {"icon","name"};
    private int [] id = {R.id.simple_list_view_img,R.id.simple_list_view_text};
    private SimpleAdapter adapter_1;
    private SimpleAdapter adapter_2;
    private ListView listView_1;
    private ListView listView_2;
    private static ShopmemberMainActivity context;
    //自建变量

    public MemberFragment() {
    }


    public static MemberFragment newInstance(String param1,ShopmemberMainActivity mContext) {
        context = mContext;
        MemberFragment fragment = new MemberFragment();
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
        final View view = inflater.inflate(R.layout.activity_main_member, container, false);
        initToolbar();
        initListView(view);
        return view;
    }

    private void initToolbar() {
        context.getSupportActionBar().setTitle("会员");
    }

    private void initListView(View view) {
        ShopmemberMainActivity shopmemberMainActivity = (ShopmemberMainActivity)getActivity();
        mapList = initData(values,icons);
        mapList_2 = initData(values_2,icons_2);
        listView_1 = (ListView)view.findViewById(R.id.fragment_member_lv_1);
        listView_2 = (ListView)view.findViewById(R.id.fragment_member_lv_2);
        adapter_1 = new SimpleAdapter(shopmemberMainActivity,mapList,R.layout.tile_simple_list_view_with_icon,map,id);
        adapter_2 = new SimpleAdapter(shopmemberMainActivity,mapList_2,R.layout.tile_simple_list_view_with_icon,map,id);
        listView_1.setAdapter(adapter_1);
        listView_2.setAdapter(adapter_2);
        listView_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(getActivity(),AddNewMemberActivity.class);
                        startActivity(intent);
                    case 1:
                        intent = new Intent(getActivity(),MemberListActivity.class);
                        startActivity(intent);
                    default:break;
                }
            }
        });
        listView_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(getActivity(), AddNewMemberCardActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), MemberCardListActivity.class);
                        intent.putExtra("source","ShopmemberMainActivity");
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getActivity(), ChargeActivity.class);
                        intent.putExtra("source","ShopmemberMainActivity");
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(getActivity(), DeductionActivity.class);
                        intent.putExtra("source","ShopmemberMainActivity");
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private List<Map<String,Object>> initData(String [] strings,int [] ints) {
        List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();
        for (int i=0;i<strings.length;i++) {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("icon",ints[i]);
            map.put("name",strings[i]);
            mapList.add(map);
        }
        return  mapList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_member,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_message_fragment_member:
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
