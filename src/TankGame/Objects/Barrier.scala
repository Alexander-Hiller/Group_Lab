package TankGame.Objects

import scalafx.scene.shape.Shape

class Barrier(val name: String, val inShape: Shape) extends thing(name, inShape) {
  var health: Int = (math.random()* 1000).toInt
  var xPos: Double = 0
  var deathAnimator: Double = 0
  var yPos: Double = 0
  //store width here
  var xTar: Double =0
  //store height here
  var yTar: Double =0
  var wild: Double = 0
  var wild2: Double = 0
  var rot: Double =0
  var shape:Shape = inShape
  override def toString: String ={
    this.name
  }



}