package br.com.dio.board.persistence.dao;

import br.com.dio.board.dto.BlockInfoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlockDAO {

    private final Connection connection;

    public BlockDAO(final Connection connection) {
        this.connection = connection;
    }

    public void block(final Long cardId, final String reason) throws SQLException {
        final String sql = "INSERT INTO blocks (blocked_at, block_reason, card_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, reason);
            ps.setLong(3, cardId);
            ps.executeUpdate();
        }
    }

    public void unblock(final Long cardId, final String reason) throws SQLException {
        final String sql = "UPDATE blocks SET unblocked_at = ?, unblock_reason = ? "
                + "WHERE card_id = ? AND unblocked_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, reason);
            ps.setLong(3, cardId);
            ps.executeUpdate();
        }
    }

    public List<BlockInfoDTO> findByCardId(final Long cardId) throws SQLException {
        final String sql = "SELECT blocked_at, block_reason, unblocked_at, unblock_reason "
                + "FROM blocks WHERE card_id = ? ORDER BY id";
        final List<BlockInfoDTO> blocks = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Timestamp unblockedAt = rs.getTimestamp("unblocked_at");
                    blocks.add(new BlockInfoDTO(
                            rs.getTimestamp("blocked_at").toLocalDateTime(),
                            rs.getString("block_reason"),
                            unblockedAt == null ? null : unblockedAt.toLocalDateTime(),
                            rs.getString("unblock_reason")));
                }
            }
        }
        return blocks;
    }
}
