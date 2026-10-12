package br.com.dio.board.dto;

import java.time.LocalDateTime;

/** Uma passagem do card por uma coluna; {@code leftAt} é nulo se o card ainda está nela. */
public record ColumnPassageDTO(String columnName, LocalDateTime enteredAt, LocalDateTime leftAt) {
}
