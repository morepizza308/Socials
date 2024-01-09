package de.joergmorbitzer.Socials.repositories;

import de.joergmorbitzer.Socials.entities.SocialUpdate;
import de.joergmorbitzer.Socials.entities.SocialUser;
import de.joergmorbitzer.Socials.entities.UpdateTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UpdateRepository extends JpaRepository<SocialUpdate, Long> {
    List<SocialUpdate> findByAuthor(SocialUser author);
    List<SocialUpdate> findByTarget(UpdateTarget target);
    List<SocialUpdate> findByAuthorIn(Collection<SocialUser> friends);
    List<SocialUpdate> findByTargetOrAuthorIn(UpdateTarget target, Collection<SocialUser> friends);
}
