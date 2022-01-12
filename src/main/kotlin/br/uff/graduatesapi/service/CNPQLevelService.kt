package br.uff.graduatesapi.service

import br.uff.graduatesapi.model.CNPQLevel
import br.uff.graduatesapi.repository.CNPQLevelRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CNPQLevelService(
    private val cnpqLevelRepository: CNPQLevelRepository
) {
    fun findById(id: Int): CNPQLevel? {
        return cnpqLevelRepository.findByIdOrNull(id)
    }
}