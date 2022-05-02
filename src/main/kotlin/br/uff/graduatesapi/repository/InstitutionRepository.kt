package br.uff.graduatesapi.repository

import br.uff.graduatesapi.enums.InstitutionType
import br.uff.graduatesapi.model.Institution
import org.springframework.data.jpa.repository.JpaRepository

interface InstitutionRepository : JpaRepository<Institution, Int> {
    fun findByNameAndType(name: String, type: InstitutionType) : Institution?
}