package com.courtsync.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AIMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_ASSISTANT = 1;

    @SerializedName("conversationId")
    private long conversationId;

    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private String content;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("suggestedHalls")
    private List<SportHall> suggestedHalls;

    // Local fields for UI display (not from API)
    private boolean isUser;

    public AIMessage(String content, boolean isUser, String timestamp) {
        this.content = content;
        this.isUser = isUser;
        this.timestamp = timestamp;
        this.role = isUser ? "USER" : "ASSISTANT";
    }

    public long getConversationId() { return conversationId; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public List<SportHall> getSuggestedHalls() { return suggestedHalls; }
    public boolean isUser() { return "USER".equals(role) || isUser; }

    public void setConversationId(long id) { conversationId = id; }
    public void setSuggestedHalls(List<SportHall> halls) { suggestedHalls = halls; }
}
