package xyz.institutionmanage.sailfish.Util.Enum;

/**
 * Created by dengweixiong on 2018/1/6.
 */

public enum EnumResultCodeType {

    RESULTCODE_UPDATE(1),RESULTCODE_DELETE(2),RESULTCODE_ADD(3),RESULTCODE_NULL(0);

    private int type;

    EnumResultCodeType(int i) {
        this.type = i;
    }
}
