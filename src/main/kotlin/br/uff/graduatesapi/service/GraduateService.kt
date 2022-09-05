package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.GraduateDTO
import br.uff.graduatesapi.dto.InstitutionDTO
import br.uff.graduatesapi.dto.ListGraduatesDTO
import br.uff.graduatesapi.dto.WorkHistoryInfoDTO
import br.uff.graduatesapi.enums.Role
import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.repository.GraduateRepository
import org.springframework.context.annotation.Lazy
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GraduateService(
  private val userService: UserService,
  @Lazy
  private val workHistoryService: WorkHistoryService,
  private val graduateRepository: GraduateRepository,
  private val institutionService: InstitutionService,
) {

  fun getGraduateById(id: Int): ResponseResult<GraduateDTO> {
    val result = graduateRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.GRADUATE_NOT_FOUND)
    val graduateDTO = GraduateDTO(result.id, result.user)
    return ResponseResult.Success(graduateDTO)
  }

  fun getAllGraduates(): ResponseResult<List<Graduate>> = try {
    val graduates = graduateRepository.findAllByOrderByLatestWorkHistoryStatusDesc()
    ResponseResult.Success(graduates)
  } catch (ex: Exception) {
    ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
  }
//  fun getAllGraduates(): ResponseResult<List<ListGraduatesDTO>> {
//    return try {
//      val graduates = graduateRepository.findAllByOrderByUserNameAsc()
//      val knownHistoryGraduates = mutableListOf<Graduate>()
//      val graduatesList = mutableListOf<ListGraduatesDTO>()
//
//      for (graduate in graduates) {
//        var status = WorkHistoryStatus.PENDING
//        if (graduate.historyStatus != null) {
//          if (graduate.historyStatus!!.knownWorkplace!!) {
//            knownHistoryGraduates.add(graduate)
//          }
//          status = graduate.historyStatus!!.status
//        }
//        val item = ListGraduatesDTO(
//          name = graduate.user.name,
//          email = graduate.user.email,
//          id = graduate.id,
//          status = status,
//          workPlace = null,
//          position = null
//        )
//        graduatesList.add(item)
//      }
//      val workHistory = workHistoryService.findAllByGraduates(knownHistoryGraduates)
//        ?: return ResponseResult.Error(Errors.WORK_HISTORIES_NOT_FOUND)
//      for (graduate in knownHistoryGraduates) {
//        val singleWorkHistory = workHistory.find { history -> history.graduate.id == graduate.id }
//        val graduateResp = graduatesList.find { r -> r.name == graduate.user.name }
//        if (graduateResp != null && singleWorkHistory != null) {
//          graduateResp.position = singleWorkHistory.position
//          val workHistoryInfoDTO = WorkHistoryInfoDTO(
//            id = singleWorkHistory.id!!,
//            name = singleWorkHistory.institution!!.name,
//            type = singleWorkHistory.institution!!.type!!.id!!
//          )
//          graduateResp.workPlace = workHistoryInfoDTO
//        }
//
//      }
//      ResponseResult.Success(graduatesList)
//    } catch (ex: Exception) {
//      ResponseResult.Error(Errors.INVALID_DATA)
//    }
//  }

  fun getGraduatesByAdvisor(jwt: String): ResponseResult<List<ListGraduatesDTO>> {
    val result = userService.getUserByJwt(jwt)
    if (result is ResponseResult.Error)
      return ResponseResult.Error(result.errorReason)
    val user = result.data!!
    val graduatesDTOList = mutableListOf<ListGraduatesDTO>()
    val graduates: List<Graduate>
    when (user.role) {
      Role.PROFESSOR -> {
        graduates =
          user.advisor?.let { graduateRepository.findAllByCoursesAdvisorIsOrderByHistoryStatusDesc(it).content }
            ?: return ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
      }
      Role.ADMIN -> {
        val result = this.getAllGraduates()
        if (result is ResponseResult.Success)
          graduates = result.data!!
        else
          return ResponseResult.Error(result.errorReason)
      }
      else -> {
        return ResponseResult.Error(Errors.USER_NOT_ALLOWED)
      }
    }

    for (graduate in graduates) {
      val latestWorkHistory = graduate.latestWorkHistory
      var status = WorkHistoryStatus.PENDING

      val workHistory = if (latestWorkHistory != null && latestWorkHistory.createdAt!!.year == LocalDate.now().year) {
        status = latestWorkHistory.status
        WorkHistoryInfoDTO(
          latestWorkHistory.id!!,
          latestWorkHistory.institution?.name,
          latestWorkHistory.institution?.type?.id
        )
      } else null

      val graduateDTO = ListGraduatesDTO(
        id = graduate.id,
        name = graduate.user.name,
        email = graduate.user.email,
        status = status,
        workPlace = workHistory,
        position = latestWorkHistory?.position
      )

      graduatesDTOList.add(graduateDTO)
    }
    return ResponseResult.Success(graduatesDTOList)
  }

  fun getGraduatesByWorkHistory(institutionId: Int): ResponseResult<List<Graduate>> {
    return try {
      val graduates = graduateRepository.findAllByWorkHistoriesInstitutionId(institutionId)
      ResponseResult.Success(graduates)
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
    }
  }

  fun save(graduate: Graduate): ResponseResult<Graduate> {
    return try {
      val savedGraduate = graduateRepository.save(graduate)
      ResponseResult.Success(savedGraduate)
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.CANT_CREATE_GRADUATE)
    }
  }

  fun updatePostDoctorate(postDoctorate: InstitutionDTO, graduate: Graduate): ResponseResult<Graduate> {
    val resultInstitution = institutionService.createInstitutionByInstitutionDTO(postDoctorate)
    if (resultInstitution is ResponseResult.Error) return ResponseResult.Error(resultInstitution.errorReason)
    graduate.postDoctorate = resultInstitution.data
    val resultUpdate = this.save(graduate)
    if (resultUpdate is ResponseResult.Error)
      return ResponseResult.Error(resultUpdate.errorReason)
    return resultUpdate
  }

  fun updateGraduatePostGraduationInfo(
    graduate: Graduate,
    institutionPostDoctorateDTO: InstitutionDTO?,
    hasFinishedDoctorateOnUFF: Boolean?,
    hasFinishedMasterDegreeOnUFF: Boolean?,
  ): ResponseResult<Graduate> {
    if (institutionPostDoctorateDTO != null) {
      val result = this.updatePostDoctorate(institutionPostDoctorateDTO, graduate)
      if (result is ResponseResult.Error)
        return ResponseResult.Error(result.errorReason)
      graduate.postDoctorate = result.data!!.postDoctorate
    }
    if (hasFinishedDoctorateOnUFF != null) {
      graduate.hasFinishedDoctorateOnUFF = hasFinishedDoctorateOnUFF
    }
    if (hasFinishedMasterDegreeOnUFF != null) {
      graduate.hasFinishedMasterDegreeOnUFF = hasFinishedMasterDegreeOnUFF
    }
    return this.save(graduate)
  }
}