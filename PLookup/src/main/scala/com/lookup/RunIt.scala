package com.lookup
import java.io.File
import com.typesafe.config.ConfigFactory
import scala.io.Source
import scala.sys.SystemProperties
import scala.sys.process._

object RunIt{
  def main(args: Array[String]) {
    /*lazy val sp=new SystemProperties()
    lazy val userHome =sp.get("user.dir").get
    lazy val appConf : String = userHome + File.separator + "conf" + File.separator + "App.conf"
    lazy val config = ConfigFactory.parseFileAnySyntax(new File(appConf.toString))
    val gpath=config.getString("grammar_path")*/
    var qry=PTCLookup.cassConnect();
    for(x<-qry){
      println(x)
    }
    val qery=qry.distinct
    PTCLookup.loadH2(qery)
  }
}
