package use;

public class Account {
    private String username;
    private String password;
    private String type;

    public Account(String name, String pass, String type){
        this.username = name;
        this.password = pass;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}