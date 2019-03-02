package TankGame

abstract class thing(name: String) {
  def health(): Int
  def xPos(): Double
  def yPos(): Double
}
