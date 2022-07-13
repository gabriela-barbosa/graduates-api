package br.uff.graduatesapi

import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.model.WorkHistory

class Utils {
    companion object {
        fun getHistoryStatus(
            history: WorkHistory?,
            postDoctorate: Institution?,
            finishedDoctorateOnUFF: Boolean?
        ): WorkHistoryStatus {
            if (history != null && postDoctorate != null && finishedDoctorateOnUFF != null)
                return WorkHistoryStatus.UPDATED
            else if (history != null || postDoctorate != null || finishedDoctorateOnUFF != null)
                return WorkHistoryStatus.UPDATED_PARTIALLY
            return WorkHistoryStatus.PENDING
        }
    }
}