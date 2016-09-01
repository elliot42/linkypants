package com.linkypants.view

object IndexView {
  val form = """
    <form action="/" method="post">
      <input type="text" size="64" name="url" autofocus/>
    </form>
    """

  def link(rawHref: String): String = {
    val href = rawHref.indexOf("url=") match {
      case -1 => rawHref
      case _ => rawHref.substring(4)
    }
    Array("<li>", "<a href=\"", href, "\">", href, "</a>", "</li>", "\n").mkString
  }

  def view(lines: Array[String]): String = {
    return form + "<ul>" + lines.reverse.map(l => link(l)).mkString + "</ul>"
  }
}
