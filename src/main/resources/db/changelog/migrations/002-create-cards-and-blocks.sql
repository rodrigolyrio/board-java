--liquibase formatted sql
--changeset dio:002-create-cards
CREATE TABLE cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    board_column_id BIGINT NOT NULL,
    CONSTRAINT fk_cards_column FOREIGN KEY (board_column_id) REFERENCES boards_columns (id) ON DELETE CASCADE
);
--rollback DROP TABLE cards;

--changeset dio:002-create-blocks
CREATE TABLE blocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocked_at TIMESTAMP NOT NULL,
    block_reason VARCHAR(1000) NOT NULL,
    unblocked_at TIMESTAMP NULL,
    unblock_reason VARCHAR(1000) NULL,
    card_id BIGINT NOT NULL,
    CONSTRAINT fk_blocks_card FOREIGN KEY (card_id) REFERENCES cards (id) ON DELETE CASCADE
);
--rollback DROP TABLE blocks;
