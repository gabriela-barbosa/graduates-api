package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.*
import br.uff.graduatesapi.entity.GraduateFilters
import br.uff.graduatesapi.entity.OffsetLimit
import br.uff.graduatesapi.enum.CsvFieldsEnum
import br.uff.graduatesapi.enum.HistoryStatusEnum
import br.uff.graduatesapi.enum.RoleEnum
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
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
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

		val role = user.roleEnums.find { it == currentRoleEnum }

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

	private fun getInstitutionTypeFromCsvLine(fields: Map<CsvFieldsEnum, String>): ResponseResult<InstitutionType> {
		val institutionType = when {
			fields[CsvFieldsEnum.PUBLIC_FEDERAL_UNIVERSITY] == "1" -> InstitutionTypeEnum.PUBLIC_FEDERAL_UNIVERSITY
			fields[CsvFieldsEnum.PUBLIC_FEDERAL_INSTITUTION] == "1" -> InstitutionTypeEnum.PUBLIC_FEDERAL_INSTITUTION
			fields[CsvFieldsEnum.PUBLIC_STATE_INSTITUTION] == "1" -> InstitutionTypeEnum.PUBLIC_STATE_INSTITUTION
			fields[CsvFieldsEnum.PRIVATE_INSTITUTION] == "1" -> InstitutionTypeEnum.PRIVATE_INSTITUTION
			fields[CsvFieldsEnum.PUBLIC_ORGANIZATION] == "1" -> InstitutionTypeEnum.PUBLIC_ORGANIZATION
			fields[CsvFieldsEnum.COMPANY_OR_BRAZILIAN_INSTITUTION] == "1" -> InstitutionTypeEnum.COMPANY_OR_BRAZILIAN_INSTITUTION
			fields[CsvFieldsEnum.COMPANY_OR_FOREIGN_INSTITUTION] == "1" -> InstitutionTypeEnum.COMPANY_OR_FOREIGN_INSTITUTION
			else -> return ResponseResult.Error(Errors.INVALID_DATA)
		}

		return when (val result = institutionService.findInstitutionType(institutionType)) {
			is ResponseResult.Success -> ResponseResult.Success(result.data!!)
			is ResponseResult.Error -> ResponseResult.Error(result.errorReason!!)
		}
	}


	private fun extractGraduateInfoFromCSV(isDoctorateGraduate: Boolean, line: List<String>): ResponseResult<Nothing?> {
		val fields = mapOf(
			CsvFieldsEnum.NAME to line[0],
			CsvFieldsEnum.DEFENSE_MINUTE to line[1],
			CsvFieldsEnum.TITLE_DATE to line[2],
			CsvFieldsEnum.ADVISOR_NAME to line[3],
			CsvFieldsEnum.PROGRAM_NAME to line[4],
			CsvFieldsEnum.EMAIL to line[5],
			CsvFieldsEnum.POSITION to line[6],
			CsvFieldsEnum.INSTITUTION_NAME to line[7],
			CsvFieldsEnum.NO_INSTITUTION to line[8],
			CsvFieldsEnum.PUBLIC_FEDERAL_UNIVERSITY to line[9],
			CsvFieldsEnum.PUBLIC_FEDERAL_INSTITUTION to line[10],
			CsvFieldsEnum.PUBLIC_STATE_INSTITUTION to line[11],
			CsvFieldsEnum.PRIVATE_INSTITUTION to line[12],
			CsvFieldsEnum.PUBLIC_ORGANIZATION to line[13],
			CsvFieldsEnum.COMPANY_OR_BRAZILIAN_INSTITUTION to line[14],
			CsvFieldsEnum.COMPANY_OR_FOREIGN_INSTITUTION to line[15],
			CsvFieldsEnum.OTHERS to line[16],
			CsvFieldsEnum.SUCCESS_CASE to line[17],
			CsvFieldsEnum.CNPQ_SCHOLARSHIP to line[18],
			CsvFieldsEnum.POST_DOCTORATE to line[19],
			if (isDoctorateGraduate) CsvFieldsEnum.FINISHED_DOCTORATE_ON_UFF to line[20]
			else CsvFieldsEnum.FINISHED_MASTER_DEGREE_ON_UFF to line[20],
		).withDefault { "" }

		val email = fields.getValue(CsvFieldsEnum.EMAIL)
		var createdUser = when (val result = userService.findByEmail(email)) {
			is ResponseResult.Success -> result.data
			is ResponseResult.Error -> null
		}

		val createdGraduate: Graduate

		if (createdUser == null) {
			val userDTO = RegisterDTO(
				name = fields.getValue(CsvFieldsEnum.NAME),
				email = email,
				roleEnums = listOf(RoleEnum.GRADUATE)
			)

			//create user when there is no user with the email

			createdUser = when (val result = userService.createUser(userDTO)) {
				is ResponseResult.Success -> result.data!!
				is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
			}

			val hasFinishedDoctorateOnUFF = fields[CsvFieldsEnum.FINISHED_DOCTORATE_ON_UFF]
			val hasFinishedMasterDegreeOnUFF = fields[CsvFieldsEnum.FINISHED_MASTER_DEGREE_ON_UFF]

			val graduate = Graduate(
				user = createdUser,
				hasFinishedDoctorateOnUFF = hasFinishedDoctorateOnUFF == "1",
				hasFinishedMasterDegreeOnUFF = hasFinishedMasterDegreeOnUFF == "1",
				successCase = fields.getValue(CsvFieldsEnum.SUCCESS_CASE),
			)

			createdGraduate = when (val result = createGraduateByModel(graduate)) {
				is ResponseResult.Success -> result.data!!
				is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
			}

		} else {
			createdGraduate = createdUser.graduate!!
		}

		val postDoctorateFieldValue = fields.getValue(CsvFieldsEnum.POST_DOCTORATE)

		if (postDoctorateFieldValue.isNotEmpty()) {
			val postDoctorateInstitution = when (val result = institutionService.findByName(postDoctorateFieldValue)) {
				is ResponseResult.Success -> result.data!!
				is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
			}

			val postDoctorateDTO = CreatePostDoctorateDTO(
				id = null,
				institutionName = postDoctorateFieldValue,
				startDate = null,
				endDate = null,
				graduateId = createdUser.id,
			)

			val postDoctorate =
				when (val result = postDoctorateService.createPostDoctorate(createdUser, postDoctorateDTO)) {
					is ResponseResult.Success -> result.data!!
					is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
				}
		}


		val advisor =
			when (val result = advisorService.findAdvisorByName(fields.getValue(CsvFieldsEnum.ADVISOR_NAME))) {
				is ResponseResult.Success -> result.data!!
				is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
			}

		val ciProgram =
			when (val result = ciProgramService.findProgramByInitials(fields.getValue(CsvFieldsEnum.PROGRAM_NAME))) {
				is ResponseResult.Success -> result.data!!
				is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
			}

//        save(graduate)
	}


	@Transactional(rollbackFor = [Exception::class])
	fun createGraduateByCSV(file: MultipartFile, templateId: UUID): ResponseResult<List<Graduate>> {
		val result = csvService.readCSV(file)
		if (result is ResponseResult.Error) return ResponseResult.Error(result.errorReason!!)
		val (header, lines) = result.data!!

		lines.forEach { line ->
			val graduate = Graduate()
			graduate.name = line[index]
//            save(graduate)
		}


	}

}