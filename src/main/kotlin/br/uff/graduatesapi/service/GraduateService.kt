package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.ListGraduatesDTO
import br.uff.graduatesapi.dto.WorkPlaceDTO
import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.repository.GraduateRepository
import org.springframework.stereotype.Service

@Service
class GraduateService(
    private val userService: UserService,
    private val advisorService: AdvisorService,
    private val workHistoryService: WorkHistoryService,
    private val graduateRepository: GraduateRepository,
    ) {
    fun getGraduatesByAdvisor(jwt: String): List<ListGraduatesDTO>? {
        val user = userService.getUserByJwt(jwt) ?: return null
        val graduates = user.advisor?.let { graduateRepository.findAllByCoursesAdvisorIsOrderByHistoryStatusDesc(it) } ?: return null
        val knownHistoryGraduates = mutableListOf<Graduate>()
        val resp = mutableListOf<ListGraduatesDTO>()
        for (graduate in graduates) {
            var status = WorkHistoryStatus.PENDING
            if (graduate.historyStatus != null && graduate.historyStatus!!.knownWorkplace == true) {
                knownHistoryGraduates.add(graduate)
                status = WorkHistoryStatus.UPDATED
            }
            val item = ListGraduatesDTO(name = graduate.user!!.name, status, null, null)
            resp.add(item)
        }
        val workHistory = workHistoryService.findAllByGraduates(knownHistoryGraduates) ?: return null
        for (graduate in knownHistoryGraduates) {
            val w = workHistory.find { history -> history.graduate!!.id == graduate.id }
            val graduateResp = resp.find { r -> r.name == graduate.user!!.name }
            if (graduateResp != null) {
                if (w != null) {
                    graduateResp.position = w.position
                    val workPlaceDTO = WorkPlaceDTO(name = w.institution!!.name, type = w.institution!!.type)
                    graduateResp.workPlace = workPlaceDTO
                }
            }
        }
        return resp
    }
}