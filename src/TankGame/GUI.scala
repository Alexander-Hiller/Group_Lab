package TankGame

import java.io.FileNotFoundException
import java.nio.file.{Files, Paths}

import scala.collection.mutable.ListBuffer
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.{Button, TextField}
import scalafx.scene._
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.layout._
import scalafx.scene.media.Media

import scala.io.Source
//import scalafx.scene.media.MediaPlayer
import java.io.File
import javafx.scene.media.MediaPlayer
import play.api.libs.json._


object GUI extends JFXApp{
 //### Governing Definitions

  val windowWidth: Double = 800
  val windowHeight: Double = 800
  val tankHeight: Double = 20
  val tankWidth: Double = 40
  var bullRadius: Double = 5
  var flag: Int =0
  var tankName: Int = 0
  //var barName: Int = 0
  var player: String = ""
  var playerSpeed: Double = 5
  var bulSpeed: Double = 1.25
  val bulDmg: Int = 25
  val musicFile: String = "src\\TankGame\\Assets\\NEFFEX - Fight Back (TheJabberturtle Gun Sync).mp3"
  val fireFile: String = "src\\TankGame\\Assets\\slam-fire.mp3"
  val maxBar: Int = 30
  val minBar: Int = 5
  val JSONfile: String ="jsonFile.json"


  //###### Object Groups and lists
  var allTanks = new ListBuffer[thing]()
  var allBull = new ListBuffer[thing]()
  var allBarriers = new ListBuffer[thing]()
  var sceneGraphics: Group = new Group {}

  //Background Music... Play song and loop forever
  val music = new Media(new File(musicFile).toURI.toString)
  val mediaPlayer = new MediaPlayer(music)
  mediaPlayer.autoPlayProperty
  mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE)
  mediaPlayer.setVolume(0.5)
  mediaPlayer.play()

  //soundFX
  val fire = new Media(new File(fireFile).toURI.toString)
  val firePlayer = new MediaPlayer(fire)


  //######## Text box and Buttons
  val playerName:TextField=new TextField {
    style = "-fx-font: 18 ariel;"
    text = "Name"
  }
  val button: Button = new Button {
    minWidth = 100
    minHeight = 50
    style = "-fx-font: 16 ariel;"
    text = "Start Your Game"
    onAction = event => buttonPressed()
  }
  val generate: Button = new Button {
    minWidth = 100
    minHeight = 50
    style = "-fx-font: 16 ariel;"
    text = "Generate New Map"
    onAction = event => genButtonPressed()
  }
  //#### Login Button Logic
  def buttonPressed(): Unit = {
    val input: String = playerName.text.value
    if(input!="Name" & flag==0){
      val x: Double= math.random()*windowWidth
      val y: Double= math.random()*windowHeight
      drawTank(x,y,input)
      button.text = "Welcome to the game"
      flag = 1
      player=input
      playerName.editable=false
      playerName.disable = true
      button.disable = true
      generate.disable = true
    }
    else if (flag!=0){
      button.text = "You're already in Game"
    }
    else{
      button.text = "Enter a real name first"
    }
  }

