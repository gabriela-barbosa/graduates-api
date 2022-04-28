package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.ListGraduatesDTO
import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.dto.WorkPlaceDTO
import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.model.*
import br.uff.graduatesapi.repository.GraduateRepository
import org.springframework.stereotype.Service
import java.util.*

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

    fun save(graduate: Graduate) : Graduate? {
        return graduateRepository.save(graduate)
    }


    fun createGraduateWorkHistory(workDTO: WorkHistoryDTO): Int? {
        // todo add error
        val graduateUser = userService.findByEmail(workDTO.email) ?: return null
        val graduate = graduateUser.graduate
        if (workDTO.newEmail != null) {
            graduateUser.email = workDTO.newEmail
            userService.update(graduateUser)
            //todo add error validation

        }
        val historyStatus = HistoryStatus()
        historyStatus.knownWorkplace = workDTO.knownWorkPlace
        historyStatus.graduate = graduate
        if (!workDTO.knownWorkPlace) {
            historyStatus.status = WorkHistoryStatus.UNKNOWN
            historyStatusService.save(historyStatus)
            // todo add error validation
        } else {
            val history = WorkHistory()
            history.position = workDTO.position
            history.graduate = graduate
            var institution: Institution?
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
                    val institutionSaved = institutionService.save(institution)
                    if (institutionSaved == null) {
                        return null
                        //TODO add error
                    }
                    history.institution = institutionSaved
                }
            }
            workHistoryService.save(history)
        }

        if (workDTO.postDoctorate != null) {
            if (workDTO.postDoctorate.id != null) {
                val institution = institutionService.findById(workDTO.postDoctorate.id)
                if (institution == null) return null
                //TODO add error
                graduate!!.postDoctorate = institution
            } else {
                if (workDTO.postDoctorate.name == null || workDTO.postDoctorate.type == null) {
                    return null
                    //TODO add error
                }
                val institution = Institution()
                institution.name = workDTO.postDoctorate.name
                institution.type = workDTO.postDoctorate.type
                val institutionSaved = institutionService.save(institution)
                if (institutionSaved == null) {
                    return null
                    //TODO add error
                }
                graduate!!.postDoctorate = institutionSaved
            }
            this.save(graduate)
        }

        if (workDTO.finishedDoctorateOnUFF != null) {
            graduate!!.finishedDoctorateOnUFF = workDTO.finishedDoctorateOnUFF
            this.save(graduate)
        }

        if (workDTO.cnpqLevelId != null) {
            val level = cnpqLevelService.findById(workDTO.cnpqLevelId)
            if (level == null) {
                return null
                //TODO add error
            }
            val scholarship = cnpqScholarshipService.findByGraduate(graduate!!)

            if (scholarship == null || scholarship.level!!.id != workDTO.cnpqLevelId) {
                if (scholarship != null) {
//                    scholarship.endYear = Date(System.currentTimeMillis())
                    cnpqScholarshipService.save(scholarship)
                }
                val newScholarship = CNPQScholarship()
                newScholarship.level = level
                newScholarship.graduate = graduate
                cnpqScholarshipService.save(newScholarship)
            }
        }

        return graduate!!.id
    }
}