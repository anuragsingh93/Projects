package models

import javax.inject.{Inject, Singleton}
import play.api.db._
import scala.collection.JavaConversions._

class ChatModel @Inject()(db: Database){

    val conn=db.getConnection();

  def addUsers(id:Int, firstName:String, lastName:String,creationDate:String,isActive:String="n"): Unit ={
    //val stmt = conn.createStatement()
    //stmt.executeUpdate(s"Insert into users(firstname,lastname,creationDate,isactive) values ('$firstName','$lastName','$creationDate','n')")
  }

  def getUserDetails() ={
    val names =scala.collection.mutable.ListBuffer[(String, String)]()
    db.withConnection { conn =>
      // do whatever you need with the connection
      val stmt = conn.createStatement()
      val rs=stmt.executeQuery("select * from user")
      while(rs.next()){
       // println(rs.getInt(1))
        //print(" "+rs.getString(2))
         val a=(rs.getString(2),rs.getString("isActive"))
         names +=a
      }
    }

    names.toList
  }

  def insertMessage(msg:Message): Unit ={
    db.withConnection { conn =>
      val stmt = conn.createStatement()
      val rs=stmt.executeUpdate(s"insert into message(userid,msgts,content) values('${msg.userid}',${msg.msgts},'${msg.content}')")
    }
  }

  def messages(msgid:Int): List[Message] ={
    val lMsg=scala.collection.mutable.ListBuffer[Message]();
    val stmt = conn.createStatement()
    println("message id "+msgid)
    val rs=stmt.executeQuery(s"select * from message where msgid>$msgid")
    while(rs.next()){

      lMsg +=Message(Option(rs.getInt(1)),rs.getInt(2),rs.getLong(3),rs.getString(4))
    }
    lMsg.toList
  }

}
