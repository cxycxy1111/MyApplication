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
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.dengweixiong.Shopmember.Member.AddNewMemberActivity;
import com.example.dengweixiong.Shopmember.Member.MemberListActivity;
import com.example.dengweixiong.Shopmember.MemberCard.AddNewMemberCardActivity;
import com.example.dengweixiong.Shopmember.MemberCard.ChargeActivity;
import com.example.dengweixiong.Shopmember.MemberCard.DeductionActivity;
import com.example.dengweixiong.Shopmember.MemberCard.MemberCardListActivity;
import com.example.dengweixiong.myapplication.R;

public class MemberFragment
        extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private RelativeLayout rl_add_member,rl_member_manage,rl_add_member_card,rl_member_card_manage,rl_charge,rl_deduction;
    private ScrollView scrollView;
    private String mParam1;
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_main_member, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_add_member_f_member:
                intent = new Intent(getActivity(),AddNewMemberActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_member_manage_f_member:
                intent = new Intent(getActivity(),MemberListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_add_member_card_f_member:
                intent = new Intent(getActivity(),AddNewMemberCardActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_member_card_manage_f_member:
                intent = new Intent(getActivity(),MemberCardListActivity.class);
                intent.putExtra("source","ShopmemberMainActivity");
                startActivity(intent);
                break;
            case R.id.rl_charge_f_member:
                intent = new Intent(getActivity(),ChargeActivity.class);
                intent.putExtra("source","ShopmemberMainActivity");
                startActivity(intent);
                break;
            case R.id.rl_deduction_f_member:
                intent = new Intent(getActivity(),DeductionActivity.class);
                intent.putExtra("source","ShopmemberMainActivity");
                startActivity(intent);
                break;
            default:break;
        }
    }

    private void initToolbar() {
        context.getSupportActionBar().setTitle("会员");
    }

    private void initView(View view) {
        ShopmemberMainActivity shopmemberMainActivity = (ShopmemberMainActivity)getActivity();
        rl_add_member = (RelativeLayout)view.findViewById(R.id.rl_add_member_f_member);
        rl_member_manage = (RelativeLayout)view.findViewById(R.id.rl_member_manage_f_member);
        rl_add_member_card = (RelativeLayout)view.findViewById(R.id.rl_add_member_card_f_member);
        rl_member_card_manage = (RelativeLayout)view.findViewById(R.id.rl_member_card_manage_f_member);
        rl_charge = (RelativeLayout)view.findViewById(R.id.rl_charge_f_member);
        rl_deduction = (RelativeLayout)view.findViewById(R.id.rl_deduction_f_member);
        scrollView = (ScrollView)view.findViewById(R.id.sv_f_member);

        rl_add_member.setOnClickListener(this);
        rl_member_manage.setOnClickListener(this);
        rl_add_member_card.setOnClickListener(this);
        rl_member_card_manage.setOnClickListener(this);
        rl_charge.setOnClickListener(this);
        rl_deduction.setOnClickListener(this);
        scrollView.setVerticalScrollBarEnabled(false);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_member,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_message_fragment_member:
                break;
            default:break;
        }
        return true;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
