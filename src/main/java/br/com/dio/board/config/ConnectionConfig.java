package br.com.dio.board.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Configuração da conexão com o banco.
 *
 * <p>Por padrão usa o H2 em arquivo ({@code ./data/board}), que não exige instalação.
 * Para usar outro banco (ex.: MySQL), defina as variáveis de ambiente
 * {@code BOARD_DB_URL}, {@code BOARD_DB_USER} e {@code BOARD_DB_PASSWORD}.</p>
 */
public final class ConnectionConfig {

    private static final String DEFAULT_URL = "jdbc:h2:file:./data/board;MODE=MySQL";

    private static final String URL = env("BOARD_DB_URL", DEFAULT_URL);
    private static final String USER = env("BOARD_DB_USER", "sa");
    private static final String PASSWORD = env("BOARD_DB_PASSWORD", "");

    private ConnectionConfig() {
    }

    /** Abre uma conexão com commit manual (as transações são controladas pela camada de serviço). */
    public static Connection getConnection() throws SQLException {
        final Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.setAutoCommit(false);
        return connection;
    }

    public static String getUrl() {
        return URL;
    }

    private static String env(final String name, final String defaultValue) {
        final String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
