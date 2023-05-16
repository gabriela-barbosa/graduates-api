package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.InstitutionDTO
import br.uff.graduatesapi.dto.CreateWorkHistoryDTO
import br.uff.graduatesapi.dto.toCreateCNPQScholarshipDTO
import br.uff.graduatesapi.dto.toDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.*
import br.uff.graduatesapi.repository.WorkHistoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class WorkHistoryService(
  private val workHistoryRepository: WorkHistoryRepository,
  private val cnpqScholarshipService: CNPQScholarshipService,
  private val institutionService: InstitutionService,
  private val institutionTypeService: InstitutionTypeService,
  private val graduateService: GraduateService,
  private val userService: UserService,
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

  fun getLastWorkHistoryByGraduate(graduateId: UUID): ResponseResult<CreateWorkHistoryDTO> {
    return try {
      val workHistory: WorkHistory = workHistoryRepository.findFirstByGraduateIdIsOrderByCreatedAtDesc(graduateId)
      val workHistoryDTO = getWorkHistoryDTO(workHistory.id)
      if (workHistoryDTO != null)
        return ResponseResult.Success(workHistoryDTO)
      ResponseResult.Error(Errors.CANT_GET_LAST_WORK_HISTORY)
    } catch (ex: Exception) {
      return ResponseResult.Error(Errors.LAST_WORK_HISTORY_NOT_FOUND)
    }
  }

  fun getWorkHistoryDTO(id: UUID): CreateWorkHistoryDTO? {
    try {
      val workHistory: WorkHistory = workHistoryRepository.findByIdOrNull(id) ?: return null
      val graduate: Graduate = workHistory.graduate
      val scholarships: List<CNPQScholarship> = cnpqScholarshipService.findActualCNPQScholarshipsByGraduate(graduate)

      return CreateWorkHistoryDTO(
        email = graduate.user.email,
        position = workHistory.position,
        startedAt = workHistory.startedAt,
        finishedAt = workHistory.finishedAt,
        institution = workHistory.institution.toDTO(),
        cnpqLevels = scholarships.map { it.toCreateCNPQScholarshipDTO() },
        hasFinishedDoctorateOnUFF = workHistory.graduate.hasFinishedDoctorateOnUFF,
        hasFinishedMasterDegreeOnUFF = workHistory.graduate.hasFinishedMasterDegreeOnUFF,
        successCase = graduate.successCase,
        postDoctorate = graduate.postDoctorate?.toDTO()
      )
    } catch (err: Exception) {
      throw err
    }
  }

  fun getWorkHistory(id: UUID): ResponseResult<WorkHistory> {
    val resp = workHistoryRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.WORK_HISTORY_NOT_FOUND)
    return ResponseResult.Success(resp)
  }


  fun updateWorkHistory(
    historyId: UUID,
    institutionDTO: InstitutionDTO,
    position: String?,
    startedAt: LocalDate,
    finishedAt: LocalDate?,
  ): ResponseResult<WorkHistory> {
    val oldWorkHistory = when (val result = getWorkHistory(historyId)) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)

    }
    if (oldWorkHistory.institution.name != institutionDTO.name || oldWorkHistory.institution.type.id != institutionDTO.type) {
      oldWorkHistory.institution =
        when (val result = institutionService.findByNameAndType(institutionDTO.name, institutionDTO.type)) {
          is ResponseResult.Success -> result.data!!
          is ResponseResult.Error -> {
            val institutionType =
              when (val resultInstitutionType = institutionTypeService.findById(institutionDTO.type)) {
                is ResponseResult.Success -> resultInstitutionType.data!!
                is ResponseResult.Error -> return ResponseResult.Error(resultInstitutionType.errorReason)
              }
            when (val resultCreateInstitution =
              institutionService.createInstitution(Institution(institutionDTO.name, institutionType))) {
              is ResponseResult.Success -> resultCreateInstitution.data!!
              is ResponseResult.Error -> return ResponseResult.Error(resultCreateInstitution.errorReason)
            }
          }
        }
    }
    if (position != null) oldWorkHistory.position = position
    oldWorkHistory.startedAt = startedAt
    oldWorkHistory.updatedAt = LocalDate.now()
    if (finishedAt != null) oldWorkHistory.finishedAt = finishedAt

    return save(oldWorkHistory)
  }

  fun createWorkHistory(
    graduate: Graduate,
    position: String?,
    institutionDTO: InstitutionDTO,
    startedAt: LocalDate
  ): ResponseResult<WorkHistory> {

    val institution = when (val result = institutionService.createInstitutionByInstitutionDTO(institutionDTO)) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
    }
    val hw = WorkHistory(
      position = position,
      graduate = graduate,
      institution = institution,
      startedAt = startedAt,
      updatedAt = LocalDate.now()
    )

    return this.save(hw)
  }

  fun updateGraduateHistory(workDTO: CreateWorkHistoryDTO, historyId: UUID): ResponseResult<UUID> {
    val resultCreateWH =
      this.updateWorkHistory(historyId, workDTO.institution, workDTO.position, workDTO.startedAt, workDTO.finishedAt)

    if (resultCreateWH is ResponseResult.Error) return ResponseResult.Error(resultCreateWH.errorReason)

    val workHistory = when (val result = getWorkHistory(historyId)) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> {
        if (result.errorReason == Errors.WORK_HISTORY_NOT_FOUND)
          return ResponseResult.Error(Errors.INVALID_DATA)
        return ResponseResult.Error(result.errorReason)
      }
    }

    val graduate = workHistory.graduate

    if (workDTO.email != graduate.user.email) {
      val userResult = userService.updateEmail(graduate.user.id, workDTO.email)
      if (userResult is ResponseResult.Error) {
        return ResponseResult.Error(userResult.errorReason)
      }
    }

    val result = graduateService.updateGraduatePostGraduationInfo(
      graduate,
      workDTO.postDoctorate,
      workDTO.hasFinishedDoctorateOnUFF,
      workDTO.hasFinishedMasterDegreeOnUFF,
    )
    if (result is ResponseResult.Error) return ResponseResult.Error(result.errorReason)


  }

  fun createGraduateHistory(workDTO: CreateWorkHistoryDTO, graduateId: UUID): ResponseResult<UUID> {

    val graduate = when (val result = graduateService.getGraduateById(graduateId)) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> {
        if (result.errorReason == Errors.USER_NOT_FOUND)
          return ResponseResult.Error(Errors.INVALID_DATA)
        return ResponseResult.Error(result.errorReason)
      }
    }

    if (workDTO.email != graduate.user.email) {
      val userResult = userService.updateEmail(graduate.user.id, workDTO.email)
      if (userResult is ResponseResult.Error) {
        return ResponseResult.Error(userResult.errorReason)
      }
    }

    val resultCreateWH = this.createWorkHistory(graduate, workDTO.position, workDTO.institution, workDTO.startedAt)
    if (resultCreateWH is ResponseResult.Error) return ResponseResult.Error(resultCreateWH.errorReason)

    val result = graduateService.updateGraduatePostGraduationInfo(
      graduate,
      workDTO.postDoctorate,
      workDTO.hasFinishedDoctorateOnUFF,
      workDTO.hasFinishedMasterDegreeOnUFF,
    )

    if (result is ResponseResult.Error) return ResponseResult.Error(result.errorReason)

    if (workDTO.cnpqLevels.isNotEmpty()) {
      val resultCNPQScholarship =
        cnpqScholarshipService.createCNPQScholarships(workDTO.cnpqLevels, graduate)
      if (resultCNPQScholarship is ResponseResult.Error) return ResponseResult.Error(resultCNPQScholarship.errorReason)
    }

    return ResponseResult.Success(graduate.id)
  }
}