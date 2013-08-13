
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle {

  implicit val swagger = new FlowersSwagger

  override def init(context: ServletContext) {
    context.mount(new DemoService, "/*")
    context.mount (new ResourcesApp, "/api-docs/*")
  }
}
