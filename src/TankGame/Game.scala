package TankGame



import scala.collection.mutable.ListBuffer

import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}

import java.nio.file.{Files, Paths}
import TankGame.Objects._
import play.api.libs.json._

class Game {
  val windowWidth: Double = 800
  val windowHeight: Double = 800
  val tankHeight: Double = 20
  val tankWidth: Double = 40
  var bullRadius: Double = 5
  var flag: Int = 0
  var tankName: Int = 0
  //var barName: Int = 0
  var player: String = ""
  var playerSpeed: Double = 5
  var bulSpeed: Double = 1.25
  val bulDmg: Int = 25

  val maxBar: Int = 30
  val minBar: Int = 5
  val JSONfile: String = "operatingFile.json"

  //###### Object Groups and lists
  var allTanks = new ListBuffer[thing]()
  var allBull = new ListBuffer[thing]()
  var allBarriers = new ListBuffer[thing]()

  var wallLocations = List(
    //left side
    new GridLocation(0, 0),
    new GridLocation(0, 1),
    new GridLocation(0, 2),
    new GridLocation(0, 3),
    new GridLocation(0, 4),
    new GridLocation(0, 5),
    new GridLocation(0, 6),
    new GridLocation(0, 7),
    new GridLocation(0, 8),
    new GridLocation(0, 9),
    new GridLocation(0, 10),
    new GridLocation(0, 11),
    new GridLocation(0, 12),
    new GridLocation(0, 13),
    new GridLocation(0, 14),
    new GridLocation(0, 15),
    new GridLocation(0, 16),
    new GridLocation(0, 17),
    new GridLocation(0, 18),
    new GridLocation(0, 19),
    //right side
    new GridLocation(19, 0),
    new GridLocation(19, 1),
    new GridLocation(19, 2),
    new GridLocation(19, 3),
    new GridLocation(19, 4),
    new GridLocation(19, 5),
    new GridLocation(19, 6),
    new GridLocation(19, 7),
    new GridLocation(19, 8),
    new GridLocation(19, 9),
    new GridLocation(19, 10),
    new GridLocation(19, 11),
    new GridLocation(19, 12),
    new GridLocation(19, 13),
    new GridLocation(19, 14),
    new GridLocation(19, 15),
    new GridLocation(19, 16),
    new GridLocation(19, 17),
    new GridLocation(19, 18),
    new GridLocation(19, 19),
    //top
    new GridLocation(0, 0),
    new GridLocation(1, 0),
    new GridLocation(2, 0),
    new GridLocation(3, 0),
    new GridLocation(4, 0),
    new GridLocation(5, 0),
    new GridLocation(6, 0),
    new GridLocation(7, 0),
    new GridLocation(8, 0),
    new GridLocation(9, 0),
    new GridLocation(10, 0),
    new GridLocation(11, 0),
    new GridLocation(12, 0),
    new GridLocation(13, 0),
    new GridLocation(14, 0),
    new GridLocation(15, 0),
    new GridLocation(16, 0),
    new GridLocation(17, 0),
    new GridLocation(18, 0),
    new GridLocation(19, 0),
    //bottom
    new GridLocation(0, 19),
    new GridLocation(1, 19),
    new GridLocation(2, 19),
    new GridLocation(3, 19),
    new GridLocation(4, 19),
    new GridLocation(5, 19),
    new GridLocation(6, 19),
    new GridLocation(7, 19),
    new GridLocation(8, 19),
    new GridLocation(9, 19),
    new GridLocation(10, 19),
    new GridLocation(11, 19),
    new GridLocation(12, 19),
    new GridLocation(13, 19),
    new GridLocation(14, 19),
    new GridLocation(15, 19),
    new GridLocation(16, 19),
    new GridLocation(17, 19),
    new GridLocation(18, 19),
    new GridLocation(19, 19)
  )

  //### New Game (run this on server load)
  def newGame(): Unit = {
    //Delete Any Existing Barriers
    for (barrier <- allBarriers) {
      allBarriers -= barrier
    }
    //Delete Any Existing Tanks
    for (tank <- allTanks) {
      allTanks -= tank
    }
    //### Spawn n number of barriers
    for (i <- 0 to (math.random() * maxBar).toInt + minBar) {
      val xPos: Double = math.random() * windowWidth
      val yPos: Double = math.random() * windowHeight
      val w: Double = math.random() * 50 + 20
      val l: Double = math.random() * 50 + 20
      addBarrier(xPos, yPos, i.toString, w, l)
    }
    // Make new JSON file
    Files.write(Paths.get(JSONfile), toJSON().getBytes())
  }


