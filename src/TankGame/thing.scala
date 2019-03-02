package TankGame

import scalafx.scene.shape.Shape

abstract class thing(name: String, shape: Shape) {
  var health: Int
  var xPos: Double
  var yPos: Double
  def shape():Shape

}
