1. Content from three different sources
  a. https://en.wikipedia.org
  b. https://www.eleconomista.com.mx
  c. https://www.krone.at

2. On the first run download the web-pages and save it to file. From next run onwards read from file.

3. Extract text from the html code
  a. Assume only interesting content if present in body part of HTML and extract only english alphabets from it.
  b. Assume the words are space separated and convert web page in to bag of words.

4. Text normalization.
  b. Convert everything to lower case
  c. Remove stop words (http://xpo6.com/list-of-english-stop-words)
  d. Skipped stemming as libraries are not allowed.

5. Preparation for K-Means.
  a. Generate a dictionary of all words in the corpus, so that same length feature vector can be generated for all
     documents.
  b. For all the words in the corpus, compute how many times each word appears in how many documents. This will help
     in computing IDF for each word.

6. Calculate TF and IDF vectors and then TF-IDF for each document.

7. K-Means
  a. For K-Means initialization, randomly select one document per cluster and make it as a centroid for the cluster.
  b. Assign documents to the cluster based on cosine similarity between document and cluster centroid. A document
     belongs to a cluster with maximum similarity.
  c. Compute new centroid for each cluster based on cluster assigment in step 7.b
  d. Repeat step 7.b with new centroid computed in step 7.c until centroid shift is more than the threhold (10E-9)
  e. The output of K-Means depends on the initial centroids chosen. The algorithm always converges, but the cluster
     generation is perfect only when the initial centroid of three clusters belong three different sources.


Sample output:
~~~~ {.txt}

https://en.wikipedia.org/wiki/Deer
 https://en.wikipedia.org/wiki/Cat
 https://en.wikipedia.org/wiki/Dog

https://www.eleconomista.com.mx/economia/Presentan-2200-amparos-contra-eliminacion-de-la-compensacion-universal-20190408-0052.html
 https://www.eleconomista.com.mx/empresas/Sabores-ritmos-y-riquezas-de-Mexico-en-el-Tianguis-Turistico-20190408-0030.html
 https://www.eleconomista.com.mx/mercados/Peso-cierra-con-ganancias-dolar-cotiza-en-18.95-unidades-20190408-0043.html

https://www.krone.at/1894768
 https://www.krone.at/1899112
 https://www.krone.at/1898639
 https://www.krone.at/1896600
~~~~




