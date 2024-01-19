package de.joergmorbitzer.Socials.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long frid;
    private long requestUid;
    private long friendOfUid;
    @Enumerated(EnumType.STRING)
    private FriendState state;
    private int timesDenied;
    private Date dateOfRequest;
}
