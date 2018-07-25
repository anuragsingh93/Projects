package models

case class Message(msgid: Option[Int],userid: Int, msgts:Long, content:String)
