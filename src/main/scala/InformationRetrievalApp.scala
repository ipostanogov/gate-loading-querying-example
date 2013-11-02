/*
 *  InformationRetrievalApp.java
 *
 *  Copyright (c) 1998-2003, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Marin Dimitrov, 27/Jan/2003
 *
 *  $Id: InformationRetrievalApp.java,v 1.4 2004/12/14 14:36:24 niraj Exp $
 */

import java.io.File
import java.net._
import java.util
import gate._
import gate.util.Err
import gate.util.Out
import gate.util.GateException
import gate.creole.ir._
import gate.creole.ir.lucene._
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils.concat
import org.apache.log4j.{Level, Logger}

object InformationRetrievalApp {
  val appFolderPath = concat(System.getProperty("user.home"), ".textyour")
  val indexLocationPath: String = concat(appFolderPath, "index")
  val pluginsFolderPath = concat(appFolderPath, "plugins")
  val serialDataStoreFilePath: String = concat(appFolderPath, "datastore")

  def initFolders() {
    scala.collection.Set(appFolderPath, indexLocationPath, pluginsFolderPath).foreach({
      new File(_) {
        if (!exists() && !mkdirs())
          throw new Exception
      }
    })
    new File(serialDataStoreFilePath) {
      if (exists())
        FileUtils.deleteDirectory(this)
    }
  }

  def main(args: Array[String]) {
    Logger.getRootLogger.setLevel(Level.OFF)
    val irApp = new InformationRetrievalApp
    try {
      initFolders()
      System.setProperty("gate.plugins.home", pluginsFolderPath)
      System.setProperty("gate.site.config", new File(getClass.getResource("/gate.xml").toURI).getAbsolutePath)
      Gate.init()
      Out.prln("GATE initialised...")
    }
    catch {
      case gex: GateException => {
        Err.prln("cannot initialise GATE...")
        gex.printStackTrace()
        return
      }
    }
    try {
      val sds: DataStore = Factory.createDataStore("gate.persist.SerialDataStore", "file:///" + serialDataStoreFilePath)
      sds.open()
      val corpus: Corpus = irApp.createTestCorpus()
      val serialCorpus: Corpus = sds.adopt(corpus, null).asInstanceOf[Corpus]
      sds.sync(serialCorpus)
      val indexedCorpus: IndexedCorpus = serialCorpus.asInstanceOf[IndexedCorpus]
      val did: DefaultIndexDefinition = new DefaultIndexDefinition
      did.setIrEngineClassName(Predef.classOf[LuceneIREngine].getName)
      did.setIndexLocation(indexLocationPath)
      did.addIndexField(new IndexField("author", null, false))
      did.addIndexField(new IndexField("content", new DocumentContentReader, false))
      indexedCorpus.setIndexDefinition(did)
      indexedCorpus.getIndexManager.deleteIndex()
      indexedCorpus.getIndexManager.createIndex()
      indexedCorpus.getIndexManager.optimizeIndex()
      val search: Search = new LuceneSearch
      search.setCorpus(indexedCorpus)
      val query: String = "+content:\"until there's a cure\" +author:foundation"
      val res: QueryResultList = search.search(query)
      val it: util.Iterator[_] = res.getQueryResults
      while (it.hasNext) {
        val qr: QueryResult = it.next.asInstanceOf[QueryResult]
        val score: Float = qr.getScore
        val resultDoc: Document = sds.getLr("gate.corpora.DocumentImpl", qr.getDocumentID).asInstanceOf[Document]
        Out.prln("Query1: DOC_NAME=" + resultDoc.getName)
        Out.prln("Query1: score = " + score)
        Out.prln("Query1: author = " + resultDoc.getFeatures.get("author"))
        Out.prln("------------")
      }
      val query2: String = "+author:foundation"
      val res2: QueryResultList = search.search(query2)
      val it2: util.Iterator[_] = res2.getQueryResults
      while (it2.hasNext) {
        val qr: QueryResult = it2.next.asInstanceOf[QueryResult]
        val score: Float = qr.getScore
        val resultDoc: Document = sds.getLr("gate.corpora.DocumentImpl", qr.getDocumentID).asInstanceOf[Document]
        Out.prln("Query2: DOC_NAME=" + resultDoc.getName)
        Out.prln("Query2: score = " + score)
        Out.prln("Query2: author = " + resultDoc.getFeatures.get("author"))
        Out.prln("------------")
      }
      Out.prln("done...")
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace(Err.getPrintWriter)
      }
    }
  }
}

class InformationRetrievalApp {
  def createTestCorpus() = {
    val doc1: Document = Factory.newDocument(new URL("http://www.wish.org/"))
    doc1.getFeatures.put("author", "Make-A-Wish Foundation")
    doc1.setName("Make-A-Wish document")
    val doc2: Document = Factory.newDocument(new URL("http://www.until.org"))
    doc2.getFeatures.put("author", "Until There's A Cure Foundation")
    doc2.setName("until.org document")
    assert(doc1 != null && doc2 != null)
    val result: Corpus = Factory.newCorpus("test corpus")
    assert(result != null)
    result.add(doc1)
    result.add(doc2)
    result
  }
}