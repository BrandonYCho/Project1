import db.HiveMaster
import ui.MainMenu

object Main {
  def main(args: Array[String]): Unit = {
    HiveMaster.startDB()
    MainMenu.intro()
  }
}