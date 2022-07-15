package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.WorkHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WorkHistoryRepository : JpaRepository<WorkHistory, Int> {
    @Query(
        "WITH ranked_messages AS (\n" +
                "    SELECT m.*, ROW_NUMBER() OVER (PARTITION BY graduate_id ORDER BY created_at DESC, updated_at DESC ) AS rn\n" +
                "    FROM work_history AS m\n" +
                ")\n" +
                "SELECT * FROM ranked_messages WHERE rn = 1;", nativeQuery = true
    )
    fun findTopByGraduateOrderByCreatedAtDesc(graduates: List<Graduate>): List<WorkHistory>?

    fun findFirstByGraduateIdIsOrderByCreatedAtDesc(id: Int): WorkHistory
}