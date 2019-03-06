package TankGame

import scala.collection.mutable.ListBuffer
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.{Button, TextField}
import scalafx.scene._
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle, Shape}
import scalafx.scene.layout._
import scalafx.scene.media.Media
import scalafx.scene.media.MediaPlayer
import java.io.File



object GUI extends JFXApp{
  val windowWidth: Double = 800
  val windowHeight: Double = 800
  val tankHeight: Double = 20
  val tankWidth: Double = 40
  var bullRadius: Double = 5
  var flag: Int =0
  var tankName: Int = 0
  var player: String = ""
  var playerSpeed: Double = 5
  var bulSpeed: Double = 1.25
  val musicFile: String = "src\\TankGame\\Assets\\bxmMusicVideoGameMetal.mp3"

  var allTanks = new ListBuffer[thing]()
  var allBull = new ListBuffer[thing]()
  var allBarriers = new ListBuffer[thing]()
  var sceneGraphics: Group = new Group {}

  val music = new Media(new File(musicFile).toURI.toString)
  val mediaPlayer = new MediaPlayer(music)
  mediaPlayer.play()
  mediaPlayer.autoPlay = true
  mediaPlayer.volume.value = 0.25
  println(mediaPlayer.volume.value)

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

  def drawBullet(xTar: Double, yTar: Double, name:String): Unit ={
    val newBull: Circle = new Circle{
      for (ident <- allTanks) {
        if (ident.toString == player) {
          centerX= ident.shape.translateX.value + tankWidth /2
          centerY= ident.shape.translateY.value + tankHeight /2
          radius = bullRadius
          fill = Color.Black
        }
      }
    }
    val tempBull: thing = new Bullet(player, newBull)
    tempBull.xPos=newBull.centerX.value + tankWidth /2
    tempBull.yPos=newBull.centerY.value + tankHeight /2
    tempBull.xTar = xTar
    tempBull.yTar = yTar
    tempBull.wild = tempBull.xPos
    allBull+= tempBull
    sceneGraphics.children.add(newBull)
  }

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
    tempTank.xPos=centerX - tankWidth / 2.0
    tempTank.yPos=centerY - tankHeight / 2.0
    allTanks += tempTank
    sceneGraphics.children.add(newTank)
  }

  def keyPressed(keyCode: KeyCode): Unit = {
    for (ident <- allTanks) {
      if (ident.toString==player) {
        val angle: Double = ident.shape.rotate.value*math.Pi/180
        keyCode.getName match {
          case "X" => println (allTanks.toString () )
          case "Z" => playerLocs ()
          case ("Up" | "W") =>{
            //println(math.sin(ident.shape.rotate.value*math.Pi/180))
            ident.shape.translateY.value+= playerSpeed*math.sin(angle)
            ident.shape.translateX.value+= playerSpeed*math.cos(angle)
            ident.xPos+=playerSpeed*math.cos(angle)
            ident.yPos+=playerSpeed*math.sin(angle)
          }
          case "Down"| "S" => {
            ident.shape.translateY.value-= playerSpeed*math.sin(angle)
            ident.shape.translateX.value-= playerSpeed*math.cos(angle)
            ident.xPos-=playerSpeed*math.cos(angle)
            ident.yPos-=playerSpeed*math.sin(angle)
          }
          case "Left"| "A" => {
            ident.shape.rotate.value -= 2
          }
          case "Right"| "D" => ident.shape.rotate.value+= 2

          case _ => println (keyCode.getName + " pressed with no action")
      }
      }
    }
  }

  def playerLocs(): Unit = {
    for (ident <- allTanks) {
      println(ident.toString()+ " is at:\n X pos: "+ ident.xPos+ "   Y pos: " + ident.yPos+"\n")
    }
  }


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

      addEventHandler(KeyEvent.KEY_PRESSED, (event: KeyEvent) => keyPressed(event.getCode))


      //testing drop a tank onto the screen
      addEventHandler(MouseEvent.MOUSE_CLICKED, (event: MouseEvent) => drawBullet(event.getX, event.getY, tankName.toString))

      }

    val update: Long => Unit = (time: Long) => {
      if(flag!=0){
        button.opacity.value-=0.01
      }

      for (tank <- allTanks) {
        //tank.shape.rotate.value+=0.5
      }

      for (bull<- allBull){
        moveBull(bull)
        if (bull.health<=0){
          explode(bull)
        }

        //delete the object when its done exploding
        if (bull.deathAnimator>=120){
          sceneGraphics.children.remove(bull.shape)
          allBull -= bull
        }
      }
    }
    AnimationTimer(update).start()
  }

  def moveBull(bull: thing):Unit ={
    //computing angle towards clicked target (if behind tank make it negative)
    var angle: Double = math.atan((bull.yTar-bull.yPos)/(bull.xTar-bull.xPos))
    //if ((bull.xTar - bull.xPos) < 1)

    if (bull.health>0) {
      if ((bull.xTar - bull.wild) < 0) {
        bull.shape.translateX.value -= playerSpeed * bulSpeed * math.cos(angle)
        bull.shape.translateY.value -= playerSpeed * bulSpeed * math.sin(angle)
        bull.xPos -= playerSpeed * 2 * math.cos(angle)
        bull.yPos -= playerSpeed * 2 * math.sin(angle)
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
  }


  def explode(obj: thing):Unit ={
    if(obj.deathAnimator<15){
      //dark red 139,0,0
      obj.shape.scaleX.value+= .2
      obj.shape.scaleY.value+= .1
      obj.deathAnimator +=1
      obj.shape.fill = Color.rgb((17*obj.deathAnimator).toInt,(8*obj.deathAnimator).toInt,0)
    }
    else if (obj.deathAnimator<30){
      obj.shape().scaleX.value+= .1
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