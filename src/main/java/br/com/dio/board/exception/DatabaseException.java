package br.com.dio.board.exception;

import java.sql.SQLException;

/** Encapsula falhas de acesso ao banco (SQLException) em uma exceção não verificada. */
public class DatabaseException extends RuntimeException {

    public DatabaseException(final SQLException cause) {
        super(cause.getMessage(), cause);
    }
}
