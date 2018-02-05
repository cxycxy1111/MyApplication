package xyz.institutionmanage.sailfish.Bean;

/**
 * Created by dengweixiong on 2017/9/16.
 */

public class Classroom {

    private long id;
    private Shop owner;
    private String name;

    public Classroom() {

    }

    public Classroom(long id,Shop owner,String name) {
        this.id = id;this.owner = owner;this.name = name;
    }

    public Classroom(long id,String name) {
        this.id = id;this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Shop getOwner() {
        return owner;
    }

    public void setOwner(Shop owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
