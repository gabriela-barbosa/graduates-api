package br.uff.graduatesapi.service

import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader


@Service
class CSVService() {
    private fun throwIfFileEmpty(file: MultipartFile): ResponseResult<Nothing?> {
        if (file.isEmpty) return ResponseResult.Error(Errors.FILE_EMPTY)
        return ResponseResult.Success(null)
    }

    fun readCSV(file: MultipartFile): ResponseResult<Pair<List<String>, List<List<String>>>> {
        val result = throwIfFileEmpty(file)
        if (result is ResponseResult.Error) return ResponseResult.Error(result.errorReason!!)
        val reader = BufferedReader(file.inputStream.reader())
        val header = reader.readLine().split(',')

        val lines = reader.lineSequence().filter { it.isNotBlank() && it.isNotEmpty() && it.first() != ',' }
            .map {
                it.split(',')
                    .map { item -> item.replace("-", "") }
            }.toList()

        return ResponseResult.Success(Pair(header, lines))
    }
}