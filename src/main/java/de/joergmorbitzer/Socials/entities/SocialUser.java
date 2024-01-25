package de.joergmorbitzer.Socials.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SocialUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long uid;
    private String username;
    private String email;
    private String password;
    private String role;
    private boolean isOnline;
    @OneToMany
    private Set<SocialUser> friends;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_pid", referencedColumnName = "pid")
    private Profile profile;

    @OneToMany(mappedBy = "foundedBy")
    private List<SocialGroup> founderOf;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "memberships",
            joinColumns = @JoinColumn(name = "social_user_uid"),
            inverseJoinColumns = @JoinColumn(name = "social_group_gid")
    )
    private Set<SocialGroup> memberOf;
    @ManyToMany
    @JoinTable(name = "pending",
            joinColumns = @JoinColumn(name = "social_user_uid"),
            inverseJoinColumns = @JoinColumn(name = "social_group_gid")
    )
    private Set<SocialGroup> pendingFor;

    @OneToMany(mappedBy = "author")
    private List<SocialGroupUpdate> socialGroupUpdates;

    @OneToMany(mappedBy = "author")
    private List<SocialUpdate> socialUpdates;

    public void removeMembership(SocialGroup group)
    {
        memberOf.remove(group);
        group.getMembers().remove(this);
    }
}
