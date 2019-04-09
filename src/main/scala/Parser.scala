import scala.collection.mutable
import scala.io.Source
import scala.util.control.Breaks._

object Parser {
  //Regex to extract only english alphabets.
  val textPattern = "([a-zA-Z])".r

  //Ignore all scripting in the page
  val ignoreTags = Set("script", "noscript")

  //English stop words. Spanish and German stops were skipped.
  val stopWords = Set("a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all",
    "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "amoungst", "amount",
    "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at",
    "back", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being",
    "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot",
    "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during",
    "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every",
    "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five",
    "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go",
    "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers",
    "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest",
    "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many",
    "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must",
    "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none",
    "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto",
    "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part", "per", "perhaps",
    "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she",
    "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something",
    "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their",
    "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon",
    "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru",
    "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until",
    "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever",
    "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while",
    "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would",
    "yet", "you", "your", "yours", "yourself", "yourselves", "the")


  /**
    * Extract text from source HTML
    *
    * @param src source HTML from which text has to be extracted
    * @return
    */
  def getText(src: String): String = {

    val inputStream = Source.fromString(src)
    var tokens = mutable.Stack[String]()

    val buffer = new StringBuilder().append(' ')
    val textBuffer = new StringBuilder()

    //Assumption: Required text is always in body. So ignore everything else.
    ignoreUntilTag(inputStream, "body")
    tokens.push("body")

    breakable {
      while (inputStream.hasNext) {
        val currentTokenChar = inputStream.next()

        currentTokenChar match {
          case '<' => // New tag
            val tag: String = getTag(inputStream)
            if (tag.toLowerCase() == "/body") break

            if (endTag(tag)) { // Check is the current tag is closing
              processAndAppendText(tokens, buffer, textBuffer, tag)
              resetState(tokens, buffer)
            }
            else {
              if (ignoreTags.contains(tag.toLowerCase())) {
                ignoreUntilTag(inputStream, "/" + tag)
              }
              else {
                tokens.push(tag)
              }
            }
          case textPattern(c) =>
            buffer.append(c)

          case _ =>
            if (buffer.last != ' ') {
              buffer.append(' ')
            }
        }

      }
    }
    textBuffer.mkString
  }

  /**
    * Reset the state of buffer and pop a token as a closing token is encountered
    *
    * @param tokens
    * @param buffer
    * @return Last seen token
    */
  private def resetState(tokens: mutable.Stack[String], buffer: StringBuilder) = {
    buffer.clear()
    buffer.append(" ")
    tokens.pop()
  }

  /**
    * Process the seen text when a end token that matches the last seen token matches.
    *
    * @param tokens     Stack of all seen tokens
    * @param buffer     Buffer that stores all seen text since last seen token
    * @param textBuffer Buffer of all processed text
    * @param tag        current tag to be processed
    */
  private def processAndAppendText(tokens: mutable.Stack[String], buffer: StringBuilder, textBuffer: StringBuilder, tag: String): Unit = {
    if (tokens.length > 1) {
      if (tokens.top == tag.substring(1)) {
        if (!ignoreTags.contains(tokens.top.toLowerCase())) {
          val words = buffer.mkString.strip()
          if (!words.isBlank && words.length > 1) {
            textBuffer.append(" ")
            textBuffer.append(
              words
                .toLowerCase()
                .split(' ')
                .filter(s => {
                  !stopWords.contains(s) & s.length > 1
                })
                .mkString(" ")
            )
          }
        }
      }
    }
  }

  /**
    * Check if current tag is a end tag
    *
    * @param tag
    * @return
    */

  private def endTag(tag: String): Boolean = {
    tag.startsWith("/")
  }

  /**
    * Ignore all text until a tag match if found. Used for ignoring all scriptind and non-body text.
    *
    * @param inputStream Stream of text to be processed
    * @param tagName     Tag name until which all text has to be ignored.
    */
  private def ignoreUntilTag(inputStream: Source, tagName: String) = {
    //Ignore everything until body
    breakable {
      while (inputStream.hasNext) {
        val currentTokenChar = inputStream.next()
        currentTokenChar match {
          case '<' => // New tag
            val tag: String = getTag(inputStream)
            if (tagName.equalsIgnoreCase(tag)) {
              break
            }
          case _ =>
        }
      }
    }
  }

  /**
    * Find a begining tag
    *
    * @param inputStream Stream of text that has to be processed.
    * @return Beginning tag name
    */
  private def getTag(inputStream: Source): String = {
    val buffer = new StringBuilder()

    breakable {
      while (inputStream.hasNext) {
        val c = inputStream.next()
        if (c == '>') break
        buffer.append(c)
      }
    }
    buffer.mkString.split(' ')(0)
  }
}
