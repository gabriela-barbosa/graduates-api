package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.HistoryStatus
import br.uff.graduatesapi.repository.HistoryStatusRepository
import org.springframework.stereotype.Service

@Service
class HistoryStatusService(
  private val historyStatusRepository: HistoryStatusRepository,
) {

  fun upsertHistoryStatusByGraduate(
    graduate: Graduate,
    hasCurrentWorkHistory: Boolean?,
    hasCurrentCNPQScholarship: Boolean?,
    hasPostDoctorate: Boolean?,
  ): ResponseResult<HistoryStatus> {
    val (status, pendingFields, emptyFields) = Utils.getHistoryStatus(
      graduate = graduate,
      hasCurrentWorkHistory = hasCurrentWorkHistory,
      hasCurrentCNPQScholarship = hasCurrentCNPQScholarship,
      hasPostDoctorate = hasPostDoctorate
    )

    val historyStatus =
      graduate.currentHistoryStatus ?: HistoryStatus(
        status = status,
        pendingFields = pendingFields.joinToString(","),
        emptyFields = emptyFields.joinToString(","),
        graduate = graduate
      )

    return try {
      ResponseResult.Success(historyStatusRepository.save(historyStatus))
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.UNABLE_TO_INSERT_UPDATE_HISTORY_STATUS)
    }

  }
}