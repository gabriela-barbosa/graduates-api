package br.uff.graduatesapi.service

import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.repository.AdvisorRepository
import br.uff.graduatesapi.repository.GraduateRepository
import org.springframework.stereotype.Service

@Service
class GraduateService(
    private val userService: UserService,
    private val advisorService: AdvisorService,
    private val advisorService: AdvisorService,
    private val graduateRepository: GraduateRepository,
    ) {
    fun getGraduatesByAdvisor(jwt: String): List<Graduate>? {
        val user = userService.getUserByJwt(jwt) ?: return null
        val advisor = advisorService.findAdvisorByUser(user) ?: return null

        return graduateRepository.findAllByAdvisor(advisor)
    }
}