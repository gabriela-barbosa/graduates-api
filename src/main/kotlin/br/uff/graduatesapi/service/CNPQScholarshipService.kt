package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CNPQScholarshipDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.CNPQScholarship
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.repository.CNPQScholarshipRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class CNPQScholarshipService(
    private val cnpqScholarshipRepository: CNPQScholarshipRepository,
    private val cnpqLevelService: CNPQLevelService,
) {
    fun findActualCNPQScholarshipsByGraduate(graduateId: UUID): List<CNPQScholarship> {
        return cnpqScholarshipRepository.findActualCNPQScholarshipByGraduateId(graduateId)
    }

    fun createCNPQScholarship(cnpqScholarship: CNPQScholarship): ResponseResult<CNPQScholarship> =
        try {
            ResponseResult.Success(cnpqScholarshipRepository.save(cnpqScholarship))
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_CREATE_CNPQSCHOLARSHIP)
        }

    fun updateScholarship(id: UUID, scholarshipDTO: CNPQScholarshipDTO): ResponseResult<CNPQScholarship> {
        try {
            val oldScholarship = cnpqScholarshipRepository.findByIdOrNull(scholarshipDTO.id)
                ?: return ResponseResult.Error(Errors.CNPQSCHOLARSHIP_NOT_FOUND)
            if (oldScholarship.level.id != scholarshipDTO.levelId) {
                val cnpqLevel = when (val result = cnpqLevelService.findById(scholarshipDTO.levelId)) {
                    is ResponseResult.Success -> result.data!!
                    is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
                }
                oldScholarship.level = cnpqLevel
            }
            oldScholarship.startedAt = scholarshipDTO.startedAt
            oldScholarship.endedAt = scholarshipDTO.endedAt

            return createCNPQScholarship(oldScholarship)
        } catch (ex: Exception) {
            return ResponseResult.Error(Errors.CNPQSCHOLARSHIP_NOT_FOUND)
        }
    }


    fun createCNPQScholarships(
        graduate: Graduate,
        scholarships: List<CNPQScholarshipDTO>,
    ): ResponseResult<List<CNPQScholarship>> {
        val newScholarships = scholarships.map {
            if (it.id != null) {
                when (val result = updateScholarship(it.id, it)) {
                    is ResponseResult.Success -> result.data!!
                    is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
                }
            } else {
                val cnpqLevel = when (val result = cnpqLevelService.findById(it.levelId)) {
                    is ResponseResult.Success -> result.data!!
                    is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
                }
                when (val result = createCNPQScholarship(
                    CNPQScholarship(
                        cnpqLevel,
                        graduate,
                        startedAt = it.startedAt,
                        endedAt = it.endedAt
                    )
                )) {
                    is ResponseResult.Success -> result.data!!
                    is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
                }
            }
        }
        return ResponseResult.Success(newScholarships)
    }
}