package TankGame

class Tank(val name: String) extends thing(name) {
  override def health(): Int = 100

  override def xPos(): Double = math.random()*1920

  override def yPos(): Double = math.random()*1080
}
