package TankGame

import scala.collection.mutable.ListBuffer
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.{Button, TextField}
import scalafx.scene._
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Rectangle, Shape}
import scalafx.scene.layout._

object GUI extends JFXApp{
  val windowWidth: Double = 800
  val windowHeight: Double = 800
  val tankHeight: Double = 20
  val tankWidth: Double = 40
  var flag: Int =0
  var tankName: Int = 0
  var player: String = ""
  var playerSpeed: Int = 5

  var allTanks = new ListBuffer[thing]()
  var sceneGraphics: Group = new Group {}

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
    tempTank.xPos=centerX
    tempTank.yPos=centerY
    allTanks += tempTank
    sceneGraphics.children.add(newTank)
  }

  def keyPressed(keyCode: KeyCode): Unit = {
    for (ident <- allTanks) {
      if (ident.toString==player) {
        keyCode.getName match {
          case "X" => println (allTanks.toString () )
          case "Z" => playerLocs ()
          case "Up" =>{
            //println(math.sin(ident.shape.rotate.value*math.Pi/180))
            ident.shape.translateY.value+= playerSpeed*math.sin(ident.shape.rotate.value*math.Pi/180)
            ident.shape.translateX.value+= playerSpeed*math.cos(ident.shape.rotate.value*math.Pi/180)
            ident.xPos+=playerSpeed*math.cos(ident.shape.rotate.value*math.Pi/180)
            ident.yPos+=playerSpeed*math.sin(ident.shape.rotate.value*math.Pi/180)
          }
          case "Down" => {
            ident.shape.translateY.value-= playerSpeed*math.sin(ident.shape.rotate.value*math.Pi/180)
            ident.shape.translateX.value-= playerSpeed*math.cos(ident.shape.rotate.value*math.Pi/180)
            ident.xPos-=playerSpeed*math.cos(ident.shape.rotate.value*math.Pi/180)
            ident.yPos-=playerSpeed*math.sin(ident.shape.rotate.value*math.Pi/180)
          }
          case "Left" => {
            ident.shape.rotate.value -= 1
          }
          case "Right" => ident.shape.rotate.value+= 1
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
      content = List(
        sceneGraphics,
          new VBox() {
          children = List(playerName, button)
          }
      )

      addEventHandler(KeyEvent.KEY_PRESSED, (event: KeyEvent) => keyPressed(event.getCode))


      //testing drop a tank onto the screen
      addEventHandler(MouseEvent.MOUSE_CLICKED, (event: MouseEvent) => drawTank(event.getX, event.getY, tankName.toString))

      }

    val update: Long => Unit = (time: Long) => {
      if(flag!=0){
        button.opacity.value-=0.01
      }

      for (tank <- allTanks) {
        //tank.shape.rotate.value+=0.5
      }
    }
    AnimationTimer(update).start()
  }

}