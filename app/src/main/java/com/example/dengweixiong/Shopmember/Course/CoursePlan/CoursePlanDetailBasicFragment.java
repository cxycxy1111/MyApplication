package com.example.dengweixiong.Shopmember.Course.CoursePlan;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dengweixiong.Util.Enum.EnumRespStatType;
import com.example.dengweixiong.Util.Enum.EnumRespType;
import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.MethodTool;
import com.example.dengweixiong.Util.NetUtil;
import com.example.dengweixiong.Util.Ref;
import com.example.dengweixiong.myapplication.R;

import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class CoursePlanDetailBasicFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private String selected_classroom,sm_id,s_id,cp_id,course_name,str_start_time;
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
            s_id = getArguments().getString("s_id");
            sm_id = getArguments().getString("sm_id");
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
        okhttp3.Callback callback = new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(), Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                EnumRespType respType = EnumRespType.dealWithResponse(resp);
                switch (respType) {
                    case RESP_STAT:
                        EnumRespStatType respStatType = EnumRespStatType.dealWithRespStat(resp);
                        switch (respStatType) {
                            case NSR:
                                MethodTool.showToast(getActivity(),Ref.OP_NSR);
                                getActivity().finish();
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getActivity());break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(getActivity());
                                break;
                            default:break;
                        }
                        break;
                    case RESP_MAPLIST:
                        mapList_detail = JsonHandler.strToListMap(resp,strs_detail_keys);
                        final List<Map<String, String>> final_list_map_detail = mapList_detail;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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

                                String str_classroom_name = map.get("classroom_name");

                                et_course_name.setText(map.get("course_name"));
                                et_start_date.setText(str_start_time.split(" ")[0]);
                                et_start_date.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dpd_date.show();
                                    }
                                });
                                et_start_time.setText(str_start_time.split(" ")[1]);
                                et_start_time.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        tpd_time.show();
                                    }
                                });
                                et_last_time.setText(String.valueOf(map.get("last_time")));
                                et_remark.setText(map.get("remark"));
                                initClassroomSpinner(str_classroom_name);
                            }
                        });
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
    }

    //初始化课室
    private void initClassroomSpinner(final String str_classroom_name) {
        String url = "/ClassroomListQuery";
        okhttp3.Callback callback = new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MethodTool.showToast(getActivity(),Ref.CANT_CONNECT_INTERNET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                switch (EnumRespType.dealWithResponse(resp)) {
                    case RESP_MAPLIST:
                        mapList_classroom = JsonHandler.strToListMap(resp,str_classroom_keys);
                        for (int i = 0;i < mapList_classroom.size();i++) {
                            list_classroom.add(mapList_classroom.get(i).get("name"));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,list_classroom);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                current_position = list_classroom.indexOf(str_classroom_name);

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
                            }
                        });
                        break;
                    case RESP_STAT:
                        switch (EnumRespStatType.dealWithRespStat(resp)) {
                            case NSR:
                                Map<String,String> map = new HashMap<>();
                                map = JsonHandler.strToMap(resp);
                                if (map.get(Ref.STATUS).equals(Ref.STAT_NSR)) {
                                    MethodTool.showToast(getActivity(),Ref.STAT_NSR);
                                }
                                break;
                            case SESSION_EXPIRED:
                                MethodTool.showExitAppAlert(getActivity());
                                break;
                            case AUTHORIZE_FAIL:
                                MethodTool.exitAcitivityDueToAuthorizeFail(getActivity());
                                break;
                            default:break;
                        }
                        break;
                    default:break;
                }
            }
        };
        NetUtil.sendHttpRequest(getActivity(),url,callback);
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
            okhttp3.Callback callback = new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    MethodTool.showToast(getActivity(),Ref.CANT_CONNECT_INTERNET);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    switch (EnumRespType.dealWithResponse(resp)) {
                        case RESP_STAT:
                            switch (EnumRespStatType.dealWithRespStat(resp)) {
                                case EXE_SUC:
                                    MethodTool.showToast(getActivity(),Ref.OP_MODIFY_SUCCESS);
                                    break;
                                case EXE_FAIL:
                                    MethodTool.showToast(getActivity(),Ref.OP_FAIL);
                                    break;
                                case SESSION_EXPIRED:
                                    MethodTool.showExitAppAlert(getActivity());
                                    break;
                                case AUTHORIZE_FAIL:
                                    MethodTool.showAuthorizeFailToast(getActivity());
                                    break;
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
            NetUtil.sendHttpRequest(getActivity(),url,callback);
        }
    }
}
