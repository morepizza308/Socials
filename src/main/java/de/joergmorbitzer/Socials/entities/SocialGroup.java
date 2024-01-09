package de.joergmorbitzer.Socials.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SocialGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gid;
    private String name;
    private String description;
    private boolean isPrivate;
    @ManyToOne
    private SocialUser foundedBy;
    @ManyToMany(mappedBy = "memberOf")
    private Set<SocialUser> members;
    @ManyToMany(mappedBy = "pendingFor")
    private Set<SocialUser> pending;
    @OneToMany(mappedBy = "socialGroup")
    private List<SocialGroupUpdate> groupUpdates;
}
