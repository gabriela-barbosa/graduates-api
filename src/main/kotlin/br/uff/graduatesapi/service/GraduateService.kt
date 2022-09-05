package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.*
import br.uff.graduatesapi.enums.Role
import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.repository.GraduateRepository
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GraduateService(
  private val userService: UserService,
  private val graduateRepository: GraduateRepository,
  private val institutionService: InstitutionService,
) {

  fun getGraduateById(id: Int): ResponseResult<GraduateDTO> {
    val result = graduateRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.GRADUATE_NOT_FOUND)
    val graduateDTO = GraduateDTO(result.id, result.user)
    return ResponseResult.Success(graduateDTO)
  }

  fun getAllGraduates(page: PageRequest): ResponseResult<Page<Graduate>> = try {
    val graduates = graduateRepository.findAllByOrderByLatestWorkHistoryStatusDesc(page)
    ResponseResult.Success(graduates)
  } catch (ex: Exception) {
    ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
  }

  fun getGraduatesByAdvisor(jwt: String, page: PageRequest): ResponseResult<ListGraduatesDTO> {
    val result = userService.getUserByJwt(jwt)
    if (result is ResponseResult.Error)
      return ResponseResult.Error(result.errorReason)
    val user = result.data!!
    val graduatesDTOList: ListGraduatesDTO
    val dataList = mutableListOf<DataListGraduatesDTO>()
    var metaList = MetaListGraduatesDTO(0, 0, 0)
    val graduates: List<Graduate>
    when (user.role) {
      Role.PROFESSOR -> {
        graduates =
          user.advisor?.let {
            val resp = graduateRepository.findAllByCoursesAdvisorIsOrderByHistoryStatusDesc(it, page)
            metaList = MetaListGraduatesDTO(resp.number, resp.size, resp.totalElements)
            resp.content
          }
            ?: return ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
      }
      Role.ADMIN -> {
        val result = this.getAllGraduates(page)
        if (result is ResponseResult.Success) {
          val resp = result.data!!
          metaList = MetaListGraduatesDTO(resp.number, resp.size, resp.totalElements)
          graduates = resp.content
        } else
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

      val graduateDTO = DataListGraduatesDTO(
        id = graduate.id,
        name = graduate.user.name,
        email = graduate.user.email,
        status = status,
        workPlace = workHistory,
        position = latestWorkHistory?.position
      )

      dataList.add(graduateDTO)
    }
    graduatesDTOList = ListGraduatesDTO(dataList, metaList)
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