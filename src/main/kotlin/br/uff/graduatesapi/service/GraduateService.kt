package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.ListGraduatesDTO
import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.dto.WorkHistoryInfoDTO
import br.uff.graduatesapi.enums.Role
import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.HistoryStatus
import br.uff.graduatesapi.repository.GraduateRepository
import org.springframework.stereotype.Service

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
    val result = userService.getUserByJwt(jwt)
    if (result is ResponseResult.Error)
      return null
    val user = result.data!!
    if (user.role != Role.PROFESSOR) return null
    val graduates = user.advisor?.let { graduateRepository.findAllByCoursesAdvisorIsOrderByHistoryStatusDesc(it) }
      ?: return null
    val knownHistoryGraduates = mutableListOf<Graduate>()
    val graduatesList = mutableListOf<ListGraduatesDTO>()
    for (graduate in graduates) {
      var status = WorkHistoryStatus.PENDING
      if (graduate.historyStatus != null) {
        if (graduate.historyStatus!!.knownWorkplace!!) {
          knownHistoryGraduates.add(graduate)
        }
        status = graduate.historyStatus!!.status
      }
      val item = ListGraduatesDTO(
        name = graduate.user.name,
        email = graduate.user.email,
        id = graduate.id,
        status = status,
        workPlace = null,
        position = null
      )
      graduatesList.add(item)
    }
    val workHistory = workHistoryService.findAllByGraduates(knownHistoryGraduates) ?: return null
    for (graduate in knownHistoryGraduates) {
      val singleWorkHistory = workHistory.find { history -> history.graduate.id == graduate.id }
      val graduateResp = graduatesList.find { r -> r.name == graduate.user.name }
      if (graduateResp != null && singleWorkHistory != null) {
        graduateResp.position = singleWorkHistory.position
        val workHistoryInfoDTO = WorkHistoryInfoDTO(
          id = singleWorkHistory.id!!,
          name = singleWorkHistory.institution!!.name,
          type = singleWorkHistory.institution!!.type!!.id!!
        )
        graduateResp.workPlace = workHistoryInfoDTO
      }

    }
    return graduatesList
  }

  fun getGraduatesByWorkHistory(institutionId: Int): ResponseResult<List<Graduate>> {
    return try {
      val graduates = graduateRepository.findAllByWorkHistoryInstitutionId(institutionId)
      ResponseResult.Success(graduates)
    }catch (ex: Exception) {
      ResponseResult.Error(Errors.INVALID_DATA)
    }
  }

  fun save(graduate: Graduate): Graduate? {
    return graduateRepository.save(graduate)
  }

  fun createGraduateWorkHistory(workDTO: WorkHistoryDTO, id: Int? = null): ResponseResult<Int> {
    val respUser = userService.findByEmail(workDTO.email)
    if (respUser is ResponseResult.Error) {
      if (respUser.errorReason == Errors.USER_NOT_FOUND)
        return ResponseResult.Error(Errors.INVALID_DATA)
      return ResponseResult.Error(respUser.errorReason)
    }

    val graduate = respUser.data!!.graduate!!

    if (workDTO.newEmail != null) {
      val userResult = userService.updateEmail(workDTO.email, workDTO.newEmail)
      if (userResult is ResponseResult.Error) {
        return ResponseResult.Error(userResult.errorReason)
      }
    }

    val resultHistory =
      workHistoryService.createOrUpdateWorkHistory(id, graduate, workDTO.position, workDTO.institution)
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
    val hasFinishedMasterDegreeOnUFF: Boolean?
    if (workDTO.hasFinishedDoctorateOnUFF != null) {
      finishedDoctorateOnUFF = workDTO.hasFinishedDoctorateOnUFF
      graduate.hasFinishedDoctorateOnUFF = finishedDoctorateOnUFF
      this.save(graduate)
    }
    if (workDTO.hasFinishedMasterDegreeOnUFF != null) {
      hasFinishedMasterDegreeOnUFF = workDTO.hasFinishedMasterDegreeOnUFF
      graduate.hasFinishedMasterDegreeOnUFF = hasFinishedMasterDegreeOnUFF
      this.save(graduate)
    }

    if (workDTO.cnpqLevelId != null) {
      val resultCNPQScholarship = cnpqScholarshipService.createOrUpdateCNPQScholarship(workDTO.cnpqLevelId, graduate)
      if (resultCNPQScholarship is ResponseResult.Error) return ResponseResult.Error(resultCNPQScholarship.errorReason)
    }

    val status: WorkHistoryStatus =
      if (workDTO.knownWorkPlace != null && !workDTO.knownWorkPlace) WorkHistoryStatus.UNKNOWN else Utils.getHistoryStatus(
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