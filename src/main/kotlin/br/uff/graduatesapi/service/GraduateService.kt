package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.ListGraduatesDTO
import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.dto.WorkPlaceDTO
import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.model.*
import br.uff.graduatesapi.repository.GraduateRepository
import org.springframework.stereotype.Service

@Service
class GraduateService(
    private val userService: UserService,
    private val advisorService: AdvisorService,
    private val institutionService: InstitutionService,
    private val historyStatusService: HistoryStatusService,
    private val workHistoryService: WorkHistoryService,
    private val cnpqScholarshipService: CNPQScholarshipService,
    private val cnpqLevelService: CNPQLevelService,
    private val graduateRepository: GraduateRepository,
) {
    fun getGraduatesByAdvisor(jwt: String): List<ListGraduatesDTO>? {
        val user = userService.getUserByJwt(jwt) ?: return null
        val graduates = user.advisor?.let { graduateRepository.findAllByCoursesAdvisorIsOrderByHistoryStatusDesc(it) }
            ?: return null
        val knownHistoryGraduates = mutableListOf<Graduate>()
        val resp = mutableListOf<ListGraduatesDTO>()
        for (graduate in graduates) {
            var status = WorkHistoryStatus.PENDING
            if (graduate.historyStatus != null) {
                if (graduate.historyStatus!!.knownWorkplace == true) {
                    knownHistoryGraduates.add(graduate)
                }
                status = graduate.historyStatus!!.status
            }
            val item = ListGraduatesDTO(name = graduate.user!!.name, status, null, null)
            resp.add(item)
        }
        val workHistory = workHistoryService.findAllByGraduates(knownHistoryGraduates) ?: return null
        for (graduate in knownHistoryGraduates) {
            val singleWorkHistory = workHistory.find { history -> history.graduate!!.id == graduate.id }
            val graduateResp = resp.find { r -> r.name == graduate.user!!.name }
            if (graduateResp != null && singleWorkHistory != null) {
                graduateResp.position = singleWorkHistory.position
                val workPlaceDTO = WorkPlaceDTO(
                    name = singleWorkHistory.institution!!.name,
                    type = singleWorkHistory.institution!!.type
                )
                graduateResp.workPlace = workPlaceDTO
            }

        }
        return resp
    }


    fun createGraduateWorkHistory(workDTO: WorkHistoryDTO): Unit? {
        // todo add error
        val graduateUser = userService.findByEmail(workDTO.email) ?: return null
        if (workDTO.newEmail != null) {
            userService.updateEmail(workDTO.email, workDTO.newEmail)
            //todo add error validation
            graduateUser.email = workDTO.newEmail
        }
        val historyStatus = HistoryStatus()
        historyStatus.knownWorkplace = workDTO.knownWorkPlace
        historyStatus.graduate = graduateUser.graduate
        if (!workDTO.knownWorkPlace) {
            historyStatus.status = WorkHistoryStatus.UNKNOWN
            historyStatusService.save(historyStatus)
            // todo add error validation
            return null
        }

        val history = WorkHistory()
        history.position = workDTO.position
        history.graduate = graduateUser.graduate
        var institution: Institution? = null
        if (workDTO.institution != null) {
            if (workDTO.institution!!.id != null) {
                institution = institutionService.findById(workDTO.institution!!.id!!)
                if (institution == null) return null
                //TODO add error
            } else {
                if (workDTO.institution!!.name == null || workDTO.institution!!.type == null) {
                    return null
                    //TODO add error
                }
                institution = Institution()
                institution.name = workDTO.institution!!.name!!
                institution.type = workDTO.institution!!.type!!
                val resp = institutionService.save(institution)
                if (resp == null) {
                    return null
                    //TODO add error
                }
            }
        }
        if (workDTO.postDoctorate != null) {
            if (workDTO.postDoctorate!!.id != null) {
                institution = institutionService.findById(workDTO.postDoctorate!!.id!!)
                if (institution == null) return null
                //TODO add error
            } else {
                if (workDTO.postDoctorate!!.name == null || workDTO.postDoctorate!!.type == null) {
                    return null
                    //TODO add error
                }
                institution = Institution()
                institution.name = workDTO.postDoctorate!!.name!!
                institution.type = workDTO.postDoctorate!!.type!!
                val resp = institutionService.save(institution)
                if (resp == null) {
                    return null
                    //TODO add error
                }
            }
        }

        if (workDTO.cnpqLevelId != null) {
            val level = cnpqLevelService.findById(workDTO.cnpqLevelId)
            if (level == null) {
                return null
                //TODO add error
            }
            val scholarship = cnpqScholarshipService.findByGraduate(graduateUser.graduate!!)
            if (scholarship == null) {
                return null
                //TODO add error
            }
        }

        if (history.position == null || history.graduate == null || institution == null ||)

            history.institution = institution

    }
}