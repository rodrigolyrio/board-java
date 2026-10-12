package br.com.dio.board.persistence.dao;

import br.com.dio.board.dto.ColumnPassageDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Registra por quais colunas o card passou e quando (base do relatório de tempo). */
public class CardHistoryDAO {

    private final Connection connection;

    public CardHistoryDAO(final Connection connection) {
        this.connection = connection;
    }

    public void enterColumn(final Long cardId, final Long columnId, final LocalDateTime when) throws SQLException {
        final String sql = "INSERT INTO card_column_history (card_id, board_column_id, entered_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.setLong(2, columnId);
            ps.setTimestamp(3, Timestamp.valueOf(when));
            ps.executeUpdate();
        }
    }

    public void leaveCurrentColumn(final Long cardId, final LocalDateTime when) throws SQLException {
        final String sql = "UPDATE card_column_history SET left_at = ? WHERE card_id = ? AND left_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(when));
            ps.setLong(2, cardId);
            ps.executeUpdate();
        }
    }

    public List<ColumnPassageDTO> findByCardId(final Long cardId) throws SQLException {
        final String sql = "SELECT bc.name, h.entered_at, h.left_at FROM card_column_history h "
                + "JOIN boards_columns bc ON bc.id = h.board_column_id WHERE h.card_id = ? ORDER BY h.id";
        final List<ColumnPassageDTO> passages = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Timestamp leftAt = rs.getTimestamp("left_at");
                    passages.add(new ColumnPassageDTO(
                            rs.getString("name"),
                            rs.getTimestamp("entered_at").toLocalDateTime(),
                            leftAt == null ? null : leftAt.toLocalDateTime()));
                }
            }
        }
        return passages;
    }
}
