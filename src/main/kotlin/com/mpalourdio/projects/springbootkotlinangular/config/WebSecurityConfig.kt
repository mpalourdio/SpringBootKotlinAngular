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
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        // Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
        // default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
        val requestHandler = CsrfTokenRequestHandler(XorCsrfTokenRequestAttributeHandler()::handle)
        http {
            csrf {
                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
                csrfTokenRequestHandler = requestHandler
            }
        }

        return http.build()
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

