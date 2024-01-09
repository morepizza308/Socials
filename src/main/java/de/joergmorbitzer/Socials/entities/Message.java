package de.joergmorbitzer.Socials.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;
    private String heading;
    private String message;
    private boolean isRead;
    @OneToOne
    private SocialUser fromuser;
    @OneToOne
    private SocialUser touser;
}
