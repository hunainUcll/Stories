package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    @NotBlank(message = "E-mail is required.")
    @Email(message = "E-mail must be a valid email format.")
    @Column(unique = true)
    private String email;

    @Min(value = 0, message = "Age must be at least 0.")
    @Max(value = 150, message = "Age must be less than 150.")
    private int age;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER) // Add fetch = EAGER
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Membership> memberships = new ArrayList<>();

    protected User() {}

    public User(String name, String password, String email, int age) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.age = age;
        this.profile = null;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public Profile getProfile() { return profile; }
    public List<Membership> getMemberships() { return memberships; }

    // Setters (email has manual validation)
    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        if (this.email != null && !this.email.equals(email)) {
            throw new RuntimeException("Email cannot be changed.");
        }
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setProfile(Profile profile) {
        if (profile != null && this.age < 18) {
            throw new RuntimeException("User must be at least 18 years old to have a profile.");
        }
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }

    public void addMembership(Membership membership) {
        memberships.add(membership);
        membership.setUser(this);
    }

}