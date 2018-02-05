package xyz.institutionmanage.sailfish.Util.Enum;

/**
 * Created by dengweixiong on 2018/1/6.
 */

public enum EnumCourseType {

    MEMBER_COURSE(1),TRAINEE_COURSE(2),COLLETCT_COURSE(3),PRIVATE_COURSE(4);

    private int anInt;

    EnumCourseType(int i) {
        this.anInt = i;
    }


}
