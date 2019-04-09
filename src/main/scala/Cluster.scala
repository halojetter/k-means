object Cluster {
  def main(args: Array[String]): Unit = {

    // Download the webpages
    val docs = Data.gatherData()

    //Parse, clean and tokenize
    val parsedText = docs.map(doc => doc.name -> Parser.getText(doc.htmlText))

    // Build a dictionary of all words in all documents. It contains the number of documents that have a particular word
    val dict = parsedText.map(doc => (new DocToVec).prepareDict(doc._2)).reduce((a, b) => a ++ b.map { case (k, v) => k -> (a.getOrElse(k, 0) + v) })

    // Used to create feature vector
    val docToVec = new DocToVec(dict)

    //Feature vector for all documents
    val x = parsedText.map(doc => ParsedDocument(doc._1, docToVec.getTFIDFVector(doc._2, parsedText.length)))


    //Runs KMmeans
    val cluster = KMeans.cluster(x, 3)

    //Print the url of the documents in the cluster
    val p = cluster.map{case (k,v) => v.map(_.name)}
    println(p.toString().replaceAll(",|List", "\n").replaceAll("\\(|\\)", ""))

  }
}
