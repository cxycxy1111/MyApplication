package com.example.dengweixiong.Shopmember.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.dengweixiong.Shopmember.Profile.Card.CardTypeListActivity;
import com.example.dengweixiong.Shopmember.Profile.Classroom.ClassroomListActivity;
import com.example.dengweixiong.Shopmember.Profile.HelpActivity;
import com.example.dengweixiong.Shopmember.Profile.Shopmember.ShopmemberListActivity;
import com.example.dengweixiong.Util.ActivityManager;
import com.example.dengweixiong.myapplication.R;

public class PersonFragment
        extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "Nothing to Tell";
    private String mParam1;
    private RelativeLayout rl_card_type,rl_teacher,rl_classroom,rl_logout,rl_help;
    private ScrollView scrollView;
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_person, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_card_type_f_person:
                intent = new Intent(getActivity(),CardTypeListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_teacher_f_person:
                intent = new Intent(getActivity(),ShopmemberListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_classroom_f_person:
                intent = new Intent(getActivity(),ClassroomListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_logout_f_person:
                SharedPreferences preferences_sasm = getActivity().getSharedPreferences("sasm",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor_sasm = preferences_sasm.edit();
                editor_sasm.clear();
                editor_sasm.commit();
                SharedPreferences preferences_login = getActivity().getSharedPreferences("login_data",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor_login = preferences_login.edit();
                editor_login.clear();
                editor_login.commit();
                ActivityManager.removeAllActivity();
                break;
            case R.id.rl_help_f_person:
                intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            default:break;
        }
    }

    private void initViews(View view) {
        rl_card_type = (RelativeLayout)view.findViewById(R.id.rl_card_type_f_person);
        rl_teacher = (RelativeLayout)view.findViewById(R.id.rl_teacher_f_person);
        rl_classroom = (RelativeLayout)view.findViewById(R.id.rl_classroom_f_person);
        rl_logout = (RelativeLayout)view.findViewById(R.id.rl_logout_f_person);
        rl_help = (RelativeLayout)view.findViewById(R.id.rl_help_f_person);
        scrollView = (ScrollView)view.findViewById(R.id.sv_f_person);
        scrollView.setVerticalScrollBarEnabled(false);

        rl_card_type.setOnClickListener(this);
        rl_teacher.setOnClickListener(this);
        rl_classroom.setOnClickListener(this);
        rl_logout.setOnClickListener(this);
        rl_help.setOnClickListener(this);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_person,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
}
