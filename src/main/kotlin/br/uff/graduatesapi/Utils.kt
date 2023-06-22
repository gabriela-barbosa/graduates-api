package br.uff.graduatesapi

import br.uff.graduatesapi.entity.OffsetLimit
import br.uff.graduatesapi.enum.HistoryStatusEnum
import br.uff.graduatesapi.enum.NamePendingFieldsEnum
import br.uff.graduatesapi.model.CNPQScholarship
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.WorkHistory

class Utils {
  companion object {
    fun convertPagination(page: Int?, pageSize: Int?): OffsetLimit {
      if (page != null && pageSize != null) {
        val offset = page * pageSize
        return OffsetLimit(offset = offset, limit = pageSize, page = page, pageSize = pageSize)
      }
      return OffsetLimit(offset = 0, limit = 100000000)
    }

    fun getRandomString(length: Int): String {
      val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
      return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
    }

    fun getHistoryStatus(
      graduate: Graduate,
      workHistories: List<WorkHistory>?,
      cnpqScholarships: List<CNPQScholarship>?,
      isPostDoctorateKnown: Boolean,
    ): Pair<HistoryStatusEnum, MutableList<String>> {

      val newPendingFields: MutableList<String> = mutableListOf()

      val isPositionPending = graduate.workHistories.any { it.position == null }

      isPositionPending && newPendingFields.add(NamePendingFieldsEnum.POSITION.value)

      val isWorkHistoryPending = workHistories == null

      isWorkHistoryPending && newPendingFields.add(NamePendingFieldsEnum.WORK_HISTORY.value)

      val isCNPQScholarshipPending = cnpqScholarships == null

      isCNPQScholarshipPending && newPendingFields.add(NamePendingFieldsEnum.CNPQ_SCHOLARSHIP.value)

      val isPostDoctoratePending = !isPostDoctorateKnown

      isPostDoctoratePending && newPendingFields.add(NamePendingFieldsEnum.POST_DOCTORATE.value)

      val isFinishedDoctorateOnUFFPending = graduate.hasFinishedDoctorateOnUFF == null

      isFinishedDoctorateOnUFFPending && newPendingFields.add(NamePendingFieldsEnum.FINISHED_DOCTORATE_ON_UFF.value)

      val isFinishedMasterDegreeOnUFFPending = graduate.hasFinishedMasterDegreeOnUFF == null

      isFinishedMasterDegreeOnUFFPending && newPendingFields.add(NamePendingFieldsEnum.FINISHED_MASTER_DEGREE_ON_UFF.value)


      val pendingItems = listOf(
        isPositionPending,
        isWorkHistoryPending,
        isCNPQScholarshipPending,
        isFinishedDoctorateOnUFFPending,
        isPostDoctoratePending,
        isFinishedMasterDegreeOnUFFPending,
      )

      val status = if (pendingItems.all { !it }) HistoryStatusEnum.UPDATED
      else if (pendingItems.any { !it }) HistoryStatusEnum.UPDATED_PARTIALLY
      else HistoryStatusEnum.PENDING

      return Pair(status, newPendingFields)
    }
  }
}