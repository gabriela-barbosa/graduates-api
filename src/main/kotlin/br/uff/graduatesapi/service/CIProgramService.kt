package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CIProgramDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.CIProgram
import br.uff.graduatesapi.repository.CIProgramRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CIProgramService(
  private val programRepository: CIProgramRepository,
) {
  fun findPrograms(): ResponseResult<List<CIProgram>> {
    return try {
      val result = programRepository.findAllActives()
      ResponseResult.Success(result)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_RETRIEVE_CI_PROGRAMS)
    }
  }

  fun findProgram(id: Int): ResponseResult<CIProgram?> {
    val result = programRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.CI_PROGRAM_NOT_FOUND)
    return ResponseResult.Success(result)
  }

  fun deleteProgram(id: Int): ResponseResult<Nothing?> {
    return try {
      programRepository.deleteById(id)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_DELETE_CI_PROGRAM)
    }
  }

  fun createProgram(ciProgramDTO: CIProgramDTO): ResponseResult<Nothing?> {
    val program = CIProgram(initials = ciProgramDTO.initials)
    return try {
      programRepository.save(program)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_CREATE_CI_PROGRAM)
    }
  }

  fun editProgram(ciProgramDTO: CIProgramDTO, id: Int): ResponseResult<Nothing?> {
    return try {
      val result = this.findProgram(id)
      if (result is ResponseResult.Error)
        return ResponseResult.Error(Errors.INVALID_DATA)
      val ciProgram = result.data!!
      ciProgram.initials = ciProgramDTO.initials
      programRepository.updateInitials(ciProgram.initials, id)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_UPDATE_CI_PROGRAM)
    }
  }
}