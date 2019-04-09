/***
  * Stores word along with its weight
  * @param word
  * @param weight
  */
case class WordMap(word: String, weight: Double)

/***
  * Stores document along with its raw HTML text
  * @param name
  * @param htmlText
  */
case class Document(name: String, htmlText: String)

/***
  * Stores parsed documents along with list of words present in the document
  * @param name
  * @param words
  */
case class ParsedDocument(name: String, words: List[WordMap])

