package TankGame
import scalafx.scene.shape.Shape

class Bullet(val name: String, val shape: Shape) extends thing(name, shape)  {
  //Health indicates range... Health decreases with distance
  var health: Int = 40
  //println(this.name +" Has Fired!")
  var xPos: Double = 0
  //wild card stores original x data
  var deathAnimator: Double = 0
  var wild: Double = xPos
  var yPos: Double = 0

  var xTar: Double =0
  var yTar: Double =0


  override def toString: String ={
    this.name
  }
}
