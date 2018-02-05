package xyz.institutionmanage.sailfish.Bean;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by dengweixiong on 2017/9/16.
 */

public class Course {

    private long id;
    private String name;
    private int type;
    private boolean del;
    private int last_time;
    private int max_book_num;
    private ArrayList<Card> supportedCard;
    private ArrayList<Card> price;
    private String summary;
    private Date create_time;
    private ShopMember creator;
    private Date last_modify_time;
    private ShopMember last_modify_user;
    private Shop owner;
    private ArrayList<CoursePlan> coursePlan;
    //私教教师
    private ShopMember teacher;
    //私教学生
    private Member student;
    //私教课程上课次数
    private int total_times;
    //私教课程失效时间
    private Date valid_time;
    //私教收费
    private float actual_cost;
    //私教已用次数
    private int used_time;

    public Course() {
    }

    //普通课程
    public Course(long id,String name,int type,boolean del, int last_time,int max_book_num,
                  ArrayList<Card> supportedCard,ArrayList<Card> price,String summary,Date create_time,ShopMember creator,
                  Date last_modify_time,ShopMember last_modify_user,Shop owner,ArrayList<CoursePlan> coursePlan) {
        this.id = id;this.name = name;this.type = type;this.del = del;this.last_time = last_time;this.max_book_num = max_book_num;
        this.supportedCard = supportedCard;this.price = price;this.summary = summary;this.create_time = create_time;
        this.creator = creator;this.last_modify_time = last_modify_time;this.last_modify_user = last_modify_user;this.owner = owner;
        this.coursePlan = coursePlan;
    }
    //私教课程
    public Course(long id,String name,int type,boolean del, Date create_time,ShopMember creator,
                  Date last_modify_time,ShopMember last_modify_user,Shop owner,ShopMember teacher,Member student,
                  int total_times,Date valid_time,float actual_cost,int used_time,ArrayList<CoursePlan> coursePlan) {
        this.id = id;this.name = name;this.type = type;this.del = del;this.create_time = create_time;this.creator = creator;
        this.last_modify_time = last_modify_time;this.last_modify_user = last_modify_user;this.owner = owner;this.teacher = teacher;
        this.student = student;this.total_times = total_times;this.valid_time = valid_time;this.actual_cost = actual_cost;this.used_time = used_time;
        this.coursePlan = coursePlan;
    }

    //课程列表
    public Course(long id,String name,int last_time,ArrayList<Card> supportedCard) {
        this.id = id;this.name= name;this.last_time = last_time;this.supportedCard = supportedCard;
    }
    //普通课程
    public Course(long id,String name,int last_time,int max_book_num,
                  ArrayList<Card> supportedCard,ArrayList<Card> price,String summary,
                  boolean del,ArrayList<CoursePlan> coursePlan) {
        this.id = id;this.name = name;this.last_time = last_time;this.max_book_num = max_book_num;this.supportedCard = supportedCard;
        this.price = price;this.summary = summary;this.del = del;this.coursePlan = coursePlan;
    }
    //私教课程
    public Course(long id,String name,int type,boolean del, Shop owner,ShopMember teacher,Member student,
                  int total_times,Date valid_time,float actual_cost,int used_time,ArrayList<CoursePlan> coursePlan) {
        this.id = id;this.name = name;this.type = type;this.del = del;this.owner = owner;this.teacher = teacher;
        this.student = student;this.total_times = total_times;this.valid_time = valid_time;this.actual_cost = actual_cost;this.used_time = used_time;
        this.coursePlan = coursePlan;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public int getLast_time() {
        return last_time;
    }

    public void setLast_time(int last_time) {
        this.last_time = last_time;
    }

    public int getMax_book_num() {
        return max_book_num;
    }

    public void setMax_book_num(int max_book_num) {
        this.max_book_num = max_book_num;
    }

    public ArrayList<Card> getSupportedCard() {
        return supportedCard;
    }

    public void setSupportedCard(ArrayList<Card> supportedCard) {
        this.supportedCard = supportedCard;
    }

    public ArrayList<Card> getPrice() {
        return price;
    }

    public void setPrice(ArrayList<Card> price) {
        this.price = price;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public ShopMember getCreator() {
        return creator;
    }

    public void setCreator(ShopMember creator) {
        this.creator = creator;
    }

    public Date getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(Date last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public ShopMember getLast_modify_user() {
        return last_modify_user;
    }

    public void setLast_modify_user(ShopMember last_modify_user) {
        this.last_modify_user = last_modify_user;
    }

    public Shop getOwner() {
        return owner;
    }

    public void setOwner(Shop owner) {
        this.owner = owner;
    }

    public ShopMember getTeacher() {
        return teacher;
    }

    public void setTeacher(ShopMember teacher) {
        this.teacher = teacher;
    }

    public Member getStudent() {
        return student;
    }

    public void setStudent(Member student) {
        this.student = student;
    }

    public int getTotal_times() {
        return total_times;
    }

    public void setTotal_times(int total_times) {
        this.total_times = total_times;
    }

    public Date getValid_time() {
        return valid_time;
    }

    public void setValid_time(Date valid_time) {
        this.valid_time = valid_time;
    }

    public float getActual_cost() {
        return actual_cost;
    }

    public void setActual_cost(float actual_cost) {
        this.actual_cost = actual_cost;
    }

    public int getUsed_time() {
        return used_time;
    }

    public void setUsed_time(int used_time) {
        this.used_time = used_time;
    }

    public ArrayList<CoursePlan> getCoursePlan() {
        return coursePlan;
    }

    public void setCoursePlan(ArrayList<CoursePlan> coursePlan) {
        this.coursePlan = coursePlan;
    }
}
