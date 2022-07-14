package br.uff.graduatesapi.service

import br.uff.graduatesapi.model.HistoryStatus
import br.uff.graduatesapi.repository.HistoryStatusRepository
import org.springframework.stereotype.Service

@Service
class HistoryStatusService(
    private val historyStatusRepository: HistoryStatusRepository,
) {
    fun save(historyStatus: HistoryStatus): HistoryStatus {
        return historyStatusRepository.save(historyStatus)
    }
}