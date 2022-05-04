package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.WorkHistoryDTO
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.WorkHistory
import br.uff.graduatesapi.repository.WorkHistoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class WorkHistoryService(
    private val workHistoryRepository: WorkHistoryRepository,
    private val userService: UserService,
    private val cnpqScholarshipService: CNPQScholarshipService,
    ) {
    fun findAllByGraduates(graduates: List<Graduate>): List<WorkHistory>? {
        return workHistoryRepository.findTopByGraduateOrderByCreatedAtDesc(graduates)
    }
    fun save(workHistory: WorkHistory): WorkHistory? {
        return workHistoryRepository.save(workHistory)
}
    fun getWorkHistory(id: Int): WorkHistoryDTO? {
        val workHistory: WorkHistory = workHistoryRepository.findByIdOrNull(1) ?: return null
        val graduate: Graduate = workHistory.graduate
        val cnpqId: Int? = cnpqScholarshipService.findByGraduate(graduate)?.id
        val workHistoryDTO = WorkHistoryDTO(
            email = workHistory.graduate.user.email,
            position = workHistory.position,
            cnpqLevelId = cnpqId,
            finishedDoctorateOnUFF = workHistory.graduate.finishedDoctorateOnUFF,
            knownWorkPlace = graduate.historyStatus!!.knownWorkplace,
        )
        if(workHistory.institution != null)
            workHistoryDTO.addInstitutionInfo(workHistory.institution!!)
        if (graduate.postDoctorate != null)
            workHistoryDTO.addPostDoctorate(graduate.postDoctorate!!)
        return workHistoryDTO
    }
}