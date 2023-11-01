package org.mozilla.tiktokreporter.data.model

import org.mozilla.tiktokreporter.data.remote.response.PolicyDTO

data class Policy(
    val id: String,
    val title: String,
    val subtitle: String,
    val text: String,
    val type: Type
) {
    enum class Type {
        Privacy,
        TermsAndConditions
    }
}

fun PolicyDTO.toPolicy(): Policy {
    return Policy(
        id = id,
        title = title,
        subtitle = subtitle,
        text = text,
        type = type.toPolicyType()
    )
}

private fun PolicyDTO.Type.toPolicyType(): Policy.Type {
    return when(this) {
        PolicyDTO.Type.PrivacyPolicy -> Policy.Type.Privacy
        PolicyDTO.Type.TermsOfService -> Policy.Type.TermsAndConditions
    }
}