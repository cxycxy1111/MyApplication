package xyz.institutionmanager.sailfish.Shopmember.Main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Shopmember.Member.AddNewMemberActivity;
import xyz.institutionmanager.sailfish.Shopmember.Member.MemberListActivity;
import xyz.institutionmanager.sailfish.Shopmember.MemberCard.AddNewMemberCardActivity;
import xyz.institutionmanager.sailfish.Shopmember.MemberCard.Batch.AddNewMemberCardBatchActivity;
import xyz.institutionmanager.sailfish.Shopmember.MemberCard.ChargeActivity;
import xyz.institutionmanager.sailfish.Shopmember.MemberCard.DeductionActivity;
import xyz.institutionmanager.sailfish.Shopmember.MemberCard.MemberCardListActivity;

public class MemberFragment
        extends Fragment
        implements View.OnClickListener{

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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main_member,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
/**
            case R.id.batch_import_member_menu_main_member:
                Intent intent_1 = new Intent(getActivity(),ImportMemberActivity.class);
                startActivity(intent_1);
                break;
            case R.id.export_member_menu_main_member:
                break;
             **/
            case R.id.batch_add_member_card_menu_main_member:
                Intent intent = new Intent(getActivity(),AddNewMemberCardBatchActivity.class);
                startActivity(intent);
                break;
                /**
            case R.id.export_member_card_menu_main_member:
                break;
                 **/
            default:break;
        }
        return true;
    }
}
