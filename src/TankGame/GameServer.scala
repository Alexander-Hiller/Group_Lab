package TankGame

import java.io.FileNotFoundException

import TankGame.Level
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
  //make a new game
  val game = new Game
  game.newGame()
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

      else if(action=="disconnected"){
        println("User disconnected")
      }
      else if(action=="move"){
        val name:String = (parsed \ "name").as[String]
        val xPos:String =(parsed \ "xPos").as[String]
        val yPos:String =(parsed \ "yPos").as[String]
        for(tank<-game.allTanks){
          if(tank.toString==name) {
            tank.xPos = xPos.toDouble
            tank.yPos = yPos.toDouble
            tank.shape.translateX.value = xPos.toDouble
            tank.shape.translateY.value = yPos.toDouble
          }
        }
      }
      else if(action=="rot"){
        val name:String = (parsed \ "name").as[String]
        val rot:String =(parsed \ "rot").as[String]
        for(tank<-game.allTanks){
          if(tank.toString==name) {
            tank.rot = rot.toDouble
            tank.shape.rotate.value= rot.toDouble
          }
        }
      }
      else if(action=="newTank"){
        val name:String = (parsed \ "name").as[String]
        val xPos:String =(parsed \ "xPos").as[String]
        val yPos:String =(parsed \ "yPos").as[String]
        game.addTank(xPos.toDouble,yPos.toDouble,name)
      }

      else if(action=="bull"){
        val name:String=(parsed \ "name").as[String]
        val xTar:String=(parsed\"xTar").as[String]
        val yTar:String=(parsed\"yTar").as[String]
        val xPos:String=(parsed\"xPos").as[String]
        val yPos:String=(parsed\"yPos").as[String]
        val bullNum:String=(parsed\"bullNum").as[String]
        println("bullet fired from: "+ name)
        game.newBullet(xPos.toDouble,yPos.toDouble,xTar.toDouble, yTar.toDouble, name,bullNum.toDouble)
      }

      else if(action=="update"){
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

      else if(action=="test"){
        println("received a test")
        //this.clients.foreach((client: ActorRef) => client ! Write(ByteString("Test Satisfactory")))
      }

      else{
        println("Action \""+action+"\" not understood")
      }


    case UpdateGames =>
      game.update()
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
    actorSystem.scheduler.schedule(0 milliseconds, 25 milliseconds, server, UpdateGames)
    //actorSystem.scheduler.schedule(0 milliseconds, 2000 milliseconds, server, SendToClients("Ping from server"))
  }
}