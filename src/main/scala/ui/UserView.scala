package ui

object UserMenu {
  def login(): Unit = {
    println(
      """
        ||----------------------------------|
        ||             Spoofify             |
        ||----------------------------------|
        || 1 -> Log In                      |
        ||----------------------------------|
        || 2 -> Sign Up                     |
        ||----------------------------------|
        || 3 -> Exit                        |
        ||----------------------------------|
        |""".stripMargin
    )
  }

  def returning(): Unit = {
    print("Returning")
    Thread.sleep(1000)
    print(" .")
    Thread.sleep(1000)
    print(" .")
    Thread.sleep(1000)
    println(" .")
    Thread.sleep(1000)
  }
}

object BasicView {
  def userMenu(): Unit = {
    println(
      """
        ||----------------------------------|
        ||            User Menu             |
        ||----------------------------------|
        || 1 -> Query Data                  |
        ||----------------------------------|
        || 2 -> Edit Username               |
        ||----------------------------------|
        || 3 -> Edit Password               |
        ||----------------------------------|
        || 4 -> Log Out                     |
        ||----------------------------------|
        |""".stripMargin)
  }
  def manageQueries(): Unit = {
    println(
      """
        ||----------------------------------|
        ||            Query Menu            |
        ||----------------------------------|
        || 1 -> Query 1                     |
        ||----------------------------------|
        || 2 -> Query 2                     |
        ||----------------------------------|
        || 3 -> Query 3                     |
        ||----------------------------------|
        || 4 -> Query 4                     |
        ||----------------------------------|
        || 5 -> Query 5                     |
        ||----------------------------------|
        || 6 -> Query 6                     |
        ||----------------------------------|
        || 0 -> Return                      |
        ||----------------------------------|
        |""".stripMargin)
  }
}

object AdminView {
  def userMenu(): Unit = {
    println(
      """
        ||----------------------------------|
        ||         Admin User Menu          |
        ||----------------------------------|
        || 1 -> Query Data                  |
        ||----------------------------------|
        || 2 -> Manage Users                |
        ||----------------------------------|
        || 3 -> Edit Username               |
        ||----------------------------------|
        || 4 -> Edit Password               |
        ||----------------------------------|
        || 5 -> Log Out                     |
        ||----------------------------------|
        |""".stripMargin)
  }

  def manageQueries(): Unit = {
    println(
      """
        ||----------------------------------|
        ||            Query Menu            |
        ||----------------------------------|
        || 1 -> Average Popularity of Songs |
        ||----------------------------------|
        || 2 -> Most Popular Songs          |
        ||----------------------------------|
        || 3 -> Total Songs by Key          |
        ||----------------------------------|
        || 4 -> Loudness/Energy             |
        ||----------------------------------|
        || 5 -> Average Artist Tempo        |
        ||----------------------------------|
        || 6 -> Track Duration/Liveness     |
        ||----------------------------------|
        || 7 -> Return                      |
        ||----------------------------------|
        |""".stripMargin)
  }

  def manageUsers(): Unit = {
    println(
      """
        ||----------------------------------|
        ||       User Management Menu       |
        ||----------------------------------|
        || 1 -> Remove User                 |
        ||----------------------------------|
        || 2 -> Change Admin Status         |
        ||----------------------------------|
        || 0 -> Return                      |
        ||----------------------------------|
        |""".stripMargin)
  }
}