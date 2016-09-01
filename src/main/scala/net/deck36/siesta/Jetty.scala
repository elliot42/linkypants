package wingnut.jetty

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.Cookie

import org.eclipse.jetty.server.{Request => JettyRequest}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler

import wingnut.http._

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
        case None => Response("Not goody", Array(), 400)
      }

      for (h <- resp.headers) {
        h match  {
          case Location(location) => response.addHeader("Location", location)
          case ContentType(contentType) => response.addHeader("Content-Type", contentType)
          case _ => None
        }
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
