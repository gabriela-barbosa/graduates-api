package br.uff.graduatesapi.enum

enum class NamePendingFieldsEnum(val value: String) {
    POSITION("position"),
    WORK_HISTORY("workHistory"),
    CNPQ_SCHOLARSHIP("cnpqScholarship"),
    POST_DOCTORATE("postDoctorate"),
    FINISHED_DOCTORATE_ON_UFF("hasFinishedDoctorateOnUFF"),
    FINISHED_MASTER_DEGREE_ON_UFF("hasFinishedMasterDegreeOnUFF"),
}