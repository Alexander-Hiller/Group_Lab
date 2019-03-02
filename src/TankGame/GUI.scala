package TankGame

import scala.collection.mutable.ListBuffer
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Rectangle, Shape}

object GUI extends JFXApp{
  val windowWidth: Double = 800
  val windowHeight: Double = 600
  val tankHeight: Double = 40
  val tankWidth: Double = 80

  var tankName: Int = 0

  var allTanks = new ListBuffer[thing]()
  var sceneGraphics: Group = new Group {}

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
    allTanks += tempTank
    sceneGraphics.children.add(newTank)
  }

  def keyPressed(keyCode: KeyCode): Unit = {
    keyCode.getName match {
      case "X" => println(allTanks(1))
      case "Z" => playerIdents()
      case _ => println(keyCode.getName + " pressed with no action")
    }
  }

  def playerIdents(): Unit = {
    for (ident <- allTanks) {
      println(ident)
    }
  }


  this.stage = new PrimaryStage{
    this.title = "Crappy Tanks"
    scene = new Scene(windowWidth, windowHeight) {
      content = List(sceneGraphics)

      addEventHandler(KeyEvent.KEY_PRESSED, (event: KeyEvent) => keyPressed(event.getCode))


      //testing drop a tank onto the screen
      addEventHandler(MouseEvent.MOUSE_CLICKED, (event: MouseEvent) => drawTank(event.getX, event.getY, tankName.toString))

      }

    val update: Long => Unit = (time: Long) => {
      for (tank <- allTanks) {
        tank.shape.rotate.value+=0.5
      }
    }
    AnimationTimer(update).start()
  }

}