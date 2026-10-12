--liquibase formatted sql
--changeset dio:003-create-card-column-history
-- Histórico de passagem do card por cada coluna (usado no relatório de tempo).
CREATE TABLE card_column_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id BIGINT NOT NULL,
    board_column_id BIGINT NOT NULL,
    entered_at TIMESTAMP NOT NULL,
    left_at TIMESTAMP NULL,
    CONSTRAINT fk_history_card FOREIGN KEY (card_id) REFERENCES cards (id) ON DELETE CASCADE,
    CONSTRAINT fk_history_column FOREIGN KEY (board_column_id) REFERENCES boards_columns (id) ON DELETE CASCADE
);
--rollback DROP TABLE card_column_history;
