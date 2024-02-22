package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.CNPQLevel
import br.uff.graduatesapi.model.ResetPasswordCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.lang.Nullable
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ResetPasswordCodeRepository : JpaRepository<ResetPasswordCode, UUID> {

    @Nullable
    fun findByUserEmail(email: String): ResetPasswordCode?

    @Transactional

    fun deleteByUserEmail(email: String): Long
}