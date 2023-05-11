package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.*
import br.uff.graduatesapi.entity.GraduateFilters
import br.uff.graduatesapi.entity.OffsetLimit
import br.uff.graduatesapi.enum.Role
import br.uff.graduatesapi.enum.WorkHistoryStatus
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
import java.util.*
import javax.persistence.criteria.JoinType

@Service
class GraduateService(
  private val userService: UserService,
  private val graduateRepository: GraduateRepository,
  private val institutionService: InstitutionService,
  private val queryFactory: SpringDataQueryFactory,
) {
  fun getTotal(filters: GraduateFilters, pageSettings: OffsetLimit): Long {
    return queryFactory.singleQuery {
      select(count(column(Graduate::id)))
      from(entity(Graduate::class))
      join(Graduate::user)
      join(Graduate::currentWorkHistories, JoinType.LEFT)
      join(Graduate::historyStatus, JoinType.LEFT)
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
      join(Graduate::currentWorkHistories, JoinType.LEFT)
      join(Graduate::historyStatus, JoinType.LEFT)
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
      orderBy(column(HistoryStatus::status).desc())
      limit(
        pageSettings.offset,
        pageSettings.limit,
      )
    }
  }

  fun getGraduateById(id: UUID): ResponseResult<Graduate> {
    val result = graduateRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.GRADUATE_NOT_FOUND)
    return ResponseResult.Success(result)
  }

  fun getAllGraduates(page: PageRequest): ResponseResult<Page<Graduate>> = try {
    val graduates = graduateRepository.findAllByOrderByCurrentWorkHistoryStatusDesc(page)
    ResponseResult.Success(graduates)
  } catch (ex: Exception) {
    ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
  }

  fun getGraduatePaged(filters: GraduateFilters, page: OffsetLimit) =
    try {
      val total = getTotal(filters, page)
      val grad = getGraduates(filters, page)
      val metaList = MetaDTO(page.page, grad.count(), total)
      ResponseResult.Success(Pair(grad, metaList))
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
    }


  fun getGraduatesByAdvisor(
    jwt: String,
    currentRole: Role,
    page: OffsetLimit,
    filters: GraduateFilters
  ): ResponseResult<ListGraduatesDTO> {
    val user: PlatformUser

    when (val result = userService.getUserByJwt(jwt)) {
      is ResponseResult.Success -> user = result.data!!
      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
    }

    val graduatesDTOList: ListGraduatesDTO
    val dataList = mutableListOf<DataListGraduatesDTO>()
    val metaList: MetaDTO
    val graduates: List<Graduate>

    val role = user.roles.find { it == currentRole }

    if (role == Role.GRADUATE) return ResponseResult.Error(Errors.USER_NOT_ALLOWED)
    if (role == Role.PROFESSOR) filters.advisor = user.advisor

    when (val result = getGraduatePaged(filters, page)) {
      is ResponseResult.Success -> {
        val (grad, meta) = result.data!!
        graduates = grad
        metaList = meta
      }

      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
    }

    for (graduate in graduates) {
      val latestWorkHistory = graduate.currentWorkHistories
      var status = WorkHistoryStatus.PENDING

      val workHistory =
        if (latestWorkHistory != null) {
          status = latestWorkHistory.status
          WorkHistoryInfoDTO(
            latestWorkHistory.id,
            latestWorkHistory.institution?.name,
            latestWorkHistory.institution?.type?.name
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
    dataList.sortBy { it.status.value }
    graduatesDTOList = ListGraduatesDTO(dataList, metaList)
    return ResponseResult.Success(graduatesDTOList)
  }

  fun getGraduatesByWorkHistory(institutionId: UUID): ResponseResult<List<Graduate>> {
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
    graduate.updatedAt = LocalDate.now()
    return this.save(graduate)
  }
}