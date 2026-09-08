package com.frameforward.auth.model.dto;

import java.time.Instant;

public record AccountDeletionJob(String jobId, String state, String failureReason, String deletionToken,
        Instant deletionTokenExpiresAt) {
}
