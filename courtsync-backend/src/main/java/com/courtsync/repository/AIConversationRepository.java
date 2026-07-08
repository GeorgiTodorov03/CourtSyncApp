package com.courtsync.repository;

import com.courtsync.entity.AIConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AIConversationRepository extends JpaRepository<AIConversation, Long> {
    List<AIConversation> findByUserIdOrderByUpdatedAtDesc(Long userId);
    Optional<AIConversation> findByIdAndUserId(Long id, Long userId);
}
