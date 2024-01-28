package br.uff.graduatesapi.service

import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.AdvisorRepository
import org.springframework.stereotype.Service

@Service
class AdvisorService(
    private val advisorRepository: AdvisorRepository,
) {
    fun findAdvisorByUser(user: PlatformUser): Advisor? {
        return advisorRepository.findAdvisorByUser(user)
    }

    fun createAdvisor(advisor: Advisor): Advisor {
        return advisorRepository.save(advisor)
    }

    fun findAdvisorByName(name: String): ResponseResult<Advisor> {
        val advisor =
            advisorRepository.findAdvisorByUserName(name) ?: return ResponseResult.Error(Errors.ADVISOR_NOT_FOUND)
        return ResponseResult.Success(advisor)
    }
}