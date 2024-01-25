package de.joergmorbitzer.Socials.repositories;

import de.joergmorbitzer.Socials.entities.FriendState;
import de.joergmorbitzer.Socials.entities.Friendship;
import de.joergmorbitzer.Socials.entities.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findByRequestorOrFriendToBe(SocialUser requestor, SocialUser friendToBe);
    Optional<Friendship> findByRequestorAndFriendToBe(SocialUser requestor, SocialUser friendToBe);
    Optional<Friendship> findByState(FriendState state);
    @Modifying
    @Query("DELETE Friendship c WHERE c.id = ?1")
    void deleteById(long id);
}
