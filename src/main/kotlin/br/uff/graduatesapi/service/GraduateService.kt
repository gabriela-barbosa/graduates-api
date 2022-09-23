package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.*
import br.uff.graduatesapi.enums.Role
import br.uff.graduatesapi.enums.WorkHistoryStatus
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.*
import br.uff.graduatesapi.repository.GraduateRepository
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.join
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.persistence.criteria.JoinType

@Service
class GraduateService(
  private val userService: UserService,
  private val graduateRepository: GraduateRepository,
  private val institutionService: InstitutionService,
  private val queryFactory: SpringDataQueryFactory,

  ) {


  fun getCount(filters: GraduateFilters, pageSettings: OffsetLimit): Long {
    return queryFactory.singleQuery {
      select(count(column(Graduate::id)))
      from(entity(Graduate::class))
      join(Graduate::user)
      join(Graduate::currentWorkHistory, JoinType.LEFT)
      join(Graduate::courses, JoinType.LEFT)
      join(Course::advisor, JoinType.LEFT)
      join(WorkHistory::institution, JoinType.LEFT)
      join(Institution::type, JoinType.LEFT)
      where(
        and(
          filters.name?.run { column(PlatformUser::name).like("%${this}%") },
          filters.institutionType?.run { column(InstitutionType::id).equal(this) },
          filters.institutionName?.run { column(Institution::name).like("%${this}%") },
          filters.advisor?.run { column(Advisor::id).equal(this.id) },
        )
      )
    }
  }

  fun getGraduates(filters: GraduateFilters, pageSettings: OffsetLimit): List<Graduate> {
    return queryFactory.listQuery {
      select(entity(Graduate::class))
      from(entity(Graduate::class))
      join(Graduate::user)
      join(Graduate::currentWorkHistory, JoinType.LEFT)
      join(Graduate::courses, JoinType.LEFT)
      join(Course::advisor, JoinType.LEFT)
      join(WorkHistory::institution, JoinType.LEFT)
      join(Institution::type, JoinType.LEFT)
      where(
        and(
          filters.name?.run { column(PlatformUser::name).like("%${this}%") },
          filters.institutionType?.run { column(InstitutionType::id).equal(this) },
          filters.institutionName?.run { column(Institution::name).like("%${this}%") },
          filters.advisor?.run { column(Advisor::id).equal(this.id) },
        )
      )
      orderBy(column(WorkHistory::status).desc())
      limit(
        pageSettings.offset,
        pageSettings.limit,
      )
    }
  }

  fun getGraduateById(id: Int): ResponseResult<GraduateDTO> {
    val result = graduateRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.GRADUATE_NOT_FOUND)
    val graduateDTO = GraduateDTO(result.id, result.user)
    return ResponseResult.Success(graduateDTO)
  }

  fun getAllGraduates(page: PageRequest): ResponseResult<Page<Graduate>> = try {
    val graduates = graduateRepository.findAllByOrderByCurrentWorkHistoryStatusDesc(page)
    ResponseResult.Success(graduates)
  } catch (ex: Exception) {
    ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
  }

  fun getGraduatesByAdvisor(
    jwt: String,
    page: OffsetLimit,
    filters: GraduateFilters
  ): ResponseResult<ListGraduatesDTO> {
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
            filters.advisor = it
            try {
              val count = getCount(filters, OffsetLimit())
              val grad = getGraduates(filters, page)
              metaList = MetaListGraduatesDTO(page.page, grad.count(), count)
              grad
            } catch (ex: Exception) {
              return ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
            }
//            val resp = graduateRepository.findAllByCoursesAdvisorIsOrderByHistoryStatusDesc(it, page)
          }
            ?: return ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
      }
      Role.ADMIN -> {
        try {
          val count = getCount(filters, OffsetLimit())
          val grad = getGraduates(filters, page)
          metaList = MetaListGraduatesDTO(page.page, grad.count(), count)
          graduates = grad
        } catch (ex: Exception) {
          return ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
        }
//        val result = this.getAllGraduates(page)
//        if (result is ResponseResult.Success) {
//          val resp = result.data!!
//        } else
//          return ResponseResult.Error(result.errorReason)
      }
      else -> {
        return ResponseResult.Error(Errors.USER_NOT_ALLOWED)
      }
    }

    for (graduate in graduates) {
      val latestWorkHistory = graduate.currentWorkHistory
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