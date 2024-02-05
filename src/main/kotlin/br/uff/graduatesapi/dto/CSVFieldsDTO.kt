package br.uff.graduatesapi.dto

data class CSVFieldsDTO(
    val name: String,
    val defenseMinute: String,
    val titleDate: String,
    val advisorName: String,
    val ciProgramName: String,
    val email: String,
    val position: String,
    val institutionName: String? = null,
    val institutionType: String? = null,
    val successCase: String,
    val cnpqLevel: String,
    val postDoctorate: String,
    val finishedDoctorateOnUFF: Boolean,
    val finishedMasterDegreeOnUFF: Boolean,

    )

fun List<String>.toCSVFieldsDTO(isDoctorateGraduate: Boolean, headers: List<String>): CSVFieldsDTO {
    val noInstitution = this[8] == "1"
    val institutionName = this[7]
    val hasInstitution = !noInstitution && institutionName.isNotEmpty()
    val institutionType = if (hasInstitution) {
        val institutionTypeFields = this.subList(9, this.size - 4)
        val institutionTypeHeaders = headers.subList(9, this.size - 4)
        val institutionTypeIndex = institutionTypeFields.indexOf("1")
        institutionTypeHeaders[institutionTypeIndex].replace("\"", "")
    } else null

    return CSVFieldsDTO(
        name = this[0],
        defenseMinute = this[1],
        titleDate = this[2],
        advisorName = this[3],
        ciProgramName = this[4],
        email = this[5],
        position = this[6],
        institutionName = if (hasInstitution) institutionName else null,
        institutionType = institutionType,
        successCase = this[this.size - 4],
        cnpqLevel = this[this.size - 3],
        postDoctorate = this[this.size - 2],
        finishedDoctorateOnUFF = !isDoctorateGraduate && this.last() == "1",
        finishedMasterDegreeOnUFF = isDoctorateGraduate && this.last() == "1",
    )
}