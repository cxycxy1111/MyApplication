package com.example.dengweixiong.Util.Enum;

import com.example.dengweixiong.Util.JsonHandler;
import com.example.dengweixiong.Util.Ref;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dengweixiong on 2018/1/6.
 */

public enum EnumRespStatType {

    NSR("no_such_record"),
    EXE_FAIL("exe_fail"),
    EXE_SUC("exe_suc"),
    DUPLICATE("duplicate"),
    NST_NOT_MATCH("institution_not_match"),
    EMPTY_RESULT("empty_result"),
    PARTYLY_FAIL("exe_partly_fail"),
    NOT_MATCH("not_match"),
    SESSION_EXPIRED("session_expired");

    private String type;

    EnumRespStatType(String string) {
        this.type = string;
    }

    public static EnumRespStatType dealWithRespStat(String resp) {
        Map<String,String> map = new HashMap<>();
        try {
            map = JsonHandler.strToMap(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String value = map.get(Ref.STATUS);
        if (value.equals(Ref.STAT_EXE_SUC)) {//执行成功
            return EnumRespStatType.EXE_SUC;
        } else if (value.equals(Ref.STAT_EXE_FAIL)) {//执行失败
            return EnumRespStatType.EXE_FAIL;
        } else if (value.equals(Ref.STAT_PARTYLY_FAIL)) {//执行部分失败
            return EnumRespStatType.PARTYLY_FAIL;
        } else if (value.equals(Ref.STAT_DUPLICATE)) {//重复
            return EnumRespStatType.DUPLICATE;
        } else if (value.equals(Ref.STAT_NSR)) {//没有此类记录
            return EnumRespStatType.NSR;
        } else if (value.equals(Ref.STAT_EMPTY_RESULT)) {
            return EnumRespStatType.EMPTY_RESULT;
        } else if (value.equals(Ref.STAT_INST_NOT_MATCH)) {
            return EnumRespStatType.NST_NOT_MATCH;
        } else if (value.equals(Ref.NOT_MATCH)){
            return EnumRespStatType.NOT_MATCH;
        } else if (value.equals(Ref.SESSION_EXPIRED)){
            return EnumRespStatType.SESSION_EXPIRED;
        } else {
            return null;
        }
    }

}
