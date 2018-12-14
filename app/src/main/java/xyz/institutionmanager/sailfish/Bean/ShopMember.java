package xyz.institutionmanager.sailfish.Bean;

/**
 * Created by dengweixiong on 2017/9/15.
 */

public class ShopMember {

    private long id;
    private String user_name;
    private String password;
    private int type;
    private Shop shop;

    public ShopMember(long id,String user_name,String password,int type) {
        this.id = id;
        this.user_name = user_name;
        this.password = password;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
