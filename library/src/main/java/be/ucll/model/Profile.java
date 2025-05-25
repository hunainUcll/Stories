package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bio is required.")
    private String bio;

    @NotBlank(message = "Location is required.")
    private String location;

    @NotBlank(message = "Interests are required.")
    private String interests;

    @JsonIgnore
    @OneToOne(mappedBy = "profile")
    private User user;

    protected Profile() {}

    public Profile(String bio, String location, String interests) {
        this.bio = bio;
        this.location = location;
        this.interests = interests;
    }

    // Getters
    public Long getId() { return id; }
    public String getBio() { return bio; }
    public String getLocation() { return location; }
    public String getInterests() { return interests; }
    public User getUser() { return user; }

    // Setters
    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setId(Long id) {
        this.id = id;
    }
}