//generation button logic
  def genButtonPressed(): Unit = {
    //Delete Any Existing Barriers
    for (barrier<- allBarriers){
        sceneGraphics.children.remove(barrier.shape)
        allBarriers -= barrier
      }
    //Delete Any Existing Tanks
    for (tank<- allTanks){
      sceneGraphics.children.remove(tank.shape)
      allTanks-= tank
    }

    //### Spawn n number of barriers
    for(i<-0 to (math.random()*maxBar).toInt + minBar) {
      val xPos: Double = math.random()*windowWidth
      val yPos: Double= math.random()*windowHeight
      val w: Double = math.random()*50 + 20
      val l: Double = math.random()*50 + 20
      drawBarrier(xPos,yPos,i.toString,w,l)
    }

    // Make new JSON file
    Files.write(Paths.get(JSONfile), toJSON().getBytes())
  }


  //##### How to format the JSON String
  def toJSON(): String={
    var tankMap: Map[String,JsValue]=Map()
    var barMap: Map[String,JsValue]=Map()
    var bullMap: Map[String,JsValue]=Map()
    //map all tanks
    for(tank<-allTanks){
      var tempMap: Map[String,JsValue]=
                    Map("name"->Json.toJson(tank.toString),
                    "xPos"-> Json.toJson(tank.xPos),
                    "yPos"-> Json.toJson(tank.yPos),
                    "xTar"-> Json.toJson(tank.xTar),
                    "yTar"-> Json.toJson(tank.yTar),
                    "death"-> Json.toJson(tank.deathAnimator),
                    "wild"-> Json.toJson(tank.wild),
                    "health"-> Json.toJson(tank.health)
        )
        var newTank=Json.toJson(tempMap)
        tankMap= tankMap ++ Map(tank.toString->newTank)
    }
    //map all barriera
    for(bar<-allBarriers){
      var tempMap: Map[String,JsValue]=
      Map("name"->Json.toJson(bar.toString),
        "xPos"-> Json.toJson(bar.xPos),
        "yPos"-> Json.toJson(bar.yPos),
        "xTar"-> Json.toJson(bar.xTar),
        "yTar"-> Json.toJson(bar.yTar),
        "death"-> Json.toJson(bar.deathAnimator),
        "wild"-> Json.toJson(bar.wild),
        "health"-> Json.toJson(bar.health)
      )
      //println(bar.toString)
      var newBar=Json.toJson(tempMap)
      barMap=barMap ++ Map(bar.toString->newBar)
    }

    //map all bullets
    for(bull<-allBull){
      val tempMap: Map[String,JsValue]=
      Map("name"->Json.toJson(bull.toString),
        "xPos"-> Json.toJson(bull.xPos),
        "yPos"-> Json.toJson(bull.yPos),
        "xTar"-> Json.toJson(bull.xTar),
        "yTar"-> Json.toJson(bull.yTar),
        "death"-> Json.toJson(bull.deathAnimator),
        "wild"-> Json.toJson(bull.wild),
        "wild2"-> Json.toJson(bull.wild2),
        "health"-> Json.toJson(bull.health)
      )
      var newBull=Json.toJson(tempMap)
      bullMap=bullMap++ Map(bull.toString->newBull)
    }

    //convert all to JSON
    val tanks: JsValue = Json.toJson(tankMap)
    val bars: JsValue = Json.toJson(barMap)
    val bulls: JsValue = Json.toJson(bullMap)

    val thingMap: Map[String, JsValue]=Map(
      "tanks"->tanks,
      "bars"->bars,
      "bulls"->bulls
    )

    Json.stringify(Json.toJson(thingMap))

  }

  //######### Create Objects from a JSON string
  def fromJSON(jsonGameState: String): Unit = {
  // make lists of objects by type
    var JSONbarriers = new ListBuffer[JsValue]()
    val parsed: JsValue = Json.parse(jsonGameState)

    //Barrier Update and Management
    val elements = (parsed \ "bars").as [Map[String,JsValue]]
    var barList:List[String] = List()
    for(bar<-allBarriers){
      barList::=bar.toString
    }
    for(elem<-elements.keys){
      val name: String = (elements(elem)\"name").as[String]
      val xPos: Double = (elements(elem)\"xPos").as[Double]
      val yPos: Double = (elements(elem)\"yPos").as[Double]
      val w:Double=(elements(elem)\"xTar").as[Double]
      val l:Double=(elements(elem)\"yTar").as[Double]
      val health: Int=(elements(elem)\"health").as[Int]
      val wild:Double=(elements(elem)\"wild").as[Double]
      val death:Double=(elements(elem)\"death").as[Double]
      //If item already exists update it
      if(barList.contains(name)){
        for(thing<-allBarriers){
          if(name==thing.toString){
            thing.health=health
            thing.xPos=xPos
            thing.yPos=yPos
            thing.health=health
            thing.wild=wild
            thing.deathAnimator=death
          }
        }
      }
      // else add it to the game
      else drawBarrier(xPos,yPos,name,w,l)
    }


    //Tank Update and Management
    val tElements = (parsed \ "tanks").as [Map[String,JsValue]]
    var tankList:List[String] = List()
    for(tank<-allTanks){
      tankList::=tank.toString
    }
    for(elem<-tElements.keys){
      val name: String = (tElements(elem)\"name").as[String]
      val xPos: Double = (tElements(elem)\"xPos").as[Double]
      val yPos: Double = (tElements(elem)\"yPos").as[Double]
      val xTar:Double=(tElements(elem)\"xTar").as[Double]
      val yTar:Double=(tElements(elem)\"yTar").as[Double]
      val health: Int=(tElements(elem)\"health").as[Int]
      val wild:Double=(tElements(elem)\"wild").as[Double]
      val death:Double=(tElements(elem)\"death").as[Double]
      //If item already exists update it
      if(tankList.contains(name)){
        for(thing<-allTanks){
          if(name==thing.toString){
            thing.health=health
            thing.xPos=xPos
            thing.yPos=yPos
            thing.xTar=xTar
            thing.yTar=yTar
            thing.health=health
            thing.wild=wild
            thing.deathAnimator=death
          }
        }
      }
      // else add it to the game
      else drawTank(xPos,yPos,name)
    }

    //Bullet Update and Management
    val bElements = (parsed \ "tanks").as [Map[String,JsValue]]
    var bullList:List[String] = List()
    for(bull<-allBull){
      bullList::=bull.toString
    }
    for(elem<-bElements.keys){
      val name: String = (bElements(elem)\"name").as[String]
      val xPos: Double = (bElements(elem)\"xPos").as[Double]
      val yPos: Double = (bElements(elem)\"yPos").as[Double]
      val xTar:Double=(bElements(elem)\"xTar").as[Double]
      val yTar:Double=(bElements(elem)\"yTar").as[Double]
      val health: Int=(bElements(elem)\"health").as[Int]
      val wild:Double=(bElements(elem)\"wild").as[Double]
      val wild2:Double=(bElements(elem)\"wild2").as[Double]
      val death:Double=(bElements(elem)\"death").as[Double]
      //If item already exists update it
      if(bullList.contains(name)){
        for(thing<-allBull){
          if(name==thing.toString){
            if(wild2==thing.wild2) {
              thing.health = health
              thing.xPos = xPos
              thing.yPos = yPos
              thing.xTar = xTar
              thing.yTar = yTar
              thing.health = health
              thing.wild = wild
              thing.deathAnimator = death
            }
          }
        }
      }
      // else add it to the game
      else drawBullet(xTar,yTar,name, wild2)
    }

  }

  def loadData(): Unit={
    try {
      fromJSON(Source.fromFile(JSONfile).mkString)
    }catch{
      case ex: FileNotFoundException => ex.printStackTrace()
    }
  }

  //######## Bullet Spawner
  def drawBullet(xTar: Double, yTar: Double, name:String,bullNum:Double): Unit ={
    var startX: Double = 0
    var startY: Double = 0

    val newBull: Circle = new Circle{
      for (ident <- allTanks) {
        if (ident.toString == player) {
          startX = ident.xPos
          startY = ident.yPos
          centerX= startX   //ident.shape.translateX.value + tankWidth /2
          centerY= startY   //ident.shape.translateY.value + tankHeight /2
          radius = bullRadius
          fill = Color.Black
        }
      }
    }
    val tempBull: thing = new Bullet(player, newBull)
    tempBull.xPos= startX  //newBull.centerX.value + tankWidth /2
    tempBull.yPos= startY  //newBull.centerY.value + tankHeight /2
    tempBull.xTar = xTar
    tempBull.yTar = yTar
    tempBull.wild = tempBull.xPos
    tempBull.wild2 = bullNum
    allBull+= tempBull
    sceneGraphics.children.add(newBull)

    //Play Fire sounds
    firePlayer.seek(firePlayer.getStartTime)
    firePlayer.play()
    firePlayer.setVolume(0.25)

  }


  //#### Tank Spawner
  def drawTank(centerX: Double, centerY: Double, name:String): Unit = {
    val newTank = new Rectangle() {
      //tankName is just for testing
      tankName += 1
      width = tankWidth
      height = tankHeight
      translateX = centerX - tankWidth / 2.0
      translateY = centerY - tankHeight / 2.0
      fill = Color.OliveDrab
    }
    val tempTank: thing = new Tank(name,newTank)
    tempTank.xPos=centerX //- tankWidth / 2.0
    tempTank.yPos=centerY //- tankHeight / 2.0
    allTanks += tempTank
    sceneGraphics.children.add(newTank)
  }


  //####### Barrier Spawner
  def drawBarrier(centerX: Double, centerY: Double, name:String, w: Double, l:Double): Unit = {
    val newBarrier = new Rectangle() {
      width=w
      height=l
      translateX = centerX - w / 2.0
      translateY = centerY - l / 2.0
      fill = Color.rgb((math.random()*255).toInt, (math.random()*255).toInt,(math.random()*255).toInt)
    }
    val tempBar: thing = new Barrier(name,newBarrier)
    tempBar.xPos=centerX //- w / 2.0
    tempBar.yPos=centerY //- l / 2.0
    //store height and width in target variables
    tempBar.xTar = w
    tempBar.yTar = l
    allBarriers += tempBar
    sceneGraphics.children.add(newBarrier)
  }


  //########Key Listener
  def keyPressed(keyCode: KeyCode): Unit = {
    for (ident <- allTanks) {
      if (ident.toString==player) {
        val angle: Double = ident.shape.rotate.value*math.Pi/180
        keyCode.getName match {
          case "X" => println (allTanks.toString () )
          case "C" => loadData()
          case "Z" => playerLocs ()
          case "Up" | "W" =>moveFwd(ident,angle)
          case "Down"| "S" => moveBack(ident,angle)
          case "Left"| "A" => rotateLeft(ident)
          case "Right"| "D" => rotateRight(ident)
          //case _ => println (keyCode.getName + " pressed with no action")
      }
      }
    }
  }


  //########################## Movement of Tank Commands
  def moveFwd(obj : thing, angle : Double): Unit={
    obj.shape.translateY.value+= playerSpeed*math.sin(angle)
    obj.shape.translateX.value+= playerSpeed*math.cos(angle)
    obj.xPos+=playerSpeed*math.cos(angle)
    obj.yPos+=playerSpeed*math.sin(angle)
    if(obj.shape.translateX.value >= 760){
      obj.shape.translateX.value = 760
      obj.xPos = 780
    }
    if(obj.shape.translateY.value >= 770){
      obj.shape.translateY.value = 770
      obj.yPos = 780
    }
    if(obj.shape.translateX.value <= 0){
      obj.shape.translateX.value = 0
      obj.xPos = 20
    }
    if(obj.shape.translateY.value <= 10){
      obj.shape.translateY.value = 10
      obj.yPos = 20
    }
  }
  def moveBack(obj : thing, angle : Double): Unit={
    obj.shape.translateY.value-= playerSpeed*math.sin(angle)
    obj.shape.translateX.value-= playerSpeed*math.cos(angle)
    obj.xPos-=playerSpeed*math.cos(angle)
    obj.yPos-=playerSpeed*math.sin(angle)
    if(obj.shape.translateX.value >= 760){
      obj.shape.translateX.value = 760
      obj.xPos = 780
    }
    if(obj.shape.translateY.value >= 770){
      obj.shape.translateY.value = 770
      obj.yPos = 780
    }
    if(obj.shape.translateX.value <= 0){
      obj.shape.translateX.value = 0
      obj.xPos = 20
    }
    if(obj.shape.translateY.value <= 10){
      obj.shape.translateY.value = 10
      obj.yPos = 20
    }
  }
  def rotateLeft(obj: thing): Unit ={
    obj.shape.rotate.value -= 2
  }
  def rotateRight(obj: thing): Unit ={
    obj.shape.rotate.value += 2
  }


  //######Print out all player locations
  def playerLocs(): Unit = {
    for (ident <- allTanks) {
      println(ident.toString+ " is at:\n Health: " + ident.health + "   X pos: "+ ident.xPos+ "   Y pos: " + ident.yPos+"\n")
    }
  }



  //###########Stage and Update
  this.stage = new PrimaryStage{
    this.title = "Crappy Tanks"
    scene = new Scene(windowWidth, windowHeight) {
      fill = Color.LightGreen
      content = List(
        sceneGraphics,
          new VBox() {
          children = List(playerName, button, generate)
          }
      )

      //User uses keyboard
      addEventHandler(KeyEvent.KEY_PRESSED, (event: KeyEvent) => keyPressed(event.getCode))

      //Fire a bullet based on where clicked
      addEventHandler(MouseEvent.MOUSE_CLICKED, (event: MouseEvent) => {
        //println("X: "+ event.getX + " Y:" +
        val bullNum:Double=Math.random()*1000
        drawBullet(event.getX, event.getY, tankName.toString, bullNum)
      })

      }
    // Do this on every update
    val update: Long => Unit = (time: Long) => {

      //fade the button out of view after player has entered game
      if(flag!=0){
        button.opacity.value-=0.01
        generate.opacity.value-=0.01
      }

      //For updating positions of all tanks except the player
      for (tank <- allTanks) {
        //tank.shape.rotate.value+=0.5
      }

      //For updating all the positions of the bullets
      for (bull<- allBull){
        if(bull.health>0){
          bullCollision(bull)
          moveBull(bull)
        }
        if (bull.health<=0){
          explode(bull)
        }
        //delete the object when its done exploding
        if (bull.deathAnimator>=120){
          sceneGraphics.children.remove(bull.shape)
          allBull -= bull
        }
      }

      //For destroying barriers that have been damaged
      for (bar<- allBarriers){
        if (bar.health<=0){
          explode(bar)
        }
        if (bar.deathAnimator>=120){
          sceneGraphics.children.remove(bar.shape)
          allBarriers -= bar
        }
      }
    ///**** Save the game to JSON
     // Files.write(Paths.get(JSONfile), toJSON().getBytes())
    }
    AnimationTimer(update).start()
  }



  //######## logic behind bullet movement
  def moveBull(bull: thing):Unit ={
    //computing angle towards clicked target
    val angle: Double = math.atan((bull.yTar-bull.yPos)/(bull.xTar-bull.xPos))


    //if angle is behind the tank
    if ((bull.xTar - bull.wild) < 0) {
      //angle = angle * -1
      bull.shape.translateX.value -= playerSpeed * bulSpeed * math.cos(angle)
      bull.shape.translateY.value -= playerSpeed * bulSpeed * math.sin(angle)
      bull.xPos -= playerSpeed * bulSpeed * math.cos(angle)
      bull.yPos -= playerSpeed * bulSpeed * math.sin(angle)
    }
    else {
      bull.shape.translateX.value += playerSpeed * bulSpeed * math.cos(angle)
      bull.shape.translateY.value += playerSpeed * bulSpeed * math.sin(angle)
      bull.xPos += playerSpeed * bulSpeed * math.cos(angle)
      bull.yPos += playerSpeed * bulSpeed * math.sin(angle)
    }
    bull.health -= 1
    //stop bullet if it reaches destination
    if((math.abs(bull.xPos-bull.xTar)<playerSpeed)& math.abs(bull.yPos-bull.yTar)<playerSpeed) bull.health=0
  }


  //####### logic behind bullet collisions
  def bullCollision(bull: thing):Unit ={
    //check for barrier collisions
    for (barrier <- allBarriers) {
      val width: Double = barrier.xTar/2
      val height: Double = barrier.yTar/2

      if ((bull.xPos< (barrier.xPos+width))&(bull.xPos > (barrier.xPos-width))){
        if ((bull.yPos < (barrier.yPos + height))&(bull.yPos > (barrier.yPos - height))){
          bull.health=0
          barrier.health-=bulDmg
          println("Barrier: " + barrier.toString + " is at "+ barrier.health + " health")
          //println("Hit barrier " + barrier.toString)
          //println("bullet at X:" + bull.xPos + "   Y:"+bull.yPos)
          //println("Barrier Left edge at " + (barrier.xPos - width))
          //println("Barrier Right edge at " + (barrier.xPos + width))
          //sceneGraphics.children.remove(barrier.shape)
          //allBarriers -= barrier
        }
      }
    }
    for (tank <- allTanks) {
      //tank.shape.rotate.value+=0.5
    }
  }

//############## animation of an object exploding
  def explode(obj: thing):Unit ={
    if(obj.deathAnimator<15){
      //dark red 139,0,0
      obj.shape.scaleX.value+= .2
      obj.shape.scaleY.value+= .1
      obj.deathAnimator +=1
      obj.shape.fill = Color.rgb((17*obj.deathAnimator).toInt,(8*obj.deathAnimator).toInt,0)
    }
    else if (obj.deathAnimator<30){
      obj.shape.scaleX.value+= .1
      obj.shape.scaleY.value+= .2
      obj.deathAnimator +=1
      obj.shape.fill = Color.rgb(255,(8*obj.deathAnimator).toInt,0)
    }
    else if (obj.deathAnimator<120){
      obj.shape.opacity.value -= 0.012
      obj.deathAnimator +=1
    }
  }

}