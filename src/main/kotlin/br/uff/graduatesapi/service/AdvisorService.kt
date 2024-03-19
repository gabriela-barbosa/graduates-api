package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.GetUserLeanDTO
import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.passError
import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.AdvisorRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class AdvisorService(
    private val advisorRepository: AdvisorRepository,
    private val userService: UserService
) {
    fun findAdvisorByUser(user: PlatformUser): Advisor? {
        return advisorRepository.findAdvisorByUser(user)
    }

    fun createAdvisor(advisor: Advisor): Advisor {
        return advisorRepository.save(advisor)
    }

    fun findAdvisorByNameLikeAndPaged(name: String, page: Int, size: Int): ResponseResult<List<GetUserLeanDTO>> {
        return try {
            return when (val result =
                userService.findUserByNameAndRolePageable(name, RoleEnum.PROFESSOR, PageRequest.of(page, size))) {
                is ResponseResult.Success -> ResponseResult.Success(result.data!!)
                is ResponseResult.Error -> result.passError()
            }
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_ADVISOR, errorData = name)
        }
    }

    fun findAdvisorByName(name: String): ResponseResult<Advisor> {
        return try {
            val result = advisorRepository.findAdvisorByUserNameIgnoreCase(name)
                ?: return ResponseResult.Error(errorReason = Errors.ADVISOR_NOT_FOUND, errorData = name)
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_ADVISOR, errorData = name)
        }
    }


}