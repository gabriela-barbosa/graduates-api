package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.HistoryStatus
import org.springframework.data.jpa.repository.JpaRepository

interface HistoryStatusRepository : JpaRepository<HistoryStatus, Int>