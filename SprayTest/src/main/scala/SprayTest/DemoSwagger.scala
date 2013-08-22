import org.scalatra.swagger.{NativeSwaggerBase, Swagger}
import org.scalatra.ScalatraServlet
import org.json4s.{DefaultFormats, Formats}

/**
 * Class responsible for listing resources in api ( this time in json, could be in xml)
 */
class ResourcesApp(implicit val swagger: Swagger) extends ScalatraServlet with NativeSwaggerBase {
  implicit override val jsonFormats: Formats = DefaultFormats
}

/**
 * Class which allows for swagger usage, first number is swagger version, second one is for api
 */
class DemoSwagger extends Swagger("1.0", "1")
