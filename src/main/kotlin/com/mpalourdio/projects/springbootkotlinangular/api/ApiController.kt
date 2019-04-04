/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.springbootkotlinangular.api

import com.mpalourdio.projects.springbootkotlinangular.config.IGNORED_PATH
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping(path = [IGNORED_PATH])
class ApiController(private val serverProperties: ServerProperties) {

    private val webClient: WebClient = WebClient.create(
            "http://localhost:"
                    + serverProperties.port
                    + StringUtils.stripToEmpty(serverProperties.servlet.contextPath)
                    + IGNORED_PATH
    )

    @PostMapping(path = ["/fast"])
    fun fast(): ResponseEntity<List<String>> {
        val results = ArrayList<String>()
        results.add("Hey, I am the fast response")

        return ResponseEntity.ok(results)
    }

    @GetMapping(path = ["/slow-but-reactive"])
    fun slowButReactive(): Mono<ResponseEntity<List<String>>> {
        return webClient
                .get()
                .uri("/slow")
                .exchange()
                .flatMap { r ->
                    r.toEntity(object : ParameterizedTypeReference<List<String>>() {

                    })
                }
                .onErrorReturn(ResponseEntity.ok(ArrayList()))
    }

    @CrossOrigin(origins = ["http://localhost:4200"], allowedHeaders = ["x-requested-with"])
    @GetMapping(path = ["/slow"])
    fun slow(): ResponseEntity<List<String>> {
        Thread.sleep(3000)
        val results = ArrayList<String>()
        results.add("Hey, I am the slow cross-origin response "
                + "(if performed from a port different from " + serverProperties.port + ")")

        return ResponseEntity.ok(results)
    }
}
