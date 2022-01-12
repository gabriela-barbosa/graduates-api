package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.CNPQLevel
import org.springframework.data.jpa.repository.JpaRepository

interface CNPQLevelRepository : JpaRepository<CNPQLevel, Int> {
}