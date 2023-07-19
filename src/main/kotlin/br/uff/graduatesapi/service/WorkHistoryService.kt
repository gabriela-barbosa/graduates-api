package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.*
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.model.WorkHistory
import br.uff.graduatesapi.repository.WorkHistoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class WorkHistoryService(
  private val workHistoryRepository: WorkHistoryRepository,
  private val cnpqScholarshipService: CNPQScholarshipService,
  private val institutionService: InstitutionService,
  private val institutionTypeService: InstitutionTypeService,
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

  fun getWorkHistoriesByGraduate(userId: UUID): ResponseResult<GraduateWorkHistoriesDTO> {
    return try {
      val workHistories: List<WorkHistory> =
        workHistoryRepository.findAllByGraduateUserIdIsOrderByEndedAtDesc(userId)

      val graduate = if (workHistories.isEmpty()) {
        when (val result = userService.getById(userId)) {
          is ResponseResult.Success -> result.data!!.graduate
          is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
        }
      } else workHistories[0].graduate

      if (graduate == null)
        return ResponseResult.Error(Errors.CANT_GET_CURRENT_HISTORIES)

      ResponseResult.Success(graduate.toGraduateWorkHistoriesDTO(workHistories))
    } catch (ex: Exception) {
      return ResponseResult.Error(Errors.CANT_GET_CURRENT_HISTORIES)
    }
  }

  fun getWorkHistory(id: UUID): ResponseResult<WorkHistory> {
    val resp = workHistoryRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.WORK_HISTORY_NOT_FOUND)
    return ResponseResult.Success(resp)
  }


  fun updateWorkHistory(
    id: UUID,
    institutionDTO: CreateInstitutionDTO,
    position: String?,
    startedAt: String,
    endedAt: String?,
  ): ResponseResult<WorkHistory> {
    val oldWorkHistory = when (val result = getWorkHistory(id)) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)

    }
    if (oldWorkHistory.institution.name != institutionDTO.name || oldWorkHistory.institution.type.id != institutionDTO.typeId) {
      oldWorkHistory.institution =
        when (val result = institutionService.findByNameAndType(institutionDTO.name, institutionDTO.typeId)) {
          is ResponseResult.Success -> result.data!!
          is ResponseResult.Error -> {
            val institutionType =
              when (val resultInstitutionType = institutionTypeService.findById(institutionDTO.typeId)) {
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
    oldWorkHistory.startedAt = Utils.parseUTCToLocalDateTime(startedAt)
    oldWorkHistory.updatedAt = LocalDateTime.now()
    endedAt?.let { oldWorkHistory.endedAt = Utils.parseUTCToLocalDateTime(it) }
    return save(oldWorkHistory)
  }

  fun createWorkHistory(
    graduate: Graduate,
    position: String?,
    institutionDTO: CreateInstitutionDTO,
    startedAt: String,
    endedAt: String?
  ): ResponseResult<WorkHistory> {

    val institution = when (val result = institutionService.createInstitutionByInstitutionDTO(institutionDTO)) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
    }

    val newStartedAt = Utils.parseUTCToLocalDateTime(startedAt)
    val newEndedAt = endedAt?.let { Utils.parseUTCToLocalDateTime(it) }

    val hw = WorkHistory(
      position = position,
      graduate = graduate,
      institution = institution,
      startedAt = newStartedAt,
      endedAt = newEndedAt,
      updatedAt = null
    )

    return this.save(hw)
  }

  fun createWorkHistories(
    graduate: Graduate,
    workHistoriesDTO: List<CreateWorkHistoryDTO>
  ): ResponseResult<List<WorkHistory>> {

    val workHistories = workHistoriesDTO.map {
      when (val result =
        if (it.id != null)
          this.updateWorkHistory(it.id!!, it.institution, it.position, it.startedAt, it.endedAt)
        else
          this.createWorkHistory(graduate, it.position, it.institution, it.startedAt, it.endedAt)) {
        is ResponseResult.Success -> result.data!!
        is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
      }
    }

    return ResponseResult.Success(workHistories)
  }


//  fun updateGraduateHistory(workDTO: CreateWorkHistoriesDTO, historyId: UUID): ResponseResult<UUID> {
//
//    val workHistory = when (val result =
//      this.updateWorkHistory(historyId, workDTO.institution, workDTO.position, workDTO.startedAt, workDTO.endedAt)) {
//      is ResponseResult.Success -> result.data!!
//      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
//    }
//
//    val graduate = workHistory.graduate
//
//    if (workDTO.email != graduate.user.email) {
//      val userResult = userService.updateEmail(graduate.user.id, workDTO.email)
//      if (userResult is ResponseResult.Error) {
//        return ResponseResult.Error(userResult.errorReason)
//      }
//    }
//
//    val result = graduateService.updateGraduateInfo(
//      graduate,
//      workDTO.postDoctorates,
//      workDTO.hasFinishedDoctorateOnUFF,
//      workDTO.hasFinishedMasterDegreeOnUFF,
//      workDTO.successCase,
//    )
//    if (result is ResponseResult.Error) return ResponseResult.Error(result.errorReason)
//
//    return when (val response = this.save(workHistory)) {
//      is ResponseResult.Success -> ResponseResult.Success(response.data!!.id)
//      is ResponseResult.Error -> ResponseResult.Error(response.errorReason)
//    }
//  }

  fun createGraduateHistories(worksDTO: CreateWorkHistoriesDTO, graduateId: UUID): ResponseResult<UUID> {

    val graduate = when (val result = graduateService.getGraduateById(graduateId)) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> {
        if (result.errorReason == Errors.USER_NOT_FOUND)
          return ResponseResult.Error(Errors.INVALID_DATA)
        return ResponseResult.Error(result.errorReason)
      }
    }

    if (worksDTO.email != graduate.user.email || worksDTO.graduateName != graduate.user.name) {
      val userResult = userService.updateUserEmailAndName(graduate.user, worksDTO.email, worksDTO.graduateName)
      if (userResult is ResponseResult.Error) {
        return ResponseResult.Error(userResult.errorReason)
      }
    }

    if (worksDTO.workHistories != null) {
      when (val result = createWorkHistories(graduate, worksDTO.workHistories)) {
        is ResponseResult.Success -> result.data!!
        is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
      }
    }


    if (worksDTO.cnpqScholarships != null) {
      when (val result = cnpqScholarshipService.createCNPQScholarships(graduate, worksDTO.cnpqScholarships)) {
        is ResponseResult.Success -> result.data!!
        is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
      }
    }

    val graduateUpdated = when (val result = graduateService.updateGraduateInfo(
      graduate,
      worksDTO.postDoctorate,
      worksDTO.hasFinishedDoctorateOnUFF,
      worksDTO.hasFinishedMasterDegreeOnUFF,
      worksDTO.successCase,
    )) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
    }
    historyStatusService.upsertHistoryStatusByGraduate(
      graduate = graduateUpdated,
      hasCurrentWorkHistory = worksDTO.hasCurrentWorkHistory,
      hasCurrentCNPQScholarship = worksDTO.hasCurrentCNPQScholarship,
      hasPostDoctorate = worksDTO.hasPostDoctorate,
    )

    return ResponseResult.Success(graduate.id)
  }
}