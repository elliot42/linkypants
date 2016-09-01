package linkypants

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import siesta.http._
import siesta.http.Method
import siesta.jetty.JettyServer

import com.linkypants.view

object IndexHandler {
  val logger = Logger(LoggerFactory.getLogger("name"))

  def content(): Array[String] = {
    this.synchronized {
      val source = scala.io.Source.fromFile("test.txt")
      val lines = try source.getLines().toArray finally source.close()
      lines
    }
  }

  def get(r: Request): Response = {
    Response(view.IndexView.view(content()), Array(ContentType("text/html")), 200)
  }

  def post(r: Request): Response = {
    val df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    logger.info(df.format(new java.util.Date()) + "\t" + r.body)
    this.synchronized {
      val fw = new java.io.FileWriter("test.txt", true)
      val decodedUrl = java.net.URLDecoder.decode(r.body, "UTF-8");
      try {
          fw.write(decodedUrl + "\n")
      }
      finally fw.close()
    }
    Response("Foey!", Array(Location("/"), ContentType("text/html")), 303)
  }

  def handler(r: Request): Response = {
    r.method match {
      case Method.GET => get(r)
      case Method.POST => post(r)
      case _ => Response("Method not allowed", Array(), 405)
    }
  }
}

object Linkypants {
  def main(argv: Array[String]) {
    val s = new JettyServer(8080, IndexHandler.handler)
    s.start
    s.join
  }
}
