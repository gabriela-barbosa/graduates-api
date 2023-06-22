package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.CNPQScholarship
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.HistoryStatus
import br.uff.graduatesapi.model.WorkHistory
import br.uff.graduatesapi.repository.HistoryStatusRepository
import org.springframework.stereotype.Service

@Service
class HistoryStatusService(
  private val historyStatusRepository: HistoryStatusRepository,
) {

  fun upsertHistoryStatusByGraduate(
    graduate: Graduate,
    workHistories: List<WorkHistory>?,
    cnpqScholarships: List<CNPQScholarship>?,
    isPostDoctorateKnown: Boolean,
  ): ResponseResult<HistoryStatus> {
    val (status, pendingFields) = Utils.getHistoryStatus(graduate = graduate, workHistories = workHistories, cnpqScholarships = cnpqScholarships, isPostDoctorateKnown = isPostDoctorateKnown)

    val historyStatus = graduate.currentHistoryStatus ?: HistoryStatus(status, pendingFields.joinToString(","), graduate)

    return try {
      ResponseResult.Success(historyStatusRepository.save(historyStatus))
    }catch (ex: Exception) {
      ResponseResult.Error(Errors.UNABLE_TO_INSERT_UPDATE_HISTORY_STATUS)
    }

  }
}