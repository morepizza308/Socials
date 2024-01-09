package de.joergmorbitzer.Socials.repositories;

import de.joergmorbitzer.Socials.entities.SocialGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<SocialGroup, Long> {

    Optional<SocialGroup> findByName(String groupName);
}