  //### LOGIC TO CREATE JSON STRING
  def toJSON(): String = {
    var tankMap: Map[String, JsValue] = Map()
    var barMap: Map[String, JsValue] = Map()
    var bullMap: Map[String, JsValue] = Map()

    //map all tanks
    for (tank <- allTanks) {
      if (tank.deathAnimator <= 40) {
        var tempMap: Map[String, JsValue] =
          Map("name" -> Json.toJson(tank.toString),
            "xPos" -> Json.toJson(tank.xPos),
            "yPos" -> Json.toJson(tank.yPos),
            "xTar" -> Json.toJson(tank.xTar),
            "yTar" -> Json.toJson(tank.yTar),
            "death" -> Json.toJson(tank.deathAnimator),
            "wild" -> Json.toJson(tank.wild),
            "health" -> Json.toJson(tank.health),
            "rot" -> Json.toJson(tank.rot)
          )
        var newTank = Json.toJson(tempMap)
        tankMap = tankMap ++ Map(tank.toString -> newTank)
      }
      //else allTanks-=tank
    }
    //map all barriera
    for (bar <- allBarriers) {
      if (bar.deathAnimator <= 40) {
        var tempMap: Map[String, JsValue] =
          Map("name" -> Json.toJson(bar.toString),
            "xPos" -> Json.toJson(bar.xPos),
            "yPos" -> Json.toJson(bar.yPos),
            "xTar" -> Json.toJson(bar.xTar),
            "yTar" -> Json.toJson(bar.yTar),
            "death" -> Json.toJson(bar.deathAnimator),
            "wild" -> Json.toJson(bar.wild),
            "health" -> Json.toJson(bar.health)
          )
        //println(bar.toString)
        var newBar = Json.toJson(tempMap)
        barMap = barMap ++ Map(bar.toString -> newBar)
      }
      //else allBarriers-=bar
    }


    //map all bullets
    for (bull <- allBull) {
      if (bull.deathAnimator <= 40) {
        val tempMap: Map[String, JsValue] =
          Map("name" -> Json.toJson(bull.toString),
            "xPos" -> Json.toJson(bull.xPos),
            "yPos" -> Json.toJson(bull.yPos),
            "xTar" -> Json.toJson(bull.xTar),
            "yTar" -> Json.toJson(bull.yTar),
            "death" -> Json.toJson(bull.deathAnimator),
            "wild" -> Json.toJson(bull.wild),
            "wild2" -> Json.toJson(bull.wild2),
            "health" -> Json.toJson(bull.health)
          )
        var newBull = Json.toJson(tempMap)
        bullMap = bullMap ++ Map(bull.toString -> newBull)
      }
      //else allBull-=bull
    }

    //convert all to JSON
    val tanks: JsValue = Json.toJson(tankMap)
    val bars: JsValue = Json.toJson(barMap)
    val bulls: JsValue = Json.toJson(bullMap)

    val thingMap: Map[String, JsValue] = Map(
      "tanks" -> tanks,
      "bars" -> bars,
      "bulls" -> bulls,
      "gridSize" -> Json.toJson(Map("x" -> 20, "y" -> 20)),
//      "walls" -> Json.toJson(this.wallLocations.map({ w => Json.toJson(Map("x" -> w.x, "y" -> w.y)) }))
    )

    Json.stringify(Json.toJson(thingMap))

  }

