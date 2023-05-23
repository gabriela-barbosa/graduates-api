package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CNPQScholarshipDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.CNPQScholarship
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.repository.CNPQScholarshipRepository
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

  fun save(cnpqScholarship: CNPQScholarship): ResponseResult<CNPQScholarship> =
    try {
      val result = cnpqScholarshipRepository.save(cnpqScholarship)
      ResponseResult.Success(result)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_CREATE_CNPQSCHOLARSHIP)
    }


  fun createCNPQScholarships(
    levels: List<CNPQScholarshipDTO>,
    graduate: Graduate
  ): ResponseResult<List<CNPQScholarship>> {
    val scholarships = mutableListOf<CNPQScholarship>()
    for (level in levels) {
      val cnpqLevel = when (val result = cnpqLevelService.findById(level.levelId)) {
        is ResponseResult.Success -> result.data!!
        is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
      }


      val scholarship = when (val result = save(
        CNPQScholarship(
          level = cnpqLevel,
          graduate = graduate,
          startedAt = level.startedAt,
          endedAt = level.endedAt
        )
      )) {
        is ResponseResult.Success -> result.data!!
        is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
      }

      scholarships.add(scholarship)
    }
    return ResponseResult.Success(scholarships)
  }

//  fun createOrUpdateCNPQScholarship(id: UUID, graduate: Graduate): ResponseResult<CNPQScholarship> {
//
//    val level = when (val resultLevel = cnpqLevelService.findById(id)) {
//      is ResponseResult.Success -> resultLevel.data!!
//      is ResponseResult.Error -> return ResponseResult.Error(resultLevel.errorReason)
//    }
//
//    val scholarships = findActualCNPQScholarshipsByGraduate(graduate)
//
//    for (scholarship in scholarships) {
//      if (scholarship.level.id != id) {
//        if (scholarship != null) {
//          scholarship.endedAt = LocalDate.now()
//          save(scholarship)
//        }
//        val newScholarship = CNPQScholarship(
//          level = level,
//          graduate = graduate,
//        )
//        val resultCNPQ = save(newScholarship)
//        if (resultCNPQ is ResponseResult.Error) return ResponseResult.Error(resultCNPQ.errorReason)
//        scholarship = resultCNPQ.data!!
//      }
//    }
//
//
//    return ResponseResult.Success(scholarship)
//  }
}