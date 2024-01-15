/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.springbootkotlinangular.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestHandler
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler

@Configuration
class WebSecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        val tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
        val delegate: XorCsrfTokenRequestAttributeHandler = getXorCsrfTokenRequestAttributeHandler()
        // Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
        // default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
        val requestHandler = CsrfTokenRequestHandler(delegate::handle)
        http {
            csrf {
                csrfTokenRepository = tokenRepository
                csrfTokenRequestHandler = requestHandler
            }
        }

        return http.build()
    }

    private fun getXorCsrfTokenRequestAttributeHandler(): XorCsrfTokenRequestAttributeHandler {
        val delegate = XorCsrfTokenRequestAttributeHandler()
        // By setting the csrfRequestAttributeName to null, the CsrfToken must first be loaded to determine what attribute name to use.
        // This causes the CsrfToken to be loaded on every request.
        // Another solution would have been to create a OncePerRequestFilter to handle CrsfFilter.
        delegate.setCsrfRequestAttributeName(null)
        return delegate
    }

    @Bean
    @Order(-1)
    @Throws(Exception::class)
    fun staticResources(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher("/static/**")
            authorizeHttpRequests {
                authorize(anyRequest, permitAll)
            }
        }

        return http.build()
    }
}

