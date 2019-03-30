/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.springbootkotlinangular.frontcontroller

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.servlet.resource.TransformedResource
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

@Service
class FrontControllerHandler(private val serverProperties: ServerProperties) {

    companion object {
        private const val BASE_HREF_PLACEHOLDER = "#base-href#"
        private val FRONT_CONTROLLER_ENCODING: String = StandardCharsets.UTF_8.name()
        const val URL_SEPARATOR = "/"
        const val FRONT_CONTROLLER = "index.html"
    }

    fun buildFrontControllerResource(resource: Resource): TransformedResource {
        Objects.requireNonNull(resource, "resource cannot be null")

        try {
            var frontControllerContent = IOUtils.toString(resource.inputStream, FRONT_CONTROLLER_ENCODING)
            if (!frontControllerContent.contains(BASE_HREF_PLACEHOLDER)) {
                throw FrontControllerException("$FRONT_CONTROLLER does not contain $BASE_HREF_PLACEHOLDER")
            }

            frontControllerContent = frontControllerContent.replace(BASE_HREF_PLACEHOLDER, buildBaseHref())
            return TransformedResource(resource, frontControllerContent.toByteArray(charset(FRONT_CONTROLLER_ENCODING)))
        } catch (e: IOException) {
            throw FrontControllerException("Unable to perform $FRONT_CONTROLLER tranformation", e)
        }

    }

    private fun buildBaseHref(): String {
        val contextPath = StringUtils.stripToNull(serverProperties.servlet.contextPath)

        return if (contextPath == null || contextPath == URL_SEPARATOR)
            URL_SEPARATOR
        else
            contextPath + URL_SEPARATOR
    }
}