  //######### Create Objects from a JSON string
  def fromJSON(jsonGameState: String): Unit = {
    // make lists of objects by type
    val parsed: JsValue = Json.parse(jsonGameState)

    //Barrier Update and Management
    val elements = (parsed \ "bars").as[Map[String, JsValue]]
    var barList: List[String] = List()
    for (bar <- allBarriers) {
      barList ::= bar.toString
    }
    for (elem <- elements.keys) {
      val name: String = (elements(elem) \ "name").as[String]
      val xPos: Double = (elements(elem) \ "xPos").as[Double]
      val yPos: Double = (elements(elem) \ "yPos").as[Double]
      val w: Double = (elements(elem) \ "xTar").as[Double]
      val l: Double = (elements(elem) \ "yTar").as[Double]
      val health: Int = (elements(elem) \ "health").as[Int]
      val wild: Double = (elements(elem) \ "wild").as[Double]
      val death: Double = (elements(elem) \ "death").as[Double]
      //If item already exists update it
      if (barList.contains(name)) {
        for (thing <- allBarriers) {
          if (name == thing.toString) {
            thing.health = health
            thing.xPos = xPos
            thing.yPos = yPos
            thing.health = health
            thing.wild = wild
          }
        }
      }
      // else add it to the game
      else addBarrier(xPos, yPos, name, w, l)
    }


    //Tank Update and Management
    val tElements = (parsed \ "tanks").as[Map[String, JsValue]]
    var tankList: List[String] = List()
    for (tank <- allTanks) {
      tankList ::= tank.toString
    }
    for (elem <- tElements.keys) {
      val name: String = (tElements(elem) \ "name").as[String]
      val xPos: Double = (tElements(elem) \ "xPos").as[Double]
      val yPos: Double = (tElements(elem) \ "yPos").as[Double]
      val xTar: Double = (tElements(elem) \ "xTar").as[Double]
      val yTar: Double = (tElements(elem) \ "yTar").as[Double]
      val health: Int = (tElements(elem) \ "health").as[Int]
      val wild: Double = (tElements(elem) \ "wild").as[Double]
      val death: Double = (tElements(elem) \ "death").as[Double]
      val rot: Double = (tElements(elem) \ "rot").as[Double]
      //If item already exists update it
      if (tankList.contains(name)) {
        for (thing <- allTanks) {
          if (name == thing.toString) {
            thing.shape.translateX.value = xPos - tankWidth / 2
            thing.shape.translateY.value = yPos - tankHeight / 2
            thing.health = health
            thing.xPos = xPos
            thing.yPos = yPos
            thing.xTar = xTar
            thing.yTar = yTar
            thing.health = health
            thing.wild = wild
            thing.rot = rot
            thing.shape.rotate.value = rot
          }
        }
      }
      // else add it to the game
      else addTank(xPos, yPos, name)
    }

    //Bullet Update and Management
    val bElements = (parsed \ "bulls").as[Map[String, JsValue]]
    var bullList: List[String] = List()
    for (bull <- allBull) {
      bullList ::= bull.toString
    }
    for (elem <- bElements.keys) {

      val name: String = (bElements(elem) \ "name").as[String]
      val xPos: Double = (bElements(elem) \ "xPos").as[Double]
      val yPos: Double = (bElements(elem) \ "yPos").as[Double]
      val xTar: Double = (bElements(elem) \ "xTar").as[Double]
      val yTar: Double = (bElements(elem) \ "yTar").as[Double]
      val health: Int = (bElements(elem) \ "health").as[Int]
      val wild: Double = (bElements(elem) \ "wild").as[Double]
      var wild2: Double = (bElements(elem) \ "wild2").as[Double]
      val death: Double = (bElements(elem) \ "death").as[Double]
      //If item already exists update it
      if (bullList.contains(name)) {
        for (thing <- allBull) {
          if (name == thing.toString) {
            if (wild2 == thing.wild2) {
              thing.health = health
              thing.xPos = xPos
              thing.yPos = yPos
              thing.xTar = xTar
              thing.yTar = yTar
              thing.health = health
              thing.wild = wild
            }
          }
        }
      }
      // else add it to the game
      else addBullet(name, xPos, yPos, xTar, yTar, health, wild, wild2, death)
    }
  }


  def newBullet(xPos: Double, yPos: Double, xTar: Double, yTar: Double, name: String, bullNum: Double): Unit = {
    var startX: Double = 0
    var startY: Double = 0
    val newBull: Circle = new Circle {
      startX = xPos
      startY = yPos
      centerX = startX //ident.shape.translateX.value + tankWidth /2
      centerY = startY //ident.shape.translateY.value + tankHeight /2
      radius = bullRadius
      fill = Color.Black
    }
    val tempBull: thing = new Bullet(name, newBull)
    tempBull.xPos = startX //newBull.centerX.value + tankWidth /2
    tempBull.yPos = startY //newBull.centerY.value + tankHeight /2
    tempBull.xTar = xTar
    tempBull.yTar = yTar
    tempBull.wild = tempBull.xPos
    tempBull.wild2 = bullNum
    allBull += tempBull
  }


  //######## Bullet Spawner
  def addBullet(name: String, xPos: Double, yPos: Double, xTar: Double, yTar: Double, health: Int, wild: Double, wild2: Double, death: Double): Unit = {
    var startX: Double = 0
    var startY: Double = 0

    val newBull: Circle = new Circle {
      startX = xPos
      startY = yPos
      centerX = startX //ident.shape.translateX.value + tankWidth /2
      centerY = startY //ident.shape.translateY.value + tankHeight /2
      radius = bullRadius
      fill = Color.Black

    }
    val tempBull: thing = new Bullet(name, newBull)
    tempBull.xPos = startX //newBull.centerX.value + tankWidth /2
    tempBull.yPos = startY //newBull.centerY.value + tankHeight /2
    tempBull.xTar = xTar
    tempBull.yTar = yTar
    tempBull.wild = wild
    tempBull.wild2 = wild2
    tempBull.health = health
    tempBull.deathAnimator = death
    allBull += tempBull
  }

