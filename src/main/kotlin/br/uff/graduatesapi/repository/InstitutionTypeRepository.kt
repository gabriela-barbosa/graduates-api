package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.InstitutionType
import org.springframework.data.jpa.repository.JpaRepository

interface InstitutionTypeRepository : JpaRepository<InstitutionType, Int> {}