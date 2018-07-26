package models

case class User(id:Int,firstname:String,lastname:String,creationEpoch:Long,isactive:String,lastmodified:Long,lastchecked:Option[Long])
