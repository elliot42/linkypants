package wingnut

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.Cookie

import org.eclipse.jetty.server.{Request => JettyRequest}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

trait HttpMethod
object HttpMethod {
  case object GET extends HttpMethod
  case object PUT extends HttpMethod
  case object POST extends HttpMethod
  case object DELETE extends HttpMethod
}

case class Request(
  cookies: Array[Cookie],
  method: HttpMethod,
  body: String)

case class Response(
  body: String,
  status: Int
)

class JettyServer(port: Int, handler: (Request) => Response) {
  class JettyHandler extends AbstractHandler {
    def convertServletRequest(r: HttpServletRequest): Request = {
      val reader = r.getReader()
      val str = Stream.continually(reader.readLine()).
        takeWhile(_ != null).
        mkString("\n")
      Request(Array(), HttpMethod.GET, str)
    }

    def handle(target: String,
               baseRequest: JettyRequest,
               request: HttpServletRequest,
               response: HttpServletResponse) {
      val r = handler(convertServletRequest(request))
      response.setStatus(r.status)
      baseRequest.setHandled(true)
      response.getWriter.println(r.body)
    }
  }

  val server = new Server(port)
  server.setHandler(new JettyHandler())

  def start() = { server.start }
  def join() = { server.join }
}

object Wingnut {
  val logger = Logger(LoggerFactory.getLogger("name"))

  def shmucker(r: Request): Response = {
    logger.info(r.body)
    Response("Foey!", 200)
  }

  def main(argv: Array[String]) {
    val s = new JettyServer(8080, shmucker)
    s.start
    s.join
  }
}
