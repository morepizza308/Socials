package de.joergmorbitzer.Socials.repositories;

import de.joergmorbitzer.Socials.entities.FriendState;
import de.joergmorbitzer.Socials.entities.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByRequestUidOrFriendOfUid(long requestUid, long friendOfUid);
    Optional<Friendship> findByRequestUidAndFriendOfUid(long requestUid, long friendOfUid);
    List<Friendship> findByRequestUid(long requestUid);
    List<Friendship> findByFriendOfUid(long friendOfUid);
    List<Friendship> findByState(FriendState state);
}
