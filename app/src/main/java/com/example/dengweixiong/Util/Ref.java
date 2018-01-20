package com.example.dengweixiong.Util;

/**
 * Created by dengweixiong on 2017/10/14.
 */

public abstract class Ref {

    public static final String CANT_CONNECT_INTERNET = "无法连接服务器";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String NULL_POINTER_ERROR = "空指针";

    public static final String STATUS = "stat";
    public static final String ID = "id";
    public static final String DATA = "data";

    public static final String OP_ADD_SUCCESS = "新增成功";
    public static final String OP_ADD_FAIL = "新增失败";
    public static final String OP_DELETE_SUCCESS = "删除成功";
    public static final String OP_DELETE_FAIL = "删除失败";
    public static final String OP_MODIFY_SUCCESS = "修改成功";
    public static final String OP_MODIFY_FAIL = "修改失败";
    public static final String OP_SUCCESS = "操作成功";
    public static final String OP_FAIL = "操作失败";
    public static final String OP_PARTLY_FAIL = "部分操作失败";
    public static final String OP_INST_NOT_MATCH = "机构匹配失败";
    public static final String OP_WRONG_NUMBER_FORMAT = "数字格式错误";
    public static final String OP_EMPTY_ESSENTIAL_INFO = "必要信息不能为空";
    public static final String OP_NSR = "不存在此记录";
    public static final String OP_DUPLICATED = "重复操作";

    /**
     * 结果码
     */
    public static final int RESULTCODE_UPDATE = 1;
    public static final int RESULTCODE_DELETE = 2;
    public static final int RESULTCODE_ADD = 3;
    public static final int RESULTCODE_NULL = 0;

    public static final int REQCODE_QUERYDETAIL = 1;
    public static final int REQCODE_ADD = 2;

    /**
     * 从服务器中返回的stat的值类型
     */
    public static final String STAT_NSR = "no_such_record";
    public static final String STAT_EXE_FAIL = "exe_fail";
    public static final String STAT_EXE_SUC = "exe_suc";
    public static final String STAT_DUPLICATE = "duplicate";
    public static final String STAT_INST_NOT_MATCH = "institution_not_match";
    public static final String STAT_EMPTY_RESULT = "empty_result";
    public static final String STAT_PARTYLY_FAIL = "exe_partly_fail";
    public static final String NOT_MATCH = "not_match";

    /**
     * 操作类型
     * 1：新增操作，2：删除操作；3：保存操作
     */
    public static final int ACTION_ADD = 1;
    public static final int ACTION_DELETE = 2;
    public static final int ACTION_SAVE = 3;

    /**
     * 教师类型
     * 1管理者，2普通教师，3外聘
     */
    public static final String TEACHER_ADMIN = "1";
    public static final String TEACHER_INNER = "2";
    public static final String TEACHER_OUTER = "3";

    public static final int RESP_TYPE_MAPLIST = 1;
    public static final int RESP_TYPE_MAP = 2;
    public static final int RESP_TYPE_LIST = 3;
    public static final int RESP_TYPE_STAT = 4;
    public static final int RESP_TYPE_ERROR = 5;
    public static final int RESP_TYPE_DATA = 6;

    public static final int DATA_TYPE_INT = 1;
    public static final int DATA_TYPE_LONG = 2;
    public static final int DATA_TYPE_STRING = 3;

    public static final int COURSE_TYPE_MEMBER = 1;
    public static final int COURSE_TYPE_TEACHER = 2;
    public static final int COURSE_TYPE_COLLECT = 3;
    public static final int COURSE_TYPE_PERSON = 4;


}
