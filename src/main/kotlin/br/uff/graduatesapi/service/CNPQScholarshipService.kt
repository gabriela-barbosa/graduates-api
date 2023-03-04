package br.uff.graduatesapi.service

import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.CNPQScholarship
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.repository.CNPQScholarshipRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class CNPQScholarshipService(
    private val cnpqScholarshipRepository: CNPQScholarshipRepository,
    private val cnpqLevelService: CNPQLevelService,
) {
    fun findActualCNPQScholarshipByGraduate(graduate: Graduate): CNPQScholarship? {
        return cnpqScholarshipRepository.findActualCNPQScholarshipByGraduate(graduate)
    }

    fun save(cnpqScholarship: CNPQScholarship): ResponseResult<CNPQScholarship> =
        try {
            val result = cnpqScholarshipRepository.save(cnpqScholarship)
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_CREATE_CNPQSCHOLARSHIP)
        }


    fun createOrUpdateCNPQScholarship(id: UUID, graduate: Graduate): ResponseResult<CNPQScholarship> {

        val level = when (val resultLevel = cnpqLevelService.findById(id)) {
            is ResponseResult.Success -> resultLevel.data!!
            is ResponseResult.Error -> return ResponseResult.Error(resultLevel.errorReason)
        }

        var scholarship = findActualCNPQScholarshipByGraduate(graduate)

        if (scholarship == null || scholarship.level.id != id) {
            if (scholarship != null) {
                scholarship.endYear = LocalDate.now()
                save(scholarship)
            }
            val newScholarship = CNPQScholarship(
                level = level,
                graduate = graduate,
            )
            val resultCNPQ = save(newScholarship)
            if (resultCNPQ is ResponseResult.Error) return ResponseResult.Error(resultCNPQ.errorReason)
            scholarship = resultCNPQ.data!!
        }
        return ResponseResult.Success(scholarship)
    }
}