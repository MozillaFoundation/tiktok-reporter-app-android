package org.mozilla.tiktokreporter.util

class OneTimeEvent<T>(
    private val value: T
) {

    private var isConsumed = false

    fun get(): T? =
        if (isConsumed) {
            null
        } else {
            isConsumed = true
            value
        }

    override fun equals(other: Any?): Boolean {
        val otherAny =  other as? OneTimeEvent<*> ?: return false

        return this.isConsumed == otherAny.isConsumed
                && this.value == otherAny.value
    }

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + isConsumed.hashCode()
        return result
    }
}

inline fun <reified T: Any> Any.toOneTimeEvent() : OneTimeEvent<T> = OneTimeEvent(this as T)