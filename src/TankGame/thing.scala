package TankGame

import scalafx.scene.shape.Shape

abstract class thing(name: String, shape: Shape) {
  def health(): Int
  def xPos(): Double
  def yPos(): Double
  def shape():Shape
}
