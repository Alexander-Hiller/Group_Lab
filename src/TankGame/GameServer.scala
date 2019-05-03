package TankGame

import java.io.FileNotFoundException

import akka.actor.Actor
import akka.io.Tcp
import java.net.InetSocketAddress
import java.nio.file.{Files, Paths}

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import play.api.libs.json.{JsValue, Json}

import scala.io.Source


case object UpdateGames

case class GameState(gameState: String)

class GameServer extends Actor {
  import Tcp._
  import context.system
  var state: String = ""
  val delimiter = "~"
  var buffer: String = ""
  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 65000))
  var clients: Set[ActorRef] = Set()
  var clientMap:Map[String,ActorRef]=Map()

  val JSONfile: String ="operatingFile.json"
  println("Game Server is up!")

  override def receive: Receive = {
    case b: Bound => println("Listening on port: " + b.localAddress.getPort)
    case c: Connected =>
      println("Client Connected: " + c.remoteAddress)
      this.clients = this.clients + sender()
      sender() ! Register(self)
    case PeerClosed =>
      println("Client Disconnected: " + sender())
      this.clients = this.clients - sender()
    case r: Received =>

      buffer= r.data.utf8String
      val parsed: JsValue = Json.parse(buffer)
      val action:String = (parsed \ "action").as[String]
      if(action=="connected"){
        try {
          state = Source.fromFile(JSONfile).mkString
        }catch{
          case ex: FileNotFoundException => ex.printStackTrace()
        }
        //println("Sending States")
        val message: String = state+delimiter
        println("User Connected")
        this.clients.foreach((client: ActorRef) => client ! Write(ByteString(message)))
      }
      if(action=="disconnected"){
        println("User disconnected")
      }
      if(action=="update"){
        val JSONdata:String = (parsed \ "JSONdata").as[String]
        println(JSONdata)
        Files.write(Paths.get(JSONfile), JSONdata.getBytes())
        try {
          state = Source.fromFile(JSONfile).mkString
        }catch{
          case ex: FileNotFoundException => ex.printStackTrace()
        }
        //println("Sending States")
        val message: String = state+delimiter
        this.clients.foreach((client: ActorRef) => client ! Write(ByteString(message)))
      }
      if(action=="test"){
        println("received a test")
        //this.clients.foreach((client: ActorRef) => client ! Write(ByteString("Test Satisfactory")))
      }


    case UpdateGames =>
      try {
        state = Source.fromFile(JSONfile).mkString
      }catch{
        case ex: FileNotFoundException => ex.printStackTrace()
      }
      //println("Sending States")
      val message: String = state+delimiter
      this.clients.foreach((client: ActorRef) => client ! Write(ByteString(message)))


  }

}

object GameServer{

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem()

    import scala.concurrent.duration._
    import actorSystem.dispatcher

    val server = actorSystem.actorOf(Props(classOf[GameServer]))
    actorSystem.scheduler.schedule(0 milliseconds, 100 milliseconds, server, UpdateGames)
    //actorSystem.scheduler.schedule(0 milliseconds, 2000 milliseconds, server, SendToClients("Ping from server"))
  }
}