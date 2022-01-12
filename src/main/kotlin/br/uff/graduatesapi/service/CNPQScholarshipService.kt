package br.uff.graduatesapi.service

import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.repository.CNPQScholarshipRepository
import org.springframework.stereotype.Service

@Service
class CNPQScholarshipService(
    private val cnpqScholarshipRepository: CNPQScholarshipRepository
) {
    fun findByGraduate(graduate: Graduate) {
        return cnpqScholarshipRepository.findActualCNPQScholarshipByGraduate(graduate)
    }
}