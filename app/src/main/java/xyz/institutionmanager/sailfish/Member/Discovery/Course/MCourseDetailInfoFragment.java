package xyz.institutionmanager.sailfish.Member.Discovery.Course;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alfred.alfredtools.*;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MCourseDetailInfoFragment extends Fragment implements HttpResultListener {

    private static final String ARG_PARAM1 = "c_id";
    private ArrayList<Map<String,String>> mapArrayList = new ArrayList<>();
    private TextView tv_type,tv_course_name,tv_last_time,tv_max_book_num;
    private long c_id;

    private OnFragmentInteractionListener mListener;

    public MCourseDetailInfoFragment() {
    }

    public static MCourseDetailInfoFragment newInstance(long param1) {
        MCourseDetailInfoFragment fragment = new MCourseDetailInfoFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            c_id = getArguments().getLong(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mcourse_detail_info, container, false);
        tv_type = (TextView)view.findViewById(R.id.tv_hint_type_a_course_detial);
        tv_course_name = (TextView)view.findViewById(R.id.tv_hint_course_name_a_course_detial);
        tv_max_book_num = (TextView)view.findViewById(R.id.tv_hint_total_book_a_course_detial);
        tv_last_time = (TextView)view.findViewById(R.id.tv_last_time_a_course_detial);
        String url = "/mCourseDetailQuery?c_id="+c_id;
        HttpCallback callback = new HttpCallback(this,getActivity(),0);
        NetUtil.reqSendGet(getActivity(),url,callback);
        return view;
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

    @Override
    public void onRespStatus(String body, int source) {
        switch (NetRespStatType.dealWithRespStat(body)) {
            case EMPTY:
                ViewHandler.toastShow(getActivity(),"不存在该课程");
                break;
            default:break;
        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        final String[] keys = new String[] {"id","type","name","last_time","max_book_num"};
        mapArrayList = JsonUtil.strToListMap(body,keys);
        final Map<String,String> map = mapArrayList.get(0);
        if (String.valueOf(map.get("type")).equals("1")) {
            tv_type.setText("会员课");
        }else if (String.valueOf(map.get("type")).equals("2")) {
            tv_type.setText("教练班");
        }else {
            tv_type.setText("集训课");
        }
        tv_course_name.setText(map.get(keys[2]));
        tv_last_time.setText(String.valueOf(map.get(keys[3]))+"分钟");
        tv_max_book_num.setText(String.valueOf(map.get(keys[4]))+"人");
    }

    @Override
    public void onRespError(int source) {
        ViewHandler.toastShow(getActivity(),Ref.UNKNOWN_ERROR);
    }

    @Override
    public void onReqFailure(Object object, int source) {
        ViewHandler.toastShow(getActivity(), Ref.CANT_CONNECT_INTERNET);
    }

    @Override
    public void onRespSessionExpired(int source) {
        ViewHandler.alertShowAndExitApp(getActivity());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
