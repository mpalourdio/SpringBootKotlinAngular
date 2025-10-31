package com.mpalourdio.projects.springbootkotlinangular.csrf

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.*
import org.springframework.util.StringUtils
import java.util.function.Supplier

class SpaCsrfTokenRequestHandler : CsrfTokenRequestHandler {

    private val plain: CsrfTokenRequestResolver = CsrfTokenRequestAttributeHandler()
    private val xor: CsrfTokenRequestHandler = XorCsrfTokenRequestAttributeHandler()

    override fun handle(request: HttpServletRequest?, response: HttpServletResponse?, csrfToken: Supplier<CsrfToken?>) {
        /*
         * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
         * the CsrfToken when it is rendered in the response body.
         */
        xor.handle(request, response, csrfToken)
        /*
         * Render the token value to a cookie by causing the deferred token to be loaded.
         */
        csrfToken.get()
    }

    override fun resolveCsrfTokenValue(request: HttpServletRequest, csrfToken: CsrfToken): String? {
        val headerValue = request.getHeader(csrfToken.headerName)
        /*
         * If the request contains a request header, use CsrfTokenRequestAttributeHandler
         * to resolve the CsrfToken. This applies when a single-page application includes
         * the header value automatically, which was obtained via a cookie containing the
         * raw CsrfToken.
         *
         * In all other cases (e.g., if the request contains a request parameter), use
         * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
         * when a server-side rendered form includes the _csrf request parameter as a
         * hidden input.
         */
        return (if (StringUtils.hasText(headerValue)) plain else xor).resolveCsrfTokenValue(request, csrfToken)
    }
}
