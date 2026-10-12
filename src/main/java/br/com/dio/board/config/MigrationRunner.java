package br.com.dio.board.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Executa as migrations do Liquibase (cria/atualiza as tabelas) ao iniciar a aplicação. */
public final class MigrationRunner {

    private static final String CHANGELOG = "db/changelog/db.changelog-master.yml";

    private MigrationRunner() {
    }

    public static void run() {
        // Evita que os logs do Liquibase poluam o menu no terminal.
        Logger.getLogger("liquibase").setLevel(Level.OFF);

        // O Liquibase imprime um resumo no System.out; ocultamos só durante a migration.
        final PrintStream originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try (Connection connection = ConnectionConfig.getConnection()) {
            final Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            try (Liquibase liquibase = new Liquibase(CHANGELOG, new ClassLoaderResourceAccessor(), database)) {
                liquibase.update();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao executar as migrations do banco: " + e.getMessage(), e);
        } finally {
            System.setOut(originalOut);
        }
    }
}
