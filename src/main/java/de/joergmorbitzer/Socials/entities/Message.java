package de.joergmorbitzer.Socials.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;
    private String heading;
    private String message;
    private boolean isRead;
    private Date created;
    @OneToOne
    private SocialUser fromuser;
    @OneToOne
    private SocialUser touser;

}
