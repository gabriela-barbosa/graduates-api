package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.PostDoctorate
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PostDoctorateRepository : JpaRepository<PostDoctorate, UUID> {
}