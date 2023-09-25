package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CreatePostDoctorateDTO
import br.uff.graduatesapi.dto.ListGraduatesDTO
import br.uff.graduatesapi.dto.MetaDTO
import br.uff.graduatesapi.dto.toGraduateItemDTO
import br.uff.graduatesapi.entity.GraduateFilters
import br.uff.graduatesapi.entity.OffsetLimit
import br.uff.graduatesapi.enum.HistoryStatusEnum
import br.uff.graduatesapi.enum.Role
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.*
import br.uff.graduatesapi.repository.BaseRepository
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
import java.time.LocalDateTime
import java.util.*
import javax.persistence.criteria.Join
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Predicate


@Service
class GraduateService(
  private val userService: UserService,
  private val graduateRepository: GraduateRepository,
  private val postDoctorateService: PostDoctorateService,
  private val queryFactory: SpringDataQueryFactory,
): BaseRepository<Graduate>() {

  override val resourceClass: Class<Graduate> get() = Graduate::class.java

  fun getTotal(filters: GraduateFilters, pageSettings: OffsetLimit): Long {
    return queryFactory.singleQuery {
      select(count(column(Graduate::id)))
      from(entity(Graduate::class))
      join(Graduate::user)
      join(Graduate::courses, JoinType.LEFT)
      join(Graduate::lastWorkHistory, JoinType.LEFT)
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
      join(Graduate::courses, JoinType.LEFT)
      join(Graduate::lastWorkHistory, JoinType.LEFT)
      join(Graduate::currentHistoryStatus, JoinType.LEFT)
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

  fun getGraduatesCriteria(filters: GraduateFilters, pageSettings: OffsetLimit): List<Graduate> {
    val typedQuery = criteria { query, entity ->


      val where = mutableListOf<Predicate>()

      val user: Join<Graduate, PlatformUser> = entity.join("user")
      val course: Join<Graduate, Course> = entity.join("courses", JoinType.LEFT)
      val lastWorkHistory: Join<Graduate, WorkHistory> = entity.join("lastWorkHistory", JoinType.LEFT)
      val currentHistoryStatus: Join<Graduate, HistoryStatus> = entity.join("currentHistoryStatus", JoinType.LEFT)
      val advisor: Join<Course, Advisor> = course.join("advisor", JoinType.LEFT)
      val institution: Join<WorkHistory, Institution> = entity.join("institution", JoinType.LEFT)
      val institutionType: Join<Institution, InstitutionType> = entity.join("type", JoinType.LEFT)


      filters.name?.run {
        where.add(like(this@criteria.upper(user.get("name")), "%${this.uppercase()}%"))
      }
      filters.institutionType?.run {
        where.add(equal(institutionType.get<String>("id"), this))
      }
      filters.institutionName?.run {
        where.add(like(this@criteria.upper(institution.get("name")), "%${this.uppercase()}%"))
      }
      filters.advisor?.run {
        where.add(equal(advisor.get<String>("id"), this))
      }

      query
        .select(entity)
        .where(*where.toTypedArray())
        .orderBy(this.desc(currentHistoryStatus.get<HistoryStatusEnum>("status")))
    }

    typedQuery.setFirstResult(pageSettings.offset)
    typedQuery.setMaxResults(pageSettings.pageSize)

    return typedQuery.resultList
  }

  fun getGraduateById(id: UUID): ResponseResult<Graduate> {
    val result = graduateRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.GRADUATE_NOT_FOUND)
    return ResponseResult.Success(result)
  }

  fun getAllGraduates(page: PageRequest): ResponseResult<Page<Graduate>> = try {
    val graduates = graduateRepository.findAllByOrderByLastWorkHistoryDesc(page)
    ResponseResult.Success(graduates)
  } catch (ex: Exception) {
    ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
  }

  fun getGraduatePaged(filters: GraduateFilters, page: OffsetLimit) =
    try {
      val total = getTotal(filters, page)
      val grad = getGraduatesCriteria(filters, page)
      val metaList = MetaDTO(page.page, grad.count(), total)
      ResponseResult.Success(Pair(grad, metaList))
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
    }


  fun getGraduatesByAdvisor(
    id: UUID,
    currentRole: Role,
    page: OffsetLimit,
    filters: GraduateFilters
  ): ResponseResult<ListGraduatesDTO> {


    val user: PlatformUser = when (val result = userService.getById(id)) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
    }

    val role = user.roles.find { it == currentRole }

    if (role == Role.GRADUATE) return ResponseResult.Error(Errors.USER_NOT_ALLOWED)
    if (role == Role.PROFESSOR) filters.advisor = user.advisor

    val metaList: MetaDTO
    val graduates: List<Graduate>

    when (val result = getGraduatePaged(filters, page)) {
      is ResponseResult.Success -> {
        val (grad, meta) = result.data!!
        graduates = grad
        metaList = meta
      }

      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
    }

    val dataList = graduates.map { it.toGraduateItemDTO() }

    dataList.sortedBy { it.status.value }
    val graduatesDTOList = ListGraduatesDTO(dataList, metaList)
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

  fun updateGraduateInfo(
    graduate: Graduate,
    postDoctorateDTO: CreatePostDoctorateDTO?,
    hasFinishedDoctorateOnUFF: Boolean?,
    hasFinishedMasterDegreeOnUFF: Boolean?,
    successCase: String?,
  ): ResponseResult<Graduate> {

    val postDoctorate = postDoctorateDTO?.let {
      if (it.id != null)
        when (val result = this.postDoctorateService.updatePostDoctorate(it.id, postDoctorateDTO)) {
          is ResponseResult.Success -> result.data!!
          is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
        }
      else
        when (val result = this.postDoctorateService.createPostDoctorate(graduate, postDoctorateDTO)) {
          is ResponseResult.Success -> result.data!!
          is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
        }
    }

    postDoctorate?.let { graduate.postDoctorate = it }

    hasFinishedDoctorateOnUFF?.let { graduate.hasFinishedDoctorateOnUFF = it }

    hasFinishedMasterDegreeOnUFF?.let {
      graduate.hasFinishedMasterDegreeOnUFF = it
    }
    graduate.updatedAt = LocalDateTime.now()

    successCase?.let { graduate.successCase = it }

    return this.save(graduate)
  }
}