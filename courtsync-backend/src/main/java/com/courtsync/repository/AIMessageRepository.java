package com.courtsync.repository;

import com.courtsync.entity.AIMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AIMessageRepository extends JpaRepository<AIMessage, Long> {
    List<AIMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}
