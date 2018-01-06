package com.example.dengweixiong.Util.Enum;

import com.example.dengweixiong.Util.Ref;

/**
 * Created by dengweixiong on 2018/1/6.
 */

public enum EnumRespType {

    RESP_STAT(4),RESP_DATA(6),RESP_ERROR(5),RESP_MAPLIST(1),RESP_MAP(2),RESP_LIST(3);

    private int type;

    EnumRespType(int i) {
        this.type = i;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static EnumRespType dealWithResponse(String resp) {

        if (resp.startsWith("[")) {
            return RESP_MAPLIST;
        }else if (resp.startsWith("{")) {
            if (resp.contains(Ref.STATUS)) {
                return RESP_STAT;
            }else if (resp.contains(Ref.DATA)){
                return RESP_DATA;
            } else {
                return RESP_STAT;
            }
        }else {
            return RESP_ERROR;
        }
    }
}
