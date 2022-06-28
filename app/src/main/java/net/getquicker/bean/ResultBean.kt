package net.getquicker.bean

data class ResultBean(
    val devices: Map<String,String>?,
    val errorMessage: String?,
    val isSuccess: Boolean?,
    val successCount: Int?,
    val timeCost: Int?
)