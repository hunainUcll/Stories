package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected User(){}


    @NotBlank(message = "Name is required.")
    // so when i use min and max the violation size becomes greater and greater ex - for 3 input validation expected violation size was 1 but i got 5
    // answer - that happens because apparently i wasnt supposed touse the above annotations on a string instead use @Length
    @Length(min = 4,max = 25,message = "Name must be in between 4 and 25 characters long")
    private String name; 

    @PositiveOrZero(message = "age cannot be under 0")
    @Max(value = 101,message = "age cannot be over 101")
    private int age;

    @Email(message = "the email does not contain @ symbol." )
    private String email;

    @Length(min = 8 ,message = "Password must be at least 8 characters long.")
    @Length(max = 25,message = "Password must not be more than 25 characters long.")
    private String password;

    public User(String name, int age, String email, String password) {
        setName(name);
        setPassword(password);
        setEmail(email);
        setAge(age);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name;}

    public int getAge() {
        return age;
    }
    public void setAge(int age) {this.age = age;}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if(email.contains(".")){this.email = email;}
        else{throw new RuntimeException("Email must contain . symbol.");}
        }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {this.password = password;}

    public void updateUser(String name, int age, String email, String password) {
        if(!email.equals(this.email)){throw new RuntimeException("Email cannot be changed.");}
        this.name =name;
        this.age = age;
        this.password = password;
    }
}