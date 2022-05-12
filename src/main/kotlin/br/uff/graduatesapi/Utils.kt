package br.uff.graduatesapi

import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.model.WorkHistory
import io.jsonwebtoken.Jwts

class Utils {
    companion object{
        fun getHistoryStatus(history: WorkHistory?, postDoctorate: Institution?, finishedDoctorateOnUFF: Boolean?): WorkHistoryStatus {
            if (history != null && postDoctorate != null && finishedDoctorateOnUFF != null)
                return WorkHistoryStatus.UPDATED
            else if (history != null || postDoctorate != null || finishedDoctorateOnUFF != null)
                return  WorkHistoryStatus.UPDATED_PARCIALlY
            return WorkHistoryStatus.PENDING
        }
        fun parseJwtToBody(jwt: String) = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
    }
}