package dev.akif.tapik.spring.restclient

import dev.akif.tapik.Helpers
import org.springframework.web.client.RestClient

/**
 * Convenience marker for Tapik clients implemented with Spring [RestClient].
 *
 * @see RestClientInterpreter
 */
interface RestClientBasedClient : Helpers {
    /** Underlying [RestClient] instance used to execute requests. */
    val restClient: RestClient

    /**
     * Lazily created interpreter that applies Tapik's response validation rules.
     *
     * @return interpreter bound to [restClient].
     */
    val interpreter: RestClientInterpreter
        get() = RestClientInterpreter(restClient)
}
