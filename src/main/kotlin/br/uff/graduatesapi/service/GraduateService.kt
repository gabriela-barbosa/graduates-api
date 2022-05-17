package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.InstitutionDTO
import br.uff.graduatesapi.dto.ListGraduatesDTO
import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.dto.WorkPlaceDTO
import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.*
import br.uff.graduatesapi.repository.GraduateRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GraduateService(
    private val userService: UserService,
    private val institutionService: InstitutionService,
    private val historyStatusService: HistoryStatusService,
    private val workHistoryService: WorkHistoryService,
    private val cnpqScholarshipService: CNPQScholarshipService,
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
                if (graduate.historyStatus!!.knownWorkplace) {
                    knownHistoryGraduates.add(graduate)
                }
                status = graduate.historyStatus!!.status
            }
            val item = ListGraduatesDTO(name = graduate.user.name, status, null, null)
            resp.add(item)
        }
        val workHistory = workHistoryService.findAllByGraduates(knownHistoryGraduates) ?: return null
        for (graduate in knownHistoryGraduates) {
            val singleWorkHistory = workHistory.find { history -> history.graduate.id == graduate.id }
            val graduateResp = resp.find { r -> r.name == graduate.user.name }
            if (graduateResp != null && singleWorkHistory != null) {
                graduateResp.position = singleWorkHistory.position
                val workPlaceDTO = WorkPlaceDTO(
                    name = singleWorkHistory.institution!!.name,
                    type = singleWorkHistory.institution!!.type!!.id!!
                )
                graduateResp.workPlace = workPlaceDTO
            }

        }
        return resp
    }

    fun save(graduate: Graduate): Graduate? {
        return graduateRepository.save(graduate)
    }

    fun createGraduateWorkHistory(workDTO: WorkHistoryDTO): ResponseResult<Int> {
        val respUser = userService.findByEmail(workDTO.email)
        if (respUser is ResponseResult.Error)
            return ResponseResult.Error(respUser.errorReason)

        val graduate = respUser.data!!.graduate!!

        if (workDTO.newEmail != null) {
            val userResult = userService.updateEmail(workDTO.email, workDTO.newEmail)
            if (userResult is ResponseResult.Error) {
                return ResponseResult.Error(userResult.errorReason)
            }
        }

        val resultHistory = workHistoryService.createOrUpdateWorkHistory(workDTO.id, graduate, workDTO.position, workDTO.institution)
        if (resultHistory is ResponseResult.Error) return ResponseResult.Error(resultHistory.errorReason)

        val postDoctorate = if (workDTO.postDoctorate != null) {
            val resultInstitution = institutionService.createInstitutionByInstitutionDTO(workDTO.postDoctorate!!)
            if (resultInstitution is ResponseResult.Error) return ResponseResult.Error(resultInstitution.errorReason)
            graduate.postDoctorate = resultInstitution.data
            this.save(graduate)
            resultInstitution.data
        } else {
            null
        }

        var finishedDoctorateOnUFF: Boolean? = null
        if (workDTO.finishedDoctorateOnUFF != null) {
            finishedDoctorateOnUFF = workDTO.finishedDoctorateOnUFF
            graduate.finishedDoctorateOnUFF = finishedDoctorateOnUFF
            this.save(graduate)
        }

        if (workDTO.cnpqLevelId != null) {
            val resultCNPQScholarship = cnpqScholarshipService.createOrUpdateCNPQScholarship(workDTO.cnpqLevelId, graduate)
            if (resultCNPQScholarship is ResponseResult.Error) return  ResponseResult.Error(resultCNPQScholarship.errorReason)
        }

        val status: WorkHistoryStatus = if (!workDTO.knownWorkPlace) WorkHistoryStatus.UNKNOWN else Utils.getHistoryStatus(
            resultHistory.data,
            postDoctorate,
            finishedDoctorateOnUFF
        )
        val historyStatus = if (graduate.historyStatus != null) {
            graduate.historyStatus!!.status = status
            graduate.historyStatus
        } else HistoryStatus(
            knownWorkplace = workDTO.knownWorkPlace,
            graduate = graduate,
            status = status,
            )
        historyStatusService.save(historyStatus!!)
        return ResponseResult.Success(graduate.id)
    }
}