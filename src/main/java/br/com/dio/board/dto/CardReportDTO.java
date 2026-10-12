package br.com.dio.board.dto;

import java.util.List;

public record CardReportDTO(CardDetailsDTO card, List<ColumnPassageDTO> passages, List<BlockInfoDTO> blocks) {
}
