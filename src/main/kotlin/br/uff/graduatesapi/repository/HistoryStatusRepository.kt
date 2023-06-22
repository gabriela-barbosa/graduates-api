package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.HistoryStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface HistoryStatusRepository : JpaRepository<HistoryStatus, UUID>