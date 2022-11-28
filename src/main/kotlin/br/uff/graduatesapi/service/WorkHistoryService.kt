package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.InstitutionDTO
import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.*
import br.uff.graduatesapi.repository.WorkHistoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class WorkHistoryService(
  private val workHistoryRepository: WorkHistoryRepository,
  private val cnpqScholarshipService: CNPQScholarshipService,
  private val institutionService: InstitutionService,
  private val graduateService: GraduateService,
  private val userService: UserService,
  private val historyStatusService: HistoryStatusService,
) {
  fun findAllByGraduates(graduates: List<Graduate>): List<WorkHistory>? {
    return workHistoryRepository.findTopByGraduateOrderByCreatedAtDesc(graduates)
  }

  fun save(workHistory: WorkHistory): ResponseResult<WorkHistory> {
    return try {
      val respWH = workHistoryRepository.save(workHistory)
      ResponseResult.Success(respWH)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_CREATE_WORK_HISTORY)
    }
  }

  fun getLastWorkHistoryByGraduate(graduateId: Int): ResponseResult<WorkHistoryDTO> {
    return try {
      val workHistory: WorkHistory = workHistoryRepository.findFirstByGraduateIdIsOrderByCreatedAtDesc(graduateId)
      val workHistoryDTO = getWorkHistoryDTO(workHistory.id!!)
      if (workHistoryDTO != null)
        return ResponseResult.Success(workHistoryDTO)
      ResponseResult.Error(Errors.CANT_GET_LAST_WORK_HISTORY)
    } catch (ex: Exception) {
      return ResponseResult.Error(Errors.LAST_WORK_HISTORY_NOT_FOUND)
    }
  }

  fun getWorkHistoryDTO(id: Int): WorkHistoryDTO? {
    try {
      val workHistory: WorkHistory = workHistoryRepository.findByIdOrNull(id) ?: return null
      val graduate: Graduate = workHistory.graduate
      val cnpq: CNPQScholarship? = cnpqScholarshipService.findActualCNPQScholarshipByGraduate(graduate)
      val cnpqScholarship = if (cnpq != null) cnpq.level!!.id else null
      val workHistoryDTO = WorkHistoryDTO(
        email = graduate.user.email,
        position = workHistory.position,
        cnpqLevelId = cnpqScholarship,
        hasFinishedDoctorateOnUFF = workHistory.graduate.hasFinishedDoctorateOnUFF,
        hasFinishedMasterDegreeOnUFF = workHistory.graduate.hasFinishedMasterDegreeOnUFF,
        knownWorkPlace = workHistory.status === WorkHistoryStatus.UNKNOWN,
      )
      if (workHistory.institution != null)
        workHistoryDTO.addInstitutionInfo(workHistory.institution!!)
      if (graduate.postDoctorate != null)
        workHistoryDTO.addPostDoctorate(graduate.postDoctorate!!)
      return workHistoryDTO
    } catch (err: Exception) {
      throw err
    }

  }

  fun getWorkHistory(id: Int): ResponseResult<WorkHistory> {
    val resp = workHistoryRepository.findByIdOrNull(1) ?: return ResponseResult.Error(Errors.WORK_HISTORY_NOT_FOUND)
    return ResponseResult.Success(resp)
  }


  fun updateWorkHistory(
    institutionDTO: InstitutionDTO?,
    position: String?,
    oldWorkHistory: WorkHistory
  ): ResponseResult<WorkHistory> {
    val newInstitution: Institution? = null

    if ((institutionDTO == null && oldWorkHistory.institution != null) || (institutionDTO != null && oldWorkHistory.institution != null)) {
      val oldWHInstitutionId = oldWorkHistory.institution!!.id
      val result = graduateService.getGraduatesByWorkHistory(oldWHInstitutionId)
      if (result is ResponseResult.Error)
        return ResponseResult.Error(result.errorReason)
      val graduatesWithOldWorkHistory = result.data!!
      if (graduatesWithOldWorkHistory.isEmpty()) {
        val resultDelete = institutionService.deleteInstitution(oldWHInstitutionId)
        if (resultDelete is ResponseResult.Error)
          return ResponseResult.Error(resultDelete.errorReason)
      }
    }
    if (institutionDTO != null) {
      institutionService.createInstitutionByInstitutionDTO(institutionDTO)
    }
    oldWorkHistory.institution = newInstitution
    oldWorkHistory.position = position
    val resultSave = this.save(oldWorkHistory)
    if (resultSave is ResponseResult.Error) {
      return ResponseResult.Error(resultSave.errorReason)
    }
    return ResponseResult.Success(resultSave.data!!)
  }

  fun createWorkHistory(
    graduate: Graduate,
    position: String?,
    institutionDTO: InstitutionDTO?
  ): ResponseResult<WorkHistory> {
    val hw = WorkHistory(
      position = position,
      graduate = graduate
    )
    if (institutionDTO != null) {
      val resultInst = institutionService.createInstitutionByInstitutionDTO(institutionDTO)
      if (resultInst is ResponseResult.Error) return ResponseResult.Error(resultInst.errorReason)
      hw.institution = resultInst.data
      hw.updatedAt = LocalDate.now()
    }

    val resultSave = this.save(hw)
    if (resultSave is ResponseResult.Error) {
      return ResponseResult.Error(resultSave.errorReason)
    }
    return ResponseResult.Success(resultSave.data!!)
  }

  fun createOrUpdateWorkHistory(
    id: Int?,
    graduate: Graduate,
    position: String?,
    institutionDTO: InstitutionDTO?
  ): ResponseResult<WorkHistory> {
    if (id != null) {
      val respHistory = this.getWorkHistory(id)
      if (respHistory is ResponseResult.Error)
        return ResponseResult.Error(respHistory.errorReason)
      return updateWorkHistory(institutionDTO, position, respHistory.data!!)
    } else {
      return createWorkHistory(graduate, position, institutionDTO)
    }
  }

  fun createGraduateHistory(workDTO: WorkHistoryDTO, id: Int? = null): ResponseResult<Int> {
    val respUser = userService.findByEmail(workDTO.email)
    if (respUser is ResponseResult.Error) {
      if (respUser.errorReason == Errors.USER_NOT_FOUND)
        return ResponseResult.Error(Errors.INVALID_DATA)
      return ResponseResult.Error(respUser.errorReason)
    }

    val graduate = respUser.data!!.graduate!!

    if (!workDTO.newEmail.isNullOrBlank()) {
      val userResult = userService.updateEmail(workDTO.email, workDTO.newEmail)
      if (userResult is ResponseResult.Error) {
        return ResponseResult.Error(userResult.errorReason)
      }
    }

    val resultHistory =
      this.createOrUpdateWorkHistory(id, graduate, workDTO.position, workDTO.institution)
    if (resultHistory is ResponseResult.Error) return ResponseResult.Error(resultHistory.errorReason)

    val institutionPostDoctorateDTO = workDTO.postDoctorate
    val hasFinishedDoctorateOnUFF = workDTO.hasFinishedDoctorateOnUFF
    val hasFinishedMasterDegreeOnUFF = workDTO.hasFinishedMasterDegreeOnUFF

    val resultUpdateGraduatePostGraduationInfo = graduateService.updateGraduatePostGraduationInfo(
      graduate,
      institutionPostDoctorateDTO,
      hasFinishedDoctorateOnUFF,
      hasFinishedMasterDegreeOnUFF,
    )
    if (resultUpdateGraduatePostGraduationInfo is ResponseResult.Error)
      return ResponseResult.Error(resultUpdateGraduatePostGraduationInfo.errorReason)

    val graduateUpdated = resultUpdateGraduatePostGraduationInfo.data!!
    val postDoctorate = graduateUpdated.postDoctorate
    val finishedDoctorateOnUFF = graduateUpdated.hasFinishedDoctorateOnUFF
    val finishedMasterDegreeOnUFF = graduateUpdated.hasFinishedMasterDegreeOnUFF

    if (workDTO.cnpqLevelId != null) {
      val resultCNPQScholarship = cnpqScholarshipService.createOrUpdateCNPQScholarship(workDTO.cnpqLevelId, graduate)
      if (resultCNPQScholarship is ResponseResult.Error) return ResponseResult.Error(resultCNPQScholarship.errorReason)
    }

    val status: WorkHistoryStatus =
      if (workDTO.knownWorkPlace == false) WorkHistoryStatus.UNKNOWN else Utils.getHistoryStatus(
        resultHistory.data,
        postDoctorate,
        finishedDoctorateOnUFF,
        finishedMasterDegreeOnUFF,
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