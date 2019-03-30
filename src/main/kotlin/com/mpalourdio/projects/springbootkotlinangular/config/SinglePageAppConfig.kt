/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.springbootkotlinangular.config

import com.mpalourdio.projects.springbootkotlinangular.frontcontroller.FrontControllerHandler
import com.mpalourdio.projects.springbootkotlinangular.frontcontroller.FrontControllerHandler.Companion.FRONT_CONTROLLER
import com.mpalourdio.projects.springbootkotlinangular.frontcontroller.FrontControllerHandler.Companion.URL_SEPARATOR
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver
import java.io.IOException
import java.util.*

@Configuration
class SinglePageAppConfig(
        resourceProperties: ResourceProperties,
        private val frontControllerHandler: FrontControllerHandler,
        private val applicationContext: ApplicationContext
) : WebMvcConfigurer {

    companion object {
        const val IGNORED_PATH = "/api"
        const val PATH_PATTERNS = "/**"
    }

    private val staticLocations: Array<String> = resourceProperties.staticLocations

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler(PATH_PATTERNS)
                .addResourceLocations(*staticLocations)
                .resourceChain(true)
                .addResolver(SinglePageAppResourceResolver())
    }

    private inner class SinglePageAppResourceResolver internal constructor() : PathResourceResolver() {

        private val frontControllerResource: Resource?

        init {
            this.frontControllerResource = Arrays
                    .stream(staticLocations)
                    .map { path -> applicationContext.getResource(path + FRONT_CONTROLLER) }
                    .filter(this::resourceExistsAndIsReadable)
                    .findFirst()
                    .map(frontControllerHandler::buildFrontControllerResource)
                    .orElseGet { null }
        }

        @Throws(IOException::class)
        override fun getResource(resourcePath: String, location: Resource): Resource? {
            val resource = location.createRelative(resourcePath)
            if (resourceExistsAndIsReadable(resource)) {
                //if the asked resource is index.html itself, we serve it with the base-href rewritten
                return if (resourcePath.endsWith(FRONT_CONTROLLER)) {
                    frontControllerResource
                } else resource
                //here we serve js, css, etc.
            }

            //do not serve a Resource on an ignored path
            if ((URL_SEPARATOR + resourcePath).startsWith(IGNORED_PATH)) {
                return null
            }

            //we are in the case of an angular route here, we rewrite to index.html
            return if (resourceExistsAndIsReadable(location.createRelative(FRONT_CONTROLLER))) {
                frontControllerResource
            } else null

        }

        private fun resourceExistsAndIsReadable(resource: Resource): Boolean {
            return resource.exists() && resource.isReadable
        }
    }
}
