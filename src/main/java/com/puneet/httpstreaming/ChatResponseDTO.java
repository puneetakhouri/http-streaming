package com.puneet.httpstreaming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatResponseDTO {
    private String model;
    private String createdAt;
    private MessageDTO message;
    private String doneReason;
    private boolean done;
    private long totalDuration;
    private long loadDuration;
    private int promptEvalCount;
    private long promptEvalDuration;
    private int evalCount;
    private long evalDuration;

    // Getters and Setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public MessageDTO getMessage() {
        return message;
    }

    public void setMessage(MessageDTO message) {
        this.message = message;
    }

    public String getDoneReason() {
        return doneReason;
    }

    public void setDoneReason(String doneReason) {
        this.doneReason = doneReason;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public long getLoadDuration() {
        return loadDuration;
    }

    public void setLoadDuration(long loadDuration) {
        this.loadDuration = loadDuration;
    }

    public int getPromptEvalCount() {
        return promptEvalCount;
    }

    public void setPromptEvalCount(int promptEvalCount) {
        this.promptEvalCount = promptEvalCount;
    }

    public long getPromptEvalDuration() {
        return promptEvalDuration;
    }

    public void setPromptEvalDuration(long promptEvalDuration) {
        this.promptEvalDuration = promptEvalDuration;
    }

    public int getEvalCount() {
        return evalCount;
    }

    public void setEvalCount(int evalCount) {
        this.evalCount = evalCount;
    }

    public long getEvalDuration() {
        return evalDuration;
    }

    public void setEvalDuration(long evalDuration) {
        this.evalDuration = evalDuration;
    }

    public static class MessageDTO {
        private String role;
        private String content;

        // Getters and Setters
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
