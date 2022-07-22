package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.InstitutionDTO
import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.CNPQScholarship
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.model.WorkHistory
import br.uff.graduatesapi.repository.WorkHistoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class WorkHistoryService(
  private val workHistoryRepository: WorkHistoryRepository,
  private val cnpqScholarshipService: CNPQScholarshipService,
  private val institutionService: InstitutionService,
  private val graduateService: GraduateService,
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

  fun getLastWorkHistoryByGraduate(graduateId: Int): WorkHistoryDTO? {
    return try {
      val workHistory: WorkHistory = workHistoryRepository.findFirstByGraduateIdIsOrderByCreatedAtDesc(graduateId)
      val workHistoryDTO = getWorkHistoryDTO(workHistory.id!!)
      workHistoryDTO
    } catch (ex: Exception) {
      null
    }
  }

  fun getWorkHistoryDTO(id: Int): WorkHistoryDTO? {
    val workHistory: WorkHistory = workHistoryRepository.findByIdOrNull(id) ?: return null
    val graduate: Graduate = workHistory.graduate
    val cnpq: CNPQScholarship? = cnpqScholarshipService.findActualCNPQScholarshipByGraduate(graduate)
    val cnpqScholarship = if (cnpq != null) cnpq.level!!.id else null
    val workHistoryDTO = WorkHistoryDTO(
      email = workHistory.graduate.user.email,
      position = workHistory.position,
      cnpqLevelId = cnpqScholarship,
      hasFinishedDoctorateOnUFF = workHistory.graduate.hasFinishedDoctorateOnUFF,
      hasFinishedMasterDegreeOnUFF = workHistory.graduate.hasFinishedMasterDegreeOnUFF,
      knownWorkPlace = graduate.historyStatus!!.knownWorkplace,
    )
    if (workHistory.institution != null)
      workHistoryDTO.addInstitutionInfo(workHistory.institution!!)
    if (graduate.postDoctorate != null)
      workHistoryDTO.addPostDoctorate(graduate.postDoctorate!!)
    return workHistoryDTO
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
      hw.updatedAt = Date(System.currentTimeMillis())
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
}