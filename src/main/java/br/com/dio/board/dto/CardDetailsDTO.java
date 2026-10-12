package br.com.dio.board.dto;

import br.com.dio.board.persistence.entity.BoardColumnKindEnum;

public record CardDetailsDTO(
        Long id,
        String title,
        String description,
        boolean blocked,
        int blocksAmount,
        Long columnId,
        String columnName,
        BoardColumnKindEnum columnKind,
        Long boardId) {
}
