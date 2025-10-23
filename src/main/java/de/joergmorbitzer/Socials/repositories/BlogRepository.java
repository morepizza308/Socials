package de.joergmorbitzer.Socials.repositories;

import de.joergmorbitzer.Socials.entities.SocialBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<SocialBlog, Long> {
}
