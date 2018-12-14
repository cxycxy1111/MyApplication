package xyz.institutionmanager.sailfish.Shopmember.Course.CoursePlan;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.alfred.alfredtools.*;
import org.apache.commons.lang.math.NumberUtils;
import xyz.institutionmanager.sailfish.R;
import xyz.institutionmanager.sailfish.Util.Ref;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CoursePlanDetailBasicFragment extends Fragment implements View.OnClickListener,HttpResultListener {

    private static final int REQUEST_CoursePlanDetailQuery = 1;
    private static final int REQUEST_ClassroomListQuery = 2;
    private static final int REQUEST_CoursePlanModify = 3;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private String selected_classroom,cp_id,course_name,str_start_time,str_current_classroom_name;
    private int selected_year,selected_month,selected_date,selected_hour,selected_minute,current_position;
    private String[] strs_detail_keys = new String[] {"course_name","classroom_name","start_time","last_time","remark"};
    private String[] str_classroom_keys = new String[] {"id","name"};
    private List<Map<String,String>> mapList_classroom = new ArrayList<>();
    private List<String> list_classroom = new ArrayList<>();
    private List<Map<String,String>> mapList_detail = new ArrayList<>();
    private EditText et_course_name,et_start_date,et_start_time,et_last_time,et_remark;
    private Spinner sp_classroom;
    private TimePickerDialog tpd_time;
    private DatePickerDialog dpd_date;
    private Button btn_save;
    private View view;
    private OnFragmentInteractionListener mListener;

    public CoursePlanDetailBasicFragment() {

    }

    public static CoursePlanDetailBasicFragment newInstance(Map<String,String> map) {
        CoursePlanDetailBasicFragment fragment = new CoursePlanDetailBasicFragment();
        Bundle bundle = new Bundle();
        for (Map.Entry<String,String> entry:map.entrySet()) {
            bundle.putString(entry.getKey(),entry.getValue());
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cp_id = getArguments().getString("cp_id");
            course_name = getArguments().getString("course_name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_course_plan_detail_basic_f, container, false);
        btn_save = (Button)view.findViewById(R.id.btn_save_f_course_plan_detail_basic);
        btn_save.setOnClickListener(this);
        try {
            initViews(view);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_f_course_plan_detail_basic:
                saveCoursePlanModify();
                break;
            default:break;
        }
    }

    @Override
    public void onRespStatus(String body, int source) {
        if (source == REQUEST_CoursePlanDetailQuery) {
            NetRespStatType respStatType = NetRespStatType.dealWithRespStat(body);
            switch (respStatType) {
                case NSR:
                    ViewHandler.toastShow(getActivity(),Ref.OP_NSR);
                    getActivity().finish();
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(getActivity());
                    break;
                default:break;
            }
        }else if (source == REQUEST_ClassroomListQuery) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case NSR:
                    Map<String,String> map = new HashMap<>();
                    try {
                        map = JsonUtil.strToMap(body);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (map.get(Ref.STATUS).equals(Ref.STAT_NSR)) {
                        ViewHandler.toastShow(getActivity(),Ref.STAT_NSR);
                    }
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(getActivity());
                    break;
                default:break;
            }
        }else if (source == REQUEST_CoursePlanModify) {
            switch (NetRespStatType.dealWithRespStat(body)) {
                case SUCCESS:
                    ViewHandler.toastShow(getActivity(),Ref.OP_MODIFY_SUCCESS);
                    break;
                case FAIL:
                    ViewHandler.toastShow(getActivity(),Ref.OP_FAIL);
                    break;
                case STATUS_AUTHORIZE_FAIL:
                    ViewHandler.exitAcitivityDueToAuthorizeFail(getActivity());
                    break;
                default:break;
            }
        }else {

        }
    }

    @Override
    public void onRespMapList(String body, int source) throws IOException {
        if (source == REQUEST_CoursePlanDetailQuery) {
            mapList_detail = JsonUtil.strToListMap(body,strs_detail_keys);
            final List<Map<String, String>> final_list_map_detail = mapList_detail;
            Map<String,String> map = final_list_map_detail.get(0);
            //初始化时间
            str_start_time = map.get("start_time");
            try {
                initDatePickerDialog();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                initTimePickerDialog();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(str_start_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            selected_year = calendar.get(Calendar.YEAR);
            selected_month = calendar.get(Calendar.MONTH + 1);
            selected_date = calendar.get(Calendar.DAY_OF_MONTH);
            selected_hour = calendar.get(Calendar.HOUR_OF_DAY);
            selected_minute = calendar.get(Calendar.MINUTE);

            str_current_classroom_name = map.get("classroom_name");

            et_course_name.setText(map.get("course_name"));
            et_start_date.setText(str_start_time.split(" ")[0]);
            et_start_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dpd_date.show();
                }
            });

            String start_time = str_start_time.split(" ")[1];
            start_time = start_time.substring(0,start_time.length()-5);
            et_start_time.setText(start_time);
            et_start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tpd_time.show();
                }
            });
            et_last_time.setText(String.valueOf(map.get("last_time")));
            et_remark.setText(map.get("remark"));
            initClassroomSpinner();
        }else if (source == REQUEST_ClassroomListQuery) {
            mapList_classroom = JsonUtil.strToListMap(body,str_classroom_keys);
            for (int i = 0;i < mapList_classroom.size();i++) {
                list_classroom.add(mapList_classroom.get(i).get("name"));
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,list_classroom);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            current_position = list_classroom.indexOf(str_current_classroom_name);

            sp_classroom.setAdapter(adapter);
            sp_classroom.setSelection(current_position);
            sp_classroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selected_classroom = String.valueOf(mapList_classroom.get(position).get("id"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    for (int i = 0;i < mapList_classroom.size();i++) {
                        Map<String,String> map = mapList_classroom.get(i);
                        if (map.get("name").equals(mapList_detail.get(0).get("classroom_name")));
                        selected_classroom = map.get("id");
                    }
                }
            });
        }else if (source == REQUEST_CoursePlanModify) {

        }else {

        }
    }

    @Override
    public void onRespError(int source) {

    }

    @Override
    public void onReqFailure(Object object, int source) {

    }

    @Override
    public void onRespSessionExpired(int source) {
    }

    private void initViews(View view) throws ParseException {
        et_course_name = (EditText)view.findViewById(R.id.et_name_f_course_plan_detail_basic);
        et_start_date = (EditText)view.findViewById(R.id.et_start_date_f_course_plan_detail_basic);
        et_start_date.setInputType(InputType.TYPE_NULL);
        et_start_time = (EditText)view.findViewById(R.id.et_start_time_f_course_plan_detail_basic);
        et_start_time.setInputType(InputType.TYPE_NULL);
        et_last_time = (EditText)view.findViewById(R.id.et_last_time_f_course_plan_detail_basic);
        sp_classroom = (Spinner) view.findViewById(R.id.sp_classroom_f_course_plan_detail_basic);
        et_remark = (EditText)view.findViewById(R.id.et_remark_f_course_plan_detail_basic);
        initCoursePlanDetail();
    }

    private void initDatePickerDialog() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(str_start_time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selected_year = year;
                selected_month = month+1;
                selected_date = dayOfMonth;
                et_start_date.setText(selected_year + "-" + selected_month+1 + "-" + selected_date);
            }
        };
        dpd_date = new DatePickerDialog(getActivity(),listener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void initTimePickerDialog() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(str_start_time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selected_hour = hourOfDay;
                selected_minute = minute;
                et_start_time.setText(selected_hour + ":" + selected_minute + ":00");
            }
        };
        tpd_time = new TimePickerDialog(getActivity(),listener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
    }

    //初始化排课信息
    private void initCoursePlanDetail() {
        String url = "/CoursePlanDetailQuery?cp_id=" + cp_id;
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_CoursePlanDetailQuery);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    //初始化课室
    private void initClassroomSpinner() {
        String url = "/ClassroomListQuery";
        HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_ClassroomListQuery);
        NetUtil.reqSendGet(getActivity(),url,callback);
    }

    @SuppressLint("ShowToast")
    private void saveCoursePlanModify() {

        String new_remark = et_remark.getText().toString();

        if (!NumberUtils.isNumber(et_last_time.getText().toString())) {
            Toast.makeText(getActivity(),Ref.OP_WRONG_NUMBER_FORMAT,Toast.LENGTH_SHORT).show();
        }else {
            //取得持续时间
            int new_last_time = Integer.parseInt(et_last_time.getText().toString());

            String new_temp_start_time = selected_year + "-" +
                    selected_month + "-" +
                    selected_date + " " +
                    selected_hour + ":" +
                    selected_minute + ":00";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = simpleDateFormat.parse(new_temp_start_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String new_start_time = simpleDateFormat.format(date);
            //取得end_time
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE,new_last_time);
            Date date_end_time = calendar.getTime();
            String new_end_time = simpleDateFormat.format(date_end_time);

            String url = "/CoursePlanModify?id=" + cp_id +
                    "&cr_id=" + selected_classroom +
                    "&s_time=" + new_start_time +
                    "&e_time=" + new_end_time +
                    "&remark=" + new_remark;
            HttpCallback callback = new HttpCallback(this,getActivity(),REQUEST_CoursePlanModify);
            NetUtil.reqSendGet(getActivity(),url,callback);
        }
    }
}
