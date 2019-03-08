package TankGame

import scalafx.scene.shape.Shape

abstract class thing(name: String, inShape: Shape) {
  var health: Int
  var xPos: Double
  var yPos: Double
  var shape:Shape
  var xTar: Double
  var yTar: Double
  var deathAnimator: Double

  //wild variable to store extra data based on needs
  var wild: Double

}
