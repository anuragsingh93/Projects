package models

import javax.inject.{Inject, Singleton}
import play.api.db._
import scala.collection.JavaConversions._

class ChatModel @Inject()(db: Database){

    val conn=db.getConnection();

  def addUsers(u:User): Unit ={
    val stmt = conn.createStatement()
    stmt.executeUpdate(s"Insert into user(id,firstname,lastname,creationepoch,isactive,lastmodified) values (${u.id},'${u.firstname}','${u.lastname}',${u.creationEpoch},'${u.isactive}',${u.lastmodified})")
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
         val a=(rs.getString(2),rs.getString("isactive"))
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
    //println("message id "+msgid)
    val rs=stmt.executeQuery(s"select * from message where msgid>$msgid")
    while(rs.next()){

      lMsg +=Message(Option(rs.getInt(1)),rs.getInt(2),rs.getLong(3),rs.getString(4))
    }
    lMsg.toList
  }

  def logout(userid:Int,lastmodified:Long): Unit ={
    db.withConnection { conn =>
      val stmt = conn.createStatement()
      val rs=stmt.executeUpdate(s"update user set isactive='n', lastmodified=$lastmodified where id=$userid")
    }
  }

  def getnotify(lastchecked:Long): List[User] ={
    val lUser=scala.collection.mutable.ListBuffer[User]();
    val stmt = conn.createStatement()
    val rs=stmt.executeQuery(s"select * from user where lastmodified>$lastchecked")
    while(rs.next()){
      lUser +=User(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getLong(4),rs.getString(5),rs.getLong(6),Option(rs.getLong(7)))
    }
    val stmt2 = conn.createStatement()
    stmt2.executeUpdate(s"update user set lastchecked=$lastchecked")
    lUser.toList
  }
}
