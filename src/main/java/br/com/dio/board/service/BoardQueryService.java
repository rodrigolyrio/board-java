package br.com.dio.board.service;

import br.com.dio.board.config.Transactions;
import br.com.dio.board.dto.BoardColumnInfoDTO;
import br.com.dio.board.dto.CardDetailsDTO;
import br.com.dio.board.dto.CardReportDTO;
import br.com.dio.board.dto.ColumnWithCardsDTO;
import br.com.dio.board.exception.EntityNotFoundException;
import br.com.dio.board.persistence.dao.BlockDAO;
import br.com.dio.board.persistence.dao.BoardColumnDAO;
import br.com.dio.board.persistence.dao.BoardDAO;
import br.com.dio.board.persistence.dao.CardDAO;
import br.com.dio.board.persistence.dao.CardHistoryDAO;
import br.com.dio.board.persistence.entity.BoardColumnEntity;
import br.com.dio.board.persistence.entity.BoardEntity;

import java.util.List;
import java.util.Optional;

/** Consultas (somente leitura). */
public class BoardQueryService {

    public List<BoardEntity> findAll() {
        return Transactions.execute(connection -> new BoardDAO(connection).findAll());
    }

    public Optional<BoardEntity> findById(final Long id) {
        return Transactions.execute(connection -> new BoardDAO(connection).findById(id));
    }

    public List<BoardColumnInfoDTO> findColumns(final Long boardId) {
        return Transactions.execute(connection -> new BoardColumnDAO(connection).findInfoByBoardId(boardId));
    }

    public ColumnWithCardsDTO findColumnWithCards(final Long boardId, final Long columnId) {
        return Transactions.execute(connection -> {
            final BoardColumnEntity column = new BoardColumnDAO(connection).findById(columnId)
                    .filter(c -> c.getBoardId().equals(boardId))
                    .orElseThrow(() -> new EntityNotFoundException(
                            "A coluna de id " + columnId + " não pertence a este board."));
            final List<CardDetailsDTO> cards = new CardDAO(connection).findDetailsByColumnId(columnId);
            return new ColumnWithCardsDTO(column.getId(), column.getName(), cards);
        });
    }

    public CardDetailsDTO findCard(final Long boardId, final Long cardId) {
        return Transactions.execute(connection -> loadCard(new CardDAO(connection), boardId, cardId));
    }

    public CardReportDTO buildCardReport(final Long boardId, final Long cardId) {
        return Transactions.execute(connection -> {
            final CardDetailsDTO card = loadCard(new CardDAO(connection), boardId, cardId);
            return new CardReportDTO(
                    card,
                    new CardHistoryDAO(connection).findByCardId(cardId),
                    new BlockDAO(connection).findByCardId(cardId));
        });
    }

    /** Carrega o card garantindo que ele pertence ao board informado. */
    static CardDetailsDTO loadCard(final CardDAO cardDAO, final Long boardId, final Long cardId)
            throws java.sql.SQLException {
        return cardDAO.findDetails(cardId)
                .filter(card -> card.boardId().equals(boardId))
                .orElseThrow(() -> new EntityNotFoundException(
                        "O card de id " + cardId + " não foi encontrado neste board."));
    }
}
