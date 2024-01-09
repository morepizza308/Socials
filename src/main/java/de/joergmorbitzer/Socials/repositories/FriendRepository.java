package de.joergmorbitzer.Socials.repositories;

import de.joergmorbitzer.Socials.entities.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByRequestUidOrFriendOfUid(long requestUid, long friendOfUid);
    List<Friendship> findByAccepted(boolean accepted);
}
