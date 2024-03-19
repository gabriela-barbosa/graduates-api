package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.*
import br.uff.graduatesapi.entity.GraduateFilters
import br.uff.graduatesapi.entity.OffsetLimit
import br.uff.graduatesapi.enum.HistoryStatusEnum
import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.passError
import br.uff.graduatesapi.model.*
import br.uff.graduatesapi.repository.BaseRepository
import br.uff.graduatesapi.repository.GraduateRepository
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.join
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.persistence.criteria.*


@Service
class GraduateService(
	private val userService: UserService,
	private val csvService: CSVService,
	private val postDoctorateService: PostDoctorateService,
	private val graduateRepository: GraduateRepository,
	private val queryFactory: SpringDataQueryFactory,
	private val advisorService: AdvisorService,
	private val ciProgramService: CIProgramService,
	private val institutionService: InstitutionService,
	private val institutionTypeService: InstitutionTypeService,
	private val courseService: CourseService,
	private val cnpqLevelService: CNPQLevelService,
	private val cnpqScholarshipService: CNPQScholarshipService,
	@Lazy
	private val workHistoryService: WorkHistoryService,
) : BaseRepository<Graduate>() {

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
					filters.advisorId?.run { column(Advisor::id).equal(this) },
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
					filters.advisorId?.run { column(Advisor::id).equal(this) },
				)
			)
			orderBy(column(HistoryStatus::status).desc())
			limit(
				pageSettings.offset,
				pageSettings.limit,
			)
		}
	}

	fun findGraduateByNameLikeAndPaged(name: String, page: Int, size: Int): ResponseResult<List<GetUserLeanDTO>> {
		return try {
			return when (val result =
				userService.findUserByNameAndRolePageable(name, RoleEnum.GRADUATE, PageRequest.of(page, size))) {
				is ResponseResult.Success -> ResponseResult.Success(result.data!!)
				is ResponseResult.Error -> result.passError()
			}
		} catch (err: Error) {
			ResponseResult.Error(Errors.CANT_RETRIEVE_ADVISOR, errorData = name)
		}
	}

	private fun addFiltersAndJoinsGetGraduatesCriteria(
		entity: Root<Graduate>,
		filters: GraduateFilters,
		builder: CriteriaBuilder
	): Triple<MutableList<Predicate>, Join<Graduate, HistoryStatus>, Join<Graduate, PlatformUser>> {
		val where = mutableListOf<Predicate>()

		val user: Join<Graduate, PlatformUser> = entity.join("user", JoinType.INNER)
		val lastWorkHistory: Join<Graduate, WorkHistory> = entity.join("lastWorkHistory", JoinType.LEFT)
		val currentHistoryStatus: Join<Graduate, HistoryStatus> = entity.join("currentHistoryStatus", JoinType.LEFT)
		val institution: Join<WorkHistory, Institution> = lastWorkHistory.join("institution", JoinType.LEFT)

		filters.name?.run {
			where.add(builder.like(builder.upper(user.get("name")), "%${this.uppercase()}%"))
		}
		filters.institutionType?.run {
			val institutionType: Join<Institution, InstitutionType> = institution.join("type", JoinType.LEFT)
			where.add(builder.equal(institutionType.get<UUID>("id"), this))
		}
		filters.institutionName?.run {
			where.add(builder.like(builder.upper(institution.get("name")), "%${this.uppercase()}%"))
		}
		filters.position?.run {
			where.add(builder.like(builder.upper(lastWorkHistory.get("position")), "%${this.uppercase()}%"))
		}
		filters.cnpqLevel?.run {
			val cnpqScholarships: Join<Graduate, CNPQScholarship> = entity.join("cnpqScholarships", JoinType.INNER)
			val cnpqLevel: Join<CNPQScholarship, CNPQLevel> = cnpqScholarships.join("level", JoinType.INNER)
			where.add(builder.equal(cnpqLevel.get<UUID>("id"), this))
		}

		if (filters.advisorId != null || !filters.advisorName.isNullOrEmpty()) {
			val course: Join<Graduate, Course> = entity.join("courses", JoinType.INNER)
			val advisor: Join<Course, Advisor> = course.join("advisor", JoinType.INNER)
			filters.advisorId?.run {
				where.add(builder.equal(advisor.get<UUID>("id"), this))
			}
			filters.advisorName?.run {
				val advisorUser: Join<Advisor, PlatformUser> = advisor.join("user", JoinType.INNER)
				where.add(builder.like(builder.upper(advisorUser.get("name")), "%${this.uppercase()}%"))
			}
		}


		return Triple(where, currentHistoryStatus, user)

	}

	fun getGraduatesCriteria(filters: GraduateFilters, pageSettings: OffsetLimit): Pair<List<Graduate>, MetaDTO> {
		val builder: CriteriaBuilder = entityManager.criteriaBuilder
		val query: CriteriaQuery<Graduate> = builder.createQuery(Graduate::class.java)
		val entity: Root<Graduate> = query.from(Graduate::class.java)

		val countQuery = builder
			.createQuery(Long::class.java)

		val entityCount = countQuery.from(Graduate::class.java)

		val (whereCount) = addFiltersAndJoinsGetGraduatesCriteria(entityCount, filters, builder)

		countQuery
			.select(builder.count(entityCount))
			.where(*whereCount.toTypedArray())

		val count: Long = entityManager.createQuery(countQuery).singleResult

		val (whereGraduates, currentHS, user) = addFiltersAndJoinsGetGraduatesCriteria(entity, filters, builder)


		query
			.select(entity)
			.where(*whereGraduates.toTypedArray())
			.orderBy(
				builder.asc(builder.coalesce(currentHS.get("status"), HistoryStatusEnum.PENDING.ordinal)),
				builder.asc(user.get<String>("name"))
			)


		val queryResult = entityManager.createQuery(query)


		queryResult.setFirstResult(pageSettings.offset)
		queryResult.setMaxResults(pageSettings.pageSize)

		return Pair(queryResult.resultList, MetaDTO(pageSettings.page, pageSettings.pageSize, count))
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

	fun getGraduatePaged(filters: GraduateFilters, page: OffsetLimit) = try {
		val result = getGraduatesCriteria(filters, page)
		ResponseResult.Success(result)
	} catch (ex: Exception) {
		ResponseResult.Error(Errors.CANT_RETRIEVE_GRADUATES)
	}


	fun getGraduatesByAdvisor(
		id: UUID, currentRoleEnum: RoleEnum, page: OffsetLimit, filters: GraduateFilters
	): ResponseResult<ListGraduatesDTO> {


		val user: PlatformUser = when (val result = userService.getById(id)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
		}

		val role = user.roles.find { it == currentRoleEnum }

		if (role == RoleEnum.GRADUATE) return ResponseResult.Error(Errors.USER_NOT_ALLOWED)
		if (role == RoleEnum.PROFESSOR) filters.advisorId = user.advisor?.id

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

	fun createGraduateByModel(graduate: Graduate): ResponseResult<Graduate> {
		return try {
			val savedGraduate = graduateRepository.save(graduate)
			ResponseResult.Success(savedGraduate)
		} catch (ex: Exception) {
			ResponseResult.Error(Errors.CANT_CREATE_GRADUATE, errorData = graduate.user.name)
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
			if (it.id != null) when (val result =
				this.postDoctorateService.updatePostDoctorate(it.id, postDoctorateDTO)) {
				is ResponseResult.Success -> result.data!!
				is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
			}
			else when (val result = this.postDoctorateService.createPostDoctorate(graduate, postDoctorateDTO)) {
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

		return this.createGraduateByModel(graduate)
	}

	private fun handleGraduateFromCSV(fields: CSVFieldsDTO): ResponseResult<Graduate> {
		return when (val result = userService.findByEmail(fields.email)) {
			is ResponseResult.Success -> ResponseResult.Success(result.data!!.graduate!!)
			is ResponseResult.Error -> {
				val userDTO = RegisterDTO(
					name = fields.name,
					email = fields.email,
					roles = listOf(RoleEnum.GRADUATE)
				)

				val createdUser = when (val resultCreatedUser = userService.createUser(userDTO)) {
					is ResponseResult.Success -> resultCreatedUser.data!!
					is ResponseResult.Error -> {
						val errorCode =
							if (resultCreatedUser.errorReason == Errors.EMAIL_IN_USE) HttpStatus.UNPROCESSABLE_ENTITY else HttpStatus.INTERNAL_SERVER_ERROR
						return resultCreatedUser.passError(errorCode)
					}
				}

				val graduate = Graduate(
					user = createdUser,
					hasFinishedDoctorateOnUFF = fields.finishedDoctorateOnUFF,
					hasFinishedMasterDegreeOnUFF = fields.finishedMasterDegreeOnUFF,
					successCase = fields.successCase,
				)

				when (val resultCreatedGraduate = createGraduateByModel(graduate)) {
					is ResponseResult.Success -> ResponseResult.Success(resultCreatedGraduate.data!!)
					is ResponseResult.Error -> resultCreatedGraduate.passError()
				}
			}
		}
	}


	private fun handlePostDoctorateFromCSV(
		institutionName: String,
		graduate: Graduate
	): ResponseResult<PostDoctorate?> {
		if (institutionName.isEmpty())
			return ResponseResult.Success(null)

		val postDoctorateInstitutionDTO =
			when (val result = institutionService.findByName(institutionName)) {
				is ResponseResult.Success -> result.data!!.toCreateInstitutionDTO()
				is ResponseResult.Error -> {
					val institutionType = institutionTypeService.findByName("outros").data!!
					CreateInstitutionDTO(name = institutionName, typeId = institutionType.id)
				}
			}
		val postDoctorateDTO = CreatePostDoctorateDTO(
			institution = postDoctorateInstitutionDTO
		)

		return when (
			val result = postDoctorateService.createPostDoctorate(graduate, postDoctorateDTO)) {
			is ResponseResult.Success -> ResponseResult.Success(result.data!!)
			is ResponseResult.Error -> result.passError()
		}
	}


	private fun handleCourseFromCSV(graduate: Graduate, fields: CSVFieldsDTO): ResponseResult<Course> {

		val advisor =
			when (val result = advisorService.findAdvisorByName(fields.advisorName)) {
				is ResponseResult.Success -> result.data!!
				is ResponseResult.Error -> {
					val errorCode =
						if (result.errorReason == Errors.ADVISOR_NOT_FOUND)
							HttpStatus.UNPROCESSABLE_ENTITY
						else
							HttpStatus.INTERNAL_SERVER_ERROR
					return result.passError(errorCode)
				}
			}

		val ciProgram =
			when (val result = ciProgramService.findProgramByInitials(fields.ciProgramName)) {
				is ResponseResult.Success -> result.data!!
				is ResponseResult.Error -> {
					val errorCode =
						if (result.errorReason == Errors.CI_PROGRAM_NOT_FOUND)
							HttpStatus.UNPROCESSABLE_ENTITY
						else
							HttpStatus.INTERNAL_SERVER_ERROR
					return result.passError(errorCode)
				}
			}

		val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
		val titleDate = LocalDate.parse(fields.titleDate, dateTimeFormatter)

		val course = Course(
			advisor = advisor,
			program = ciProgram,
			graduate = graduate,
			defenseMinute = fields.defenseMinute,
			titleDate = titleDate,
		)
		return when (val result = courseService.createCourse(course)) {
			is ResponseResult.Success -> ResponseResult.Success(result.data!!)
			is ResponseResult.Error -> result.passError()
		}
	}

	private fun handleCNPQScholarshipFromCSV(
		graduate: Graduate,
		fields: CSVFieldsDTO
	): ResponseResult<CNPQScholarship?> {
		if (fields.cnpqLevel.isEmpty())
			return ResponseResult.Success(null)

		val cnpqLevel = when (val result = cnpqLevelService.findLevelByName(fields.cnpqLevel)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> {
				val errorCode =
					if (result.errorReason == Errors.CNPQ_LEVEL_NOT_FOUND)
						HttpStatus.UNPROCESSABLE_ENTITY
					else
						HttpStatus.INTERNAL_SERVER_ERROR
				return result.passError(errorCode)
			}
		}

		val cnpqScholarship = CNPQScholarship(
			graduate = graduate,
			level = cnpqLevel,
		)

		return when (val result = cnpqScholarshipService.createCNPQScholarship(cnpqScholarship)) {
			is ResponseResult.Success -> ResponseResult.Success(result.data!!)
			is ResponseResult.Error -> result.passError()
		}
	}

	private fun handleWorkHistoryFromCSV(
		graduate: Graduate,
		fields: CSVFieldsDTO
	): ResponseResult<WorkHistory?> {
		if (fields.institutionName.isNullOrEmpty())
			return ResponseResult.Success(null)

		val institutionType =
			when (val result = institutionTypeService.findByName(fields.institutionType!!)) {
				is ResponseResult.Success -> result.data!!
				is ResponseResult.Error -> {
					val errorCode =
						if (result.errorReason == Errors.INSTITUTION_TYPE_NOT_FOUND)
							HttpStatus.UNPROCESSABLE_ENTITY
						else
							HttpStatus.INTERNAL_SERVER_ERROR
					return result.passError(errorCode)
				}
			}

		val institutionDTO = CreateInstitutionDTO(
			name = fields.institutionName,
			typeId = institutionType.id
		)

		return when (val result = workHistoryService.createWorkHistoryByDTO(
			graduate = graduate,
			position = fields.position,
			institutionDTO = institutionDTO
		)) {
			is ResponseResult.Success -> ResponseResult.Success(result.data!!)
			is ResponseResult.Error -> result.passError()
		}
	}


	private fun extractGraduateInfoFromCSV(
		isDoctorateGraduates: Boolean,
		lines: List<String>,
		headers: List<String>
	): ResponseResult<Nothing?> {
		val csvFields = lines.toCSVFieldsDTO(isDoctorateGraduates, headers)

		val graduate = when (val result = handleGraduateFromCSV(csvFields)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return result.passError()
		}

		when (val result = handleCourseFromCSV(graduate, csvFields)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return result.passError()
		}

		when (val result = handlePostDoctorateFromCSV(csvFields.postDoctorate, graduate)) {
			is ResponseResult.Success -> result.data
			is ResponseResult.Error -> return result.passError()
		}

		when (val result = handleCNPQScholarshipFromCSV(graduate, csvFields)) {
			is ResponseResult.Success -> result.data
			is ResponseResult.Error -> return result.passError()
		}

		when (val result = handleWorkHistoryFromCSV(graduate, csvFields)) {
			is ResponseResult.Success -> result.data
			is ResponseResult.Error -> return result.passError()
		}

		return ResponseResult.Success(null)
	}


	@Transactional(rollbackFor = [Exception::class])
	fun createGraduateByCSV(file: MultipartFile, isDoctorateGraduates: Boolean): ResponseResult<Nothing?> {
		val result = csvService.readCSV(file)
		if (result is ResponseResult.Error) return ResponseResult.Error(result.errorReason!!)
		val (header, lines) = result.data!!


		for (line in lines) {
			val extractResult = extractGraduateInfoFromCSV(
				isDoctorateGraduates = isDoctorateGraduates, lines = line, headers = header
			)
			if (extractResult is ResponseResult.Error) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return extractResult.passError()
			}
		}
		return ResponseResult.Success(null)
	}
}
