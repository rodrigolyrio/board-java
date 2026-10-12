package br.com.dio.board.config;

import br.com.dio.board.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

/** Executa um bloco de trabalho em uma transação: commit se tudo der certo, rollback se falhar. */
public final class Transactions {

    @FunctionalInterface
    public interface Work<T> {
        T run(Connection connection) throws SQLException;
    }

    private Transactions() {
    }

    public static <T> T execute(final Work<T> work) {
        try (Connection connection = ConnectionConfig.getConnection()) {
            try {
                final T result = work.run(connection);
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
