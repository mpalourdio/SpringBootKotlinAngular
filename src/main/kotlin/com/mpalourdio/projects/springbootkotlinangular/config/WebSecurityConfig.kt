/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.springbootkotlinangular.config

import com.mpalourdio.projects.springbootkotlinangular.csrf.SpaCsrfTokenRequestHandler
import org.springframework.boot.web.server.Cookie
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration(proxyBeanMethods = false)
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        val tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
        tokenRepository.setCookieCustomizer { c -> c.secure(true).sameSite(Cookie.SameSite.STRICT.attributeValue()) }

        http {
            csrf {
                csrfTokenRepository = tokenRepository
                csrfTokenRequestHandler = SpaCsrfTokenRequestHandler()
            }
        }

        return http.build()
    }

    @Bean
    @Order(-1)
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

