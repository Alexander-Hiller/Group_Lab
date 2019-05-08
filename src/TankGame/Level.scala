package TankGame

object Level {
    new Level {
      gridWidth = 20
      gridHeight = 20

      wallLocations = List(
        //left side
        new GridLocation(0, 0),
        new GridLocation(0, 1),
        new GridLocation(0, 2),
        new GridLocation(0, 3),
        new GridLocation(0, 4),
        new GridLocation(0, 5),
        new GridLocation(0, 6),
        new GridLocation(0, 7),
        new GridLocation(0, 8),
        new GridLocation(0, 9),
        new GridLocation(0, 10),
        new GridLocation(0, 11),
        new GridLocation(0, 12),
        new GridLocation(0, 13),
        new GridLocation(0, 14),
        new GridLocation(0, 15),
        new GridLocation(0, 16),
        new GridLocation(0, 17),
        new GridLocation(0, 18),
        new GridLocation(0, 19),
        //right side
        new GridLocation(19, 0),
        new GridLocation(19, 1),
        new GridLocation(19, 2),
        new GridLocation(19, 3),
        new GridLocation(19, 4),
        new GridLocation(19, 5),
        new GridLocation(19, 6),
        new GridLocation(19, 7),
        new GridLocation(19, 8),
        new GridLocation(19, 9),
        new GridLocation(19, 10),
        new GridLocation(19, 11),
        new GridLocation(19, 12),
        new GridLocation(19, 13),
        new GridLocation(19, 14),
        new GridLocation(19, 15),
        new GridLocation(19, 16),
        new GridLocation(19, 17),
        new GridLocation(19, 18),
        new GridLocation(19, 19),
        //top
        new GridLocation(0, 0),
        new GridLocation(1, 0),
        new GridLocation(2, 0),
        new GridLocation(3, 0),
        new GridLocation(4, 0),
        new GridLocation(5, 0),
        new GridLocation(6, 0),
        new GridLocation(7, 0),
        new GridLocation(8, 0),
        new GridLocation(9, 0),
        new GridLocation(10, 0),
        new GridLocation(11, 0),
        new GridLocation(12, 0),
        new GridLocation(13, 0),
        new GridLocation(14, 0),
        new GridLocation(15, 0),
        new GridLocation(16, 0),
        new GridLocation(17, 0),
        new GridLocation(18, 0),
        new GridLocation(19, 0),
        //bottom
        new GridLocation(0, 19),
        new GridLocation(1, 19),
        new GridLocation(2, 19),
        new GridLocation(3, 19),
        new GridLocation(4, 19),
        new GridLocation(5, 19),
        new GridLocation(6, 19),
        new GridLocation(7, 19),
        new GridLocation(8, 19),
        new GridLocation(9, 19),
        new GridLocation(10, 19),
        new GridLocation(11, 19),
        new GridLocation(12, 19),
        new GridLocation(13, 19),
        new GridLocation(14, 19),
        new GridLocation(15, 19),
        new GridLocation(16, 19),
        new GridLocation(17, 19),
        new GridLocation(18, 19),
        new GridLocation(19, 19)
      )

    }
}


class Level {

  var wallLocations:List[GridLocation] = List()

  var gridWidth: Int = 20
  var gridHeight: Int = 20

}
