package br.uff.graduatesapi

import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.model.OffsetLimit
import br.uff.graduatesapi.model.WorkHistory

class Utils {
  companion object {
    fun convertPagination(page: Int?, pageSize: Int?): OffsetLimit {
      if (page != null && pageSize != null){
        val offset = page * pageSize
        return OffsetLimit(offset = offset, limit = pageSize, page = page, pageSize = pageSize)
      }
      return OffsetLimit(offset = 0, limit = 100000000)
    }

    fun getHistoryStatus(
      history: WorkHistory?,
      postDoctorate: Institution?,
      finishedDoctorateOnUFF: Boolean?,
      finishedMasterDegreeOnUFF: Boolean?,
    ): WorkHistoryStatus {
      if (history != null && postDoctorate != null && finishedDoctorateOnUFF != null && finishedMasterDegreeOnUFF != null)
        return WorkHistoryStatus.UPDATED
      else if (history != null || postDoctorate != null || finishedDoctorateOnUFF != null || finishedMasterDegreeOnUFF != null)
        return WorkHistoryStatus.UPDATED_PARTIALLY
      return WorkHistoryStatus.PENDING
    }
  }
}