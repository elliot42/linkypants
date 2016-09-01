package wingnut

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import wingnut.http._
import wingnut.http.HttpMethod
import wingnut.jetty.JettyServer

import com.linkypants.view

object Handler {
  val logger = Logger(LoggerFactory.getLogger("name"))

  def content(): Array[String] = {
    this.synchronized {
      val source = scala.io.Source.fromFile("test.txt")
      val lines = try source.getLines().toArray finally source.close()
      lines
    }
  }

  def shmuckerGet(r: Request): Response = {
    Response(view.IndexView.view(content()), Array(ContentType("text/html")), 200)
  }

  def shmuckerPost(r: Request): Response = {
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

  def shmucker(r: Request): Response = {
    r.method match {
      case HttpMethod.GET => shmuckerGet(r)
      case HttpMethod.POST => shmuckerPost(r)
      case _ => Response("Method not allowed", Array(), 405)
    }
  }
}

object Wingnut {
  def main(argv: Array[String]) {
    val s = new JettyServer(8080, Handler.shmucker)
    s.start
    s.join
  }
}
