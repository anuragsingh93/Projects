package com.lookup;
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone
import scala.collection.mutable.HashMap
import org.h2.jdbcx.JdbcConnectionPool
import com.datastax.driver.core.Cluster

object PLookup {
  def cassConnect():List[String]={
var cip = "127.0.0.1"
    var kspace = "ptc_ptc_pod"
    var queries = List[String]();
    var cluster = Cluster.builder().addContactPoint(cip).build();
    var session = cluster.connect(kspace);
    val now = Calendar.getInstance()
    now.add(Calendar.DATE, -1);
    val day = now.get(Calendar.DAY_OF_MONTH)
    val month = now.get(Calendar.MONTH)
    val year = now.get(Calendar.YEAR)
    val sysidMap = HashMap[String, String]();
    var results1 = session.execute(s"select sysid,obs_ts from bundles_by_seen_date where mfr ='ptc' and prod ='ptc' and sch ='pod' and seen_year =$year and seen_month=4 and seen_day=10;");
    while (results1.iterator().hasNext()) {
      val bundle_data = results1.iterator().next()
      println(bundle_data)
      var dttmstamp = bundle_data.getDate("obs_ts")// bundle_data.stripPrefix("""Row[""").stripSuffix("""]""").split(",")
      var sysid = bundle_data.getString("sysid");
      var format = new SimpleDateFormat("yyyy-MM-dd");
      format.setTimeZone(TimeZone.getTimeZone("UTC"));
      val fdate = format.format(dttmstamp);
      println(fdate)
      val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy")
      val result2 = session.execute(s"select sysid,tableinfo_name,tableinfo_count,prev_site_count,obs_dt from section_tbl where mfr='ptc' and prod='ptc' and ec='ptc' and sch='pod' and sysid='$sysid' and obs_dt='${fdate} 00:00:00+0000' and tbl='tableinfo';")
      println(s"select sysid,tableinfo_name,tableinfo_count,prev_site_count,obs_dt from section_tbl where mfr='ptc' and prod='ptc' and ec='ptc' and sch='pod' and sysid='${sysid}' and obs_dt='${fdate} 00:00:00+0000' and tbl='tableinfo';");
      var count = 0;
      while (result2.iterator().hasNext()) {
        val bdata = result2.iterator().next().toString()
        val valarr = bdata.stripPrefix("""Row[""").stripSuffix("""]""").split(",")
        if (valarr(1).trim().toLowerCase().equals("site")) {
          sysidMap.put(valarr(0), valarr(1) + "," + valarr(2) + "," + valarr(3) + "," + valarr(4)) match {
            case None =>
            case Some(value) =>
              val tmp = value.split(",")
              val dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy")
              val zdt = ZonedDateTime.parse(valarr(4).trim, dtf);
              val zdt2 = ZonedDateTime.parse(tmp(3).trim, dtf)
              val epochnew = zdt.toInstant().toEpochMilli()
              val epochold = zdt2.toInstant().toEpochMilli()
              if (epochnew < epochold) {
                sysidMap.put(valarr(0), value)
              }

          }
        }
      }

    }
    cluster.close();
    for ((sysid, value) <- sysidMap) {
      val data = value.split(",")
      if (data(2).trim.equals("NULL")) {
        println(s"INSERT INTO PUBLIC.LOOKUP(KEY, VALUE, EMPS) VALUES('${sysid.trim}', '${data(1).trim()}', 'ptc/ptc/ptc/pod');")
        queries ::= s"INSERT INTO PUBLIC.LOOKUP(KEY, VALUE, EMPS) VALUES('${sysid.trim}', '${data(1).trim()}', 'ptc/ptc/ptc/pod');"
      } else {
        println(s"""UPDATE TABLE LOOKUP SET VALUE='${data(1).trim()}' where sysid='${sysid.trim}';""")
        queries ::= s"""UPDATE TABLE LOOKUP SET VALUE='${data(1).trim()}' where sysid='${sysid.trim}' and value='${data(2).trim}';"""
      }

    }
    println(sysidMap)
    return queries

  
  }
  
  def loadH2(qry:List[String]){
    val cp = JdbcConnectionPool.create("jdbc:h2:tcp://localhost/lcp-codb", "", "");
    val conn = cp.getConnection();
    for(q <-qry){
    conn.createStatement().execute(q)
    }
    
  }
  
}
