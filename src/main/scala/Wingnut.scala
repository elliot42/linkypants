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

  def fromString(str : String) : Option[HttpMethod] = {
    str.toLowerCase match {
      case "get" => Some(GET)
      case "put" => Some(PUT)
      case "post" => Some(POST)
      case "delete" => Some(DELETE)
      case _ => None
    }
  }
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

    def convertServletRequest(r: HttpServletRequest): Option[Request] = {
      val reader = r.getReader()
      val str = Stream.continually(reader.readLine()).
        takeWhile(_ != null).
        mkString("\n")

      val method = HttpMethod.fromString(r.getMethod)
      if (method.isEmpty) {
        None
      }
      else {
        Some(Request(Array(), method.get, str))
      }
    }

    def handle(target: String,
               baseRequest: JettyRequest,
               request: HttpServletRequest,
               response: HttpServletResponse) {
      val resp = convertServletRequest(request) match {
        case Some(req) => handler(req)
        case None => Response("Not goody", 400)
      }
      response.setStatus(resp.status)
      baseRequest.setHandled(true)
      response.getWriter.println(resp.body)
    }
  }

  val server = new Server(port)
  server.setHandler(new JettyHandler())

  def start() = { server.start }
  def join() = { server.join }
}

object Wingnut {
  val logger = Logger(LoggerFactory.getLogger("name"))

  def shmuckerGet(r: Request): Response = {
    Response("GET!", 200)
  }

  def shmuckerPost(r: Request): Response = {
    val df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    logger.info(df.format(new java.util.Date()) + "\t" + r.body)
    Response("Foey!", 200)
  }

  def shmucker(r: Request): Response = {
    r.method match {
      case HttpMethod.GET => shmuckerGet(r)
      case HttpMethod.POST => shmuckerPost(r)
      case _ => Response("Method not allowed", 405)
    }
  }

  def main(argv: Array[String]) {
    val s = new JettyServer(8080, shmucker)
    s.start
    s.join
  }
}
