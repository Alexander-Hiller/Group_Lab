package TankGame

import scalafx.scene.shape.Shape

class Barrier(val name: String, val shape: Shape) extends thing(name, shape) {
  var health: Int = (math.random()* 1000).toInt
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