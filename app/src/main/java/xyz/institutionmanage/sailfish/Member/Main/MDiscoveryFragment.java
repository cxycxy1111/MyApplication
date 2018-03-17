package xyz.institutionmanage.sailfish.Member.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import xyz.institutionmanage.sailfish.Member.Discovery.Course.MCourseListActivity;
import xyz.institutionmanage.sailfish.Member.Discovery.CoursePlan.MCoursePlanListActivity;
import xyz.institutionmanage.sailfish.R;

public class MDiscoveryFragment
        extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private RelativeLayout ll_all_courses,ll_all_courseplan;
    private OnFragmentInteractionListener mListener;

    public MDiscoveryFragment() {
    }

    public static MDiscoveryFragment newInstance(String param1) {
        MDiscoveryFragment fragment = new MDiscoveryFragment();
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
        View view = inflater.inflate(R.layout.activity_m_discovery_f, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        ll_all_courses = (RelativeLayout) view.findViewById(R.id.rl_all_course_f_m_discovery);
        ll_all_courseplan = (RelativeLayout) view.findViewById(R.id.rl_all_courseplan_f_m_discovery);
        ll_all_courses.setOnClickListener(this);
        ll_all_courseplan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_all_course_f_m_discovery:
                Intent intent = new Intent(getActivity(), MCourseListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_all_courseplan_f_m_discovery:
                Intent intent_2 = new Intent(getActivity(), MCoursePlanListActivity.class);
                startActivity(intent_2);
                break;
            default:break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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
}
