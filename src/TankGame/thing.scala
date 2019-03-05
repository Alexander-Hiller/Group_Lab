package TankGame

import scalafx.scene.shape.Shape

abstract class thing(name: String, shape: Shape) {
  var health: Int
  var xPos: Double
  var yPos: Double
  def shape():Shape
  var xTar: Double
  var yTar: Double
  var deathAnimator: Double

  //wild variable to store extra data based on needs
  var wild: Double

}
