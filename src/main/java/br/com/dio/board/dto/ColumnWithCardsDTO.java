package br.com.dio.board.dto;

import java.util.List;

public record ColumnWithCardsDTO(Long id, String name, List<CardDetailsDTO> cards) {
}
