package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.model.HistoryStatus
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.HistoryStatusRepository
import br.uff.graduatesapi.repository.UserRepository
import io.jsonwebtoken.Jwts
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class HistoryStatusService(
    private val historyStatusRepository: HistoryStatusRepository,
) {

    fun save(historyStatus: HistoryStatus): HistoryStatus {
        return historyStatusRepository.save(historyStatus)
    }
}