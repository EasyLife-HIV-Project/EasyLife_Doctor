package ru.dekabrsky.callersbase.data.model

import androidx.annotation.Keep
import ru.dekabrsky.login.data.model.UserInfoResponse

@Keep
class ChatResponse(
    val chatId: Int,
    val firstUser: UserInfoResponse,
    val secondUser: UserInfoResponse,
    val messages: List<MessageResponse>
)

@Keep
class MessageResponse(
    val messageId: Int,
    val userId: Int,
    val text: String,
    val createdDate: String
)