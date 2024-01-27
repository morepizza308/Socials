package de.joergmorbitzer.Socials.repositories;

import de.joergmorbitzer.Socials.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
