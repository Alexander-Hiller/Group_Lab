package TankGame.Objects

import scalafx.scene.shape.Shape

class Bullet(val name: String, val inShape: Shape) extends thing(name, inShape)  {
  //Health indicates range... Health decreases with distance
  var health: Int = 400
  //println(this.name +" Has Fired!")
  var xPos: Double = 0
  //wild card stores original x data
  var deathAnimator: Double = 0
  var wild: Double = xPos
  var yPos: Double = 0
  var wild2: Double = 0
  var xTar: Double =0
  var yTar: Double =0
  var shape:Shape = inShape
  var rot: Double =0

  override def toString: String ={
    this.name
  }
}
