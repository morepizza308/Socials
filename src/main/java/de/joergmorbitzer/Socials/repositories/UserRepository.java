package de.joergmorbitzer.Socials.repositories;

import de.joergmorbitzer.Socials.entities.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SocialUser, Long> {
    Optional<SocialUser> findByUsername(String username);
    Optional<SocialUser> findByUid(Long uid);
    List<SocialUser> findByUidNot(Long uid);
    List<SocialUser> findByRole(String role);

    List<SocialUser> findByIsOnline(boolean isOnline);
}
