package de.joergmorbitzer.Socials.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pid;
    @Column(nullable = false)
    private String title;

    @OneToOne(mappedBy = "profile")
    private SocialUser profileUser;

    public Profile(String username, SocialUser profileUser)
    {
        title = username;
        this.profileUser = profileUser;
    }
}
