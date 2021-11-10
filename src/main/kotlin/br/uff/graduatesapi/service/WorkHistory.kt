package br.uff.graduatesapi.service

import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.repository.AdvisorRepository
import org.springframework.stereotype.Service

@Service
class WorkHistory(
    private val advisorRepository: AdvisorRepository,
    ) {
    fun findAdvisorByUser(user: PlatformUser): Advisor? {
        return advisorRepository.findAdvisorByUser(user)
    }
}