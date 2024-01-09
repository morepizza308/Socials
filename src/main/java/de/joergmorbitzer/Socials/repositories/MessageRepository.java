package de.joergmorbitzer.Socials.repositories;

import de.joergmorbitzer.Socials.entities.Message;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTouserUid(Long toid);
    Message findByMid(Long mid);
}
