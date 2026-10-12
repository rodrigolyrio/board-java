package br.com.dio.board.dto;

import java.time.LocalDateTime;

public record BlockInfoDTO(LocalDateTime blockedAt, String blockReason,
                           LocalDateTime unblockedAt, String unblockReason) {
}
