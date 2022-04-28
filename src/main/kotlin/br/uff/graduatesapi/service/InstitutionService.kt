package br.uff.graduatesapi.service

import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.repository.InstitutionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class InstitutionService(
    private val institutionRepository: InstitutionRepository,
) {
    fun findById(id: Int): Institution? {
        return institutionRepository.findByIdOrNull(id)
    }

    fun save(institution: Institution): Institution? {
        return institutionRepository.save(institution)
    }
}