  def addTank(centerX: Double, centerY: Double, name: String): Unit = {
    val newTank = new Rectangle() {
      width = tankWidth
      height = tankHeight
      translateX = centerX - tankWidth / 2.0
      translateY = centerY - tankHeight / 2.0
      fill = Color.OliveDrab
    }
    val tempTank: thing = new Tank(name, newTank)
    tempTank.xPos = centerX //- tankWidth / 2.0
    tempTank.yPos = centerY //- tankHeight / 2.0
    allTanks += tempTank
  }

  def addBarrier(centerX: Double, centerY: Double, name: String, w: Double, l: Double): Unit = {
    val newBarrier = new Rectangle() {
      width = w
      height = l
      translateX = centerX - w / 2.0
      translateY = centerY - l / 2.0
      fill = Color.rgb((math.random() * 255).toInt, (math.random() * 255).toInt, (math.random() * 255).toInt)
    }
    val tempBar: thing = new Barrier(name, newBarrier)
    tempBar.xPos = centerX //- w / 2.0
    tempBar.yPos = centerY //- l / 2.0
    //store height and width in target variables
    tempBar.xTar = w
    tempBar.yTar = l
    allBarriers += tempBar
  }

  //######## logic behind bullet movement
  def moveBull(bull: thing): Unit = {
    //computing angle towards clicked target
    val angle: Double = math.atan((bull.yTar - bull.yPos) / (bull.xTar - bull.xPos))

    if (bull.health > 0) {
      //if angle was behind the tank
      if ((bull.xTar - bull.wild) < 0) {
        //angle = angle * -1
        bull.xPos -= playerSpeed * bulSpeed * math.cos(angle)
        bull.yPos -= playerSpeed * bulSpeed * math.sin(angle)
      }
      else {
        bull.xPos += playerSpeed * bulSpeed * math.cos(angle)
        bull.yPos += playerSpeed * bulSpeed * math.sin(angle)
      }
    }
    bull.health -= 1
    //stop bullet if it reaches destination
    if ((math.abs(bull.xPos - bull.xTar) < playerSpeed) & math.abs(bull.yPos - bull.yTar) < playerSpeed) bull.health = 0

    // println("Bullet ("+bull.toString+"): "++bull.xPos.toString+","+bull.yPos.toString)
  }

  //####### logic behind bullet collisions
  def bullCollision(bull: thing): Unit = {
    //check for barrier collisions
    if (bull.health > 0) {
      for (barrier <- allBarriers) {
        val width: Double = barrier.xTar / 2
        val height: Double = barrier.yTar / 2

        if ((bull.xPos < (barrier.xPos + width)) & (bull.xPos > (barrier.xPos - width))) {
          if ((bull.yPos < (barrier.yPos + height)) & (bull.yPos > (barrier.yPos - height))) {
            bull.health = 0
            barrier.health -= bulDmg
            println("Barrier: " + barrier.toString + " is at " + barrier.health + " health")
          }
        }
      }
      for (tank <- allTanks) {
        val width: Double = tankWidth / 2
        val height: Double = tankWidth / 2
        if (tank.toString != bull.toString) {
          if ((bull.xPos < (tank.xPos + width)) & (bull.xPos > (tank.xPos - width))) {
            if ((bull.yPos < (tank.yPos + height)) & (bull.yPos > (tank.yPos - height))) {
              bull.health = 0
              tank.health -= bulDmg
              println("Player: " + tank.toString + " is at " + tank.health + " health")
            }
          }
        }
      }
    }
  }

  def deathCheck(): Unit = {
    for (bull <- allBull) {
      if (bull.health <= 0) {
        bull.deathAnimator += 1
        if (bull.deathAnimator >= 120) allBull -= bull
      }
    }
    for (tank <- allTanks) {
      if (tank.health <= 0) {
        tank.deathAnimator += 1
        if (tank.deathAnimator >= 120) allTanks -= tank
      }
    }
    for (bar <- allBarriers) {
      if (bar.health <= 0) {
        bar.deathAnimator += 1
        if (bar.deathAnimator >= 120) allBarriers -= bar
      }
    }
  }

  def update(): Unit = {
    for (bull <- allBull) {
      deathCheck()
      bullCollision(bull)
      moveBull(bull)
    }
    Files.write(Paths.get(JSONfile), toJSON().getBytes())
  }
}

