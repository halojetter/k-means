class DocToVec {

  // Stores all the words that has been seen
  var dict: Map[String, Int] = Map()

  def this(dict: Map[String, Int]) {
    this
    this.dict = dict
  }

  /***
    * Prepare dictionary of seen word and store the count of how many times it has been seen
    * @param text String of words from which dictionary is to be created
    * @return
    */
  def prepareDict(text: String): Map[String, Int] = {
    val words = Set(text.split(' '): _*)

    dict = dict ++ words.map(word => word -> (dict.getOrElse(word, 0) + 1)).toMap

    dict
  }

  /***
    * Get TF-IDF vector from the input text
    * @param text String of words from which TF-IDF vector will be computed
    * @param numDocs Total number of documents in the corpus
    * @return TF-IDF vector based on dictionary and input text
    */
  def getTFIDFVector(text: String, numDocs: Int): List[WordMap] = {
    val current_dict = Set(text.split(' '): _*)  //Set of unique words in the current text
    val words = text.split(' ')
    val numWords = words.length


    // Count the number of time a word appears in a document
    val wordCount = current_dict.groupBy(identity).mapValues(_.size)

    //Calculate IDF
    val idfVec = dict.map { case (word, docCount) => word -> Math.log(numDocs * 1.0d / docCount) }

    //Calculate TF
    val tfVec = dict.map { case (word, docCount) => word -> (wordCount.getOrElse(word, 0) * 1.0d / wordCount.size) }

    //Calculate TF-IOF
    tfVec.map { case (word, tf) => WordMap(word, tf * idfVec.getOrElse(word, 0.0)) }.toList.sortBy(wordMap => wordMap.word)
  }
}
