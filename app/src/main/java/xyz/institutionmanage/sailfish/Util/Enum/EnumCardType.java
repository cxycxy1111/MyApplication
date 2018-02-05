package xyz.institutionmanage.sailfish.Util.Enum;

/**
 * Created by dengweixiong on 2018/1/6.
 */

public enum EnumCardType {

    BALANCE(1),TIME(2),TIMES(3);

    private int type;

    EnumCardType(int i) {
        this.type = i;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
