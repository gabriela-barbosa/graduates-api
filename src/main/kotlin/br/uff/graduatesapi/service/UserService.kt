package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.Utils.Companion.getRandomString
import br.uff.graduatesapi.dto.*
import br.uff.graduatesapi.entity.UserFilters
import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.passError
import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.UserRepository
import br.uff.graduatesapi.security.JWTUtil
import br.uff.graduatesapi.security.UserDetailsImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtUtil: JWTUtil,
    private val resetPasswordCodeService: ResetPasswordCodeService,
    private val graduateService: GraduateService,
    private val advisorService: AdvisorService,
    private val courseService: CourseService,
) {
    fun findByEmail(email: String): ResponseResult<PlatformUser> {
        val user = this.userRepository.findByEmail(email) ?: return ResponseResult.Error(Errors.USER_NOT_FOUND)
        return ResponseResult.Success(user)
    }

    fun findUserByNameAndRolePageable(
        name: String,
        role: RoleEnum,
        pageable: Pageable
    ): ResponseResult<List<GetUserLeanDTO>> {
        try {
            val users =
                this.userRepository.findByNameContainingIgnoreCaseAndRolesContainingOrderByName(name, role, pageable)
                    .toList()
            return ResponseResult.Success(users.map { it.toGetUserLeanDTO() })
        } catch (ex: Exception) {
            return ResponseResult.Error(Errors.CANT_RETRIEVE_USERS)
        }

    }

    fun findByIds(platformUserIds: List<UUID>): ResponseResult<List<PlatformUser>> {
        val user = this.userRepository.findByIdIn(platformUserIds) ?: return ResponseResult.Error(Errors.USER_NOT_FOUND)
        return ResponseResult.Success(user)
    }

    fun updateUser(userDTO: RegisterDTO): ResponseResult<PlatformUser> {
        try {
            val currentUser = when (val userResp = this.getById(userDTO.user.id!!)) {
                is ResponseResult.Success -> userResp.data!!
                is ResponseResult.Error -> return ResponseResult.Error(Errors.CANT_CREATE_USER)
            }

            if (userDTO.user.email != currentUser.email && this.findByEmail(userDTO.user.email) is ResponseResult.Success) {
                return ResponseResult.Error(Errors.EMAIL_IN_USE)
            }

            currentUser.email = userDTO.user.email
            currentUser.name = userDTO.user.name
            currentUser.roles = userDTO.user.roles
            currentUser.currentRole = userDTO.user.roles[0]

            val user = this.userRepository.save(currentUser)

            if (user.roles.contains(RoleEnum.GRADUATE)) {
                val currentGraduate =
                    if (userDTO.graduate?.id != null)
                        when (val result = this.getById(userDTO.graduate.id)) {
                            is ResponseResult.Success -> result.data!!.graduate
                            is ResponseResult.Error -> return result.passError()
                        } else {
                        when (val result = this.graduateService.createGraduateByModel(Graduate(user = user))) {
                            is ResponseResult.Success -> result.data!!
                            is ResponseResult.Error -> return result.passError()
                        }
                    }
                val graduateCoursesToUpdate = userDTO.graduate?.courses?.filter { it.id != null }
                val graduateCoursesToCreate = userDTO.graduate?.courses?.filter { it.id == null }
                if (!graduateCoursesToUpdate.isNullOrEmpty()) {
                    when (val result = courseService.updateCourses(graduateCoursesToUpdate, currentGraduate, null)) {
                        is ResponseResult.Success -> result.data!!
                        is ResponseResult.Error -> return result.passError()
                    }
                }
                if (!graduateCoursesToCreate.isNullOrEmpty()) {
                    when (val result = courseService.createCourses(graduateCoursesToCreate, currentGraduate)) {
                        is ResponseResult.Success -> result.data!!
                        is ResponseResult.Error -> return result.passError()
                    }
                }
            }

            if (user.roles.contains(RoleEnum.PROFESSOR)) {
                val currentAdvisor =
                    if (userDTO.advisor?.id != null)
                        when (val result = this.getById(userDTO.advisor.id)) {
                            is ResponseResult.Success -> result.data!!.advisor
                            is ResponseResult.Error -> return result.passError()
                        } else {
                        when (val result = this.advisorService.createAdvisor(Advisor(user = user))) {
                            is ResponseResult.Success -> result.data!!
                            is ResponseResult.Error -> return result.passError()
                        }
                    }
                val advisorCoursesToUpdate = userDTO.advisor?.courses?.filter { it.id != null }
                val advisorCoursesToCreate = userDTO.advisor?.courses?.filter { it.id == null }
                if (!advisorCoursesToUpdate.isNullOrEmpty()) {
                    when (val result = courseService.updateCourses(advisorCoursesToUpdate, null, currentAdvisor)) {
                        is ResponseResult.Success -> result.data!!
                        is ResponseResult.Error -> return result.passError()
                    }
                }
                if (!advisorCoursesToCreate.isNullOrEmpty()) {
                    when (val result = courseService.createCourses(advisorCoursesToCreate, null, currentAdvisor)) {
                        is ResponseResult.Success -> result.data!!
                        is ResponseResult.Error -> return result.passError()
                    }
                }
            }

            return ResponseResult.Success(this.userRepository.findById(user.id).get())
        } catch (ex: Exception) {
            return ResponseResult.Error(Errors.CANT_CREATE_USER)
        }
    }

    fun createUser(userDTO: RegisterDTO): ResponseResult<PlatformUser> {
        val (user, graduate, advisor) = userDTO
        try {
            if (this.findByEmail(user.email) is ResponseResult.Success) {
                return ResponseResult.Error(Errors.EMAIL_IN_USE, errorData = user.email)
            }
            val newUser = PlatformUser(
                name = user.name,
                email = user.email,
                roles = user.roles,
                currentRole = user.roles[0]
            )
            newUser.password = passwordEncoder.encode(getRandomString(10))
            val resp = this.userRepository.save(newUser)
            val coursesGraduate = if (user.roles.contains(RoleEnum.GRADUATE)) {
                if (graduate == null)
                    return ResponseResult.Error(Errors.INVALID_DATA)
                val createdGraduate =
                    when (val resultGraduate = graduateService.createGraduateByModel(Graduate(user = resp))) {
                        is ResponseResult.Success -> resultGraduate.data!!
                        is ResponseResult.Error -> return ResponseResult.Error(
                            Errors.CANT_CREATE_USER,
                            errorData = userDTO.user.name
                        )
                    }

                when (val resultCourses = courseService.createCourses(graduate.courses, createdGraduate)) {
                    is ResponseResult.Success -> resultCourses.data!!
                    is ResponseResult.Error -> return resultCourses.passError()
                }
            } else null
            val coursesAdvisor = if (user.roles.contains(RoleEnum.PROFESSOR)) {
                if (advisor == null)
                    return ResponseResult.Error(Errors.INVALID_DATA)
                val createdAdvisor =
                    when (val resultAdvisor = advisorService.createAdvisor(Advisor(user = resp))) {
                        is ResponseResult.Success -> resultAdvisor.data!!
                        is ResponseResult.Error -> return ResponseResult.Error(
                            Errors.CANT_CREATE_USER,
                            errorData = userDTO.user.name
                        )
                    }

                when (val resultCourses = courseService.createCourses(advisor.courses, advisor = createdAdvisor)) {
                    is ResponseResult.Success -> resultCourses.data!!
                    is ResponseResult.Error -> return resultCourses.passError()
                }
            } else null
            return ResponseResult.Success(resp)
        } catch (ex: Exception) {
            return ResponseResult.Error(Errors.CANT_CREATE_USER, errorData = userDTO.user.name)
        }
    }

    fun createUpdateUser(userDTO: RegisterDTO): ResponseResult<PlatformUser> =
        if (userDTO.user.id != null) {
            this.updateUser(userDTO)
        } else {
            this.createUser(userDTO)
        }

    fun updateCurrentRole(user: UserDetailsImpl, currentRoleEnum: RoleEnum): ResponseResult<Any> {
        val platformUser = userRepository.findByIdOrNull(UUID.fromString(user.username))
        if (platformUser != null && platformUser.roles.contains(currentRoleEnum)) {
            platformUser.currentRole = currentRoleEnum
            userRepository.save(platformUser)
            return ResponseResult.Success(platformUser)
        }
        return ResponseResult.Error(Errors.UNABLE_TO_UPDATE_CURRENT_ROLE)
    }

    fun updateUserEmailAndName(user: PlatformUser, email: String, name: String): ResponseResult<PlatformUser> =
        try {
            user.email = email
            user.name = name
            ResponseResult.Success(this.userRepository.save(user))
        } catch (ex: Exception) {
            ResponseResult.Error(Errors.CANT_UPDATE_EMAIL)
        }

    fun getById(id: UUID): ResponseResult<PlatformUser> {
        return try {
            val result = this.userRepository.getById(id)
            ResponseResult.Success(result)
        } catch (ex: Exception) {
            ResponseResult.Error(Errors.USER_NOT_FOUND)
        }
    }

    fun getUsers(pageable: Pageable, filters: UserFilters): ResponseResult<GetUsersDTO> {
        return try {
            ResponseResult.Success(this.userRepository.findAllCriteria(pageable, filters)!!)
        } catch (ex: Exception) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_USERS)
        }
    }

    fun getUsersByRole(roleEnum: RoleEnum): ResponseResult<List<PlatformUser>> {
        return try {
            ResponseResult.Success(this.userRepository.findByRolesContains(roleEnum))
        } catch (ex: Exception) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_USERS)
        }
    }

    fun getUserByJwt(jwt: String): ResponseResult<PlatformUser> =
        try {
            val body = jwtUtil.parseJwtToBody(jwt)

            when (val result = getById(UUID.fromString(body.issuer))) {
                is ResponseResult.Success -> ResponseResult.Success(result.data!!)
                is ResponseResult.Error -> ResponseResult.Error(Errors.USER_NOT_FOUND)
            }
        } catch (e: Exception) {
            ResponseResult.Error(Errors.USER_NOT_FOUND)
        }

    fun changePassword(changePasswordDTO: ChangePasswordDTO): ResponseResult<GetUserDTO> {
        val (code, newPassword) = changePasswordDTO

        val resetPasswordCode = when (val result = resetPasswordCodeService.getResetPasswordCodeById(code)) {
            is ResponseResult.Success -> result.data!!
            is ResponseResult.Error -> {
                val httpCode =
                    if (result.errorReason == Errors.RESET_PASSWORD_CODE_NOT_FOUND)
                        HttpStatus.UNPROCESSABLE_ENTITY
                    else
                        HttpStatus.INTERNAL_SERVER_ERROR
                return result.passError(httpCode)
            }
        }

        if (resetPasswordCode.isExpired()) {
            return ResponseResult.Error(
                errorReason = Errors.RESET_PASSWORD_CODE_NOT_FOUND,
                errorCode = HttpStatus.UNPROCESSABLE_ENTITY
            )
        }

        val isPasswordValid = Utils.checkIfPasswordIsValid(newPassword)

        if (!isPasswordValid) {
            return ResponseResult.Error(
                errorReason = Errors.RESET_PASSWORD_CODE_IS_EXPIRED,
            )
        }

        val user = resetPasswordCode.user

        return try {
            user.password = passwordEncoder.encode(newPassword)
            val updatedUser = userRepository.save(user)
            resetPasswordCodeService.deleteResetPasswordCode(resetPasswordCode.id)

            ResponseResult.Success(updatedUser.toGetUserDTO())
        } catch (e: Exception) {
            ResponseResult.Error(
                errorReason = Errors.CANT_UPDATE_USER_PASSWORD,
            )
        }
    }
}