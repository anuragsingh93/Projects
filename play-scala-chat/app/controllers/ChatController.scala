package controllers
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import models._
import play.api.libs.json._
import play.api.libs.functional.syntax._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class ChatController @Inject()(ch: ChatModel) extends Controller {
  case class createUserForm(id:Int, firstname: String, lastname:String, creationdate:String ,isActive: String)
  val userReads: Reads[User] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "firstname").read[String] and
      (JsPath \ "lastname").read[String] and
      (JsPath \ "creationepoch").read[Long] and
      (JsPath \ "isactive").read[String] and
      (JsPath \ "lastmodified").read[Long] and
      (JsPath \ "lastchecked").readNullable[Long]
    )(User.apply _)

  val msgReads: Reads[Message] = (
    (JsPath \ "msgid").readNullable[Int] and
      (JsPath \ "userid").read[Int] and
      (JsPath \ "msgts").read[Long] and
      (JsPath \ "content").read[String]
    )(Message.apply _)

  /*implicit val locationWrites: Writes[(String,String)] = (
    (JsPath \ "firstname").write[String] and
      (JsPath \ "isValid").write[String]
    )(unlift(String,String))*/
  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    val name=ch.getUserDetails()
    Ok(views.html.index(""))
  }

  def login = Action {
    val name=ch.getUserDetails()
    Ok(views.html.login())
  }

  def loggingin=  Action(parse.json) {request =>
   // println("called ", request.body.asText)
    val json = request.body.validate[User](userReads)
   // val stock = json[User]
    json match {
      case JsError(e) => println(e)
      case JsSuccess(a,p) => println(a, p)
        ch.addUsers(a)
    }
    //println(json)
    val r = Json.toJson("done")
    Ok(r)
    //Ok(views.html.index("loggedin"+id))
  }

/*  val userform : Form[createUserForm]=Form(
    mapping(
      "id" -> number,
      "firstname" -> nonEmptyText,
      "lastname" -> text,
      "creationdate" -> nonEmptyText,
      "isActive"  -> text
    )(createUserForm.apply)(createUserForm.unapply))*/




/*  def addUser = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    userform.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      // There were no errors in the from, so create the person.
      person => {
        repo.create(person.name, person.age).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.ChatController.index)
        }
      }
    )
  }*/

  def getAllUsers=Action{
      val nlist=ch.getUserDetails()
      val json=JsArray(nlist.map(t=> JsObject(Map("firstname"-> JsString(t._1), "isActive" -> JsString(t._2)))))
      //println(json)
      Ok(json)
  }
  def insertChat=Action(parse.json) { request =>
    val json = request.body.validate[Message](msgReads)
    // val stock = json[User]
    json match {
      case JsError(e) => println(e)
      case JsSuccess(a,p) => println(a, p)
        ch.insertMessage(a)
    }
    /*println(json)
    val r = Json.toJson("done")*/
    Ok
  }

  def getMessages(msgid:Int) =Action{
    val mlist=ch.messages(msgid)
    val json=JsArray(mlist.map(t=> JsObject(Map("msgid"-> JsNumber(t.msgid.get), "userid" -> JsNumber(t.userid),"msgts" ->JsNumber(t.msgts),"content" ->JsString(t.content)))))
    Ok(json)
  }

  def logOut()=Action(parse.json){ request =>
    val jsonString = request.body.toString()
    val jsonObject = Json.parse(jsonString)
    val login = jsonObject \ "id"
    val lcheck = jsonObject \ "lastmodified"
    val userid=login.as[Int]
    val lastmodified=lcheck.as[Long]
    println("Logout Id"+login.as[Int])
    println("Last Modified"+lastmodified)
    ch.logout(userid,lastmodified)
    Ok
  }
  def notifyuser(lastchecked:Long) =Action{
    println("lastchecked "+lastchecked)
    val ulist=ch.getnotify(lastchecked)
    val json=JsArray(ulist.map(t=> JsObject(Map("id"-> JsNumber(t.id), "firstname" -> JsString(t.firstname)))))
    Ok(json)
  }
}
