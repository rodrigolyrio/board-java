--liquibase formatted sql
--changeset dio:001-create-boards
CREATE TABLE boards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT uk_boards_name UNIQUE (name)
);
--rollback DROP TABLE boards;

--changeset dio:001-create-boards-columns
CREATE TABLE boards_columns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    column_order INT NOT NULL,
    kind VARCHAR(20) NOT NULL,
    board_id BIGINT NOT NULL,
    CONSTRAINT fk_boards_columns_board FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE,
    CONSTRAINT uk_boards_columns_order UNIQUE (board_id, column_order)
);
--rollback DROP TABLE boards_columns;
