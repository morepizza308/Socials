package de.joergmorbitzer.Socials.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SocialBlog {
    @Id
    @GeneratedValue
    private long blid;
    private String title;
    @Column(columnDefinition = "varchar(2048)")
    private String content;
    private Date created;
    @ManyToOne
    private SocialUser author;
}
