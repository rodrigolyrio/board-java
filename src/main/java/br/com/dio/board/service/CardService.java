package br.com.dio.board.service;

import br.com.dio.board.config.Transactions;
import br.com.dio.board.dto.CardDetailsDTO;
import br.com.dio.board.exception.BusinessException;
import br.com.dio.board.exception.CardBlockedException;
import br.com.dio.board.exception.CardFinishedException;
import br.com.dio.board.persistence.dao.BlockDAO;
import br.com.dio.board.persistence.dao.BoardColumnDAO;
import br.com.dio.board.persistence.dao.CardDAO;
import br.com.dio.board.persistence.dao.CardHistoryDAO;
import br.com.dio.board.persistence.entity.BoardColumnEntity;
import br.com.dio.board.persistence.entity.BoardColumnKindEnum;
import br.com.dio.board.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Regras de negócio dos cards:
 * <ul>
 *   <li>o card nasce na coluna inicial;</li>
 *   <li>só avança para a próxima coluna, na ordem; não pode avançar bloqueado nem depois de finalizado/cancelado;</li>
 *   <li>o bloqueio e o desbloqueio exigem um motivo;</li>
 *   <li>pode ser cancelado de qualquer coluna, exceto a final, e desde que não esteja bloqueado.</li>
 * </ul>
 */
public class CardService {

    public CardEntity create(final Long boardId, final CardEntity card) {
        return Transactions.execute(connection -> {
            final BoardColumnEntity initial = new BoardColumnDAO(connection).findByBoardId(boardId).stream()
                    .filter(c -> c.getKind() == BoardColumnKindEnum.INITIAL)
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("O board não possui coluna inicial."));
            card.setBoardColumnId(initial.getId());
            new CardDAO(connection).insert(card);
            new CardHistoryDAO(connection).enterColumn(card.getId(), initial.getId(), LocalDateTime.now());
            return card;
        });
    }

    public void moveToNextColumn(final Long boardId, final Long cardId) {
        Transactions.execute(connection -> {
            final CardDetailsDTO card = BoardQueryService.loadCard(new CardDAO(connection), boardId, cardId);
            if (card.columnKind() == BoardColumnKindEnum.FINAL) {
                throw new CardFinishedException("O card já foi finalizado.");
            }
            if (card.columnKind() == BoardColumnKindEnum.CANCEL) {
                throw new BusinessException("O card está cancelado e não pode ser movido.");
            }
            if (card.blocked()) {
                throw new CardBlockedException("O card " + cardId + " está bloqueado. Desbloqueie-o para movê-lo.");
            }
            final List<BoardColumnEntity> columns = new BoardColumnDAO(connection).findByBoardId(boardId);
            final BoardColumnEntity current = columns.stream()
                    .filter(c -> c.getId().equals(card.columnId())).findFirst().orElseThrow();
            final BoardColumnEntity next = columns.stream()
                    .filter(c -> c.getOrder() == current.getOrder() + 1).findFirst()
                    .orElseThrow(() -> new BusinessException("Não há próxima coluna para este card."));
            move(connection, cardId, next.getId());
            return null;
        });
    }

    public void cancel(final Long boardId, final Long cardId) {
        Transactions.execute(connection -> {
            final CardDetailsDTO card = BoardQueryService.loadCard(new CardDAO(connection), boardId, cardId);
            if (card.columnKind() == BoardColumnKindEnum.FINAL) {
                throw new CardFinishedException("Um card finalizado não pode ser cancelado.");
            }
            if (card.columnKind() == BoardColumnKindEnum.CANCEL) {
                throw new BusinessException("O card já está cancelado.");
            }
            if (card.blocked()) {
                throw new CardBlockedException("O card " + cardId + " está bloqueado. Desbloqueie-o para cancelá-lo.");
            }
            final BoardColumnEntity cancelColumn = new BoardColumnDAO(connection).findByBoardId(boardId).stream()
                    .filter(c -> c.getKind() == BoardColumnKindEnum.CANCEL)
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("O board não possui coluna de cancelamento."));
            move(connection, cardId, cancelColumn.getId());
            return null;
        });
    }

    public void block(final Long boardId, final Long cardId, final String reason) {
        Transactions.execute(connection -> {
            final CardDetailsDTO card = BoardQueryService.loadCard(new CardDAO(connection), boardId, cardId);
            if (card.columnKind() == BoardColumnKindEnum.FINAL || card.columnKind() == BoardColumnKindEnum.CANCEL) {
                throw new BusinessException("Cards finalizados ou cancelados não podem ser bloqueados.");
            }
            if (card.blocked()) {
                throw new CardBlockedException("O card " + cardId + " já está bloqueado.");
            }
            requireReason(reason, "bloqueio");
            new BlockDAO(connection).block(cardId, reason.trim());
            return null;
        });
    }

    public void unblock(final Long boardId, final Long cardId, final String reason) {
        Transactions.execute(connection -> {
            final CardDetailsDTO card = BoardQueryService.loadCard(new CardDAO(connection), boardId, cardId);
            if (!card.blocked()) {
                throw new BusinessException("O card " + cardId + " não está bloqueado.");
            }
            requireReason(reason, "desbloqueio");
            new BlockDAO(connection).unblock(cardId, reason.trim());
            return null;
        });
    }

    private void move(final Connection connection, final Long cardId, final Long targetColumnId) throws SQLException {
        final LocalDateTime now = LocalDateTime.now();
        final CardHistoryDAO history = new CardHistoryDAO(connection);
        history.leaveCurrentColumn(cardId, now);
        new CardDAO(connection).moveToColumn(cardId, targetColumnId);
        history.enterColumn(cardId, targetColumnId, now);
    }

    private void requireReason(final String reason, final String action) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("O motivo do " + action + " é obrigatório.");
        }
    }
}
