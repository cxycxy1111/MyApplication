package xyz.institutionmanager.sailfish.Bean;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by dengweixiong on 2017/9/16.
 */

public class CoursePlan {

    private long id;
    private Course course;
    private boolean del;
    private ArrayList<ShopMember> teachers;
    private ArrayList<Member> origin_students;
    private ArrayList<Member> attendance_students;
    private Classroom classroom;
    private Date start_time;
    private String remark;
    private Date create_time;
    private ShopMember creator;
    private Date last_modify_time;
    private ShopMember last_modify_user;

    public CoursePlan() {

    }

    public CoursePlan(long id,Course course,boolean del,ArrayList<ShopMember> teachers,
                      ArrayList<Member> origin_students,ArrayList<Member>attendance_students,Classroom classroom,
                      Date start_time,String remark,Date create_time,ShopMember creator,Date last_modify_time,ShopMember last_modify_user) {
        this.id = id;this.course = course;this.del = del;this.teachers = teachers;
        this.origin_students = origin_students;this.attendance_students = attendance_students;this.classroom = classroom;
        this.start_time = start_time;this.remark = remark;this.create_time = create_time;this.creator = creator;this.last_modify_time = last_modify_time;
        this.last_modify_user = last_modify_user;
    }

    public CoursePlan(long id,Course course,ArrayList<ShopMember> teachers,
                      Classroom classroom, Date start_time,String remark) {
        this.id = id;this.course = course;this.teachers = teachers;this.classroom = classroom;
        this.start_time = start_time;this.remark = remark;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public ArrayList<ShopMember> getTeachers() {
        return teachers;
    }

    public void setTeachers(ArrayList<ShopMember> teachers) {
        this.teachers = teachers;
    }

    public ArrayList<Member> getOrigin_students() {
        return origin_students;
    }

    public void setOrigin_students(ArrayList<Member> origin_students) {
        this.origin_students = origin_students;
    }

    public ArrayList<Member> getAttendance_students() {
        return attendance_students;
    }

    public void setAttendance_students(ArrayList<Member> attendance_students) {
        this.attendance_students = attendance_students;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}
