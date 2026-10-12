package br.com.dio.board.dto;

import br.com.dio.board.persistence.entity.BoardColumnKindEnum;

/** Coluna com a quantidade de cards que ela possui. */
public record BoardColumnInfoDTO(Long id, String name, int order, BoardColumnKindEnum kind, int cardsAmount) {
}
