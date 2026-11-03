package com.partiful.invitemaker.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TemplateCategory(val displayName: String) {
    ALL("Tất cả"),
    BIRTHDAY("Sinh nhật"),
    WEDDING("Tiệc cưới"),
    GRADUATION("Tốt nghiệp"),
    HALLOWEEN("Halloween"),
    CHRISTMAS("Giáng sinh"),
    NEW_YEAR("Năm mới"),
    MUSIC("Âm nhạc"),
    OUTDOOR("Ngoài trời"),
    FRIENDS("Họp mặt bạn bè"),
    GRAND_OPENING("Khai trương"),
    PRIVATE_PARTY("Tiệc riêng tư")
}
