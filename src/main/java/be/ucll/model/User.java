package be.ucll.model;

public class User {

    private String name;
    private String password;
    private String email;
    private int age;

    public User(String name, int age, String email, String password) {
        setName(name);
        setPassword(password);
        setEmail(email);
        setAge(age);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name is required.");
        }
        this.name = name;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if(age > 0 && age < 101){this.age = age;}
        else{throw new RuntimeException("Invalid age");}
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(email.contains("@")&& email.contains(".")){
            this.email = email;
        }
        else {
            throw new RuntimeException("The email is invalid");
        }

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(password.length() < 8){
            throw new RuntimeException("Password must be at least 8 characters long.");
        }
        else {
            this.password = password;
        }

    }

    public void updateUser(String name, int age, String email, String password) {
        if(!email.equals(this.email)){
            throw new RuntimeException("Email cannot be changed.");
        }
        this.name =name;
        this.age = age;
        this.password = password;
    }
}