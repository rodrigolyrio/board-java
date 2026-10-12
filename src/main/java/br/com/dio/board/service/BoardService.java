package br.com.dio.board.service;

import br.com.dio.board.config.Transactions;
import br.com.dio.board.exception.BusinessException;
import br.com.dio.board.persistence.dao.BoardColumnDAO;
import br.com.dio.board.persistence.dao.BoardDAO;
import br.com.dio.board.persistence.entity.BoardEntity;

/** Criação e exclusão de boards. */
public class BoardService {

    public BoardEntity insert(final BoardEntity entity) {
        return Transactions.execute(connection -> {
            final BoardDAO boardDAO = new BoardDAO(connection);
            if (boardDAO.existsByName(entity.getName())) {
                throw new BusinessException("Já existe um board com o nome \"" + entity.getName() + "\".");
            }
            boardDAO.insert(entity);
            final BoardColumnDAO columnDAO = new BoardColumnDAO(connection);
            for (var column : entity.getColumns()) {
                column.setBoardId(entity.getId());
                columnDAO.insert(column);
            }
            return entity;
        });
    }

    /** Exclui o board; colunas, cards, bloqueios e histórico saem junto (ON DELETE CASCADE). */
    public boolean delete(final Long id) {
        return Transactions.execute(connection -> new BoardDAO(connection).delete(id));
    }
}
