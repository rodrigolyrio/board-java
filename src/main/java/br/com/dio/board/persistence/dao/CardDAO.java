package br.com.dio.board.persistence.dao;

import br.com.dio.board.dto.CardDetailsDTO;
import br.com.dio.board.persistence.entity.BoardColumnKindEnum;
import br.com.dio.board.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardDAO {

    private static final String DETAILS_SELECT = "SELECT c.id, c.title, c.description, "
            + "bc.id AS column_id, bc.name AS column_name, bc.kind, bc.board_id, "
            + "(SELECT COUNT(*) FROM blocks b WHERE b.card_id = c.id) AS blocks_amount, "
            + "(SELECT COUNT(*) FROM blocks b WHERE b.card_id = c.id AND b.unblocked_at IS NULL) AS open_blocks "
            + "FROM cards c JOIN boards_columns bc ON bc.id = c.board_column_id ";

    private final Connection connection;

    public CardDAO(final Connection connection) {
        this.connection = connection;
    }

    public void insert(final CardEntity entity) throws SQLException {
        final String sql = "INSERT INTO cards (title, description, board_column_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getTitle());
            ps.setString(2, entity.getDescription());
            ps.setLong(3, entity.getBoardColumnId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
            }
        }
    }

    public void moveToColumn(final Long cardId, final Long columnId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE cards SET board_column_id = ? WHERE id = ?")) {
            ps.setLong(1, columnId);
            ps.setLong(2, cardId);
            ps.executeUpdate();
        }
    }

    public Optional<CardDetailsDTO> findDetails(final Long cardId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(DETAILS_SELECT + "WHERE c.id = ?")) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<CardDetailsDTO> findDetailsByColumnId(final Long columnId) throws SQLException {
        final List<CardDetailsDTO> cards = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(DETAILS_SELECT + "WHERE bc.id = ? ORDER BY c.id")) {
            ps.setLong(1, columnId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cards.add(map(rs));
                }
            }
        }
        return cards;
    }

    private CardDetailsDTO map(final ResultSet rs) throws SQLException {
        return new CardDetailsDTO(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("open_blocks") > 0,
                rs.getInt("blocks_amount"),
                rs.getLong("column_id"),
                rs.getString("column_name"),
                BoardColumnKindEnum.valueOf(rs.getString("kind")),
                rs.getLong("board_id"));
    }
}
