package xyz.institutionmanager.sailfish.Bean;

import java.sql.Date;

/**
 * Created by dengweixiong on 2017/9/15.
 */

public class MemberCard {

    private long id;
    private Member owner;
    private Card type;
    private boolean del;
    private int balance;
    private Date start_time;
    private Date valid_time;
    private ShopMember creator;
    private Date create_time;
    private ShopMember last_modify_user;
    private Date last_modify_time;

    public MemberCard() {
    }

    public MemberCard(long id,Card type) {
        this.id = id;this.type = type;
    }

    public MemberCard(long id,Member member,Card type,boolean del,int balance,Date start_time,Date valid_time,
                      ShopMember creator,Date create_time,ShopMember last_modify_user,Date last_modify_time) {
        this.id = id;this.owner = member;this.type = type;this.del = del;this.balance = balance;this.start_time = start_time;
        this.valid_time = valid_time;this.creator = creator;this.create_time = create_time;this.last_modify_user = last_modify_user;
        this.last_modify_time = last_modify_time;
    }
    //会员卡详情
    public MemberCard(Member owner,Card type,int balance,Date start_time,Date valid_time) {
        this.owner = owner;this.type = type;this.balance = balance;
        this.start_time = start_time;this.valid_time = valid_time;
    }

}
