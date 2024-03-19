package br.uff.graduatesapi.repository

import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.PlatformUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.lang.Nullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<PlatformUser, UUID>, UserRepositoryCustom {


    @Nullable
    fun findByEmail(email: String): PlatformUser?

    fun findByIdIn(userIds: List<UUID>): List<PlatformUser>?

    fun findByRolesContains(roleEnum: RoleEnum): List<PlatformUser>

    fun findByNameContainingIgnoreCaseAndRolesContainingOrderByName(
        name: String,
        role: RoleEnum,
        pageable: Pageable
    ): Page<PlatformUser>


    @Query(
        "insert into platform_user_role (platform_user_id, role) VALUES (:id, :role)", nativeQuery = true
    )
    fun insertRole(@Param("id") id: UUID, @Param("role") roleEnum: RoleEnum): PlatformUser?

    @Modifying
    @Query("update PlatformUser user set user.email=?2, user.updatedAt=now() where user.id=?1")
    fun updateEmail(id: UUID, newEmail: String): PlatformUser?

}