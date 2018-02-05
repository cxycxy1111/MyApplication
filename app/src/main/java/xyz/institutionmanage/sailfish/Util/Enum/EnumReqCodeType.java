package xyz.institutionmanage.sailfish.Util.Enum;

/**
 * Created by dengweixiong on 2018/1/6.
 */

public enum EnumReqCodeType {

    REQCODE_QUERYDETAIL(1),REQCODE_ADD(2);

    private int type;

    EnumReqCodeType(int i) {
        this.type = i;
    }
}
