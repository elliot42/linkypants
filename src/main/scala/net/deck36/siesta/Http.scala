package siesta.http

import javax.servlet.http.Cookie

sealed trait HttpMethod
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

sealed trait Header
case class Location(location: String) extends Header
case class ContentType(contentType: String) extends Header

case class Request(
  cookies: Array[Cookie],
  method: HttpMethod,
  body: String)

case class Response(
  body: String,
  headers: Array[Header],
  status: Int)
