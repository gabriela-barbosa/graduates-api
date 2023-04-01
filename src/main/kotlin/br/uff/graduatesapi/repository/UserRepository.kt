package br.uff.graduatesapi.repository

import br.uff.graduatesapi.enum.Role
import br.uff.graduatesapi.model.PlatformUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<PlatformUser, UUID>, UserRepositoryCustom {
    fun findByEmail(email: String): PlatformUser?
    @Query(
        "insert into platform_user_role (platform_user_id, role) VALUES (:id, :role)", nativeQuery = true
    )
    fun insertRole(@Param("id") id: UUID, @Param("role") role: Role): PlatformUser?

    @Modifying
    @Query("update PlatformUser user set user.email=?1 where user.email = ?2")
    fun updateEmail(newEmail: String, oldEmail: String): PlatformUser?

}