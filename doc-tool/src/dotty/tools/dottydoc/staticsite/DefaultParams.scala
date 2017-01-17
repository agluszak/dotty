package dotty.tools
package dottydoc
package staticsite

import java.util.{ List => JList }
import model.{ Entity, NonEntity }

case class DefaultParams(
  docs: JList[_],
  page: PageInfo,
  site: SiteInfo,
  entity: Entity = NonEntity
) {
  import model.java._
  import scala.collection.JavaConverters._

  def toMap: Map[String, AnyRef] = Map(
    "docs" -> docs,

    "page" -> Map(
      "url" -> page.url,
      "date" -> page.date,
      "path" -> page.path
    ),

    "site" -> Map(
      "baseurl" -> site.baseurl,
      "posts" -> site.posts.map(_.toMap),
      "project" -> site.projectTitle
    ).asJava,

    "entity" -> entity.asJava()
  )

  def withPosts(posts: Array[BlogPost]): DefaultParams =
    copy(site = SiteInfo(site.baseurl, site.projectTitle, posts))

  def withUrl(url: String): DefaultParams =
    copy(page = PageInfo(url))

  def withEntity(e: model.Entity) = copy(entity = e)

  def withDate(d: String) = copy(page = PageInfo(page.url, d))
}

case class PageInfo(url: String, date: String = "") {
  val path: Array[String] = url.split('/').reverse.drop(1)
}

case class SiteInfo(baseurl: String, projectTitle: String, posts: Array[BlogPost])
