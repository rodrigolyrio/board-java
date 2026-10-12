package br.com.dio.board.persistence.dao;

import br.com.dio.board.dto.BoardColumnInfoDTO;
import br.com.dio.board.persistence.entity.BoardColumnEntity;
import br.com.dio.board.persistence.entity.BoardColumnKindEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnDAO(final Connection connection) {
        this.connection = connection;
    }

    public void insert(final BoardColumnEntity entity) throws SQLException {
        final String sql = "INSERT INTO boards_columns (name, column_order, kind, board_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getOrder());
            ps.setString(3, entity.getKind().name());
            ps.setLong(4, entity.getBoardId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
            }
        }
    }

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        final String sql = "SELECT id, name, column_order, kind, board_id FROM boards_columns "
                + "WHERE board_id = ? ORDER BY column_order";
        final List<BoardColumnEntity> columns = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    columns.add(map(rs));
                }
            }
        }
        return columns;
    }

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        final String sql = "SELECT id, name, column_order, kind, board_id FROM boards_columns WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    /** Colunas do board com a contagem de cards em cada uma. */
    public List<BoardColumnInfoDTO> findInfoByBoardId(final Long boardId) throws SQLException {
        final String sql = "SELECT bc.id, bc.name, bc.column_order, bc.kind, "
                + "(SELECT COUNT(*) FROM cards c WHERE c.board_column_id = bc.id) AS cards_amount "
                + "FROM boards_columns bc WHERE bc.board_id = ? ORDER BY bc.column_order";
        final List<BoardColumnInfoDTO> columns = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    columns.add(new BoardColumnInfoDTO(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getInt("column_order"),
                            BoardColumnKindEnum.valueOf(rs.getString("kind")),
                            rs.getInt("cards_amount")));
                }
            }
        }
        return columns;
    }

    private BoardColumnEntity map(final ResultSet rs) throws SQLException {
        final BoardColumnEntity column = new BoardColumnEntity();
        column.setId(rs.getLong("id"));
        column.setName(rs.getString("name"));
        column.setOrder(rs.getInt("column_order"));
        column.setKind(BoardColumnKindEnum.valueOf(rs.getString("kind")));
        column.setBoardId(rs.getLong("board_id"));
        return column;
    }
}
