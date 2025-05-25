package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        if (this.age < 18){
            throw new RuntimeException("User must be at least 18 year old to have a profile.");
        }
        this.profile = profile;
    }

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Membership> memberships = new ArrayList<>();

    public User(String name, int age, String email, String password) {
        setName(name);
        setPassword(password);
        setEmail(email);
        setAge(age);
    }
    // making second constructor for testing purposes
    public User(String name, int age, String email, String password,Profile profile) {
        setName(name);
        setPassword(password);
        setEmail(email);
        setAge(age);
        setProfile(profile);
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

    // i still donot understand what i did here but it feels right ???
    // also i accidentally used Date instead of localdate and  im getting erros if i change it so if it is not broken don't fix it
    // i had to fix it turns out bean validation doesnt work on Date only on LocalDate , bean is pissing me off
    private boolean dateOverlapCheck(LocalDate start1,LocalDate start2,LocalDate end1,LocalDate end2){
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    public void addMembership(Membership membership){
        for(Membership M : memberships){
            if(dateOverlapCheck(M.getStartDate(),membership.getStartDate(), M.getEndDate(),membership.getEndDate())){
                throw new RuntimeException("User has already a membership on that date.");
            }
        }
        membership.setUser(this);
        this.memberships.add(membership);

    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public Membership getActiveMembershipOnDate(LocalDate returnDate) {
        for (Membership membership : memberships){
            if(returnDate.isAfter(membership.getStartDate()) && returnDate.isBefore(membership.getEndDate())){
                return membership;
            }
        }
        return null;
    }
}