package br.uff.graduatesapi.repository

import br.uff.graduatesapi.dto.InstitutionTypeDTO
import br.uff.graduatesapi.model.InstitutionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface InstitutionTypeRepository : JpaRepository<InstitutionType, Int> {
    @Modifying
    @Query("update InstitutionType institutionType set institutionType.active=false where institutionType.id = ?2")
    override fun deleteById(id: Int)

    @Modifying
    @Query("select new br.uff.graduatesapi.dto.InstitutionTypeDTO(institutionType.id, institutionType.name, institutionType.createdAt) from InstitutionType institutionType where institutionType.active=true")
    fun findAllActives(): List<InstitutionTypeDTO>
}