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
public class SocialGroupUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sguid;
    private String title;
    private String content;

    @ManyToOne
    private SocialGroup socialGroup;

    @ManyToOne
    private SocialUser author;
}
