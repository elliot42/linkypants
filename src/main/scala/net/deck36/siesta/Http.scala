package siesta.http

import javax.servlet.http.Cookie

sealed trait Method
object Method {
  case object GET extends Method
  case object PUT extends Method
  case object POST extends Method
  case object DELETE extends Method

  def fromString(str : String) : Option[Method] = {
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
  method: Method,
  body: String)

case class Response(
  body: String,
  headers: Array[Header],
  status: Int)
