package br.uff.graduatesapi.service

import br.uff.graduatesapi.enums.InstitutionType
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.repository.InstitutionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class InstitutionService(
    private val institutionRepository: InstitutionRepository,
) {
    fun findById(id: Int): Institution? {
        return institutionRepository.findByIdOrNull(id)
    }

    fun findByNameAndType(name: String, type: InstitutionType): Institution? {
        return institutionRepository.findByNameAndType(name, type)
    }

    fun createInstitution(institution: Institution): Institution? {
        return findByNameAndType(institution.name, institution.type) ?: institutionRepository.save(institution)
    }
}