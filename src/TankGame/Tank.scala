package TankGame

import scalafx.scene.shape.Shape

class Tank(val name: String, val shape: Shape) extends thing(name, shape) {
  override def health(): Int = 100
  println(this.name +" Has Been Added to the Game")
  override def xPos(): Double = math.random()*1920

  override def yPos(): Double = math.random()*1080



}
