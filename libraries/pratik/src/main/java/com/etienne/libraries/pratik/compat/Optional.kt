package com.etienne.libraries.pratik.compat

data class Optional<T> private constructor(private val value: T? = null) {

    /**
     * is true if it is not empty
     */
    val isPresent: Boolean
        get() = value != null

    /**
     * @return the contained value
     * @throws NoSuchElementException if no value is present
     */
    fun get(): T {
        if (value == null) {
            throw NoSuchElementException("No value present")
        }
        return value
    }

    /**
     * @param consumer a lambda that will be run if a value is present in the Optional<T> receiver
     * @return an object that can be combined with IfNotPresent.orElse(consumer: () -> Unit)
     */
    fun ifPresent(consumer: (T) -> Unit): IfNotPresent {
        value?.run { consumer.invoke(value) }
        return IfNotPresent()
    }

    /**
     * @param predicate that is used to filter the current Optional<T> receiver
     * @return a new Optional<T> with a value if the predicate returns true, empty otherwise
     */
    fun filter(predicate: (T) -> Boolean): Optional<T> =
        value?.let { if (predicate.invoke(it)) of(it) else empty() } ?: empty()

    /**
     * @param mapper a lambda used to map the value of the Optional<T> receiver to a new U value
     * @return a new Optional<U> with the mapped value
     */
    fun <U> map(mapper: (T) -> U) =
        value?.let { of(mapper.invoke(it)) } ?: empty()

    /**
     * @param mapper a lambda used to map the value of the Optional<T> receiver to a new Optional<U>
     * @return a new Optional<U>
     */
    fun <U> flatMap(mapper: (T) -> Optional<U>) =
        value?.let { mapper.invoke(it) } ?: empty()

    /**
     * @param other a nullable default value if no value is present in the Optional<T> receiver
     * @return the value of the Optional<T> receiver or a default one
     */
    fun orElseNullable(other: T?) = value ?: other

    /**
     * @param other a default value if no value is present in the Optional<T> receiver
     * @return the value of the Optional<T> receiver or a default one
     */
    fun orElse(other: T) = value ?: other

    /**
     * @return the value of the Optional<T> receiver or null
     */
    fun orNull() = value

    /**
     * @param other a lambda to provide a nullable default value if no value is present in the Optional<T> receiver
     * @return the value of the Optional<T> receiver or a default one
     */
    fun orElseGetNullable(other: () -> T?) = value ?: other.invoke()

    /**
     * @param other a lambda to provide a default value if no value is present in the Optional<T> receiver
     * @return the value of the Optional<T> receiver or a default one
     */
    fun orElseGet(other: () -> T) = value ?: other.invoke()

    /**
     * @param exceptionSupplier a lambda that provides an exception if called
     * @return the value of the Optional<T> receiver or throws an Exception returned by the exceptionSupplier
     */
    fun <X : Throwable> orElseThrow(exceptionSupplier: () -> X) =
        value ?: throw exceptionSupplier.invoke()

    override fun toString() =
        value?.let { String.format("Optional[%s]", it) } ?: "Optional.empty"

    companion object {

        /**
         * Creates a new empty Optional<T>
         */
        fun <T> empty() = Optional<T>(null)

        /**
         * Creates a new Optional<T> with a non-null value
         * @param value the value to store in the Optiona<T>
         */
        fun <T> of(value: T) = Optional(value)

        /**
         * Creates a new Optional<T> with a null value
         * @param value the nullable value to store in the Optiona<T>
         */
        fun <T> ofNullable(value: T?) = value?.let { of(it) } ?: empty()
    }

    inner class IfNotPresent internal constructor(){

        /**
         * This method can be used with Optional<T>.ifPresent()
         * @param consumer a lambda that will be run if the Optional<T> receiver is empty
         */
        infix fun orElse(consumer: () -> Unit) {
            if (value == null) {
                consumer.invoke()
            }
        }
    }
}
