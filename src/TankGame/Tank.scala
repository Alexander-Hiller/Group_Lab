package TankGame

import scalafx.scene.shape.Shape

class Tank(val name: String, val shape: Shape) extends thing(name, shape) {
  var health: Int = 100
  println(this.name +" Has Been Added to the Game")
  var xPos: Double = 0
  var deathAnimator: Double = 0
  var yPos: Double = 0
  var xTar: Double =0
  var yTar: Double =0
  var wild: Double = 0

  override def toString: String ={
    this.name
  }



}
