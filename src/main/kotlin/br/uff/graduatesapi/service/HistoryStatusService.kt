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


    val pendingFieldsStringified = pendingFields.joinToString(",")
    val emptyFieldsStringified = emptyFields.joinToString(",")

    val historyStatus =
      if (graduate.currentHistoryStatus != null) {
        graduate.currentHistoryStatus.status = status
        graduate.currentHistoryStatus.pendingFields = pendingFieldsStringified
        graduate.currentHistoryStatus.emptyFields = emptyFieldsStringified
        graduate.currentHistoryStatus
      } else
        HistoryStatus(
          status = status,
          pendingFields = pendingFieldsStringified,
          emptyFields = emptyFieldsStringified,
          graduate = graduate
        )

    return try {
      ResponseResult.Success(historyStatusRepository.save(historyStatus))
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.UNABLE_TO_INSERT_UPDATE_HISTORY_STATUS)
    }

  }
}