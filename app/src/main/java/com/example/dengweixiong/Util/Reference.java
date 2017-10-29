package com.example.dengweixiong.Util;

/**
 * Created by dengweixiong on 2017/10/14.
 */

public abstract class Reference {

    public static final String CANT_CONNECT_INTERNET = "无法连接服务器";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String NULL_POINTER_ERROR = "空指针";
    public static final String STATUS = "stat";
    public static final String ID = "id";
    public static final String DATA = "data";

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
    public static final String NSR = "no_such_record";
    public static final String EXE_FAIL = "exe_fail";
    public static final String EXE_SUC = "exe_suc";
    public static final String DUPLICATE = "duplicate";
    public static final String INST_NOT_MATCH = "institution_not_match";

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

}
