package TankGame

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
//import scalafx.scene.media.MediaPlayer
import java.io.File
import javafx.scene.media.MediaPlayer


object GUI extends JFXApp{
 //### Governing Definitions

  val windowWidth: Double = 800
  val windowHeight: Double = 800
  val tankHeight: Double = 20
  val tankWidth: Double = 40
  var bullRadius: Double = 5
  var flag: Int =0
  var tankName: Int = 0
  var barName: Int = 0
  var player: String = ""
  var playerSpeed: Double = 5
  var bulSpeed: Double = 1.25
  val bulDmg: Int = 25
  val musicFile: String = "src\\TankGame\\Assets\\NEFFEX - Fight Back (TheJabberturtle Gun Sync).mp3"
  val fireFile: String = "src\\TankGame\\Assets\\slam-fire.mp3"
  val maxBar: Int = 30
  val minBar: Int = 5


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


  //######## Text box and Button
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
    }
    else if (flag!=0){
      button.text = "You're already in Game"
    }
    else{
      button.text = "Enter a real name first"
    }
  }


  //### Spawn n number of barriers
  //CHANGE THIS WHEN WE CAN GET A LIST FROM AJSON STRING
  for(i<- 0 to (math.random()*maxBar).toInt + minBar) {
    val xPos: Double = math.random()*windowWidth
    val yPos: Double= math.random()*windowHeight
    drawBarrier(xPos,yPos,barName.toString)
  }

  //######## Bullet Spawner
  def drawBullet(xTar: Double, yTar: Double, name:String): Unit ={
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
  def drawBarrier(centerX: Double, centerY: Double, name:String): Unit = {
    barName+=1
    val w: Double = math.random()*50 + 20
    val l: Double = math.random()*50 + 20

    val newBarrier = new Rectangle() {
      width = w
      height = l
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
  }
  def moveBack(obj : thing, angle : Double): Unit={
    obj.shape.translateY.value-= playerSpeed*math.sin(angle)
    obj.shape.translateX.value-= playerSpeed*math.cos(angle)
    obj.xPos-=playerSpeed*math.cos(angle)
    obj.yPos-=playerSpeed*math.sin(angle)
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
          children = List(playerName, button)
          }
      )

      //User uses keyboard
      addEventHandler(KeyEvent.KEY_PRESSED, (event: KeyEvent) => keyPressed(event.getCode))

      //Fire a bullet based on where clicked
      addEventHandler(MouseEvent.MOUSE_CLICKED, (event: MouseEvent) => {
        //println("X: "+ event.getX + " Y:" + event.getY)
        drawBullet(event.getX, event.getY, tankName.toString)
      })

      }
    // Do this on every update
    val update: Long => Unit = (time: Long) => {

      //fade the button out of view after player has entered game
      if(flag!=0){
        button.opacity.value-=0.01
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

    }
    AnimationTimer(update).start()
  }



  //######## logic behind bullet movement
  def moveBull(bull: thing):Unit ={
    //computing angle towards clicked target
    var angle: Double = math.atan((bull.yTar-bull.yPos)/(bull.xTar-bull.xPos))


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

      if (((bull.xPos)< (barrier.xPos+width))&((bull.xPos) > (barrier.xPos-width))){
        if (((bull.yPos) < (barrier.yPos + height))&((bull.yPos) > (barrier.yPos - height))){
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