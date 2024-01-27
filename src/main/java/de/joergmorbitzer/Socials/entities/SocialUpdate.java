package de.joergmorbitzer.Socials.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SocialUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long upid;
    private String title;
    private String content;
    private Date created;
    @Enumerated(EnumType.ORDINAL)
    private PrivacyTarget target;
    @ManyToOne
    private SocialUser author;
}
