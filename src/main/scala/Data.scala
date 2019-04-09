import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

import org.jsoup.Jsoup

import scala.collection.mutable.ListBuffer
import scala.io.Source

object Data {

  // Location where the files will be stores
  val writeLocation = "resources"

  // List of websites from where the articles will be pulled
  val webList = List("https://en.wikipedia.org/wiki/", "https://www.eleconomista.com.mx/",
    "https://www.krone.at/")

  //List of pages for each website in webList.
  // First list corresponds to first element in webList
  val pageList = List(List("Deer", "Cat", "Dog"),
    List("economia/Presentan-2200-amparos-contra-eliminacion-de-la-compensacion-universal-20190408-0052.html",
      "empresas/Sabores-ritmos-y-riquezas-de-Mexico-en-el-Tianguis-Turistico-20190408-0030.html",
      "mercados/Peso-cierra-con-ganancias-dolar-cotiza-en-18.95-unidades-20190408-0043.html"),
    List("1894768", "1899112", "1898639", "1896600"))

  /**
    * Download HTML content of the webpage
    *
    * @param urlString : Url of the page to be downloaded
    * @return
    */

  def fromURL(urlString: String): Document = {
    Document(urlString, Jsoup.connect(urlString).get().outerHtml())
  }

  /**
    * Download webpages if they are not already downloaded and return HTML content of the webpages.
    *
    * @return List of HTML code of the webpages
    */
  def gatherData(): List[Document] = {
    var count = 0

    var resList = ListBuffer[Document]()
    for ((web, index) <- webList.zipWithIndex) {
      for (page <- pageList(index)) {
        count += 1

        val fileName = writeLocation + "/" + count
        if (Files.notExists(Paths.get(fileName))) { //Page are not downloaded
          val pw = new PrintWriter(new File(fileName))
          val res = Data.fromURL(web + page)
          pw.write(res.htmlText)
          resList += res
          pw.close()
        }
        else { // Pages were previously downloaded
          val file = Source.fromFile(fileName)
          val res = file.getLines().mkString
          resList += Document(web + page, res)
        }
      }
    }
    resList.toList
  }
}
